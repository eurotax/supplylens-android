"""Authentication router."""
from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select
from app.database import get_db
from app.models.user import User
from app.schemas.user import UserCreate, UserLogin, UserResponse
from app.schemas.auth import Token
from app.schemas.responses import StandardResponse
from app.services.auth import get_password_hash, verify_password, create_access_token, create_refresh_token, decode_token
from app.services.security import get_current_user
from app.utils.responses import success_response, error_response
from slowapi import Limiter
from slowapi.util import get_remote_address

limiter = Limiter(key_func=get_remote_address)
router = APIRouter(prefix="/api/v1/auth", tags=["Authentication"])


@router.post("/register", response_model=StandardResponse[UserResponse])
@limiter.limit("5/15minutes")
async def register(
    user_data: UserCreate,
    db: AsyncSession = Depends(get_db)
):
    """Register a new user."""
    # Check if user already exists
    result = await db.execute(select(User).where(User.email == user_data.email))
    existing_user = result.scalar_one_or_none()
    
    if existing_user:
        return error_response(
            code="AUTH_USER_EXISTS",
            message="User with this email already exists"
        )
    
    # Create new user
    hashed_password = get_password_hash(user_data.password)
    new_user = User(
        email=user_data.email,
        password_hash=hashed_password
    )
    
    db.add(new_user)
    await db.commit()
    await db.refresh(new_user)
    
    return success_response(
        data=UserResponse.model_validate(new_user),
        message="User registered successfully"
    )


@router.post("/login", response_model=StandardResponse[Token])
@limiter.limit("5/15minutes")
async def login(
    credentials: UserLogin,
    db: AsyncSession = Depends(get_db)
):
    """Login and get access token."""
    # Find user
    result = await db.execute(select(User).where(User.email == credentials.email))
    user = result.scalar_one_or_none()
    
    if not user or not verify_password(credentials.password, user.password_hash):
        return error_response(
            code="AUTH_INVALID_CREDENTIALS",
            message="Invalid email or password"
        )
    
    # Create tokens
    access_token = create_access_token(data={"sub": str(user.id)})
    refresh_token = create_refresh_token(data={"sub": str(user.id)})
    
    return success_response(
        data=Token(access_token=access_token, refresh_token=refresh_token),
        message="Login successful"
    )


@router.get("/me", response_model=StandardResponse[UserResponse])
async def get_me(current_user: User = Depends(get_current_user)):
    """Get current user information."""
    return success_response(
        data=UserResponse.model_validate(current_user),
        message="User retrieved successfully"
    )
