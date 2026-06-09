<script setup lang="ts">
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import request from "@/utils/request";

const router = useRouter();

const bannerList = ref([
  {
    id: 1,
    title: "铲屎的！空盆",
    subtitle: "优质宠物食品，让爱宠健康成长",
    image: "https://via.placeholder.com/1200x400/FFB347/ffffff?text=Banner+1",
    buttonText: "马上抢购",
    buttonAction: "buy",
  },
  {
    id: 2,
    title: "新品上市",
    subtitle: "多款新品限时特惠",
    image: "https://via.placeholder.com/1200x400/4FACFE/ffffff?text=Banner+2",
    buttonText: "查看详情",
    buttonAction: "detail",
  },
  {
    id: 3,
    title: "会员专享",
    subtitle: "注册即享9折优惠",
    image: "https://via.placeholder.com/1200x400/43E97B/ffffff?text=Banner+3",
    buttonText: "立即加入",
    buttonAction: "register",
  },
]);

const currentBanner = ref(0);

const categories = [
  { id: 0, name: "全部商品", key: "all" },
  { id: 1, name: "猫粮", key: "catFood" },
  { id: 2, name: "狗粮", key: "dogFood" },
  { id: 3, name: "零食", key: "snacks" },
  { id: 4, name: "营养品", key: "nutrition" },
  { id: 5, name: "保健品", key: "healthProduct" },
  { id: 6, name: "用品", key: "supplies" },
];

interface Product {
  id: number;
  name: string;
  description?: string;
  price: number;
  originalPrice?: number;
  imageUrl?: string;
  sales?: number;
  isHot?: boolean;
  isNew?: boolean;
  categoryId?: number;
}

const newProducts = ref<Product[]>([]);
const categoryProducts = ref<Record<number, Product[]>>({
  0: [],
  1: [],
  2: [],
  3: [],
  4: [],
  5: [],
  6: [],
});

const loading = ref(false);

const fetchNewProducts = async () => {
  try {
    const res = await request.get("/admin/product/list", {
      params: { page: 1, pageSize: 4, isNew: true },
    });
    if (res.code === 200 && res.data) {
      newProducts.value = res.data.records || [];
    }
  } catch (error) {
    console.error("获取新品失败:", error);
  }
};

const fetchCategoryProducts = async (categoryId: number) => {
  try {
    const params: any = { page: 1, pageSize: 6 };
    if (categoryId > 0) {
      params.categoryId = categoryId;
    }
    const res = await request.get("/admin/product/list", { params });
    if (res.code === 200 && res.data) {
      categoryProducts.value[categoryId] = res.data.records || [];
    }
  } catch (error) {
    console.error(
      `获取${categories.find((c) => c.id === categoryId)?.name}失败:`,
      error
    );
  }
};

const goToOrder = () => {
  router.push("/order");
};

onMounted(async () => {
  loading.value = true;
  await Promise.all([
    fetchNewProducts(),
    fetchCategoryProducts(0),
    fetchCategoryProducts(1),
    fetchCategoryProducts(2),
    fetchCategoryProducts(3),
    fetchCategoryProducts(4),
    fetchCategoryProducts(5),
    fetchCategoryProducts(6),
  ]);
  loading.value = false;
});
</script>

