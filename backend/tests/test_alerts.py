"""Tests for alerts endpoints."""
import pytest
from httpx import AsyncClient
from app.models.alert import AlertType, AlertCondition


@pytest.mark.asyncio
async def test_create_alert(client: AsyncClient, auth_headers):
    """Test creating an alert."""
    response = await client.post(
        "/api/v1/alerts",
        json={
            "token_symbol": "BTC",
            "token_address": "0x123",
            "alert_type": AlertType.PRICE.value,
            "condition": AlertCondition.ABOVE.value,
            "threshold_value": "50000.0",
            "is_active": True
        },
        headers=auth_headers
    )
    assert response.status_code == 200
    data = response.json()
    assert data["success"] is True
    assert data["data"]["token_symbol"] == "BTC"


@pytest.mark.asyncio
async def test_list_alerts(client: AsyncClient, auth_headers):
    """Test listing alerts."""
    # Create an alert first
    await client.post(
        "/api/v1/alerts",
        json={
            "token_symbol": "ETH",
            "alert_type": AlertType.VOLUME.value,
            "condition": AlertCondition.BELOW.value,
            "threshold_value": "1000.0"
        },
        headers=auth_headers
    )
    
    # List alerts
    response = await client.get("/api/v1/alerts", headers=auth_headers)
    assert response.status_code == 200
    data = response.json()
    assert data["success"] is True
    assert len(data["data"]) > 0


@pytest.mark.asyncio
async def test_list_alerts_unauthorized(client: AsyncClient):
    """Test listing alerts without authentication."""
    response = await client.get("/api/v1/alerts")
    assert response.status_code == 403
