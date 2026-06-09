<script setup lang="ts">
import { ref, computed } from "vue";
import { useRouter, useRoute } from "vue-router";

const router = useRouter();
const route = useRoute();
const collapsed = ref(false);

const menuList = [
  {
    index: "/product",
    title: "商品中心",
    icon: "Goods",
    children: [
      { index: "/product/list", title: "商品列表", icon: "Grid" },
      { index: "/product/category", title: "分类管理", icon: "Menu" },
      { index: "/product/brand", title: "品牌管理", icon: "Medal" },
    ],
  },
  {
    index: "/order",
    title: "订单管理",
    icon: "Document",
  },
  {
    index: "/content",
    title: "内容运营",
    icon: "Picture",
    children: [{ index: "/content/banner", title: "轮播图管理", icon: "Film" }],
  },
  {
    index: "/customer",
    title: "客户服务",
    icon: "Service",
    children: [
      { index: "/user", title: "用户管理", icon: "User" },
      { index: "/address", title: "地址管理", icon: "Location" },
    ],
  },
];

const pageDescriptions: Record<string, string> = {
  "/product/list": "维护商城商品信息、价格、库存与上下架状态",
  "/product/category": "整理商品分类，让商城内容更清晰易找",
  "/product/brand": "管理入驻品牌及品牌基础资料",
  "/order": "查看订单信息并跟进订单状态",
  "/content/banner": "配置用户端首页展示的轮播内容",
  "/user": "维护商城用户账号、角色与使用状态",
  "/address": "集中查看和维护用户收货地址",
  "/profile": "管理当前管理员的个人资料与账号安全",
};

const activeMenu = computed(() => route.path);
const pageTitle = computed(() => String(route.meta?.title || "商品列表"));
const pageDescription = computed(
  () => pageDescriptions[route.path] || "宠物用品商城日常运营管理"
);

const defaultOpeneds = computed(() => {
  const path = route.path;
  const opens: string[] = [];
  if (path.startsWith("/product")) opens.push("/product");
  if (path.startsWith("/content")) opens.push("/content");
  if (path.startsWith("/user") || path.startsWith("/address")) {
    opens.push("/customer");
  }
  return opens;
});

const toggleCollapse = () => {
  collapsed.value = !collapsed.value;
};

const handleMenuClick = (index: string) => {
  router.push(index);
};

const handleLogout = () => {
  router.push("/login");
};
</script>

