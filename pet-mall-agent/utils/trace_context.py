from contextvars import ContextVar, Token
from dataclasses import dataclass


@dataclass
class TraceState:
    trace_id: str
    model_call_count: int = 0


current_trace_state: ContextVar[TraceState | None] = ContextVar(
    "current_trace_state",
    default=None,
)


def start_trace(trace_id: str) -> Token:
    state = TraceState(trace_id=trace_id)
    return current_trace_state.set(state)


def get_trace_id() -> str:
    state = current_trace_state.get()
    if state is None:
        return "-"
    return state.trace_id


def next_model_call_number() -> int:
    state = current_trace_state.get()
    if state is None:
        raise RuntimeError("Trace 尚未启动")

    state.model_call_count += 1
    return state.model_call_count


def clear_trace(state_token: Token) -> None:
    current_trace_state.reset(state_token)