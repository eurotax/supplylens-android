"""Alert database model with enums."""
import uuid
import enum
from datetime import datetime
from sqlalchemy import Column, String, DateTime, Boolean, Numeric, ForeignKey, Enum
from sqlalchemy.dialects.postgresql import UUID
from sqlalchemy.orm import relationship
from app.database import Base


class AlertType(str, enum.Enum):
    """Alert type enumeration."""
    PRICE = "price"
    VOLUME = "volume"
    HOLDER = "holder"
    LIQUIDITY = "liquidity"


class AlertCondition(str, enum.Enum):
    """Alert condition enumeration."""
    ABOVE = "above"
    BELOW = "below"
    EQUALS = "equals"


class Alert(Base):
    """Alert model for token monitoring."""
    
    __tablename__ = "alerts"
    
    id = Column(UUID(as_uuid=True), primary_key=True, default=uuid.uuid4, index=True)
    user_id = Column(UUID(as_uuid=True), ForeignKey("users.id", ondelete="CASCADE"), nullable=False, index=True)
    token_symbol = Column(String(50), nullable=False, index=True)
    token_address = Column(String(255), nullable=True)
    alert_type = Column(Enum(AlertType), nullable=False)
    condition = Column(Enum(AlertCondition), nullable=False)
    threshold_value = Column(Numeric(20, 8), nullable=False)
    is_active = Column(Boolean, default=True, nullable=False)
    triggered_at = Column(DateTime, nullable=True)
    created_at = Column(DateTime, default=datetime.utcnow, nullable=False)
    updated_at = Column(DateTime, default=datetime.utcnow, onupdate=datetime.utcnow, nullable=False)
    
    # Relationships
    user = relationship("User", back_populates="alerts")
    
    def __repr__(self):
        return f"<Alert {self.token_symbol} {self.alert_type.value} {self.condition.value} {self.threshold_value}>"
