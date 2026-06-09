<template>
  <section class="banner-carousel" aria-label="商城推荐">
    <el-carousel height="540px" :interval="5600" arrow="hover">
      <el-carousel-item v-for="(item, index) in banners" :key="item.id || index">
        <div class="banner-item">
          <img
            :src="item.bannerUrl"
            :alt="item.alt || '宠物用品主题推荐'"
            class="banner-image"
          />
          <div class="image-wash"></div>

          <div class="banner-copy">
            <p class="eyebrow">更懂宠物的日常选择</p>
            <h1>把每一次陪伴<br />照顾得更周到</h1>
            <p class="description">
              从每日主粮到互动玩具，为不同成长阶段挑选安心、耐用的宠物用品。
            </p>
            <div class="banner-actions">
              <router-link to="/all-products" class="primary-action">
                开始选购
                <el-icon><ArrowRight /></el-icon>
              </router-link>
              <a href="#new-products" class="secondary-action">查看新品</a>
            </div>
          </div>

          <div class="banner-index">
            <span>{{ String(index + 1).padStart(2, "0") }}</span>
            <i></i>
            <span>{{ String(banners.length).padStart(2, "0") }}</span>
          </div>
        </div>
      </el-carousel-item>
    </el-carousel>
  </section>
</template>

<script setup>
import { onMounted, ref } from "vue";
import { ArrowRight } from "@element-plus/icons-vue";
import { getBannerList } from "@/api/banner";

const banners = ref([]);

onMounted(async () => {
  try {
    const res = await getBannerList();
    if (res.data && Array.isArray(res.data)) {
      banners.value = res.data
        .filter((item) => item.status === 1)
        .sort((a, b) => a.sort - b.sort);
    }
  } catch (error) {
    console.error("获取轮播图失败:", error);
  }
});
</script>

<style scoped>
.banner-carousel {
  min-width: 0;
  flex: 1;
  overflow: hidden;
  border-radius: var(--pet-radius);
  background: #2d2a25;
  box-shadow: var(--pet-shadow);
}

.banner-item {
  position: relative;
  width: 100%;
  height: 100%;
  overflow: hidden;
  background: #2d2a25;
}

.banner-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transform: scale(1.02);
}

.image-wash {
  position: absolute;
  inset: 0;
  background:
    linear-gradient(90deg, rgba(31, 28, 24, 0.88) 0%, rgba(31, 28, 24, 0.58) 42%, rgba(31, 28, 24, 0.08) 76%),
    linear-gradient(0deg, rgba(31, 28, 24, 0.28), transparent 45%);
}

.banner-copy {
  position: absolute;
  left: clamp(30px, 5vw, 72px);
  top: 50%;
  width: min(620px, 66%);
  color: #fff;
  transform: translateY(-50%);
}

.eyebrow {
  margin: 0 0 16px;
  color: #f1b38f;
  font-size: 12px;
  font-weight: 800;
}

.banner-copy h1 {
  max-width: 760px;
  margin: 0;
  font-size: clamp(42px, 5.4vw, 76px);
  line-height: 1.04;
  letter-spacing: 0;
}

.description {
  max-width: 52ch;
  margin: 24px 0 0;
  color: rgba(255, 255, 255, 0.76);
  font-size: 15px;
  line-height: 1.75;
}

.banner-actions {
  display: flex;
  align-items: center;
  gap: 18px;
  margin-top: 32px;
}

.primary-action,
.secondary-action {
  min-height: 48px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 0 20px;
  border-radius: var(--pet-radius);
  text-decoration: none;
  font-size: 13px;
  font-weight: 800;
  transition: transform 180ms ease, background-color 180ms ease;
}

.primary-action {
  background: var(--pet-accent);
  color: #fff;
}

.secondary-action {
  border: 1px solid rgba(255, 255, 255, 0.38);
  color: #fff;
}

.primary-action:hover,
.secondary-action:hover {
  transform: translateY(-2px);
}

.secondary-action:hover {
  background: rgba(255, 255, 255, 0.1);
}

.banner-index {
  position: absolute;
  right: 28px;
  bottom: 24px;
  display: flex;
  align-items: center;
  gap: 8px;
  color: #fff;
  font-size: 11px;
  font-weight: 800;
}

.banner-index i {
  width: 36px;
  height: 1px;
  background: rgba(255, 255, 255, 0.5);
}

:deep(.el-carousel__indicators) {
  left: auto;
  right: 24px;
  transform: none;
}

:deep(.el-carousel__button) {
  width: 24px;
}

@media (max-width: 768px) {
  :deep(.el-carousel),
  :deep(.el-carousel__container) {
    height: 470px !important;
  }

  .banner-copy {
    top: auto;
    bottom: 54px;
    width: calc(100% - 44px);
    left: 22px;
    transform: none;
  }

  .banner-copy h1 {
    font-size: clamp(38px, 12vw, 56px);
  }

  .description {
    font-size: 13px;
  }

  .banner-index {
    display: none;
  }
}
</style>
