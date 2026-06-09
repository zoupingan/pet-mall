<template>
  <aside class="category-sidebar">
    <div class="sidebar-heading">
      <div>
        <span>按生活场景选购</span>
        <h2>宠物用品分类</h2>
      </div>
      <el-icon><Grid /></el-icon>
    </div>

    <div class="category-list">
      <button
        v-for="category in categories"
        :key="category.id"
        type="button"
        class="category-item"
        :class="{ active: activeCategory === category.id }"
        @click="handleCategoryClick(category)"
      >
        <span class="category-icon">
          <el-icon><component :is="category.icon" /></el-icon>
        </span>
        <span class="category-copy">
          <strong>{{ category.categoryName }}</strong>
          <small>{{ category.description }}</small>
        </span>
        <el-icon class="arrow"><ArrowRight /></el-icon>
      </button>
    </div>

    <router-link to="/all-products" class="all-products-link">
      浏览全部商品
      <el-icon><ArrowRight /></el-icon>
    </router-link>
  </aside>
</template>

<script setup>
import { markRaw, onMounted, ref } from "vue";
import {
  ArrowRight,
  Box,
  Brush,
  FirstAidKit,
  Food,
  Grid,
  MagicStick,
  Present,
} from "@element-plus/icons-vue";
import { getCategoryList } from "@/api/category";

const emit = defineEmits(["categoryClick"]);
const categories = ref([]);
const activeCategory = ref(null);

const iconPool = [
  markRaw(Food),
  markRaw(Present),
  markRaw(MagicStick),
  markRaw(Brush),
  markRaw(FirstAidKit),
  markRaw(Box),
];

const descriptions = [
  "主粮与营养",
  "零食与奖励",
  "互动与陪伴",
  "清洁与护理",
  "健康与保健",
  "出行与日用",
];

const fetchCategories = async () => {
  try {
    const res = await getCategoryList();
    if (res.data && Array.isArray(res.data)) {
      categories.value = res.data.map((category, index) => ({
        id: category.id,
        categoryName: category.categoryName,
        description: descriptions[index % descriptions.length],
        icon: iconPool[index % iconPool.length],
      }));
    }
  } catch (error) {
    console.error("获取分类列表失败:", error);
  }
};

const handleCategoryClick = (category) => {
  activeCategory.value = category.id;
  emit("categoryClick", category);
};

onMounted(fetchCategories);
</script>

<style scoped>
.category-sidebar {
  width: 286px;
  height: 540px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  border: 1px solid var(--pet-line);
  border-radius: var(--pet-radius);
  background: rgba(255, 253, 248, 0.92);
  box-shadow: var(--pet-shadow);
}

.sidebar-heading {
  min-height: 112px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 22px;
  border-bottom: 1px solid var(--pet-line);
}

.sidebar-heading span {
  color: var(--pet-accent-dark);
  font-size: 11px;
  font-weight: 800;
}

.sidebar-heading h2 {
  margin: 6px 0 0;
  font-size: 21px;
  letter-spacing: 0;
}

.sidebar-heading > .el-icon {
  font-size: 24px;
  color: var(--pet-accent);
}

.category-list {
  flex: 1;
  overflow-y: auto;
  scrollbar-width: thin;
}

.category-item {
  width: 100%;
  display: grid;
  grid-template-columns: 38px 1fr auto;
  align-items: center;
  gap: 12px;
  padding: 12px 18px;
  border: 0;
  border-bottom: 1px solid rgba(70, 60, 48, 0.07);
  background: transparent;
  color: var(--pet-ink);
  text-align: left;
  cursor: pointer;
  transition: background-color 180ms ease, color 180ms ease;
}

.category-item:hover,
.category-item.active {
  background: var(--pet-accent-soft);
}

.category-item.active {
  box-shadow: inset 3px 0 0 var(--pet-accent);
}

.category-icon {
  width: 36px;
  height: 36px;
  display: grid;
  place-items: center;
  border-radius: 50%;
  background: #fff;
  color: var(--pet-accent-dark);
  box-shadow: 0 5px 14px rgba(82, 62, 39, 0.07);
}

.category-copy {
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.category-copy strong {
  overflow: hidden;
  font-size: 13px;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.category-copy small {
  color: var(--pet-muted);
  font-size: 10px;
}

.arrow {
  color: var(--pet-muted);
  font-size: 13px;
}

.all-products-link {
  min-height: 52px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  border-top: 1px solid var(--pet-line);
  color: var(--pet-ink);
  text-decoration: none;
  font-size: 12px;
  font-weight: 800;
}

.all-products-link:hover {
  color: var(--pet-accent-dark);
}

@media (max-width: 960px) {
  .category-sidebar {
    width: 100%;
    height: auto;
  }

  .sidebar-heading {
    min-height: auto;
  }

  .category-list {
    display: flex;
    overflow-x: auto;
  }

  .category-item {
    width: 180px;
    min-width: 180px;
    grid-template-columns: 34px 1fr;
    border-right: 1px solid rgba(70, 60, 48, 0.07);
  }

  .category-item .arrow {
    display: none;
  }

  .all-products-link {
    display: none;
  }
}
</style>
