import hashlib
import os.path
from utils.logger_hanlder import logger
from langchain_community.document_loaders import TextLoader
from langchain_core.documents import Document

def get_file_md5(filepath: str):
    if not os.path.exists(filepath):
        return logger.error(f"[md5计算]：文件{filepath}不存在")

    if not os.path.isfile(filepath):
        return logger.error(f"[md5计算]：{filepath}不是文件")

    md5_obj = hashlib.md5()
    chunk_size = 4096  # 4kb分片，避免文件过大爆内存
    try:
        with open(filepath, 'rb') as f:  # rb表示以二进制读取，这么写必须以二进制读取
            while chunk := f.read(chunk_size):
                md5_obj.update(chunk)
            """
            上面的代码等同于这一段
            chunk = f.read(chunk_size)
            while chunk:
                md5_obj = update(chunk)
                chunk = f.read(chunk_size)
            """
        md5_hex = md5_obj.hexdigest()
        return md5_hex
    except Exception as e:
        return logger.error(f"[md5计算]：{filepath}文件计算失败，错误信息：{e}")


def listdir_with_allowed_type(path: str, allowed_type: tuple[str]):
   files = []
   if not os.path.isdir(path):
       return logger.error(f"[listdir_with_allowed_type]：{path}不是文件夹")

   for f in os.listdir(path):       # f拿到的是文件名
       if f.endswith(allowed_type):
           files.append(os.path.join(path,f))
   return files

def text_loader(path: str) ->list[Document]:
    return TextLoader(path,encoding="utf-8").load()
