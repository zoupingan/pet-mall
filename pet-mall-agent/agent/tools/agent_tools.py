import os.path
import random
from contextvars import ContextVar, Token

from langchain_core.tools import tool

from rag.rag_service import RagSummarizeService
from utils.config_handler import agent_config
from utils.logger_hanlder import logger
from utils.path_tool import get_abs_path

rag = RagSummarizeService()
month_arr = ["2025-01", "2025-02", "2025-03", "2025-04", "2025-05", "2025-06", "2025-07", "2025-08", "2025-09",
             "2025-10", "2025-11", "2025-12"]
external_data = {}

current_user_id: ContextVar[int | None] = ContextVar("current_user_id", default=None)


def set_request_user_id(user_id: int | None) -> Token:
    return current_user_id.set(user_id)


def clear_request_user_id(token: Token) -> None:
    current_user_id.reset(token)


@tool(description="从向量库中检索参考资料")
def rag_summarize(query: str, history: str) -> str:
    return rag.rag_summarize(query, history)


@tool(description="获取用户所在城市的名字，以纯字符串形式返回")
def get_user_location() -> str:
    # 模拟，随机提供一个城市名称
    return random.choice(["北京", "上海", "广州", "深圳", "杭州"])


@tool(
    description=(
        "【重要】获取当前登录用户的真实ID。"
        "此工具必须被调用以获取用户身份，用于后续查询订单、收藏等个人数据。"
        "返回值为整数类型的用户ID。"
        "\n\n"
        "⚠️ 重要约束：\n"
        "- 必须调用此工具获取真实用户ID，禁止猜测或编造用户ID\n"
        "- 返回的ID是从系统上下文中获取的真实值，必须直接使用\n"
        "- 不要对返回值进行任何修改或计算\n"
        "\n\n"
        "使用场景：\n"
        "- 查询用户订单前必须先调用此工具获取user_id\n"
        "- 查询用户收藏、历史记录等个人信息时\n"
        "- 任何需要确认当前用户身份的场景"
    )
)
def get_user_id() -> int:
    user_id = current_user_id.get()
    if user_id is None:
        raise ValueError("当前请求没有提供用户ID")
    return user_id


@tool(description="获取当前月份，以纯字符串形式返回")
def get_current_month() -> str:
    return random.choice(month_arr)


def generate_external():
    """
    {
        "user_id":{
            "month":{"特征"：xxx,"效率":xxx}
            "month":{"特征"：xxx,"效率":xxx}
        },
        "user_id":{
            "month":{"特征"：xxx,"效率":xxx}
            "month":{"特征"：xxx,"效率":xxx}
        }...
    }
    :return:
    """
    if not external_data:
        external_data_path = get_abs_path(agent_config["external_data_path"])
        if not os.path.exists(external_data_path):
            raise FileNotFoundError(f"[外部数据]：{external_data_path}不存在")
        with open(external_data_path, "r", encoding="utf-8") as f:
            for line in f.readlines()[1:]:
                arr: list[str] = line.strip().split(",")
                user_id: str = arr[0].replace('"', "")
                feature: str = arr[1].replace('"', "")
                efficiency: str = arr[2].replace('"', "")
                consumables: str = arr[3].replace('"', "")
                comparison: str = arr[4].replace('"', "")
                time: str = arr[5].replace('"', "")

                if not user_id in external_data:
                    external_data[user_id] = {}
                external_data[user_id][time] = {"特征": feature, "效率": efficiency, "消耗": consumables,
                                                "对比": comparison}


@tool(description="从外部数据获取指定用户在指定月份的使用记录，返回纯格式字符串,如未检索到返回空字符串")
def fetch_external_data(user_id: str, month: str) -> str:
    generate_external()

    try:
        return external_data[user_id][month]
    except KeyError:
        logger.warning(f"[外部数据]：未找到用户{user_id}在{month}的使用记录")
        return ""


@tool(
    description="无入参，无返回值，调用后触发中间件自动为报告生成的场景动态注入的上下文信息，为后续提示词切换提供上下文信息")
def fill_context_for_report():
    return "fill_context_for_report已调用"


