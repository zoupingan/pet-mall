import asyncio
from pathlib import Path

from langchain_mcp_adapters.client import MultiServerMCPClient


async def main():
    project_root = Path(__file__).resolve().parents[1]

    client = MultiServerMCPClient(
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

    tools = await client.get_tools()

    print("LangChain 工具：", [tool.name for tool in tools])

    product_tool = next(
        tool
        for tool in tools
        if tool.name == "get_product_price_stock"
    )

    result = await product_tool.ainvoke(
        {"product_name": "皇家K36幼猫粮"}
    )

    print("直接调用结果：", result)


if __name__ == "__main__":
    asyncio.run(main())
