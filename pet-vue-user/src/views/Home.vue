<template>
  <div ref="homePage" class="home-page">
    <section class="hero-shell pet-page-shell">
      <CategorySidebar class="hero-item" @categoryClick="handleCategoryClick" />
      <BannerCarousel class="hero-item" />
    </section>

    <section class="service-strip pet-page-shell hero-item" aria-label="服务保障">
      <div v-for="service in services" :key="service.title" class="service-item">
        <span class="service-icon">
          <el-icon><component :is="service.icon" /></el-icon>
        </span>
        <span>
          <strong>{{ service.title }}</strong>
          <small>{{ service.description }}</small>
        </span>
      </div>
    </section>

    <main class="home-content pet-page-shell">
      <section
        id="new-products"
        ref="newProductsSection"
        class="products-section reveal-section"
      >
        <div class="section-header">
          <div>
            <p>本周刚刚上新</p>
            <h2>给毛孩子的新鲜选择</h2>
          </div>
          <router-link to="/all-products" class="view-all">
            查看全部
            <el-icon><ArrowRight /></el-icon>
          </router-link>
        </div>

        <div v-if="loading" class="skeleton-grid">
          <el-skeleton v-for="item in 4" :key="item" animated>
            <template #template>
              <el-skeleton-item variant="image" class="skeleton-image" />
              <div class="skeleton-copy">
                <el-skeleton-item variant="h3" />
                <el-skeleton-item variant="text" />
                <el-skeleton-item variant="text" style="width: 60%" />
              </div>
            </template>
          </el-skeleton>
        </div>

        <div v-else-if="newProducts.length" class="featured-products">
          <ProductCard
            class="featured-product"
            :product="formatProduct(newProducts[0])"
            variant="featured"
          />
          <div class="compact-products">
            <ProductCard
              v-for="product in newProducts.slice(1)"
              :key="product.id"
              :product="formatProduct(product)"
            />
          </div>
        </div>

        <div v-else class="empty-tip">
          <strong>新品正在整理中</strong>
          <span>可以先看看其他分类里的热门商品。</span>
        </div>
      </section>

      <section class="care-story reveal-section">
        <div class="story-media">
          <img
            :src="storyImage"
            alt="宠物家庭生活场景"
          />
        </div>
        <div class="story-copy">
          <p>不只看价格，也关心长期陪伴</p>
          <h2>从成分、耐用度到使用感受，认真挑选每件日常用品。</h2>
          <div class="story-points">
            <span>清晰商品信息</span>
            <span>真实库存状态</span>
            <span>安心售后保障</span>
          </div>
          <router-link to="/all-products" class="story-link">
            浏览商城精选
            <el-icon><ArrowRight /></el-icon>
          </router-link>
        </div>
      </section>

      <template v-if="!loading">
        <section
          v-for="(category, index) in categoryList"
          :key="category.id"
          :ref="(el) => setSectionRef(el, index)"
          class="products-section category-section reveal-section"
        >
          <div class="section-header">
            <div>
              <p>{{ getCategoryCaption(index) }}</p>
              <h2>{{ category.categoryName }}</h2>
            </div>
            <router-link to="/all-products" class="view-all">
              查看更多
              <el-icon><ArrowRight /></el-icon>
            </router-link>
          </div>

          <div v-if="category.products.length" class="products-grid">
            <ProductCard
              v-for="product in category.products"
              :key="product.id"
              :product="formatProduct(product)"
            />
          </div>

          <div v-else class="empty-tip">
            <strong>这个分类暂时没有商品</strong>
            <span>上新后会第一时间出现在这里。</span>
          </div>
        </section>
      </template>
    </main>
  </div>
</template>

