import logging
from typing_extensions import TypedDict
from mcp.server import FastMCP

from config.db_config import sync_session
from curd.product import crud_get_product_price_stock_sync

logger = logging.getLogger(__name__)

# 1. 创建 MCP Server
mcp = FastMCP("pet-mall-product-server")


class ProductInfo(TypedDict):
    product_name: str
    price: float | None
    stock: int
    status: str


class ProductQueryResult(TypedDict):
    success: bool
    message: str
    products: list[ProductInfo]


# 2.注册一个tool
@mcp.tool()
def get_product_price_stock(product_name: str) -> ProductQueryResult:
    """实时查询商品售价、库存和上下架状态。
        当用户询问某个商品多少钱、有没有货、库存数量、
        能否购买或是否在售时调用。
        """
    product_name = product_name.strip()
    if not product_name:
        return {
            "success": False,
            "message": "商品名称不能为空。",
            "products": []
        }

    db = sync_session()
    try:
        products = crud_get_product_price_stock_sync(db, product_name)
        if not products:
            return {
                "success": False,
                "message": f"没有查询到包含“{product_name}”的商品。",
                "products": []
            }

        product_data = [
            {
                "product_name": product.product_name,
                "price": (
                    float(product.price)
                    if product.price is not None
                    else None
                ),
                "stock": product.stock or 0,
                "status": "上架" if product.status == 1 else "下架",
            }
            for product in products
        ]

        return {
            "success": True,
            "message": f"查询到 {len(product_data)} 个相关商品。",
            "products": product_data,
        }
    except Exception as e:
        logger.exception("商品查询失败！")
        raise RuntimeError("商品查询失败！")
    finally:
        db.close()


if __name__ == '__main__':
    # 3. 启动 MCP Server
    mcp.run(transport="stdio")
