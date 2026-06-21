from uuid import uuid4

from langchain.agents import create_agent

from agent.tools.agent_tools import (
    clear_request_user_id,
    get_product_price_stock,
    get_user_id,
    get_user_order_status,
    get_user_orders,
    rag_summarize,
    search_products_by_category,
    search_products_by_brand,
    set_request_user_id,
)
from agent.tools.middleware import log_before_model, monitor_tool, async_monitor_tool
from config.redis_config import add_session_message, get_session_history
from model.factory import chat_model
from utils.logger_hanlder import logger
from utils.prompt_loader import load_system_prompt


class ReactAgent:
    def __init__(self):
        self.agent = create_agent(
            model=chat_model,
            system_prompt=load_system_prompt(),
            middleware=[monitor_tool, log_before_model,],
            tools=[
                rag_summarize,
                get_user_id,
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
        rag_sources = set()

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
            for chunk in self.agent.stream(input_dict, stream_mode="values"):
                latest = chunk["messages"][-1]
                if getattr(latest, "type", "") == "tool":
                    artifact = getattr(latest, "artifact", None)

                    if (
                            getattr(latest, "name", "") == "rag_summarize"
                            and isinstance(artifact, dict)
                    ):
                        rag_sources.update(artifact.get("sources", []))

                    continue

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
            if rag_sources:
                source_line = "\n参考来源：" + "、".join(sorted(rag_sources)) + "\n"
                full_response += source_line
                logger.info(f"本次 RAG 来源：{sorted(rag_sources)}")
            add_session_message(session_id, "assistant", full_response)

if __name__ == "__main__":
    agent = ReactAgent()
    queries = ["皇家K36幼猫粮现在多少钱，还有货吗？"]
    for index, query in enumerate(queries, start=1):
        session_id = f"rag-eval-{uuid4().hex}"

        print(f"\n\n===== 测试 {index}：{query} =====")
        print("session_id:", session_id)

        for chunk in agent.execute_stream(
                query=query,
                session_id=session_id,
                user_id=1,
        ):
            print(chunk, end="")