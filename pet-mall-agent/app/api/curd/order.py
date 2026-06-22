from sqlalchemy import select
from sqlalchemy.orm import Session

from app.db.orm_models.orders import Orders


def crud_get_orders_sync(db: Session, user_id: int | None = None):
    select_stmt = select(Orders).where(Orders.user_id == user_id)
    orders = db.execute(select_stmt)
    return orders.scalars().all()


def crud_get_latest_order_sync(db: Session, user_id: int):
    select_stmt = (
        select(Orders)
        .where(Orders.user_id == user_id)
        .order_by(Orders.create_time.desc())
        .limit(1)
    )
    return db.execute(select_stmt).scalars().first()


def crud_get_order_by_no_sync(db: Session, user_id: int, order_no: str):
    select_stmt = select(Orders).where(
        Orders.user_id == user_id,
        Orders.order_no == order_no,
    )
    return db.execute(select_stmt).scalars().first()
