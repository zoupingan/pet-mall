from datetime import datetime

from sqlalchemy import BigInteger, DateTime, DECIMAL, Index, Integer, String, Text
from sqlalchemy.dialects.mysql import TINYINT
from sqlalchemy.orm import DeclarativeBase, Mapped, mapped_column


class Base(DeclarativeBase):
    pass


class Product(Base):
    __tablename__ = "product"

    # 创建索引
    __table_args__ = (
        Index("idx_product_brand_id", "brand_id"),
        Index("idx_product_category_id", "category_id"),
        Index("idx_product_status", "status"),
        Index("idx_product_is_deleted", "is_deleted"),
    )

    id: Mapped[int] = mapped_column(BigInteger, primary_key=True, autoincrement=True, comment="商品ID（主键）")
    product_name: Mapped[str] = mapped_column(String(100), nullable=False, comment="商品名称")
    category_id: Mapped[int] = mapped_column(Integer, nullable=False, comment="分类ID（关联category表）")
    brand_id: Mapped[int | None] = mapped_column(Integer, nullable=True, comment="品牌ID（关联brand表）")
    cost_price: Mapped[float | None] = mapped_column(DECIMAL(10, 2), nullable=True, comment="进价")
    price: Mapped[float | None] = mapped_column(DECIMAL(10, 2), nullable=True, comment="售价")
    stock: Mapped[int | None] = mapped_column(Integer, nullable=True, comment="库存数量")
    image_url: Mapped[str | None] = mapped_column(String(255), nullable=True, comment="商品主图URL")
    description: Mapped[str | None] = mapped_column(Text, nullable=True, comment="商品描述/详情")
    status: Mapped[int | None] = mapped_column(TINYINT, nullable=True, comment="上架状态（0-下架，1-上架）")
    is_deleted: Mapped[int | None] = mapped_column(TINYINT, nullable=True, comment="逻辑删除（0-未删除，1-已删除）")
    create_time: Mapped[datetime | None] = mapped_column(DateTime, nullable=True, comment="创建时间")
    update_time: Mapped[datetime | None] = mapped_column(DateTime, nullable=True, comment="更新时间")

    def __repr__(self):
        return f"<Product(id={self.id}, product_name={self.product_name}, brand_id={self.brand_id}, price={self.price})>"
