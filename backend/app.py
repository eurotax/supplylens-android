from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

app = FastAPI(title="App Backend")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"], allow_methods=["*"], allow_headers=["*"]
)

@app.get("/healthz")
def healthz():
    return {"status": "ok"}

@app.get("/")
def root():
    return {"ok": True}
