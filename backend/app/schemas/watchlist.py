"""Watchlist schemas for validation."""
from pydantic import BaseModel, Field
from datetime import datetime
from uuid import UUID
from typing import Optional


class WatchlistCreate(BaseModel):
    """Schema for adding item to watchlist."""
    token_symbol: str = Field(..., min_length=1, max_length=50)
    token_address: Optional[str] = Field(None, max_length=255)
    notes: Optional[str] = Field(None, max_length=5000)


class WatchlistUpdate(BaseModel):
    """Schema for updating watchlist item."""
    notes: Optional[str] = Field(None, max_length=5000)


class WatchlistResponse(BaseModel):
    """Schema for watchlist response."""
    id: UUID
    user_id: UUID
    token_symbol: str
    token_address: Optional[str]
    notes: Optional[str]
    created_at: datetime
    
    class Config:
        from_attributes = True
