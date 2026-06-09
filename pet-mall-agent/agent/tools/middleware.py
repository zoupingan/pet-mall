from langchain.agents.middleware import wrap_tool_call, before_model, dynamic_prompt

from utils.logger_hanlder import logger
from utils.prompt_loader import load_report_prompt, load_system_prompt


@wrap_tool_call
def monitor_tool(request,handler):     #工具执行的监控,request是指请求的参数，handler是执行的函数本身
    logger.info(f"[执行工具]：{request.tool_call['name']}")
    logger.info(f"[传入参数]：{request.tool_call['args']}")
    try:
        if request.tool_call['name']=='fill_context_for_report':
            request.runtime.context['report'] = True
        return handler(request)
    except Exception as e:
        logger.error(f"工具{request.tool_call['name']}调用失败，错误信息：{e}")
        raise e

@before_model
def log_before_model(state,runtime):     #模型执行前输出日志
    logger.info(f"[log_before_model]即将调用模型，带有{len(state['messages'])}条消息")
    logger.debug(f"[log_before_model]消息内容：{state['messages'][-1].content.strip()}")
    return None

@dynamic_prompt                 #每一次在生成提示词之前调用此函数
def report_prompt_switch(request):     #动态切换提示词
    is_report = request.runtime.context.get('report',False)     #拿不到就返回false
    if is_report:
        print("**"*30)
        return load_report_prompt()
    return load_system_prompt()