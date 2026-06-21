from pathlib import Path
from uuid import uuid4

from langchain.agents import create_agent
from langchain_mcp_adapters.client import MultiServerMCPClient

from agent.tools.agent_tools import (
    clear_request_user_id,
    get_user_id,
    get_user_order_status,
    get_user_orders,
    rag_summarize,
    search_products_by_brand,
    search_products_by_category,
    set_request_user_id,
)
from agent.tools.middleware import (
    async_monitor_tool,
    log_before_model,
)
from config.redis_config import (
    add_session_message,
    get_session_history,
)
from model.factory import chat_model
from utils.logger_hanlder import logger
from utils.prompt_loader import load_system_prompt


class AsyncReactAgent:
    def __init__(self):
        self.mcp_client = None
        self.agent = None

    @classmethod
    async def create(cls):
        instance = cls()
        project_root = Path(__file__).resolve().parents[1]

        instance.mcp_client = MultiServerMCPClient(
            {
                "pet_product": {
                    "transport": "stdio",
                    "command": (
                        r"C:\venvs\pet-mall-agent\Scripts\python.exe"
                    ),
                    "args": [
                        "-m",
                        "mcp_server.product_server",
                    ],
                    "cwd": str(project_root),
                }
            }
        )

        mcp_tools = await instance.mcp_client.get_tools()

        product_mcp_tool = next(
            tool
            for tool in mcp_tools
            if tool.name == "get_product_price_stock"
        )

        instance.agent = create_agent(
            model=chat_model,
            system_prompt=load_system_prompt(),
            middleware=[
                async_monitor_tool,
                log_before_model,
            ],
            tools=[
                rag_summarize,
                get_user_id,
                get_user_orders,
                search_products_by_brand,
                product_mcp_tool,
                search_products_by_category,
                get_user_order_status,
            ],
        )

        return instance

    async def execute_stream(
            self,
            query: str,
            session_id: str | None = None,
            user_id: int | None = None,
    ):
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
            clear_request_user_id(user_id_token)

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
    session_id = f"mcp-agent-{uuid4().hex}"

    async for chunk in agent.execute_stream(
            query="皇家K36幼猫粮现在多少钱，还有货吗？",
            session_id=session_id,
            user_id=1,
    ):
        print(chunk, end="")


if __name__ == "__main__":
    import asyncio

    asyncio.run(main())
