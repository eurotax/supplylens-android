"""Tests for watchlist endpoints."""
import pytest
from httpx import AsyncClient


@pytest.mark.asyncio
async def test_add_to_watchlist(client: AsyncClient, auth_headers):
    """Test adding item to watchlist."""
    response = await client.post(
        "/api/v1/watchlist",
        json={
            "token_symbol": "SOL",
            "token_address": "0xabc",
            "notes": "Promising project"
        },
        headers=auth_headers
    )
    assert response.status_code == 200
    data = response.json()
    assert data["success"] is True
    assert data["data"]["token_symbol"] == "SOL"


@pytest.mark.asyncio
async def test_add_duplicate_to_watchlist(client: AsyncClient, auth_headers):
    """Test adding duplicate item to watchlist."""
    # Add first time
    await client.post(
        "/api/v1/watchlist",
        json={"token_symbol": "AVAX", "token_address": "0xdef"},
        headers=auth_headers
    )
    
    # Try to add again
    response = await client.post(
        "/api/v1/watchlist",
        json={"token_symbol": "AVAX", "token_address": "0xdef"},
        headers=auth_headers
    )
    assert response.status_code == 200
    data = response.json()
    assert data["success"] is False
    assert "duplicate" in data["error"]["message"].lower()


@pytest.mark.asyncio
async def test_list_watchlist(client: AsyncClient, auth_headers):
    """Test listing watchlist."""
    # Add an item first
    await client.post(
        "/api/v1/watchlist",
        json={"token_symbol": "MATIC"},
        headers=auth_headers
    )
    
    # List watchlist
    response = await client.get("/api/v1/watchlist", headers=auth_headers)
    assert response.status_code == 200
    data = response.json()
    assert data["success"] is True
    assert len(data["data"]) > 0
