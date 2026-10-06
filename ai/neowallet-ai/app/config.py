import os
from dataclasses import dataclass
from functools import lru_cache

from dotenv import load_dotenv

load_dotenv()


@dataclass(frozen=True)
class Settings:
    app_name: str = "neowallet-ai"
    debug: bool = False
    ai_provider: str = "stub"
    ai_api_key: str = ""
    ai_model: str = ""
    port: int = 8000


@lru_cache
def get_settings() -> Settings:
    return Settings(
        app_name=os.getenv("APP_NAME", "neowallet-ai"),
        debug=os.getenv("DEBUG", "false").lower() == "true",
        ai_provider=os.getenv("AI_PROVIDER", "stub"),
        ai_api_key=os.getenv("AI_API_KEY", ""),
        ai_model=os.getenv("AI_MODEL", ""),
        port=int(os.getenv("PORT", "8000")),
    )
