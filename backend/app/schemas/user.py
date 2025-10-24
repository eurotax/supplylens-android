"""User schemas for validation."""
from pydantic import BaseModel, EmailStr, Field, field_validator
from datetime import datetime
from uuid import UUID
import re


class UserCreate(BaseModel):
    """Schema for user registration."""
    email: EmailStr
    password: str = Field(..., min_length=10, max_length=100)
    
    @field_validator('password')
    @classmethod
    def validate_password_strength(cls, v: str) -> str:
        """Validate password meets security requirements."""
        # Count character classes
        has_lower = bool(re.search(r'[a-z]', v))
        has_upper = bool(re.search(r'[A-Z]', v))
        has_digit = bool(re.search(r'\d', v))
        has_special = bool(re.search(r'[!@#$%^&*(),.?":{}|<>]', v))
        
        classes_count = sum([has_lower, has_upper, has_digit, has_special])
        
        if classes_count < 3:
            raise ValueError(
                'Password must contain at least 3 of: lowercase, uppercase, digit, special character'
            )
        
        return v


class UserLogin(BaseModel):
    """Schema for user login."""
    email: EmailStr
    password: str


class UserResponse(BaseModel):
    """Schema for user response."""
    id: UUID
    email: EmailStr
    created_at: datetime
    
    class Config:
        from_attributes = True
