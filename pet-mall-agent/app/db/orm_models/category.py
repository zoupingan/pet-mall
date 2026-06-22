from datetime import datetime

from sqlalchemy import DateTime, Integer, String
from sqlalchemy.orm import DeclarativeBase, Mapped, mapped_column


class Base(DeclarativeBase):
    pass


class Category(Base):
    __tablename__ = "category"

    id: Mapped[int] = mapped_column(Integer, primary_key=True, autoincrement=True, comment="分类ID（主键）")
    category_name: Mapped[str] = mapped_column(String(50), nullable=False, comment="分类名称")
    icon: Mapped[str | None] = mapped_column(String(255), nullable=True, comment="分类图标/图片URL")
    create_time: Mapped[datetime | None] = mapped_column(DateTime, nullable=True, comment="创建时间")
    update_time: Mapped[datetime | None] = mapped_column(DateTime, nullable=True, comment="更新时间")
