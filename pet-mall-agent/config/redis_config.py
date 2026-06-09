import json
import os

import redis

REDIS_HOST = os.getenv("REDIS_HOST", "localhost")
REDIS_PORT = int(os.getenv("REDIS_PORT", "6379"))
REDIS_DB = 4
SESSION_HISTORY_EXPIRE_SECONDS = 60 * 60 * 24

redis_client = redis.Redis(
    host=REDIS_HOST,
    port=REDIS_PORT,
    db=REDIS_DB,
    decode_responses=True,
    max_connections=50,
    socket_timeout=5,
    socket_connect_timeout=5,
    health_check_interval=30,
)


def _get_session_history_key(session_id: str) -> str:
    return f"history:session:{session_id}"


def get_session_history(session_id: str, max_messages: int = 50) -> list[dict]:
    try:
        key = _get_session_history_key(session_id)
        raw_messages = redis_client.lrange(key, 0, -1)
        history = []

        for message_text in raw_messages:
            try:
                message = json.loads(message_text)
                if isinstance(message, list):
                    history.extend(message)
                elif isinstance(message, dict):
                    history.append(message)
            except json.JSONDecodeError:
                continue

        if raw_messages and redis_client.ttl(key) == -1:
            redis_client.expire(key, SESSION_HISTORY_EXPIRE_SECONDS)

        return history[-max_messages:]
    except redis.RedisError as error:
        print(f"Redis操作失败: {error}")
        return []


def add_session_message(
    session_id: str,
    role: str,
    content: str,
    expire: int = SESSION_HISTORY_EXPIRE_SECONDS,
) -> bool:
    try:
        key = _get_session_history_key(session_id)
        message = json.dumps(
            {"role": role, "content": content},
            ensure_ascii=False,
        )

        redis_client.rpush(key, message)
        redis_client.ltrim(key, -50, -1)
        redis_client.expire(key, expire)
        return True
    except redis.RedisError as error:
        print(f"Redis操作失败: {error}")
        return False
