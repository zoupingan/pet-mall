import logging
import os
from datetime import datetime

from utils.path_tool import get_abs_path

# 日志保存路径
LOG_ROOT = get_abs_path("storage/logs")
# exist_ok表示如果日志存在就啥事不干，没有则创建
os.makedirs(LOG_ROOT, exist_ok=True)

# 日志配置格式  error info debug
DEFAULT_LOG_FORMAT = logging.Formatter(
    # 时间-名字-日志级别-文件名:行号-日志信息
    "%(asctime)s - %(name)s - %(levelname)s-%(filename)s:%(lineno)d - %(message)s"
)


def get_logger(
        name: str = 'agent',
        console_lever: int = logging.INFO,
        file_lever: int = logging.DEBUG,
        log_file  = None
) ->logging.Logger:
    logger = logging.getLogger(name)
    logger.setLevel(logging.DEBUG)

    #避免重复添加handler
    if logger.handlers:
        return logger

    # 控制台handler
    console_handler = logging.StreamHandler()
    console_handler.setLevel(console_lever)
    console_handler.setFormatter(DEFAULT_LOG_FORMAT)
    logger.addHandler(console_handler)

    #文件handler
    if not log_file:
        log_file = os.path.join(LOG_ROOT, f"{name}{datetime.now().strftime('%Y%m%d')}")
    file_handler = logging.FileHandler(log_file, encoding='utf-8')
    file_handler.setLevel(file_lever)
    file_handler.setFormatter(DEFAULT_LOG_FORMAT)
    logger.addHandler(file_handler)
    return logger

#获取日志器
logger = get_logger()


if __name__ == '__main__':
    logger.info("hello world")
    logger.error("hello world")
    logger.warning("hello world")