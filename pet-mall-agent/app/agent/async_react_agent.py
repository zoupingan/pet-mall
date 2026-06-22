from pathlib import Path
from time import perf_counter
from uuid import uuid4

from langchain.agents import create_agent
from langchain_mcp_adapters.client import MultiServerMCPClient

from app.agent.tools.agent_tools import (
    clear_request_user_id,
    get_user_id,
    rag_summarize,
    set_request_user_id,
)
from app.agent.tools.middleware import (
    async_monitor_tool,
    async_monitor_model,
)
from config.redis_config import (
    add_session_message,
    get_session_history,
)
from app.llm.factory import chat_model
from utils.logger_hanlder import logger
from utils.prompt_loader import load_system_prompt
from utils.trace_context import start_trace, clear_trace


class AsyncReactAgent:
    def __init__(self):
        self.mcp_client = None
        self.agent = None

    @classmethod
    async def create(cls):
        instance = cls()
        project_root = Path(__file__).resolve().parents[2]

        instance.mcp_client = MultiServerMCPClient(
            {
                "pet_product": {
                    "transport": "stdio",
                    "command": r"C:\venvs\pet-mall-agent\Scripts\python.exe",
                    "args": ["-m","app.mcp.mcp_server.product_server",],
                    "cwd": str(project_root),
                },
                "pet_order": {
                    "transport": "stdio",
                    "command": r"C:\venvs\pet-mall-agent\Scripts\python.exe",
                    "args": ["-m", "app.mcp.mcp_server.order_server"],
                    "cwd": str(project_root),
                },
            }
        )

        mcp_tools = await instance.mcp_client.get_tools()

        instance.agent = create_agent(
            model=chat_model,
            system_prompt=load_system_prompt(),
            middleware=[
                async_monitor_tool,
                async_monitor_model,
            ],
            tools=[
                rag_summarize,
                get_user_id,
                *mcp_tools
            ],
        )

        return instance

    async def execute_stream(
            self,
            query: str,
            session_id: str | None = None,
            user_id: int | None = None,
    ):
        #生成 trace_id, 并开启 trace
        trace_id = uuid4().hex[:8]
        state_token = start_trace(trace_id)
        request_started_at = perf_counter()

        logger.info(
            f"[trace={trace_id}] 请求开始："
            f"session_id={session_id}，user_id={user_id}，"
            f"query={query!r}"
        )

        rag_sources = set()
        messages = []

        if session_id:
            history = get_session_history(
                session_id,
                max_messages=20,
            )
            for message in history:
                messages.append(
                    {
                        "role": message["role"],
                        "content": message["content"],
                    }
                )

        messages.append(
            {
                "role": "user",
                "content": query,
            }
        )

        user_id_token = set_request_user_id(user_id)
        input_dict = {"messages": messages}

        if session_id:
            add_session_message(session_id, "user", query)

        full_response = ""
        last_content = ""

        try:
            async for chunk in self.agent.astream(
                    input_dict,
                    stream_mode="values",
            ):
                latest = chunk["messages"][-1]

                if getattr(latest, "type", "") == "tool":
                    artifact = getattr(latest, "artifact", None)

                    if (
                            getattr(latest, "name", "")
                            == "rag_summarize"
                            and isinstance(artifact, dict)
                    ):
                        rag_sources.update(
                            artifact.get("sources", [])
                        )
                    continue

                if getattr(latest, "type", "") != "ai":
                    continue

                if getattr(latest, "tool_calls", None):
                    continue

                if latest.content:
                    current_content = latest.content.strip()

                    if (
                            current_content
                            and current_content != last_content
                    ):
                        if current_content.startswith(last_content):
                            new_part = current_content[
                                len(last_content):
                            ]
                        else:
                            new_part = current_content

                        if new_part.strip():
                            response_part = new_part.strip() + "\n"
                            full_response += response_part
                            yield response_part

                        last_content = current_content
        finally:
            elapsed = perf_counter() - request_started_at

            logger.info(
                f"[trace={trace_id}] 请求结束，"
                f"总耗时={elapsed:.3f}秒"
            )

            clear_request_user_id(user_id_token)
            clear_trace(state_token)

        if session_id and full_response:
            if rag_sources:
                logger.info(
                    f"本次 RAG 来源：{sorted(rag_sources)}"
                )

            add_session_message(
                session_id,
                "assistant",
                full_response,
            )


async def main():
    agent = await AsyncReactAgent.create()


    queries = ["忘记密码了怎么办"]

    for query in queries:
        session_id = f"mcp-agent-{uuid4().hex}"

        async for chunk in agent.execute_stream(
                query=query,
                session_id=session_id,
                user_id=1,
        ):
            print(chunk, end="")


if __name__ == "__main__":
    import asyncio

    asyncio.run(main())
