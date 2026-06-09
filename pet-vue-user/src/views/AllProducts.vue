<template>
  <div class="category-page">
    <div class="category-container">
      <aside class="category-sidebar">
        <div class="sidebar-banner">
          <h2 class="banner-title">宠装<br />萌食</h2>
          <div class="cat-illustration">
            <div class="cat-body"></div>
            <div class="cat-face">
              <div class="ear left"></div>
              <div class="ear right"></div>
              <div class="eyes">
                <div class="eye left"></div>
                <div class="eye right"></div>
              </div>
            </div>
          </div>
        </div>

        <nav class="category-nav">
          <div
            class="category-item"
            :class="{ active: activeCategory === 0 }"
            @click="switchCategory(0)"
          >
            全部商品
          </div>
          <div
            v-for="(category, index) in categoryList"
            :key="category.id"
            class="category-item"
            :class="{ active: activeCategory === index + 1 }"
            @click="switchCategory(index + 1)"
          >
            {{ category.categoryName }}
          </div>
        </nav>
      </aside>

      <main class="products-main">
        <div class="page-header">
          <h1>{{ currentCategoryName }}</h1>
        </div>

        <div v-if="loading" class="loading-container">
          <el-skeleton :rows="6" animated />
          <el-skeleton :rows="6" animated style="margin-top: 20px" />
        </div>

        <div v-else class="products-grid">
          <ProductCard
            v-for="product in currentProducts"
            :key="product.id"
            :product="formatProduct(product)"
          />
        </div>

        <div v-if="!loading && currentProducts.length === 0" class="empty-tip">
          暂无商品
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import ProductCard from "../components/ProductCard.vue";
import { getCategoryList } from "../api/category";
import { getProductListByCategory } from "../api/product";

const loading = ref(true);
const activeCategory = ref(0);
const categoryList = ref([]);
const allProductsMap = ref({});

const currentCategoryName = computed(() => {
  if (activeCategory.value === 0 || !categoryList.value.length) {
    return "全部商品";
  }
  const category = categoryList.value[activeCategory.value - 1];
  return category?.categoryName || "全部商品";
});

const currentProducts = computed(() => {
  if (activeCategory.value === 0) {
    const allLists = Object.values(allProductsMap.value);
    const products = allLists.flat().filter((p) => p && p.status === 1);
    return products;
  }

  const category = categoryList.value[activeCategory.value - 1];
  if (category?.id && allProductsMap.value[category.id]) {
    return allProductsMap.value[category.id].filter((p) => p.status === 1);
  }

  return [];
});

const formatProduct = (product) => {
  return {
    id: product.id,
    name: product.productName,
    description: product.description || "",
    price: product.price ? String(product.price) : "0",
    originalPrice: product.costPrice ? String(product.costPrice) : "",
    image:
      product.imageUrl || "https://via.placeholder.com/400x400?text=No+Image",
    badge: "",
    tags: [],
  };
};

const switchCategory = async (index) => {
  activeCategory.value = index;

  if (index > 0 && categoryList.value[index - 1]) {
    const categoryId = categoryList.value[index - 1].id;
    if (!allProductsMap.value[categoryId]) {
      await fetchProductsByCategory(categoryId);
    }
  }
};

const fetchCategories = async () => {
  try {
    const res = await getCategoryList();
    if (res.data && Array.isArray(res.data)) {
      categoryList.value = res.data;
    }
  } catch (error) {
    console.error("获取分类列表失败:", error);
  }
};

const fetchProductsByCategory = async (categoryId) => {
  try {
    const res = await getProductListByCategory({
      categoryId: categoryId,
    });

    let products = [];
    if (Array.isArray(res.data)) {
      products = res.data;
    } else if (
      res.data &&
      res.data.records &&
      Array.isArray(res.data.records)
    ) {
      products = res.data.records;
    } else if (res.data && res.data.id) {
      products = [res.data];
    }

    allProductsMap.value[categoryId] = products;
  } catch (error) {
    console.error(`获取分类 ${categoryId} 的商品失败:`, error);
    allProductsMap.value[categoryId] = [];
  }
};

const fetchAllData = async () => {
  try {
    loading.value = true;
    await fetchCategories();

    for (const category of categoryList.value) {
      await fetchProductsByCategory(category.id);
    }
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  fetchAllData();
});
</script>

<style scoped>
.category-page {
  min-height: 100vh;
  background-color: #f5f7fa;
  padding-top: 20px;
}

.category-container {
  max-width: 1400px;
  margin: 0 auto;
  display: flex;
  gap: 25px;
  padding: 0 30px 60px;
}

.category-sidebar {
  width: 240px;
  flex-shrink: 0;
}

.sidebar-banner {
  background: linear-gradient(135deg, #ff9966 0%, #ffcc99 100%);
  border-radius: 12px;
  padding: 30px 20px;
  margin-bottom: 20px;
  position: relative;
  overflow: hidden;
  min-height: 320px;
}

.banner-title {
  font-size: 36px;
  color: #fff;
  font-weight: 900;
  line-height: 1.2;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
  position: relative;
  z-index: 2;
}

.cat-illustration {
  position: absolute;
  bottom: -20px;
  right: -20px;
  width: 180px;
  height: 180px;
  animation: float 3s ease-in-out infinite;
}

.cat-body {
  width: 130px;
  height: 120px;
  background: linear-gradient(135deg, #ffb74d 0%, #ffa726 100%);
  border-radius: 50% 50% 45% 45%;
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
}

.cat-face {
  width: 110px;
  height: 100px;
  background: linear-gradient(135deg, #ffe0b2 0%, #ffcc80 100%);
  border-radius: 50% 50% 45% 45%;
  position: absolute;
  top: 15px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 2;
}

.ear {
  width: 28px;
  height: 35px;
  background: linear-gradient(135deg, #ffb74d 0%, #ffa726 100%);
  position: absolute;
  top: -12px;
  clip-path: polygon(50% 0%, 0% 100%, 100% 100%);
}

.ear.left {
  left: 12px;
  transform: rotate(-15deg);
}

.ear.right {
  right: 12px;
  transform: rotate(15deg);
}

.eyes {
  position: absolute;
  top: 35px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 22px;
}

.eye {
  width: 11px;
  height: 14px;
  background: #333;
  border-radius: 50%;
}

@keyframes float {
  0%,
  100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

.category-nav {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.category-item {
  padding: 16px 24px;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 15px;
  color: #666;
  border-bottom: 1px solid #f5f5f5;
}

.category-item:last-child {
  border-bottom: none;
}

.category-item:hover {
  background-color: #fff5f0;
  color: #ff6b35;
  padding-left: 30px;
}

.category-item.active {
  background: linear-gradient(90deg, #ff6b35 0%, #ff8c42 100%);
  color: white;
  font-weight: bold;
  position: relative;
}

.category-item.active::before {
  content: "";
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 4px;
  background: #fff;
}

.products-main {
  flex: 1;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
}

.page-header h1 {
  font-size: 26px;
  color: #333;
  font-weight: bold;
  margin: 0;
}

.loading-container {
  margin-top: 20px;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 20px;
}

.empty-tip {
  text-align: center;
  padding: 60px;
  color: #999;
  font-size: 16px;
}
</style>
