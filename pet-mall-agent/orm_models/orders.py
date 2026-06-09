from datetime import datetime

from sqlalchemy import BigInteger, DateTime, DECIMAL, ForeignKey, Index, String
from sqlalchemy.dialects.mssql import TINYINT
from sqlalchemy.orm import DeclarativeBase, Mapped, mapped_column


class Base(DeclarativeBase):
    create_time: Mapped[datetime] = mapped_column(DateTime, default=datetime.now, comment="创建时间")
    update_time: Mapped[datetime] = mapped_column(DateTime, default=datetime.now, onupdate=datetime.now, comment="更新时间")


class Orders(Base):
    __tablename__ = "orders"

    # 创建索引
    __table_args__ = (
        Index("idx_order_order_no", "order_no"),
        Index("idx_order_user_id", "user_id"),
        Index("idx_order_status", "status"),
    )

    id: Mapped[int] = mapped_column(BigInteger, primary_key=True, autoincrement=True, comment="订单ID（主键）")
    order_no: Mapped[str] = mapped_column(String(50), nullable=False, unique=True, comment="订单号")
    user_id: Mapped[int] = mapped_column(BigInteger, ForeignKey("user.id", onupdate="CASCADE", ondelete="CASCADE"), nullable=False, comment="下单用户ID（关联user表）")
    total_amount: Mapped[float | None] = mapped_column(DECIMAL(10, 2), nullable=True, comment="订单总金额")
    status: Mapped[int | None] = mapped_column(TINYINT, nullable=True, comment="订单状态：0-待付款，1-待发货，2-已发货，3-已完成")
    address_book_id: Mapped[int | None] = mapped_column(BigInteger, nullable=True, comment="收货地址ID")

    # 关联关系（如果需要）
    # user: Mapped["User"] = relationship("User", back_populates="orders")

    def __repr__(self):
        return f"<Order(order_no={self.order_no}, user_id={self.user_id}, status={self.status})>"
