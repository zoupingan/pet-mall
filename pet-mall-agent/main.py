from fastapi import FastAPI

from router.agent import router

app = FastAPI()
app.include_router(router)
