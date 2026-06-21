import asyncio
from pathlib import Path

from langchain.agents import create_agent
from langchain_mcp_adapters.client import MultiServerMCPClient

from agent.tools.agent_tools import (
    get_user_id,
    get_user_order_status,
    get_user_orders,
    rag_summarize,
    search_products_by_brand,
    search_products_by_category,
)
from agent.tools.middleware import (
    async_monitor_tool,
    log_before_model,
)
from model.factory import chat_model
from utils.prompt_loader import load_system_prompt


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

    product_mcp_tool = next(
        tool
        for tool in mcp_tools
        if tool.name == "get_product_price_stock"
    )

    agent = create_agent(
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

    queries = [
        "忘记密码怎么办？"
    ]

    for query in queries:
        print(f"\n===== {query} =====")

        result = await agent.ainvoke(
            {
                "messages": [
                    {
                        "role": "user",
                        "content": query,
                    }
                ]
            }
        )

        print("最终回答：", result["messages"][-1].content)


if __name__ == "__main__":
    asyncio.run(main())