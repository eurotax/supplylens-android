"""Watchlist router for CRUD operations."""
from fastapi import APIRouter, Depends, HTTPException, status, Query
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select, func
from sqlalchemy.exc import IntegrityError
from uuid import UUID
from typing import List
from app.database import get_db
from app.models.user import User
from app.models.watchlist import Watchlist
from app.schemas.watchlist import WatchlistCreate, WatchlistUpdate, WatchlistResponse
from app.schemas.responses import StandardResponse, PaginatedResponse
from app.services.security import get_current_user
from app.utils.responses import success_response, error_response, paginated_response

router = APIRouter(prefix="/api/v1/watchlist", tags=["Watchlist"])


@router.get("", response_model=PaginatedResponse[WatchlistResponse])
async def list_watchlist(
    page: int = Query(1, ge=1),
    per_page: int = Query(20, ge=1, le=100),
    current_user: User = Depends(get_current_user),
    db: AsyncSession = Depends(get_db)
):
    """List user's watchlist with pagination."""
    # Get total count
    count_result = await db.execute(
        select(func.count()).select_from(Watchlist).where(Watchlist.user_id == current_user.id)
    )
    total = count_result.scalar()
    
    # Get paginated data
    offset = (page - 1) * per_page
    result = await db.execute(
        select(Watchlist)
        .where(Watchlist.user_id == current_user.id)
        .order_by(Watchlist.created_at.desc())
        .offset(offset)
        .limit(per_page)
    )
    items = result.scalars().all()
    
    return paginated_response(
        data=[WatchlistResponse.model_validate(item) for item in items],
        page=page,
        per_page=per_page,
        total=total,
        message="Watchlist retrieved successfully"
    )


@router.post("", response_model=StandardResponse[WatchlistResponse])
async def add_to_watchlist(
    item_data: WatchlistCreate,
    current_user: User = Depends(get_current_user),
    db: AsyncSession = Depends(get_db)
):
    """Add item to watchlist."""
    new_item = Watchlist(
        user_id=current_user.id,
        **item_data.model_dump()
    )
    
    try:
        db.add(new_item)
        await db.commit()
        await db.refresh(new_item)
    except IntegrityError:
        await db.rollback()
        return error_response(
            code="WATCHLIST_DUPLICATE",
            message="This token is already in your watchlist"
        )
    
    return success_response(
        data=WatchlistResponse.model_validate(new_item),
        message="Item added to watchlist successfully"
    )


@router.get("/{item_id}", response_model=StandardResponse[WatchlistResponse])
async def get_watchlist_item(
    item_id: UUID,
    current_user: User = Depends(get_current_user),
    db: AsyncSession = Depends(get_db)
):
    """Get a specific watchlist item."""
    result = await db.execute(
        select(Watchlist).where(Watchlist.id == item_id, Watchlist.user_id == current_user.id)
    )
    item = result.scalar_one_or_none()
    
    if not item:
        return error_response(
            code="WATCHLIST_ITEM_NOT_FOUND",
            message="Watchlist item not found"
        )
    
    return success_response(
        data=WatchlistResponse.model_validate(item),
        message="Watchlist item retrieved successfully"
    )


@router.put("/{item_id}", response_model=StandardResponse[WatchlistResponse])
async def update_watchlist_item(
    item_id: UUID,
    item_data: WatchlistUpdate,
    current_user: User = Depends(get_current_user),
    db: AsyncSession = Depends(get_db)
):
    """Update watchlist item (notes only)."""
    result = await db.execute(
        select(Watchlist).where(Watchlist.id == item_id, Watchlist.user_id == current_user.id)
    )
    item = result.scalar_one_or_none()
    
    if not item:
        return error_response(
            code="WATCHLIST_ITEM_NOT_FOUND",
            message="Watchlist item not found"
        )
    
    # Update notes
    if item_data.notes is not None:
        item.notes = item_data.notes
    
    await db.commit()
    await db.refresh(item)
    
    return success_response(
        data=WatchlistResponse.model_validate(item),
        message="Watchlist item updated successfully"
    )


@router.delete("/{item_id}", response_model=StandardResponse[dict])
async def remove_from_watchlist(
    item_id: UUID,
    current_user: User = Depends(get_current_user),
    db: AsyncSession = Depends(get_db)
):
    """Remove item from watchlist."""
    result = await db.execute(
        select(Watchlist).where(Watchlist.id == item_id, Watchlist.user_id == current_user.id)
    )
    item = result.scalar_one_or_none()
    
    if not item:
        return error_response(
            code="WATCHLIST_ITEM_NOT_FOUND",
            message="Watchlist item not found"
        )
    
    await db.delete(item)
    await db.commit()
    
    return success_response(
        data={"deleted": True},
        message="Item removed from watchlist successfully"
    )
