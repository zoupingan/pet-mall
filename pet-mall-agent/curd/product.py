import re

from sqlalchemy import select
from sqlalchemy.orm import Session

from orm_models.brand import Brand
from orm_models.category import Category
from orm_models.product import Product


PRODUCT_SEARCH_WORDS = [
    "皇家",
    "冠能",
    "麦富迪",
    "里兜",
    "秋冬",
    "幼猫",
    "成猫",
    "幼犬",
    "成犬",
    "猫粮",
    "狗粮",
    "犬粮",
    "猫砂",
    "罐头",
    "沐浴露",
    "磨牙棒",
    "冻干",
    "猫条",
    "玩具",
    "清洁",
    "保健",
]


def _normalize_keyword(text: str | None) -> str:
    text = (text or "").lower()
    for word in [
        "帮我",
        "查询",
        "查一下",
        "查",
        "多少",
        "多少钱",
        "价格",
        "售价",
        "有没有货",
        "有货吗",
        "库存",
        "还有吗",
        "还有货吗",
        "能不能买",
        "吗",
        "？",
        "?",
        " ",
    ]:
        text = text.replace(word, "")
    return text.strip()


# 提取要放进 SQL LIKE 的关键词，例如“皇家k36猫粮”会得到：皇家、k36、猫粮。
def _build_match_terms(keyword: str) -> list[str]:
    keyword = _normalize_keyword(keyword)
    if not keyword:
        return []

    terms = re.findall(r"[a-z0-9]+", keyword)
    chinese_text = re.sub(r"[a-z0-9]+", "", keyword)
    terms.extend(
        word for word in PRODUCT_SEARCH_WORDS
        if word in chinese_text
    )

    if not terms and len(chinese_text) >= 2:
        terms.append(chinese_text)

    return list(dict.fromkeys(terms))


def crud_get_products_sync(db: Session, brand_name: str = None):
    brand_stmt = select(Brand).where(Brand.brand_name.like(f"%{brand_name}%"))
    brand = db.execute(brand_stmt).scalars().first()
    if brand is None:
        return []

    product_stmt = select(Product).where(
        Product.brand_id == brand.id,
        Product.is_deleted == 0,
        Product.status == 1,
    )
    return db.execute(product_stmt).scalars().all()


def crud_get_product_price_stock_sync(db: Session, product_name: str):
    """
    等价 SQL：
    where is_deleted = 0
      and product_name like '%皇家%'
      and product_name like '%k36%'
      and product_name like '%猫粮%'
    """
    terms = _build_match_terms(product_name)
    if not terms:
        return []

    product_stmt = select(Product).where(Product.is_deleted == 0)
    for term in terms:
        product_stmt = product_stmt.where(
            Product.product_name.like(f"%{term}%")
        )

    product_stmt = product_stmt.order_by(
        Product.status.desc(),
        Product.stock.desc(),
    ).limit(5)
    return db.execute(product_stmt).scalars().all()


def crud_get_products_by_category_sync(db: Session, category_name: str):
    category_stmt = select(Category).where(Category.category_name.like(f"%{category_name}%"))
    category = db.execute(category_stmt).scalars().first()
    if category is None:
        return []

    product_stmt = select(Product).where(
        Product.category_id == category.id,
        Product.is_deleted == 0,
        Product.status == 1,
    )
    return db.execute(product_stmt).scalars().all()