<template>
  <div class="admin-layout">
    <aside class="sidebar" :class="{ collapsed }">
      <div class="sidebar-logo">
        <div class="logo-mark" aria-hidden="true">
          <svg viewBox="0 0 48 48">
            <path
              d="M14 21c-4 0-7-3.2-7-7.2S9.5 7 13 7s6 3.2 6 7.2S17.5 21 14 21Zm20 0c-3.5 0-5-2.8-5-6.8S31.5 7 35 7s6 2.8 6 6.8S38 21 34 21ZM24 42c-8.2 0-14-4.8-14-10.6 0-4.1 3-6.2 6.2-8.4 2.4-1.6 3.5-4 7.8-4s5.4 2.4 7.8 4c3.2 2.2 6.2 4.3 6.2 8.4C38 37.2 32.2 42 24 42Z"
              fill="currentColor"
            />
          </svg>
        </div>
        <div v-show="!collapsed" class="brand-copy">
          <strong>PetMall</strong>
          <span>运营管理中心</span>
        </div>
      </div>

      <div v-show="!collapsed" class="menu-caption">商城管理</div>

      <el-menu
        :default-active="activeMenu"
        :default-openeds="defaultOpeneds"
        :collapse="collapsed"
        :collapse-transition="false"
        router
        class="sidebar-menu"
        @select="handleMenuClick"
      >
        <template v-for="menu in menuList" :key="menu.index">
          <el-sub-menu v-if="menu.children" :index="menu.index">
            <template #title>
              <el-icon><component :is="menu.icon" /></el-icon>
              <span>{{ menu.title }}</span>
            </template>
            <el-menu-item
              v-for="child in menu.children"
              :key="child.index"
              :index="child.index"
            >
              <el-icon><component :is="child.icon" /></el-icon>
              <span>{{ child.title }}</span>
            </el-menu-item>
          </el-sub-menu>
          <el-menu-item v-else :index="menu.index">
            <el-icon><component :is="menu.icon" /></el-icon>
            <template #title>{{ menu.title }}</template>
          </el-menu-item>
        </template>
      </el-menu>

      <div v-show="!collapsed" class="sidebar-footer">
        <span class="status-dot"></span>
        <div>
          <strong>系统运行正常</strong>
          <small>PetMall Admin</small>
        </div>
      </div>
    </aside>

    <div class="main-area">
      <header class="topbar">
        <div class="topbar-left">
          <button
            class="collapse-btn"
            type="button"
            aria-label="切换侧边栏"
            @click="toggleCollapse"
          >
            <el-icon :size="18">
              <Fold v-if="!collapsed" />
              <Expand v-else />
            </el-icon>
          </button>
          <div class="breadcrumb">
            <span>管理后台</span>
            <el-icon><ArrowRight /></el-icon>
            <strong>{{ pageTitle }}</strong>
          </div>
        </div>

        <div class="topbar-right">
          <div class="console-state">
            <span class="state-indicator"></span>
            <span>运营中</span>
          </div>
          <el-dropdown trigger="click">
            <div class="user-dropdown">
              <div class="avatar">管</div>
              <div class="user-copy">
                <strong>管理员</strong>
                <span>商城运营</span>
              </div>
              <el-icon class="dropdown-arrow"><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="router.push('/profile')">
                  <el-icon><UserFilled /></el-icon>个人中心
                </el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">
                  <el-icon><SwitchButton /></el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>

      <main class="content-area">
        <section class="page-heading">
          <div>
            <span class="page-eyebrow">PETMALL MANAGEMENT</span>
            <h1>{{ pageTitle }}</h1>
            <p>{{ pageDescription }}</p>
          </div>
          <div class="heading-mark" aria-hidden="true">
            <span></span>
            <span></span>
            <span></span>
          </div>
        </section>

        <RouterView />
      </main>
    </div>
  </div>
</template>

<style scoped>
.admin-layout {
  min-height: 100vh;
  background: var(--pm-bg);
}

.sidebar {
  position: fixed;
  inset: 0 auto 0 0;
  z-index: 100;
  display: flex;
  width: 248px;
  flex-direction: column;
  overflow: hidden;
  color: #dce3e9;
  background:
    radial-gradient(circle at 20% 0%, rgba(232, 121, 46, 0.16), transparent 27%),
    linear-gradient(180deg, var(--pm-sidebar) 0%, var(--pm-sidebar-deep) 100%);
  transition: width 0.28s cubic-bezier(0.16, 1, 0.3, 1);
}

.sidebar::after {
  position: absolute;
  right: 0;
  bottom: 0;
  width: 120px;
  height: 220px;
  border-radius: 999px 0 0 0;
  background: rgba(255, 255, 255, 0.018);
  content: "";
  pointer-events: none;
}

.sidebar.collapsed {
  width: 76px;
}

.sidebar-logo {
  display: flex;
  height: 84px;
  align-items: center;
  gap: 12px;
  padding: 0 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.07);
}

.logo-mark {
  display: grid;
  width: 38px;
  height: 38px;
  flex: 0 0 38px;
  place-items: center;
  border: 1px solid rgba(255, 255, 255, 0.14);
  border-radius: 12px;
  color: #fff;
  background: var(--pm-primary);
  box-shadow: 0 10px 24px rgba(232, 121, 46, 0.22);
}

.logo-mark svg {
  width: 22px;
  height: 22px;
}

.brand-copy {
  display: flex;
  min-width: 0;
  flex-direction: column;
  white-space: nowrap;
}

.brand-copy strong {
  color: #fff;
  font-size: 18px;
  font-weight: 750;
  letter-spacing: 0.03em;
}

.brand-copy span {
  color: #85929e;
  font-size: 11px;
  letter-spacing: 0.12em;
}

.menu-caption {
  padding: 24px 24px 10px;
  color: #71808d;
  font-size: 10px;
  font-weight: 700;
  letter-spacing: 0.18em;
}

.sidebar-menu {
  --el-menu-bg-color: transparent;
  --el-menu-text-color: #aeb9c2;
  --el-menu-hover-bg-color: rgba(255, 255, 255, 0.05);
  --el-menu-active-color: #fff;
  flex: 1;
  overflow-x: hidden;
  overflow-y: auto;
  border-right: 0 !important;
  background: transparent !important;
}