# ---------------------------------------------------------------------------------------------------------------------------
# 查询数据库
@tool(
    description=(
        "查询指定用户的所有订单数据。\n"
        "入参：user_id (int类型，必需) - 用户ID，必须来自get_user_id工具的返回值\n"
        "出参：字符串类型的订单列表，包含订单号、金额、状态、创建时间等详细信息\n"
        "\n\n"
        "⚠️ 使用规则（必须严格遵守）：\n"
        "1. 必须先调用 get_user_id() 工具获取真实的用户ID\n"
        "2. 将 get_user_id() 返回的值原封不动地作为 user_id 参数传入本工具\n"
        "3. 禁止自己编造或猜测 user_id 的值\n"
        "4. 如果用户没有订单，会返回'暂无订单记录'的友好提示\n"
        "\n\n"
        "示例流程：\n"
        "用户:'查询我的订单'\n"
        "Agent思考: 需要查询订单 → 调用 get_user_id() → 得到 user_id=2 → 调用 get_user_orders(2)"
    )
)
def get_user_orders(user_id: int):
    if user_id is None:
        return "请先调用 get_user_id() 工具获取当前用户的真实ID，然后将该ID传入本工具。"

    if not isinstance(user_id, int):
        try:
            user_id = int(user_id)
        except (ValueError, TypeError):
            return "错误：无效的用户ID '{user_id}'，必须是整数类型。请使用 get_user_id() 返回的原始值。"

    from config.db_config import sync_session
    from curd.order import crud_get_orders_sync

    db = sync_session()
    try:
        orders = crud_get_orders_sync(db, user_id)
        if not orders:
            return f"该用户暂无订单记录。\n"

        result_lines = [f"\n用户 {user_id} 的订单列表（共 {len(orders)} 条）："]
        for i, order in enumerate(orders, 1):
            order_info = (
                f"{i}. 订单号: {order.order_no}\n"
                f"   用户ID: {order.user_id}\n"
                f"   总金额: ¥{order.total_amount:.2f}\n"
                f"   状态: {_get_order_status_text(order.status)}\n"
                f"   创建时间: {order.create_time}"
            )
            result_lines.append(order_info)

        return "\n".join(result_lines)

    except Exception as e:
        return f"查询订单失败：{str(e)}"

    finally:
        db.close()

def _get_order_status_text(status: int) -> str:
    """将订单状态码转换为可读文本"""
    status_map = {
        0: "待付款",
        1: "待发货",
        2: "已发货",
        3: "已完成",
        4: "已取消",
        5: "退款中",
        6: "已退款"
    }
    return status_map.get(status, f"未知状态({status})")


def _format_money(value) -> str:
    if value is None:
        return "暂无价格"
    return f"¥{float(value):.2f}"


def _format_product(product) -> str:
    status_text = "上架" if product.status == 1 else "下架"
    return (
        f"商品名: {product.product_name}\n"
        f"   售价: {_format_money(product.price)}\n"
        f"   库存: {product.stock or 0}\n"
        f"   状态: {status_text}"
    )


def _format_order(order) -> str:
    return (
        f"订单号: {order.order_no}\n"
        f"   总金额: {_format_money(order.total_amount)}\n"
        f"   状态: {_get_order_status_text(order.status)}\n"
        f"   下单时间: {order.create_time}"
    )


@tool(description="根据用户输入的品牌名称查询该品牌下面的所有商品")
def search_products_by_brand(brand_name: str):
    if brand_name is None:
        return "请告诉我想查询哪个品牌。"

    from config.db_config import sync_session
    from curd.product import crud_get_products_sync
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


@tool(
    description=(
        "根据商品名称查询商品的实时售价、库存和上架状态。"
        "当用户询问某个商品多少钱、有没有货、库存多少、能不能买时调用。"
        "入参 product_name 为用户提到的商品名称关键词。"
    )
)
def get_product_price_stock(product_name: str):
    if not product_name:
        return "请告诉我想查询哪个商品。"

    from config.db_config import sync_session
    from curd.product import crud_get_product_price_stock_sync

    db = sync_session()
    try:
        products = crud_get_product_price_stock_sync(db, product_name)
        if not products:
            return f"没有查询到包含“{product_name}”的商品。"

        result_lines = [f"\n查询到 {len(products)} 个相关商品："]
        for i, product in enumerate(products, 1):
            result_lines.append(f"{i}. {_format_product(product)}")
        return "\n".join(result_lines)
    except Exception as e:
        return f"查询商品价格库存失败：{str(e)}"
    finally:
        db.close()


@tool(
    description=(
        "根据分类名称查询该分类下的上架商品。"
        "当用户询问猫粮有哪些、狗粮有哪些、宠物清洁类有什么、某分类商品列表时调用。"
        "入参 category_name 为分类名称关键词。"
    )
)
def search_products_by_category(category_name: str):
    if not category_name:
        return "请告诉我想查询哪个分类。"

    from config.db_config import sync_session
    from curd.product import crud_get_products_by_category_sync

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


@tool(
    description=(
        "查询当前用户的订单状态或最近一笔订单。"
        "入参 user_id 必须来自 get_user_id 工具；order_no 为订单号，可为空。"
        "用户问某个订单到哪了、订单发货了吗时传入订单号；用户问最近订单、最新订单、我最近买了什么时 order_no 传空字符串。"
    )
)
def get_user_order_status(user_id: int, order_no: str = ""):
    if user_id is None:
        return "请先调用 get_user_id() 工具获取当前用户的真实ID。"

    if not isinstance(user_id, int):
        try:
            user_id = int(user_id)
        except (ValueError, TypeError):
            return f"无效的用户ID：{user_id}"

    from config.db_config import sync_session
    from curd.order import crud_get_latest_order_sync, crud_get_order_by_no_sync

    db = sync_session()
    try:
        order_no = (order_no or "").strip()
        if order_no:
            order = crud_get_order_by_no_sync(db, user_id, order_no)
            if order is None:
                return f"没有查询到当前用户的订单：{order_no}"
            return "\n当前订单状态：\n" + _format_order(order)

        order = crud_get_latest_order_sync(db, user_id)
        if order is None:
            return "当前用户暂无订单记录。"
        return "\n当前用户最近一笔订单：\n" + _format_order(order)
    except Exception as e:
        return f"查询订单状态失败：{str(e)}"
    finally:
        db.close()