<template>
  <div class="shop-home">
    <header class="shop-header">
      <div class="header-content">
        <div class="logo" @click="router.push('/')">
          <span class="logo-icon">🛍️</span>
          <span class="logo-text">宠物用品商城</span>
        </div>
        <nav class="nav-menu">
          <a href="#" class="nav-item active">首页</a>
          <a href="#" class="nav-item">商品分类</a>
          <a href="#" class="nav-item">购物车</a>
        </nav>
        <div class="user-info">
          <span class="location">📍 北京</span>
          <span class="user-avatar">👤</span>
        </div>
      </div>
    </header>

    <!-- 轮播图 -->
    <section class="banner-section">
      <el-carousel :interval="5000" height="400px" indicator-position="outside">
        <el-carousel-item v-for="item in bannerList" :key="item.id">
          <div
            class="banner-item"
            :style="{ backgroundImage: `url(${item.image})` }"
          >
            <div class="banner-content">
              <h1 class="banner-title">{{ item.title }}</h1>
              <p class="banner-subtitle">{{ item.subtitle }}</p>
              <div class="banner-actions">
                <button class="btn-primary">{{ item.buttonText }} ↓</button>
                <button class="btn-secondary">继续抢货</button>
              </div>
            </div>
            <div class="banner-mascot">😺</div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </section>

    <main class="main-content">
      <!-- 新品专区 -->
      <section class="section new-products-section">
        <div class="section-header">
          <h2 class="section-title">
            <span class="title-bar"></span>
            新品专区
          </h2>
          <a href="#" class="view-more">查看全部 →</a>
        </div>
        <div v-loading="loading" class="product-grid">
          <div
            v-for="product in newProducts"
            :key="product.id"
            class="product-card"
          >
            <div class="product-image">
              <img
                :src="
                  product.imageUrl ||
                  'https://via.placeholder.com/300x300?text=Product'
                "
                :alt="product.name"
              />
              <span v-if="product.isHot" class="badge hot">热销</span>
              <span v-if="product.isNew" class="badge new">新品</span>
            </div>
            <div class="product-info">
              <h3 class="product-name">{{ product.name }}</h3>
              <p class="product-desc">
                {{ product.description || "优质宠物食品" }}
              </p>
              <div class="price-row">
                <span class="price">¥{{ product.price || "0.00" }}</span>
                <span v-if="product.originalPrice" class="original-price"
                  >¥{{ product.originalPrice }}</span
                >
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- 各个分类的商品栏目 -->
      <section
        v-for="category in categories"
        :key="category.id"
        class="section category-section"
      >
        <div class="section-header">
          <h2 class="section-title">
            <span class="title-bar"></span>
            {{ category.name }}
          </h2>
          <a href="#" class="view-more">查看更多 →</a>
        </div>
        <div v-loading="loading" class="product-grid">
          <div
            v-for="product in categoryProducts[category.id]"
            :key="product.id"
            class="product-card"
          >
            <div class="product-image">
              <img
                :src="
                  product.imageUrl ||
                  'https://via.placeholder.com/300x300?text=Product'
                "
                :alt="product.name"
              />
              <span v-if="product.isHot" class="badge hot">热销</span>
              <span v-if="product.isNew" class="badge new">新品</span>
            </div>
            <div class="product-info">
              <h3 class="product-name">{{ product.name }}</h3>
              <p class="product-desc">
                {{ product.description || "优质宠物食品，关爱您的爱宠" }}
              </p>
              <div class="product-bottom">
                <div class="price-row">
                  <span class="price">¥{{ product.price || "0.00" }}</span>
                  <span v-if="product.originalPrice" class="original-price"
                    >¥{{ product.originalPrice }}</span
                  >
                </div>
                <span class="sales-tag" v-if="product.sales"
                  >已售{{ product.sales }}</span
                >
              </div>
            </div>
          </div>
        </div>
      </section>
    </main>

    <!-- 页脚 -->
    <footer class="site-footer">
      <div class="footer-services">
        <div class="service-item">
          <span class="service-icon">✓</span>
          <span>正品保障</span>
        </div>
        <div class="service-item">
          <span class="service-icon">↩</span>
          <span>7天无理由退换</span>
        </div>
        <div class="service-item">
          <span class="service-icon">⚡</span>
          <span>15天免费质保</span>
        </div>
        <div class="service-item">
          <span class="service-icon">📞</span>
          <span>24小时客服热线020-520 1314</span>
        </div>
        <div class="service-item">
          <span class="service-icon">💳</span>
          <span>支持多种支付方式</span>
        </div>
      </div>
      <div class="footer-copyright">
        <span class="footer-logo">🐱 宠物用品商城</span>
        <span>Copyright © 2024 宠物用品商城 京ICP备XXXXXXX号</span>
      </div>
    </footer>
  </div>
</template>

<style scoped>
.shop-home {
  min-height: 100vh;
  background: #f5f7fa;
}

.shop-header {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 16px 32px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  font-size: 20px;
  font-weight: 700;
  color: #ff6b35;
}

.logo-icon {
  font-size: 28px;
}

.nav-menu {
  display: flex;
  gap: 40px;
  margin-left: 60px;
  flex: 1;
}

.nav-item {
  text-decoration: none;
  color: #666;
  font-size: 15px;
  font-weight: 500;
  padding: 8px 0;
  border-bottom: 2px solid transparent;
  transition: all 0.3s;
}

