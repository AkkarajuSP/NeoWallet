from fastapi.testclient import TestClient

from app.main import app
from app.providers.stub import StubAIProvider

app.state.provider = StubAIProvider()
client = TestClient(app)


def test_health():
    response = client.get("/health")
    assert response.status_code == 200
    data = response.json()
    assert data["status"] == "UP"


def test_ready():
    response = client.get("/ready")
    assert response.status_code == 200
    data = response.json()
    assert data["status"] == "READY"
