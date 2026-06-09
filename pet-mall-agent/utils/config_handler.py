import yaml
from yaml import FullLoader

from utils.path_tool import get_abs_path


def load_rag_config(config_path: str = get_abs_path("config/rag.yaml"), encoding: str = "utf-8"):
    with open(config_path, "r", encoding=encoding) as f:
        return yaml.load(f, Loader=FullLoader)

def load_chroma_config(config_path: str = get_abs_path("config/chroma.yaml"), encoding: str = "utf-8"):
    with open(config_path, "r", encoding=encoding) as f:
        return yaml.load(f, Loader=FullLoader)

def load_agent_config(config_path: str = get_abs_path("config/agent.yaml"), encoding: str = "utf-8"):
    with open(config_path, "r", encoding=encoding) as f:
        return yaml.load(f, Loader=FullLoader)

def load_prompt_config(config_path: str = get_abs_path("config/prompt.yaml"), encoding: str = "utf-8"):
    with open(config_path, "r", encoding=encoding) as f:
        return yaml.load(f, Loader=FullLoader)


rag_config = load_rag_config()
chroma_config = load_chroma_config()
agent_config = load_agent_config()
prompt_config = load_prompt_config()

if __name__ == '__main__':
    print(rag_config["model_name"])