<script setup>
import {
  markRaw,
  nextTick,
  onMounted,
  onUnmounted,
  ref,
  watch,
} from "vue";
import {
  ArrowRight,
  CircleCheck,
  CreditCard,
  Service,
  Van,
} from "@element-plus/icons-vue";
import { gsap } from "gsap";
import { ScrollTrigger } from "gsap/ScrollTrigger";
import BannerCarousel from "../components/BannerCarousel.vue";
import CategorySidebar from "../components/CategorySidebar.vue";
import ProductCard from "../components/ProductCard.vue";
import { getCategoryList } from "../api/category";
import { getAllProductList, getProductListByCategory } from "../api/product";
import storyImage from "@/assets/pet-store-hero.jpg";

gsap.registerPlugin(ScrollTrigger);

const homePage = ref(null);
const loading = ref(true);
const newProducts = ref([]);
const categoryList = ref([]);
const sectionRefs = ref([]);
const newProductsSection = ref(null);
let motionMedia;
let contentContext;

const services = [
  {
    title: "正品保障",
    description: "商品来源清晰可查",
    icon: markRaw(CircleCheck),
  },
  {
    title: "快速配送",
    description: "常用好物及时送达",
    icon: markRaw(Van),
  },
  {
    title: "安心支付",
    description: "支持多种付款方式",
    icon: markRaw(CreditCard),
  },
  {
    title: "售后支持",
    description: "问题反馈及时处理",
    icon: markRaw(Service),
  },
];

const categoryCaptions = [
  "每日营养从这里开始",
  "训练和互动的小奖励",
  "让独处时间也有乐趣",
  "保持干净舒适的生活空间",
  "照顾不同阶段的健康需求",
];

const getCategoryCaption = (index) =>
  categoryCaptions[index % categoryCaptions.length];

const setSectionRef = (el, index) => {
  if (el) sectionRefs.value[index] = el;
};

const handleCategoryClick = async (category) => {
  await nextTick();

  if (category.categoryName === "全部商品" || category.id === "all") {
    newProductsSection.value?.scrollIntoView({
      behavior: "smooth",
      block: "start",
    });
    return;
  }

  const index = categoryList.value.findIndex((item) => item.id === category.id);
  sectionRefs.value[index]?.scrollIntoView({
    behavior: "smooth",
    block: "start",
  });
};

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

const fetchNewProducts = async () => {
  try {
    const res = await getAllProductList();

    let products = [];
    if (res.data) {
      if (Array.isArray(res.data)) {
        products = res.data.filter((p) => p.status === 1);
      } else if (res.data.records && Array.isArray(res.data.records)) {
        products = res.data.records.filter((p) => p.status === 1);
      } else if (res.data.id) {
        const item = res.data;
        if (item.status === 1) {
          products = [item];
        }
      }
    }

    newProducts.value = products.slice(0, 4);
  } catch (error) {
    console.error("获取新品失败:", error);
  }
};

const fetchCategoriesAndProducts = async () => {
  try {
    const categoryRes = await getCategoryList();
    if (categoryRes.data && Array.isArray(categoryRes.data)) {
      const categories = categoryRes.data;

      for (const category of categories) {
        try {
          const productRes = await getProductListByCategory({
            categoryId: category.id,
          });

          let products = [];
          if (productRes.data) {
            if (Array.isArray(productRes.data)) {
              products = productRes.data.filter((p) => p.status === 1);
            } else if (
              productRes.data.records &&
              Array.isArray(productRes.data.records)
            ) {
              products = productRes.data.records.filter((p) => p.status === 1);
            } else if (productRes.data.id) {
              const item = productRes.data;
              if (item.status === 1) {
                products = [item];
              }
            }
          }

          categoryList.value.push({
            id: category.id,
            categoryName: category.categoryName,
            icon: category.icon,
            products: products.slice(0, 4),
          });
        } catch (error) {
          console.error(`获取分类 ${category.categoryName} 的商品失败:`, error);
          categoryList.value.push({
            id: category.id,
            categoryName: category.categoryName,
            icon: category.icon,
            products: [],
          });
        }
      }
    }
  } catch (error) {
    console.error("获取分类列表失败:", error);
  }
};

const initData = async () => {
  try {
    loading.value = true;
    await Promise.all([fetchNewProducts(), fetchCategoriesAndProducts()]);
  } finally {
    loading.value = false;
  }
};

