<template>
  <div class="category-page">
    <div class="category-container">
      <aside class="category-sidebar">
        <div class="sidebar-banner">
          <h2 class="banner-title">宠装<br/>萌食</h2>
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
            v-for="(category, index) in categories"
            :key="index"
            class="category-item"
            :class="{ active: activeCategory === index }"
            @click="activeCategory = index"
          >
            {{ category.name }}
          </div>
        </nav>
      </aside>

      <main class="products-main">
        <div class="page-header">
          <h1>宠物食品</h1>
          <router-link to="/cart" class="view-cart">查看更多 →</router-link>
        </div>

        <div class="products-grid">
          <ProductCard
            v-for="product in filteredProducts"
            :key="product.id"
            :product="product"
          />
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import ProductCard from '../components/ProductCard.vue'

const activeCategory = ref(0)

const categories = ref([
  { name: '全部商品' },
  { name: '猫粮' },
  { name: '狗粮' },
  { name: '零食' },
  { name: '营养品' },
  { name: '保健品' },
  { name: '用品' }
])

const allProducts = ref([
  {
    id: 101,
    name: '猫粮 成猫专用营养配方',
    description: '优质原料，均衡营养，适合成年猫咪日常食用',
    price: '55',
    originalPrice: '79',
    image: 'https://images.unsplash.com/photo-1574158622682-e40e69881006?w=400&h=400&fit=crop',
    badge: '热销',
    tags: ['包邮'],
    category: 1
  },
  {
    id: 102,
    name: '全价通用型 狗粮成犬幼犬',
    description: '科学配方，营养全面，适合各年龄段狗狗',
    price: '145',
    image: 'https://images.unsplash.com/photo-1589924691995-400dc9ecc119?w=400&h=400&fit=crop',
    badge: '新品',
    tags: ['热卖'],
    category: 2
  },
  {
    id: 103,
    name: '宠物冻干零食 小鱼干鸡胸肉',
    description: '纯天然食材，无添加，适口性极佳',
    price: '39',
    image: 'https://images.unsplash.com/photo-1583337130417-3346a1be7dee?w=400&h=400&fit=crop',
    tags: ['促销'],
    category: 3
  },
  {
    id: 104,
    name: '宠物卵磷脂 美毛护肤',
    description: '改善毛发质量，让爱宠更加健康亮丽',
    price: '89',
    originalPrice: '120',
    image: 'https://images.unsplash.com/photo-1548199973-03cce0bbc87b?w=400&h=400&fit=crop',
    badge: '推荐',
    tags: ['必备'],
    category: 4
  },
  {
    id: 105,
    name: '麦富迪狗粮鲜肉夹心双拼全价',
    description: '双拼配方，营养丰富，狗狗超爱吃',
    price: '66',
    image: 'https://images.unsplash.com/photo-1587300003388-59208cc962cb?w=400&h=400&fit=crop',
    category: 2
  },
  {
    id: 106,
    name: '麦富迪barf猫咪主食罐头',
    description: '高肉含量，满足猫咪食肉天性',
    price: '35',
    originalPrice: '45',
    image: 'https://images.unsplash.com/photo-1574158622682-e40e69881006?w=400&h=400&fit=crop',
    tags: ['优惠'],
    category: 1
  },
  {
    id: 107,
    name: '麦富迪bb猫粮幼猫/成猫主粮',
    description: '专为猫咪设计，营养均衡易消化',
    price: '159',
    originalPrice: '199',
    image: 'https://images.unsplash.com/photo-1574158622682-e40e69881006?w=400&h=400&fit=crop',
    badge: '爆款',
    tags: ['热销'],
    category: 1
  },
  {
    id: 108,
    name: '量多实惠 狗粮20kg超大包装',
    description: '经济实惠，长期喂养首选，品质保证',
    price: '299',
    originalPrice: '380',
    image: 'https://images.unsplash.com/photo-1589924691995-400dc9ecc119?w=400&h=400&fit=crop',
    badge: '超值',
    tags: ['量大', '省钱'],
    category: 2
  },
  {
    id: 109,
    name: '宠物益生菌 调理肠胃',
    description: '改善消化问题，增强免疫力',
    price: '68',
    image: 'https://images.unsplash.com/photo-1583337130417-3346a1be7dee?w=400&h=400&fit=crop',
    category: 4
  },
  {
    id: 110,
    name: '猫条零食 混合口味装',
    description: '多种口味，猫咪的最爱零食',
    price: '25',
    originalPrice: '35',
    image: 'https://images.unsplash.com/photo-1574158622682-e40e69881006?w=400&h=400&fit=crop',
    tags: ['特价'],
    category: 3
  }
])

const filteredProducts = computed(() => {
  if (activeCategory.value === 0) {
    return allProducts.value
  }
  return allProducts.value.filter(p => p.category === activeCategory.value)
})
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
  0%, 100% {
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
  content: '';
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

.view-cart {
  color: #666;
  text-decoration: none;
  font-size: 15px;
  transition: color 0.3s;
}

.view-cart:hover {
  color: #ff6b35;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 20px;
}
</style>