.nav-item:hover,
.nav-item.active {
  color: #ff6b35;
  border-bottom-color: #ff6b35;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 16px;
  font-size: 14px;
  color: #666;
}

.user-avatar {
  cursor: pointer;
  font-size: 24px;
}

/* 轮播图 */
.banner-section {
  background: #fff;
}

.banner-item {
  height: 100%;
  background-size: cover;
  background-position: center;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 100px;
  position: relative;
}

.banner-content {
  z-index: 2;
}

.banner-title {
  font-size: 56px;
  font-weight: 900;
  color: #333;
  margin: 0 0 12px;
  letter-spacing: 2px;
}

.banner-subtitle {
  font-size: 20px;
  color: #666;
  margin: 0 0 32px;
}

.banner-actions {
  display: flex;
  gap: 16px;
}

.btn-primary {
  padding: 14px 36px;
  background: linear-gradient(135deg, #ff6b35 0%, #ff8c42 100%);
  color: #fff;
  border: none;
  border-radius: 25px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 4px 15px rgba(255, 107, 53, 0.4);
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 107, 53, 0.5);
}

.btn-secondary {
  padding: 14px 36px;
  background: rgba(0, 0, 0, 0.7);
  color: #fff;
  border: none;
  border-radius: 25px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-secondary:hover {
  background: rgba(0, 0, 0, 0.85);
}

.banner-mascot {
  font-size: 180px;
  z-index: 1;
  animation: bounce 2s ease-in-out infinite;
}

@keyframes bounce {
  0%,
  100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-20px);
  }
}

/* 主要内容区 */
.main-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 32px;
}

.section {
  margin-bottom: 48px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.section-title {
  font-size: 22px;
  font-weight: 700;
  color: #333;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 10px;
}

.title-bar {
  width: 4px;
  height: 24px;
  background: linear-gradient(180deg, #ff6b35 0%, #ff8c42 100%);
  border-radius: 2px;
}

.view-more {
  color: #999;
  text-decoration: none;
  font-size: 14px;
  transition: color 0.3s;
}

.view-more:hover {
  color: #ff6b35;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 20px;
  min-height: 200px;
}

.product-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  transition: all 0.3s;
  cursor: pointer;
}

.product-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.product-image {
  position: relative;
  width: 100%;
  height: 220px;
  overflow: hidden;
  background: #f8f9fa;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.product-card:hover .product-image img {
  transform: scale(1.05);
}

.badge {
  position: absolute;
  top: 12px;
  left: 12px;
  padding: 4px 10px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
  color: #fff;
}

.badge.hot {
  background: linear-gradient(135deg, #ff6b35 0%, #ff8c42 100%);
}

.badge.new {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.product-info {
  padding: 16px;
}

.product-name {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  margin: 0 0 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-desc {
  font-size: 13px;
  color: #999;
  margin: 0 0 12px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.price-row {
  display: flex;
  align-items: baseline;
  gap: 8px;
}

.price {
  font-size: 20px;
  font-weight: 700;
  color: #ff6b35;
}

.original-price {
  font-size: 12px;
  color: #ccc;
  text-decoration: line-through;
}

.sales-tag {
  font-size: 12px;
  color: #999;
  background: #f5f5f5;
  padding: 2px 8px;
  border-radius: 10px;
}

/* 页脚 */
.site-footer {
  background: #fff;
  margin-top: 60px;
  border-top: 1px solid #eee;
}

.footer-services {
  max-width: 1400px;
  margin: 0 auto;
  padding: 32px;
  display: flex;
  justify-content: space-around;
  border-bottom: 1px solid #f0f0f0;
}

.service-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #666;
}

.service-icon {
  font-size: 18px;
  color: #ff6b35;
}

.footer-copyright {
  max-width: 1400px;
  margin: 0 auto;
  padding: 24px 32px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 13px;
  color: #999;
}

.footer-logo {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

@media (max-width: 1024px) {
  .banner-item {
    padding: 40px;
  }

  .banner-title {
    font-size: 36px;
  }

  .banner-mascot {
    font-size: 120px;
  }

  .product-grid {
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  }

  .footer-services {
    flex-wrap: wrap;
    gap: 16px;
  }
}
</style>
