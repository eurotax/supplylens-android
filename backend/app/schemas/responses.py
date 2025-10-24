"""Standard response schemas."""
from pydantic import BaseModel
from typing import Generic, TypeVar, Optional, List, Dict, Any

T = TypeVar('T')


class StandardResponse(BaseModel, Generic[T]):
    """Standard API response format."""
    success: bool
    data: Optional[T] = None
    message: Optional[str] = None
    error: Optional[Dict[str, Any]] = None


class PaginationMeta(BaseModel):
    """Pagination metadata."""
    page: int
    per_page: int
    total: int
    pages: int


class PaginatedResponse(BaseModel, Generic[T]):
    """Paginated API response format."""
    success: bool
    data: List[T]
    pagination: PaginationMeta
    message: Optional[str] = None
