from fastapi import APIRouter, Query

from agent.react_agent import ReactAgent

router = APIRouter(prefix="/agent")


@router.get("/stream")
async def chat(
    query: str,
    session_id: str | None = None,
    user_id: int | None = Query(None),
):
    agent = ReactAgent()
    stream = agent.execute_stream(query, session_id, user_id)
    result = "".join(chunk for chunk in stream)

    if result.startswith(query):
        result = result[len(query):]
    return result
