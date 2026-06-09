from langchain_core.output_parsers import StrOutputParser
from langchain_core.prompts import PromptTemplate

from model.factory import chat_model
from rag.vector_store import VectorStoreService
from utils.prompt_loader import load_rag_prompt


def print_prompt(prompt):
    print("=" * 20, prompt.to_string(), "=" * 20)
    return prompt


class RagSummarizeService:
    def __init__(self):
        self.vector_store = VectorStoreService()
        self.retriever = self.vector_store.get_retriever()
        self.prompt_text = load_rag_prompt()
        self.prompt_template = PromptTemplate.from_template(self.prompt_text)
        self.model = chat_model
        self.chain = self.init_chain()

    def init_chain(self):
        chain = self.prompt_template | print_prompt | self.model | StrOutputParser()
        return chain

    def retriever_doc(self, query: str):
        return self.retriever.invoke(query)

    #   获取参考资料
    def rag_summarize(self, query: str, history:str = None):
        retriever_doc = self.retriever_doc(query)
        content = ""
        count = 0
        for doc in retriever_doc:
            count += 1
            content += f"[参考资料{count}]:{doc.page_content}|参考元数据{doc.metadata}"
        return self.chain.invoke({"context": content, "input": query, "history": history})


if __name__ == '__main__':
    rag_summarize_service = RagSummarizeService()
    res = rag_summarize_service.rag_summarize("皇家猫粮多少钱")
    print(res)
