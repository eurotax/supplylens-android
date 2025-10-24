"""Watchlist database model."""
import uuid
from datetime import datetime
from sqlalchemy import Column, String, DateTime, ForeignKey, Text, UniqueConstraint, func
from sqlalchemy.dialects.postgresql import UUID
from sqlalchemy.orm import relationship
from app.database import Base


class Watchlist(Base):
    """Watchlist model for tracking tokens."""
    
    __tablename__ = "watchlist"
    
    id = Column(UUID(as_uuid=True), primary_key=True, default=uuid.uuid4, index=True)
    user_id = Column(UUID(as_uuid=True), ForeignKey("users.id", ondelete="CASCADE"), nullable=False, index=True)
    token_symbol = Column(String(50), nullable=False, index=True)
    token_address = Column(String(255), nullable=True)
    notes = Column(Text, nullable=True)
    created_at = Column(DateTime, default=datetime.utcnow, nullable=False)
    
    # Relationships
    user = relationship("User", back_populates="watchlist_items")
    
    # Unique constraint: user cannot add same token twice
    __table_args__ = (
        UniqueConstraint(
            'user_id', 
            'token_symbol', 
            func.coalesce('token_address', ''),
            name='uq_user_token'
        ),
    )
    
    def __repr__(self):
        return f"<Watchlist {self.token_symbol}>"
