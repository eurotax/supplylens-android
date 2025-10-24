"""Authentication schemas."""
from pydantic import BaseModel
from typing import Optional
from uuid import UUID


class Token(BaseModel):
    """Schema for JWT token response."""
    access_token: str
    refresh_token: str
    token_type: str = "bearer"


class TokenData(BaseModel):
    """Schema for decoded JWT token data."""
    user_id: Optional[UUID] = None
    email: Optional[str] = None


class RefreshToken(BaseModel):
    """Schema for refresh token request."""
    refresh_token: str
