from abc import ABC, abstractmethod
from typing import Any, Dict


class AIProvider(ABC):
    """Provider abstraction for AI orchestration.

    Implementations are replaceable without changing business logic.
    """

    @abstractmethod
    def generate(self, prompt: str, context: Dict[str, Any]) -> str:
        """Send a prompt to the AI provider and return generated text."""
        ...

    @abstractmethod
    def health(self) -> bool:
        """Return True if the provider is reachable."""
        ...


class StubAIProvider(AIProvider):
    """Stub provider for local development and testing."""

    def generate(self, prompt: str, context: Dict[str, Any]) -> str:
        return f"Stub AI response for prompt: {prompt}"

    def health(self) -> bool:
        return True
