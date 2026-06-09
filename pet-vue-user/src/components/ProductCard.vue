<template>
  <article
    class="product-card"
    :class="{ featured: variant === 'featured' }"
    tabindex="0"
    @click="goToDetail"
    @keyup.enter="goToDetail"
  >
    <div class="product-image">
      <img :src="product.image" :alt="product.name" />
      <span v-if="product.badge" class="badge">{{ product.badge }}</span>
    </div>

    <div class="product-info">
      <div v-if="visibleTags.length" class="product-topline">
        <div class="product-tags">
          <span v-for="tag in visibleTags" :key="tag">{{ tag }}</span>
        </div>
      </div>

      <h3 class="product-name">{{ product.name }}</h3>
      <p class="product-desc">{{ product.description || "精选宠物日常用品" }}</p>

      <div class="product-footer">
        <div class="product-price">
          <span class="currency">¥</span>
          <strong>{{ product.price }}</strong>
          <del v-if="product.originalPrice">¥{{ product.originalPrice }}</del>
        </div>
        <span class="detail-link">
          查看
          <el-icon><ArrowRight /></el-icon>
        </span>
      </div>
    </div>
  </article>
</template>

<script setup>
import { computed } from "vue";
import { useRouter } from "vue-router";
import { ArrowRight } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";

const props = defineProps({
  product: {
    type: Object,
    required: true,
  },
  variant: {
    type: String,
    default: "default",
  },
});

const router = useRouter();
const visibleTags = computed(() => {
  const tags = Array.isArray(props.product.tags) ? props.product.tags : [];
  return tags.slice(0, 2);
});

const goToDetail = () => {
  if (!props.product.id) {
    ElMessage.warning("商品信息异常，请稍后重试");
    return;
  }
  router.push(`/product/${props.product.id}`);
};

</script>

<style scoped>
.product-card {
  min-width: 0;
  height: 100%;
  display: grid;
  grid-template-rows: minmax(230px, 1fr) auto;
  overflow: hidden;
  border: 1px solid var(--pet-line);
  border-radius: var(--pet-radius);
  background: var(--pet-surface-strong);
  cursor: pointer;
  outline: none;
  transition:
    transform 420ms cubic-bezier(0.16, 1, 0.3, 1),
    box-shadow 420ms cubic-bezier(0.16, 1, 0.3, 1),
    border-color 220ms ease;
}

.product-card:hover,
.product-card:focus-visible {
  transform: translateY(-7px);
  border-color: rgba(217, 111, 61, 0.34);
  box-shadow: var(--pet-shadow-hover);
}

.product-image {
  position: relative;
  min-height: 230px;
  display: grid;
  place-items: center;
  overflow: hidden;
  background:
    linear-gradient(145deg, rgba(255, 255, 255, 0.2), transparent),
    #f2eee8;
}

.product-image img {
  width: 86%;
  height: 86%;
  object-fit: contain;
  transition: transform 700ms cubic-bezier(0.16, 1, 0.3, 1);
}

.product-card:hover .product-image img {
  transform: scale(1.06);
}

.badge {
  position: absolute;
  top: 14px;
  left: 14px;
  padding: 6px 9px;
  border-radius: 4px;
  background: var(--pet-ink);
  color: #fff;
  font-size: 11px;
  font-weight: 800;
}

.product-info {
  padding: 20px;
}

.product-topline,
.product-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.product-tags {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

.product-tags span {
  padding: 4px 7px;
  border-radius: 4px;
  background: var(--pet-accent-soft);
  color: var(--pet-accent-dark);
  font-size: 10px;
  font-weight: 800;
}

.product-name {
  min-height: 48px;
  margin: 17px 0 7px;
  overflow: hidden;
  color: var(--pet-ink);
  font-size: 17px;
  line-height: 1.45;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
}

.product-desc {
  min-height: 42px;
  margin: 0 0 20px;
  overflow: hidden;
  color: var(--pet-muted);
  font-size: 13px;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
}

.product-price {
  display: flex;
  align-items: baseline;
  gap: 4px;
  color: var(--pet-accent-dark);
}

.product-price .currency {
  font-size: 14px;
  font-weight: 800;
}

.product-price strong {
  font-size: 25px;
  line-height: 1;
}

.product-price del {
  margin-left: 4px;
  color: #aaa39a;
  font-size: 12px;
  font-weight: 500;
}

.detail-link {
  display: inline-flex;
  align-items: center;
  gap: 3px;
  color: var(--pet-muted);
  font-size: 12px;
  font-weight: 800;
}

.featured {
  grid-template-rows: minmax(380px, 1fr) auto;
}

.featured .product-image {
  min-height: 380px;
}

.featured .product-info {
  padding: 24px;
}

.featured .product-name {
  min-height: auto;
  font-size: 24px;
}

.featured .product-desc {
  max-width: 48ch;
  min-height: auto;
}

@media (max-width: 768px) {
  .product-card,
  .featured {
    grid-template-rows: minmax(220px, 1fr) auto;
  }

  .product-image,
  .featured .product-image {
    min-height: 220px;
  }

}
</style>
