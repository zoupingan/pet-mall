from pathlib import Path

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
        if not retriever_doc:
            return "知识库中暂未找到与该问题相关的资料。", []

        content = ""
        count = 0
        for doc in retriever_doc:
            count += 1
            content += (
                f"\n\n===== 参考资料 {count} =====\n"
                f"{doc.page_content}\n"
                f"来源：{doc.metadata.get('source', '未知')}"
            )


        answer = self.chain.invoke({
            "context": content,
            "input": query,
            "history": history,
        })

        sources = sorted({
            Path(doc.metadata["source"]).name
            for doc in retriever_doc
            if doc.metadata.get("source")
        })

        return answer, sources


if __name__ == '__main__':
    service = VectorStoreService()
    queries = [
        "皇家K36幼猫粮适合多大的猫？",
        "皇家K36幼猫粮现在多少钱？",
        "皇家K36幼猫粮还有库存吗？"
    ]

    retriever = service.get_retriever()

    for query in queries:
        print(f"\n\n######## {query} ########")

        documents = retriever.invoke(query)

        print("通过阈值的数量：", len(documents))

        for index, document in enumerate(documents, start=1):
            print(f"{index}. {document.page_content}")