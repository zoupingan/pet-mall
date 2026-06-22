


from mcp.server import FastMCP



mcp = FastMCP("pet-mall-order-server")

@mcp.tool()
def get_user_order_status(user_id: int, order_no: str = ""):
    """
        "查询当前用户的订单状态或最近一笔订单。"
        "入参 user_id 必须来自 get_user_id 工具；order_no 为订单号，可为空。"
        "用户问某个订单到哪了、订单发货了吗时传入订单号；用户问最近订单、最新订单、我最近买了什么时 order_no 传空字符串。"
    """
    if user_id is None:
        return "请先调用 get_user_id() 工具获取当前用户的真实ID。"

    if not isinstance(user_id, int):
        try:
            user_id = int(user_id)
        except (ValueError, TypeError):
            return f"无效的用户ID：{user_id}"

    from config.db_config import sync_session
    from app.api.curd.order import crud_get_latest_order_sync, crud_get_order_by_no_sync

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


def _format_order(order) -> str:
    return (
        f"订单号: {order.order_no}\n"
        f"   总金额: {order.total_amount}\n"
        f"   状态: {_get_order_status_text(order.status)}\n"
        f"   下单时间: {order.create_time}"
    )



@mcp.tool()
def get_user_orders(user_id: int):
    """
        "查询指定用户的所有订单数据。\n"
        "入参：user_id (int类型，必需) - 用户ID，必须来自get_user_id工具的返回值\n"
        "出参：字符串类型的订单列表，包含订单号、金额、状态、创建时间等详细信息\n"
        "使用规则（必须严格遵守）：\n"
        "1. 必须先调用 get_user_id() 工具获取真实的用户ID\n"
        "2. 将 get_user_id() 返回的值原封不动地作为 user_id 参数传入本工具\n"
        "3. 禁止自己编造或猜测 user_id 的值\n"
        "4. 如果用户没有订单，会返回'暂无订单记录'的友好提示\n"
        "示例流程：\n"
        "用户:'查询我的订单'\n"
        "Agent思考: 需要查询订单 → 调用 get_user_id() → 得到 user_id=2 → 调用 get_user_orders(2)"
    """
    if user_id is None:
        return "请先调用 get_user_id() 工具获取当前用户的真实ID，然后将该ID传入本工具。"

    if not isinstance(user_id, int):
        try:
            user_id = int(user_id)
        except (ValueError, TypeError):
            return "错误：无效的用户ID '{user_id}'，必须是整数类型。请使用 get_user_id() 返回的原始值。"

    from config.db_config import sync_session
    from app.api.curd.order import crud_get_orders_sync

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

if __name__ == "__main__":
    mcp.run(transport="stdio")