.sidebar-menu:not(.el-menu--collapse) {
  width: 248px;
}

.sidebar-menu.el-menu--collapse {
  width: 76px;
}

.sidebar-menu::-webkit-scrollbar {
  width: 4px;
}

.sidebar-menu::-webkit-scrollbar-thumb {
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.12);
}

:deep(.el-menu) {
  border-right: 0 !important;
  background: transparent !important;
}

:deep(.el-menu-item),
:deep(.el-sub-menu__title) {
  height: 46px;
  margin: 4px 12px;
  border-radius: 10px;
  color: #aeb9c2 !important;
  font-size: 13px;
  font-weight: 550;
  transition:
    color 0.2s ease,
    background-color 0.2s ease,
    transform 0.2s ease;
}

:deep(.el-menu-item:hover),
:deep(.el-sub-menu__title:hover) {
  color: #fff !important;
  background: rgba(255, 255, 255, 0.06) !important;
}

:deep(.el-menu-item.is-active) {
  color: #fff !important;
  background: rgba(232, 121, 46, 0.96) !important;
  box-shadow: 0 10px 22px rgba(232, 121, 46, 0.18);
}

:deep(.el-sub-menu .el-menu-item) {
  min-width: auto !important;
  height: 42px;
  padding-left: 49px !important;
  color: #8997a3 !important;
  font-size: 12px;
}

:deep(.el-sub-menu .el-menu-item.is-active) {
  color: #f6c6a5 !important;
  background: rgba(232, 121, 46, 0.1) !important;
  box-shadow: none;
}

:deep(.el-menu--collapse .el-menu-item),
:deep(.el-menu--collapse .el-sub-menu__title) {
  justify-content: center;
  padding: 0 !important;
}

:deep(.el-menu--collapse .el-icon) {
  margin: 0;
}

.sidebar-footer {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  gap: 10px;
  margin: 14px;
  padding: 14px;
  border: 1px solid rgba(255, 255, 255, 0.07);
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.035);
}

.status-dot,
.state-indicator {
  width: 7px;
  height: 7px;
  flex: 0 0 7px;
  border-radius: 50%;
  background: #59b985;
  box-shadow: 0 0 0 4px rgba(89, 185, 133, 0.12);
}

.sidebar-footer div {
  display: flex;
  min-width: 0;
  flex-direction: column;
}

.sidebar-footer strong {
  color: #dfe5ea;
  font-size: 11px;
  font-weight: 650;
}

.sidebar-footer small {
  color: #71808d;
  font-size: 10px;
}

.main-area {
  display: flex;
  min-height: 100vh;
  margin-left: 248px;
  flex-direction: column;
  transition: margin-left 0.28s cubic-bezier(0.16, 1, 0.3, 1);
}

.sidebar.collapsed + .main-area {
  margin-left: 76px;
}

.topbar {
  position: sticky;
  top: 0;
  z-index: 50;
  display: flex;
  height: 72px;
  align-items: center;
  justify-content: space-between;
  padding: 0 28px;
  border-bottom: 1px solid rgba(220, 225, 230, 0.8);
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(16px);
}

.topbar-left,
.topbar-right,
.breadcrumb,
.user-dropdown,
.console-state {
  display: flex;
  align-items: center;
}

.topbar-left {
  gap: 18px;
}

.collapse-btn {
  display: grid;
  width: 38px;
  height: 38px;
  padding: 0;
  place-items: center;
  border: 1px solid var(--pm-border);
  border-radius: 10px;
  color: var(--pm-text-secondary);
  background: #fff;
  cursor: pointer;
  transition:
    color 0.2s ease,
    border-color 0.2s ease,
    background-color 0.2s ease,
    transform 0.2s ease;
}

.collapse-btn:hover {
  border-color: var(--pm-primary-muted);
  color: var(--pm-primary);
  background: var(--pm-primary-soft);
}

.collapse-btn:active {
  transform: scale(0.96);
}

.breadcrumb {
  gap: 8px;
  color: var(--pm-text-muted);
  font-size: 13px;
}

