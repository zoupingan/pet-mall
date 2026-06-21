from fastapi import APIRouter, Query,Request

from agent.async_react_agent import AsyncReactAgent

router = APIRouter(prefix="/agent")


@router.get("/stream")
async def chat(
        request: Request,
        query: str,
        session_id: str | None = None,
        user_id: int | None = Query(None),
):
    agent: AsyncReactAgent = request.app.state.agent

    response_parts = []

    async for chunk in agent.execute_stream(
            query=query,
            session_id=session_id,
            user_id=user_id,
    ):
        response_parts.append(chunk)

    result = "".join(response_parts)

    if result.startswith(query):
        result = result[len(query):]

    return result
