from app.providers.stub import StubAIProvider


def test_stub_generate():
    provider = StubAIProvider()
    response = provider.generate("test prompt", {})
    assert response.startswith("Stub AI response")


def test_stub_health():
    provider = StubAIProvider()
    assert provider.health() is True