.breadcrumb strong {
  color: var(--pm-text);
  font-weight: 650;
}

.breadcrumb .el-icon {
  font-size: 11px;
}

.topbar-right {
  gap: 18px;
}

.console-state {
  gap: 9px;
  padding: 7px 12px;
  border: 1px solid var(--pm-border);
  border-radius: 999px;
  color: var(--pm-text-secondary);
  background: #fff;
  font-size: 12px;
  font-weight: 600;
}

.user-dropdown {
  gap: 10px;
  padding: 5px 8px 5px 5px;
  border-radius: 12px;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.user-dropdown:hover {
  background: #f7f7f8;
}

.avatar {
  display: grid;
  width: 38px;
  height: 38px;
  place-items: center;
  border-radius: 11px;
  color: #fff;
  background: var(--pm-primary);
  font-size: 13px;
  font-weight: 700;
}

.user-copy {
  display: flex;
  min-width: 72px;
  flex-direction: column;
}

.user-copy strong {
  color: var(--pm-text);
  font-size: 13px;
  font-weight: 700;
}

.user-copy span {
  color: var(--pm-text-muted);
  font-size: 10px;
}

.dropdown-arrow {
  color: var(--pm-text-muted);
  font-size: 12px;
}

.content-area {
  width: 100%;
  max-width: 1680px;
  margin: 0 auto;
  padding: 30px 34px 42px;
}

.page-heading {
  position: relative;
  display: flex;
  min-height: 104px;
  align-items: flex-end;
  justify-content: space-between;
  margin-bottom: 22px;
  padding: 0 2px 20px;
  border-bottom: 1px solid var(--pm-border);
}

.page-eyebrow {
  display: block;
  margin-bottom: 5px;
  color: var(--pm-primary);
  font-size: 9px;
  font-weight: 800;
  letter-spacing: 0.2em;
}

.page-heading h1 {
  margin: 0;
  color: var(--pm-text);
  font-size: clamp(25px, 2.2vw, 34px);
  font-weight: 750;
  letter-spacing: -0.035em;
  line-height: 1.2;
}

.page-heading p {
  margin: 7px 0 0;
  color: var(--pm-text-secondary);
  font-size: 13px;
}

.heading-mark {
  display: flex;
  align-items: flex-end;
  gap: 5px;
  padding: 0 8px 3px 0;
}

.heading-mark span {
  display: block;
  width: 5px;
  border-radius: 999px;
  background: var(--pm-primary);
}

.heading-mark span:nth-child(1) {
  height: 16px;
  opacity: 0.32;
}

.heading-mark span:nth-child(2) {
  height: 28px;
  opacity: 0.62;
}

.heading-mark span:nth-child(3) {
  height: 42px;
}

@media (max-width: 900px) {
  .sidebar {
    width: 76px;
  }

  .sidebar .brand-copy,
  .sidebar .menu-caption,
  .sidebar .sidebar-footer {
    display: none;
  }

  .sidebar-logo {
    justify-content: center;
    padding: 0;
  }

  .sidebar-menu:not(.el-menu--collapse) {
    width: 76px;
  }

  :deep(.sidebar-menu:not(.el-menu--collapse) .el-menu-item),
  :deep(.sidebar-menu:not(.el-menu--collapse) .el-sub-menu__title) {
    justify-content: center;
    padding: 0 !important;
  }

  :deep(.sidebar-menu:not(.el-menu--collapse) span),
  :deep(.sidebar-menu:not(.el-menu--collapse) .el-sub-menu__icon-arrow) {
    display: none;
  }

  :deep(.sidebar-menu:not(.el-menu--collapse) .el-icon) {
    margin: 0;
  }

  .main-area,
  .sidebar.collapsed + .main-area {
    margin-left: 76px;
  }

  .content-area {
    padding: 24px 20px 34px;
  }
}

@media (max-width: 640px) {
  .topbar {
    height: 64px;
    padding: 0 14px;
  }

  .breadcrumb,
  .console-state,
  .user-copy,
  .dropdown-arrow {
    display: none;
  }

  .content-area {
    padding: 20px 14px 30px;
  }

  .page-heading {
    min-height: 88px;
    align-items: center;
  }

  .page-heading h1 {
    font-size: 24px;
  }

  .heading-mark {
    display: none;
  }
}
</style>
