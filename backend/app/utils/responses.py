"""Response utility functions."""
from typing import TypeVar, List, Optional, Dict, Any
from math import ceil
from app.schemas.responses import StandardResponse, PaginatedResponse, PaginationMeta

T = TypeVar('T')


def success_response(
    data: T,
    message: Optional[str] = None
) -> StandardResponse[T]:
    """Create a success response."""
    return StandardResponse(
        success=True,
        data=data,
        message=message
    )


def error_response(
    code: str,
    message: str,
    details: Optional[Dict[str, Any]] = None
) -> StandardResponse:
    """Create an error response."""
    error_data = {
        "code": code,
        "message": message
    }
    if details:
        error_data["details"] = details
    
    return StandardResponse(
        success=False,
        error=error_data
    )


def paginated_response(
    data: List[T],
    page: int,
    per_page: int,
    total: int,
    message: Optional[str] = None
) -> PaginatedResponse[T]:
    """Create a paginated response."""
    pages = ceil(total / per_page) if per_page > 0 else 0
    
    return PaginatedResponse(
        success=True,
        data=data,
        pagination=PaginationMeta(
            page=page,
            per_page=per_page,
            total=total,
            pages=pages
        ),
        message=message
    )
