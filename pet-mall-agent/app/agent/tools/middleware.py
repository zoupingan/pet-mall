from time import perf_counter

from langchain.agents.middleware import wrap_tool_call, before_model, wrap_model_call

from utils.logger_hanlder import logger
from utils.trace_context import get_trace_id, next_model_call_number


@wrap_tool_call
async def async_monitor_tool(request, handler):
    trace_id = get_trace_id()
    tool_name = request.tool_call["name"]
    tool_args = request.tool_call["args"]
    started_at = perf_counter()

    logger.info(
        f"[trace={trace_id}] 工具开始："
        f"{tool_name}，参数={tool_args}"
    )


    try:
        result = await handler(request)
        elapsed = perf_counter() - started_at

        logger.info(
            f"[trace={trace_id}] 工具完成："
            f"{tool_name}，耗时={elapsed:.3f}秒"
        )
        return result
    except Exception:
        elapsed = perf_counter() - started_at

        logger.exception(
            f"[trace={trace_id}] 工具失败："
            f"{tool_name}，耗时={elapsed:.3f}秒"
        )
        raise

@wrap_model_call
async def async_monitor_model(request, handler):
    trace_id = get_trace_id()
    call_number = next_model_call_number()
    started_at = perf_counter()

    logger.info(
        f"[trace={trace_id}] "
        f"模型调用{call_number}开始，"
        f"消息数={len(request.state['messages'])}"
    )

    try:
        response = await handler(request)
        elapsed = perf_counter() - started_at

        logger.info(
            f"[trace={trace_id}] "
            f"模型调用{call_number}完成，"
            f"耗时={elapsed:.3f}秒"
        )
        return response
    except Exception:
        elapsed = perf_counter() - started_at

        logger.exception(
            f"[trace={trace_id}] "
            f"模型调用{call_number}失败，"
            f"耗时={elapsed:.3f}秒"
        )
        raise