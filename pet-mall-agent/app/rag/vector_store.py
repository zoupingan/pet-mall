import os.path

from langchain_chroma import Chroma
from langchain_core.documents import Document

from app.llm.factory import embedding_model
from utils.config_handler import chroma_config
from langchain_text_splitters import RecursiveCharacterTextSplitter

from utils.file_handler import get_file_md5, listdir_with_allowed_type, text_loader
from utils.logger_hanlder import logger
from utils.path_tool import get_abs_path


# 向量存储/检索服务
class VectorStoreService:
    def __init__(self):
        self.vector_store = Chroma(
            collection_name=chroma_config["collection_name"],
            persist_directory=get_abs_path(chroma_config["persist_directory"]),
            embedding_function=embedding_model
        )
        self.spliter = RecursiveCharacterTextSplitter(
            chunk_size=chroma_config["chunk_size"],
            chunk_overlap=chroma_config["chunk_overlap"],
            separators=chroma_config["separators"],
            length_function=len
        )

    def get_retriever(self):
        return self.vector_store.as_retriever(
            search_type="similarity_score_threshold",
            search_kwargs={
                "k": chroma_config["k"],
                "score_threshold": chroma_config["score_threshold"],
            }, )

    def load_document(self):
        # 从数据文件夹中加载数据文件，转为向量存入向量数据库,需要计算md5值去重
        def check_md5_hex(md5_for_check: str):
            md5_path = get_abs_path(chroma_config["md5_hex_store"])
            if not os.path.exists(md5_path):
                # 创建文件
                open(md5_path, "w", encoding="utf-8").close()
                return False  # md5未处理过
            with open(md5_path, "r", encoding="utf-8") as f:
                for line in f.readlines():
                    line = line.strip()
                    if line == md5_for_check:
                        return True  # md5已处理过
                return False

        def save_md5(md5_for_check: str):
            with open(get_abs_path(chroma_config["md5_hex_store"]), "a", encoding="utf-8") as f:
                f.write(md5_for_check + "\n")

        def get_file_documents(file_path: str):
            if file_path.endswith(".txt"):
                return text_loader(file_path)
            return []

        allowed_file_path: list[str] = listdir_with_allowed_type(get_abs_path(chroma_config["data_path"]),
                                                                 tuple(chroma_config["allow_knowledge_file_type"]))
        for path in allowed_file_path:
            md5_hex = get_file_md5(path)
            if check_md5_hex(md5_hex):
                logger.info(f"加载知识库{path}已存在知识库内")
                continue
            try:
                documents: list[Document] = get_file_documents(path)
                if not documents:
                    logger.warning(f"加载文件{path}为空")
                    continue

                split_documents: list[Document] = self.spliter.split_documents(documents)
                if not split_documents:
                    logger.warning(f"分片后{path}没有有效的内容")
                    continue
                # 将内容存入向量库中
                self.vector_store.add_documents(split_documents)
                save_md5(md5_hex)
                logger.info(f"加载文件{path}成功")
            except Exception as e:
                logger.error(f"加载文件{path}失败，错误信息：{str(e)}")
                continue


if __name__ == '__main__':
    vs = VectorStoreService()
    vs.load_document()
    # retriever = vs.get_retriever()
    # res = retriever.invoke("皇家K36幼猫粮多少钱")
    # for i in res:
    #     print(i.page_content)
