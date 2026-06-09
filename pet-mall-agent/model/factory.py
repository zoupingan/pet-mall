import time

from langchain_community.chat_models import ChatTongyi
from langchain_community.embeddings import DashScopeEmbeddings
from requests.exceptions import ConnectionError, SSLError, Timeout

from utils.config_handler import rag_config
from utils.logger_hanlder import logger


class ReliableChatTongyi(ChatTongyi):
    def completion_with_retry(self, **kwargs):
        for attempt in range(3):
            try:
                return super().completion_with_retry(**kwargs)
            except (SSLError, ConnectionError, Timeout) as error:
                if attempt == 2:
                    raise
                wait_seconds = 2 ** attempt
                logger.warning(
                    f"DashScope 连接异常，{wait_seconds} 秒后重试：{error}"
                )
                time.sleep(wait_seconds)


chat_model = ReliableChatTongyi(model=rag_config["model_name"])
embedding_model = DashScopeEmbeddings(model=rag_config["embedding_model_name"])
