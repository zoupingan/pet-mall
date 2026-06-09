from langchain.agents import create_agent

from agent.tools.agent_tools import (
    clear_request_user_id,
    fetch_external_data,
    fill_context_for_report,
    get_current_month,
    get_product_price_stock,
    get_user_id,
    get_user_location,
    get_user_order_status,
    get_user_orders,
    rag_summarize,
    search_products_by_category,
    search_products_by_brand,
    set_request_user_id,
)
from agent.tools.middleware import log_before_model, monitor_tool, report_prompt_switch
from config.redis_config import add_session_message, get_session_history
from model.factory import chat_model
from utils.prompt_loader import load_system_prompt


class ReactAgent:
    def __init__(self):
        self.agent = create_agent(
            model=chat_model,
            system_prompt=load_system_prompt(),
            middleware=[monitor_tool, log_before_model, report_prompt_switch],
            tools=[
                rag_summarize,
                get_user_location,
                get_user_id,
                get_current_month,
                fetch_external_data,
                fill_context_for_report,
                get_user_orders,
                search_products_by_brand,
                get_product_price_stock,
                search_products_by_category,
                get_user_order_status,
            ],
        )

    def execute_stream(
        self,
        query: str,
        session_id: str | None = None,
        user_id: int | None = None,
    ):
        messages = []

        if session_id:
            history = get_session_history(session_id, max_messages=20)
            for message in history:
                messages.append({
                    "role": message["role"],
                    "content": message["content"],
                })

        messages.append({"role": "user", "content": query})

        user_id_token = set_request_user_id(user_id)
        input_dict = {"messages": messages}

        if session_id:
            add_session_message(session_id, "user", query)

        full_response = ""
        last_content = ""
        try:
            for chunk in self.agent.stream(input_dict, stream_mode="values", context={"report": False}):
                latest = chunk["messages"][-1]
                if getattr(latest, "type", "") != "ai":
                    continue
                if getattr(latest, "tool_calls", None):
                    continue
                if latest.content:
                    current_content = latest.content.strip()
                    if current_content and current_content != last_content:
                        new_part = current_content[len(last_content):] if current_content.startswith(last_content) else current_content
                        if new_part.strip():
                            res = new_part.strip() + "\n"
                            full_response += res
                            yield res
                        last_content = current_content

        finally:
            clear_request_user_id(user_id_token)

        if session_id and full_response:
            add_session_message(session_id, "assistant", full_response)