const initContentAnimations = () => {
  contentContext?.revert();
  if (!homePage.value) return;

  const reduceMotion = window.matchMedia(
    "(prefers-reduced-motion: reduce)"
  ).matches;
  if (reduceMotion) return;

  contentContext = gsap.context(() => {
    gsap.utils.toArray(".reveal-section").forEach((section) => {
      gsap.from(section, {
        autoAlpha: 0,
        y: 54,
        duration: 0.9,
        ease: "power3.out",
        scrollTrigger: {
          trigger: section,
          start: "top 86%",
          once: true,
        },
      });

      const cards = section.querySelectorAll(".product-card");
      if (cards.length) {
        gsap.from(cards, {
          autoAlpha: 0,
          y: 34,
          scale: 0.97,
          duration: 0.72,
          stagger: 0.08,
          ease: "power2.out",
          scrollTrigger: {
            trigger: section,
            start: "top 78%",
            once: true,
          },
        });
      }
    });

    gsap.fromTo(
      ".story-media img",
      { scale: 0.88 },
      {
        scale: 1.04,
        ease: "none",
        scrollTrigger: {
          trigger: ".care-story",
          start: "top bottom",
          end: "bottom top",
          scrub: 1,
        },
      }
    );
  }, homePage.value);

  ScrollTrigger.refresh();
};

onMounted(() => {
  motionMedia = gsap.matchMedia();
  const select = gsap.utils.selector(homePage.value);

  motionMedia.add(
    {
      allowMotion: "(prefers-reduced-motion: no-preference)",
      desktop: "(min-width: 961px)",
    },
    ({ conditions }) => {
      if (!conditions.allowMotion) return;

      gsap
        .timeline({ defaults: { ease: "power3.out" } })
        .from(select(".hero-item"), {
          autoAlpha: 0,
          y: 34,
          duration: 0.9,
          stagger: 0.12,
        })
        .from(
          select(".service-item"),
          {
            autoAlpha: 0,
            y: 18,
            duration: 0.55,
            stagger: 0.07,
          },
          "-=0.45"
        );

      if (conditions.desktop) {
        gsap.to(select(".banner-image"), {
          scale: 1.08,
          ease: "none",
          scrollTrigger: {
            trigger: select(".hero-shell")[0],
            start: "top top",
            end: "bottom top",
            scrub: 1,
          },
        });
      }
    }
  );

  initData();
});

watch(loading, async (isLoading) => {
  if (isLoading) return;
  await nextTick();
  initContentAnimations();
});

onUnmounted(() => {
  contentContext?.revert();
  motionMedia?.revert();
});
</script>

<style scoped>
.home-page {
  min-height: 100vh;
  padding-bottom: 80px;
}

.hero-shell {
  display: flex;
  gap: 16px;
  align-items: stretch;
  padding-top: 24px;
}

.service-strip {
  min-height: 106px;
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  margin-top: 16px;
  overflow: hidden;
  border: 1px solid var(--pet-line);
  border-radius: var(--pet-radius);
  background: rgba(255, 253, 248, 0.88);
}

.service-item {
  display: flex;
  align-items: center;
  gap: 13px;
  padding: 22px;
  border-right: 1px solid var(--pet-line);
}

.service-item:last-child {
  border-right: 0;
}

.service-icon {
  width: 42px;
  height: 42px;
  flex-shrink: 0;
  display: grid;
  place-items: center;
  border-radius: 50%;
  background: var(--pet-accent-soft);
  color: var(--pet-accent-dark);
}

