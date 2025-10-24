"""Alert schemas for validation."""
from pydantic import BaseModel, Field
from datetime import datetime
from uuid import UUID
from typing import Optional
from decimal import Decimal
from app.models.alert import AlertType, AlertCondition


class AlertCreate(BaseModel):
    """Schema for creating an alert."""
    token_symbol: str = Field(..., min_length=1, max_length=50)
    token_address: Optional[str] = Field(None, max_length=255)
    alert_type: AlertType
    condition: AlertCondition
    threshold_value: Decimal = Field(..., gt=0)
    is_active: bool = True


class AlertUpdate(BaseModel):
    """Schema for updating an alert."""
    token_symbol: Optional[str] = Field(None, min_length=1, max_length=50)
    token_address: Optional[str] = Field(None, max_length=255)
    alert_type: Optional[AlertType] = None
    condition: Optional[AlertCondition] = None
    threshold_value: Optional[Decimal] = Field(None, gt=0)
    is_active: Optional[bool] = None


class AlertToggle(BaseModel):
    """Schema for toggling alert active status."""
    is_active: bool


class AlertResponse(BaseModel):
    """Schema for alert response."""
    id: UUID
    user_id: UUID
    token_symbol: str
    token_address: Optional[str]
    alert_type: AlertType
    condition: AlertCondition
    threshold_value: Decimal
    is_active: bool
    triggered_at: Optional[datetime]
    created_at: datetime
    updated_at: datetime
    
    class Config:
        from_attributes = True
