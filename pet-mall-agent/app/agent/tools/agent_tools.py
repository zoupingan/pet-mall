from contextvars import ContextVar, Token
from langchain_core.tools import tool
from app.rag.rag_service import RagSummarizeService


rag = RagSummarizeService()

current_user_id: ContextVar[int | None] = ContextVar("current_user_id", default=None)

def set_request_user_id(user_id: int | None) -> Token:
    return current_user_id.set(user_id)

def clear_request_user_id(token: Token) -> None:
    current_user_id.reset(token)

@tool(
    response_format="content_and_artifact",
    description="查询商城静态知识。query 应保留用户问题的核心关键词，不得宽泛改写。"
)
def rag_summarize(query: str, history: str = ""):
    answer, sources = rag.rag_summarize(query, history)

    return answer, {
        "sources": sources
    }

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









