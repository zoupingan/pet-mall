from contextlib import asynccontextmanager

from fastapi import FastAPI

from agent.async_react_agent import AsyncReactAgent
from router.agent import router


# 使用lifespan初始化agent以及mcp,防止每次请求都初始化
@asynccontextmanager
async def lifespan(app: FastAPI):
    app.state.agent = await AsyncReactAgent.create()

    yield

    app.state.agent = None


app = FastAPI(lifespan=lifespan)
app.include_router(router)
