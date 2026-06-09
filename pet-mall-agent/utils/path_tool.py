import os

def get_project_root():
    # 获取当前文件的绝对路径
    current_file_path = os.path.abspath(__file__)
    # 获取当前文件的目录
    current_dir = os.path.dirname(current_file_path)
    # 获取当前文件的上一级目录,也就是根目录
    root_path = os.path.dirname(current_dir)
    return root_path

# 传递相对路径，获得文件的绝对路径
def get_abs_path(file_path: str) -> str:
    # 获取根目录
    root_path = get_project_root()
    return os.path.join(root_path, file_path)

if __name__ == '__main__':
    #传入的路径不能以/开头，否则会是为绝对路径，把前面的路径给忽略掉
    print(get_abs_path("text/demo.txt"))
