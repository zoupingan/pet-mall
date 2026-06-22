import asyncio
from pathlib import Path

from mcp import ClientSession, StdioServerParameters
from mcp.client.stdio import stdio_client


async def main():
    project_root = Path(__file__).resolve().parents[3]
    server_params = StdioServerParameters(
        command=r"C:\venvs\pet-mall-agent\Scripts\python.exe",
        args=["-m", "app.mcp.mcp_server.product_server"],
        cwd=str(project_root),
    )

    async with stdio_client(server_params) as (read, write):
        async with ClientSession(read, write) as session:
            await session.initialize()

            tools = await session.list_tools()
            print("发现的工具：", [tool.name for tool in tools.tools])

            test_cases = [
                "皇家K36幼猫粮",
                "完全不存在的商品xyz",
                "   ",
            ]

            for product_name in test_cases:
                print(f"\n测试商品名：{product_name!r}")

                result = await session.call_tool(
                    "get_product_price_stock",
                    arguments={"product_name": product_name},
                )

                print("MCP 调用失败：", result.isError)
                print("结构化结果：", result.structuredContent)


if __name__ == "__main__":
    asyncio.run(main())
