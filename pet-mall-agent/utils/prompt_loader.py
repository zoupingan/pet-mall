from utils.config_handler import prompt_config
from utils.logger_hanlder import logger
from utils.path_tool import get_abs_path


def load_system_prompt():
    try:
        system_prompt_path = get_abs_path(prompt_config["main_prompt_path"])
    except KeyError as e:
        logger.error("请检查prompt.yaml文件，没有找到main_prompt_path")
        raise e
    try:
        return open(system_prompt_path,"r",encoding="utf-8").read()
    except Exception as e:
        logger.error(f"解析提示词失败，错误信息{e}")
        raise e

def load_rag_prompt():
    try:
        rag_prompt_path = get_abs_path(prompt_config["rag_summarize_prompt_path"])
    except KeyError as e:
        logger.error("请检查prompt.yaml文件，rag_summarize_prompt_path")
        raise e
    try:
        return open(rag_prompt_path,"r",encoding="utf-8").read()
    except Exception as e:
        logger.error(f"解析提示词失败，错误信息{e}")
        raise e

def load_report_prompt():
    try:
        report_prompt_path = get_abs_path(prompt_config["report_prompt_path"])
    except KeyError as e:
        logger.error("请检查prompt.yaml文件，report_prompt_path")
        raise e
    try:
        return open(report_prompt_path,"r",encoding="utf-8").read()
    except Exception as e:
        logger.error(f"解析提示词失败，错误信息{e}")
        raise e

if __name__ == '__main__':
    print(load_report_prompt())