.service-item > span:last-child {
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.service-item strong {
  font-size: 13px;
}

.service-item small {
  color: var(--pet-muted);
  font-size: 11px;
}

.home-content {
  padding-top: 110px;
}

.products-section {
  scroll-margin-top: 120px;
  margin-bottom: 128px;
}

.section-header {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 24px;
  margin-bottom: 34px;
}

.section-header p,
.story-copy > p {
  margin: 0 0 8px;
  color: var(--pet-accent-dark);
  font-size: 12px;
  font-weight: 800;
}

.section-header h2 {
  max-width: 760px;
  margin: 0;
  font-size: clamp(32px, 4vw, 54px);
  line-height: 1.12;
  letter-spacing: 0;
}

.view-all,
.story-link {
  display: inline-flex;
  align-items: center;
  gap: 7px;
  padding-bottom: 6px;
  border-bottom: 1px solid var(--pet-ink);
  color: var(--pet-ink);
  text-decoration: none;
  font-size: 13px;
  font-weight: 800;
  white-space: nowrap;
}

.featured-products {
  display: grid;
  grid-template-columns: minmax(0, 5fr) minmax(0, 7fr);
  gap: 16px;
}

.compact-products {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.compact-products > :last-child:nth-child(odd) {
  grid-column: 1 / -1;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
}

.skeleton-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
}

.skeleton-grid :deep(.el-skeleton) {
  overflow: hidden;
  border: 1px solid var(--pet-line);
  border-radius: var(--pet-radius);
  background: #fff;
}

.skeleton-image {
  width: 100%;
  height: 260px;
}

.skeleton-copy {
  display: grid;
  gap: 12px;
  padding: 20px;
}

.empty-tip {
  min-height: 230px;
  display: grid;
  place-items: center;
  align-content: center;
  gap: 8px;
  border: 1px dashed rgba(70, 60, 48, 0.22);
  border-radius: var(--pet-radius);
  color: var(--pet-muted);
  text-align: center;
}

.empty-tip strong {
  color: var(--pet-ink);
  font-size: 18px;
}

.care-story {
  min-height: 620px;
  display: grid;
  grid-template-columns: 1.08fr 0.92fr;
  margin-bottom: 150px;
  overflow: hidden;
  border-radius: var(--pet-radius);
  background: #292720;
  color: #fff;
}

.story-media {
  min-height: 620px;
  overflow: hidden;
}

.story-media img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  filter: saturate(0.72) contrast(1.06);
}

.story-copy {
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: clamp(34px, 6vw, 82px);
}

.story-copy > p {
  color: #efad88;
}

.story-copy h2 {
  max-width: 700px;
  margin: 0;
  font-size: clamp(32px, 4.4vw, 58px);
  line-height: 1.12;
  letter-spacing: 0;
}

.story-points {
  display: grid;
  gap: 12px;
  margin: 36px 0;
  color: rgba(255, 255, 255, 0.72);
  font-size: 13px;
}

.story-points span {
  padding-bottom: 12px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.14);
}

.story-link {
  align-self: flex-start;
  border-color: #fff;
  color: #fff;
}

@media (max-width: 1100px) {
  .products-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .service-strip {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .service-item:nth-child(2) {
    border-right: 0;
  }

  .service-item:nth-child(-n + 2) {
    border-bottom: 1px solid var(--pet-line);
  }
}

@media (max-width: 960px) {
  .hero-shell {
    flex-direction: column;
  }

  .featured-products,
  .care-story {
    grid-template-columns: 1fr;
  }

  .story-media {
    min-height: 420px;
  }

  .care-story {
    min-height: auto;
  }
}

@media (max-width: 768px) {
  .home-content {
    padding-top: 80px;
  }

  .products-section {
    margin-bottom: 96px;
  }

  .section-header {
    align-items: flex-start;
  }

  .section-header h2 {
    font-size: 34px;
  }

  .compact-products,
  .products-grid,
  .skeleton-grid {
    grid-template-columns: 1fr;
  }

  .service-strip {
    grid-template-columns: 1fr;
  }

  .service-item,
  .service-item:nth-child(2) {
    border-right: 0;
    border-bottom: 1px solid var(--pet-line);
  }

  .service-item:last-child {
    border-bottom: 0;
  }

  .care-story {
    margin-bottom: 100px;
  }

  .story-media {
    min-height: 320px;
  }
}

@media (max-width: 520px) {
  .section-header {
    display: grid;
  }

  .view-all {
    justify-self: start;
  }
}
</style>
