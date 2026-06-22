import logging
from typing_extensions import TypedDict
from mcp.server import FastMCP

from app.api.curd.product import crud_get_product_price_stock_sync
from config.db_config import sync_session

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


@mcp.tool()
async def search_products_by_brand(brand_name: str):
    """
    根据用户输入的品牌名称查询该品牌下面的所有商品
    """

    if brand_name is None:
        return "请告诉我想查询哪个品牌。"

    from config.db_config import sync_session
    from app.api.curd.product import crud_get_products_sync
    db = sync_session()
    try:
        products = crud_get_products_sync(db, brand_name)
        if not products:
            return f"该品牌暂无商品。\n"

        result_lines = [f"\n品牌 {brand_name} 的商品列表（共 {len(products)} 条）："]
        for i, product in enumerate(products, 1):
            result_lines.append(f"{i}. {_format_product(product)}")

        return "\n".join(result_lines)

    except Exception as e:
        return f"查询品牌商品失败：{str(e)}"

    finally:
        db.close()


@mcp.tool()
def search_products_by_category(category_name: str):
    """
        "根据分类名称查询该分类下的上架商品。"
        "当用户询问猫粮有哪些、狗粮有哪些、宠物清洁类有什么、某分类商品列表时调用。"
        "入参 category_name 为分类名称关键词。"
    """
    if not category_name:
        return "请告诉我想查询哪个分类。"

    from config.db_config import sync_session
    from app.api.curd.product import crud_get_products_by_category_sync

    db = sync_session()
    try:
        products = crud_get_products_by_category_sync(db, category_name)
        if not products:
            return f"该分类暂无上架商品，或分类“{category_name}”不存在。"

        result_lines = [f"\n分类 {category_name} 下的商品（共 {len(products)} 个）："]
        for i, product in enumerate(products, 1):
            result_lines.append(f"{i}. {_format_product(product)}")
        return "\n".join(result_lines)
    except Exception as e:
        return f"查询分类商品失败：{str(e)}"
    finally:
        db.close()


def _format_product(product) -> str:
    status_text = "上架" if product.status == 1 else "下架"
    return (
        f"商品名: {product.product_name}\n"
        f"   售价: {product.price}\n"
        f"   库存: {product.stock or 0}\n"
        f"   状态: {status_text}"

    )


if __name__ == '__main__':
    # 3. 启动 MCP Server
    mcp.run(transport="stdio")
