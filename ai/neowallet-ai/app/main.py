import logging
import uuid
from contextlib import asynccontextmanager

from fastapi import FastAPI, Request
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import JSONResponse

from .config import Settings, get_settings
from .providers.stub import StubAIProvider

logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s - %(name)s - %(levelname)s - %(message)s",
)
logger = logging.getLogger(__name__)


@asynccontextmanager
async def lifespan(app: FastAPI):
    logger.info("NeoWallet AI service starting")
    app.state.settings = get_settings()
    app.state.provider = StubAIProvider()
    yield
    logger.info("NeoWallet AI service stopping")


app = FastAPI(
    title="NeoWallet AI",
    description="Neo AI orchestration service",
    version="0.0.1",
    lifespan=lifespan,
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=[],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


@app.middleware("http")
async def correlation_middleware(request: Request, call_next):
    correlation_id = request.headers.get("X-Correlation-Id", str(uuid.uuid4()))
    request.state.correlation_id = correlation_id
    response = await call_next(request)
    response.headers["X-Correlation-Id"] = correlation_id
    return response


@app.get("/health")
async def health():
    return {"status": "UP", "service": "neowallet-ai"}


@app.get("/ready")
async def ready():
    provider = getattr(app.state, "provider", None)
    return {
        "status": "READY" if provider and provider.health() else "NOT_READY",
        "provider": type(provider).__name__ if provider else "none",
    }


@app.post("/v1/chat")
async def chat(request: Request):
    body = await request.json()
    prompt = body.get("prompt", "")
    provider = getattr(app.state, "provider", StubAIProvider())
    response = provider.generate(prompt, context={})
    return {"response": response}
