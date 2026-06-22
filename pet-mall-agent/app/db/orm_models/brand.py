from datetime import datetime
from sqlalchemy import DateTime, String, Integer, Index
from sqlalchemy.orm import DeclarativeBase, Mapped, mapped_column


class Base(DeclarativeBase):
    pass


class Brand(Base):
    __tablename__ = "brand"

    # 创建索引
    __table_args__ = (
        Index("idx_brand_name", "brand_name"),
    )

    id: Mapped[int] = mapped_column(Integer, primary_key=True, autoincrement=True, comment="品牌ID（主键）")
    brand_name: Mapped[str] = mapped_column(String(100), nullable=False, comment="品牌名称")
    description: Mapped[str | None] = mapped_column(String(500), nullable=True, comment="品牌描述")
    create_time: Mapped[datetime | None] = mapped_column(DateTime, nullable=True, comment="创建时间")
    update_time: Mapped[datetime | None] = mapped_column(DateTime, nullable=True, comment="更新时间")

    def __repr__(self):
        return f"<Brand(id={self.id}, brand_name={self.brand_name})>"
