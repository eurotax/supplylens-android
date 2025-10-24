"""Alerts router for CRUD operations."""
from fastapi import APIRouter, Depends, HTTPException, status, Query
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select, func
from uuid import UUID
from typing import List
from app.database import get_db
from app.models.user import User
from app.models.alert import Alert
from app.schemas.alert import AlertCreate, AlertUpdate, AlertResponse, AlertToggle
from app.schemas.responses import StandardResponse, PaginatedResponse
from app.services.security import get_current_user
from app.utils.responses import success_response, error_response, paginated_response

router = APIRouter(prefix="/api/v1/alerts", tags=["Alerts"])


@router.get("", response_model=PaginatedResponse[AlertResponse])
async def list_alerts(
    page: int = Query(1, ge=1),
    per_page: int = Query(20, ge=1, le=100),
    current_user: User = Depends(get_current_user),
    db: AsyncSession = Depends(get_db)
):
    """List user's alerts with pagination."""
    # Get total count
    count_result = await db.execute(
        select(func.count()).select_from(Alert).where(Alert.user_id == current_user.id)
    )
    total = count_result.scalar()
    
    # Get paginated data
    offset = (page - 1) * per_page
    result = await db.execute(
        select(Alert)
        .where(Alert.user_id == current_user.id)
        .order_by(Alert.created_at.desc())
        .offset(offset)
        .limit(per_page)
    )
    alerts = result.scalars().all()
    
    return paginated_response(
        data=[AlertResponse.model_validate(alert) for alert in alerts],
        page=page,
        per_page=per_page,
        total=total,
        message="Alerts retrieved successfully"
    )


@router.post("", response_model=StandardResponse[AlertResponse])
async def create_alert(
    alert_data: AlertCreate,
    current_user: User = Depends(get_current_user),
    db: AsyncSession = Depends(get_db)
):
    """Create a new alert."""
    new_alert = Alert(
        user_id=current_user.id,
        **alert_data.model_dump()
    )
    
    db.add(new_alert)
    await db.commit()
    await db.refresh(new_alert)
    
    return success_response(
        data=AlertResponse.model_validate(new_alert),
        message="Alert created successfully"
    )


@router.get("/{alert_id}", response_model=StandardResponse[AlertResponse])
async def get_alert(
    alert_id: UUID,
    current_user: User = Depends(get_current_user),
    db: AsyncSession = Depends(get_db)
):
    """Get a specific alert."""
    result = await db.execute(
        select(Alert).where(Alert.id == alert_id, Alert.user_id == current_user.id)
    )
    alert = result.scalar_one_or_none()
    
    if not alert:
        return error_response(
            code="ALERT_NOT_FOUND",
            message="Alert not found"
        )
    
    return success_response(
        data=AlertResponse.model_validate(alert),
        message="Alert retrieved successfully"
    )


@router.put("/{alert_id}", response_model=StandardResponse[AlertResponse])
async def update_alert(
    alert_id: UUID,
    alert_data: AlertUpdate,
    current_user: User = Depends(get_current_user),
    db: AsyncSession = Depends(get_db)
):
    """Update an alert."""
    result = await db.execute(
        select(Alert).where(Alert.id == alert_id, Alert.user_id == current_user.id)
    )
    alert = result.scalar_one_or_none()
    
    if not alert:
        return error_response(
            code="ALERT_NOT_FOUND",
            message="Alert not found"
        )
    
    # Update fields
    update_data = alert_data.model_dump(exclude_unset=True)
    for field, value in update_data.items():
        setattr(alert, field, value)
    
    await db.commit()
    await db.refresh(alert)
    
    return success_response(
        data=AlertResponse.model_validate(alert),
        message="Alert updated successfully"
    )


@router.delete("/{alert_id}", response_model=StandardResponse[dict])
async def delete_alert(
    alert_id: UUID,
    current_user: User = Depends(get_current_user),
    db: AsyncSession = Depends(get_db)
):
    """Delete an alert."""
    result = await db.execute(
        select(Alert).where(Alert.id == alert_id, Alert.user_id == current_user.id)
    )
    alert = result.scalar_one_or_none()
    
    if not alert:
        return error_response(
            code="ALERT_NOT_FOUND",
            message="Alert not found"
        )
    
    await db.delete(alert)
    await db.commit()
    
    return success_response(
        data={"deleted": True},
        message="Alert deleted successfully"
    )


@router.patch("/{alert_id}/toggle", response_model=StandardResponse[AlertResponse])
async def toggle_alert(
    alert_id: UUID,
    toggle_data: AlertToggle,
    current_user: User = Depends(get_current_user),
    db: AsyncSession = Depends(get_db)
):
    """Toggle alert active status."""
    result = await db.execute(
        select(Alert).where(Alert.id == alert_id, Alert.user_id == current_user.id)
    )
    alert = result.scalar_one_or_none()
    
    if not alert:
        return error_response(
            code="ALERT_NOT_FOUND",
            message="Alert not found"
        )
    
    alert.is_active = toggle_data.is_active
    await db.commit()
    await db.refresh(alert)
    
    return success_response(
        data=AlertResponse.model_validate(alert),
        message=f"Alert {'activated' if toggle_data.is_active else 'deactivated'} successfully"
    )
