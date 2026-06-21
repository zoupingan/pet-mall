import asyncio
from pathlib import Path

from langchain.agents import create_agent
from langchain_mcp_adapters.client import MultiServerMCPClient

from agent.tools.middleware import async_monitor_tool, log_before_model
from model.factory import chat_model


async def main():
    project_root = Path(__file__).resolve().parents[1]

    mcp_client = MultiServerMCPClient(
        {
            "pet_product": {
                "transport": "stdio",
                "command": (
                    r"C:\venvs\pet-mall-agent\Scripts\python.exe"
                ),
                "args": ["-m", "mcp_server.product_server"],
                "cwd": str(project_root),
            }
        }
    )

    mcp_tools = await mcp_client.get_tools()

    agent = create_agent(
        model=chat_model,
        tools=mcp_tools,
        system_prompt=(
            "你是宠物商城助手。"
            "商品价格、库存和销售状态必须调用工具查询，"
            "不能凭记忆回答。"
        ),
        middleware=[
            async_monitor_tool,
            log_before_model,
        ],
    )

    result = await agent.ainvoke(
        {
            "messages": [
                {
                    "role": "user",
                    "content": "皇家K36幼猫粮现在多少钱，还有货吗？",
                }
            ]
        }
    )

    for message in result["messages"]:
        print(
            type(message).__name__,
            getattr(message, "content", ""),
        )


if __name__ == "__main__":
    asyncio.run(main())