<template>
  <header class="app-header">
    <div class="header-container">
      <router-link to="/" class="brand" aria-label="返回商城首页">
        <span class="brand-mark">
          <el-icon><Goods /></el-icon>
        </span>
        <span class="brand-copy">
          <strong>宠物用品商城</strong>
          <small>给陪伴更好的日常</small>
        </span>
      </router-link>

      <nav class="desktop-nav" aria-label="主导航">
        <router-link to="/">首页</router-link>
        <router-link to="/all-products">全部商品</router-link>
        <router-link to="/orders">我的订单</router-link>
      </nav>

      <div class="header-actions">
        <router-link to="/cart" class="icon-action" aria-label="购物车">
          <el-icon><ShoppingCart /></el-icon>
          <span>购物车</span>
        </router-link>

        <el-popover
          placement="bottom-end"
          :width="240"
          trigger="click"
          popper-class="user-popover"
        >
          <template #reference>
            <button class="profile-button" type="button" aria-label="用户菜单">
              <img :src="userInfo.avatar || defaultAvatar" alt="用户头像" />
              <span class="profile-copy">
                <small>欢迎回来</small>
                <strong>{{ userInfo.username || userInfo.name || "宠物家长" }}</strong>
              </span>
            </button>
          </template>

          <div class="user-dropdown">
            <div class="user-info">
              <img
                class="dropdown-avatar"
                :src="userInfo.avatar || defaultAvatar"
                alt="用户头像"
              />
              <div class="info-content">
                <p class="username">
                  {{ userInfo.username || userInfo.name || "宠物家长" }}
                </p>
                <p class="role-text">{{ getRoleText(userInfo.role) }}</p>
              </div>
            </div>
            <el-divider />
            <div class="delivery-note">
              <el-icon><Location /></el-icon>
              <span>默认配送城市：北京</span>
            </div>
            <el-button plain class="logout-btn" @click="handleLogout">
              <el-icon><SwitchButton /></el-icon>
              退出登录
            </el-button>
          </div>
        </el-popover>

        <button
          class="mobile-menu-button"
          type="button"
          :aria-expanded="mobileMenuOpen"
          aria-label="打开导航菜单"
          @click="mobileMenuOpen = !mobileMenuOpen"
        >
          <el-icon>
            <Close v-if="mobileMenuOpen" />
            <Menu v-else />
          </el-icon>
        </button>
      </div>
    </div>

    <transition name="mobile-nav">
      <div v-if="mobileMenuOpen" class="mobile-nav">
        <router-link to="/" @click="mobileMenuOpen = false">首页</router-link>
        <router-link to="/all-products" @click="mobileMenuOpen = false">
          全部商品
        </router-link>
        <router-link to="/orders" @click="mobileMenuOpen = false">
          我的订单
        </router-link>
        <router-link to="/cart" @click="mobileMenuOpen = false">
          购物车
        </router-link>
      </div>
    </transition>
  </header>
</template>

<script setup>
import { computed, onBeforeMount, ref, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import {
  Close,
  Goods,
  Location,
  Menu,
  ShoppingCart,
  SwitchButton,
} from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import { getUserInfo } from "@/api/user";
import { useUserStore } from "@/stores/user";

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();
const userInfo = computed(() => userStore.userInfo);
const mobileMenuOpen = ref(false);
const defaultAvatar = "https://api.dicebear.com/7.x/thumbs/svg?seed=pet-parent";

const getRoleText = (role) => {
  const roleMap = {
    0: "普通用户",
    1: "管理员",
  };
  return roleMap[role] || "商城会员";
};

const fetchUserInfo = async () => {
  try {
    const res = await getUserInfo();
    if (res.data) {
      const userData = res.data;
      userStore.setUserInfo({
        id: userData.id,
        name: userData.username,
        username: userData.username,
        role: userData.role,
        status: userData.status,
        token: userData.token,
        avatar: "",
        email: "",
        phone: "",
      });
    }
  } catch (error) {
    console.error("获取用户信息失败:", error);
  }
};

const handleLogout = () => {
  localStorage.removeItem("authentication");
  localStorage.removeItem("isLoggedIn");
  localStorage.removeItem("userInfo");
  userStore.clearUserInfo();
  ElMessage.success("已退出登录");
  router.push("/login");
};

onBeforeMount(() => {
  if (!localStorage.getItem("authentication")) return;

  const storedUserInfo = localStorage.getItem("userInfo");
  if (storedUserInfo) {
    try {
      const parsed = JSON.parse(storedUserInfo);
      if (parsed && (parsed.id || parsed.username)) {
        userStore.setUserInfo({
          id: parsed.id,
          name: parsed.username,
          username: parsed.username,
          role: parsed.role,
          status: parsed.status,
          token: parsed.token,
          avatar: "",
          email: "",
          phone: "",
        });
      }
    } catch (error) {
      console.error("解析 userInfo 失败:", error);
    }
  }

  fetchUserInfo();
});

watch(
  () => route.path,
  () => {
    mobileMenuOpen.value = false;
    if (
      localStorage.getItem("authentication") &&
      (!userInfo.value.id || !userInfo.value.username)
    ) {
      fetchUserInfo();
    }
  }
);
</script>

<style scoped>
.app-header {
  position: sticky;
  top: 0;
  z-index: 100;
  background: rgba(255, 253, 248, 0.9);
  border-bottom: 1px solid var(--pet-line);
  backdrop-filter: blur(18px);
}

.header-container {
  width: var(--pet-container);
  min-height: 84px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: auto 1fr auto;
  align-items: center;
  gap: 30px;
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
  text-decoration: none;
  white-space: nowrap;
}

.brand-mark {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  display: grid;
  place-items: center;
  color: #fff;
  background: var(--pet-accent);
  box-shadow: 0 10px 24px rgba(217, 111, 61, 0.24);
}

.brand-mark .el-icon {
  font-size: 22px;
}

.brand-copy {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.brand-copy strong {
  font-size: 17px;
  line-height: 1.2;
}

.brand-copy small {
  color: var(--pet-muted);
  font-size: 11px;
}

.desktop-nav {
  display: flex;
  align-items: center;
  gap: 24px;
  white-space: nowrap;
}

.desktop-nav a {
  position: relative;
  padding: 30px 0 27px;
  color: var(--pet-muted);
  font-size: 14px;
  font-weight: 700;
  text-decoration: none;
}

.desktop-nav a::after {
  content: "";
  position: absolute;
  left: 0;
  right: 0;
  bottom: 18px;
  height: 2px;
  background: var(--pet-accent);
  transform: scaleX(0);
  transition: transform 220ms ease;
}

.desktop-nav a:hover,
.desktop-nav a.router-link-active {
  color: var(--pet-ink);
}

.desktop-nav a:hover::after,
.desktop-nav a.router-link-active::after {
  transform: scaleX(1);
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.icon-action {
  min-height: 42px;
  display: inline-flex;
  align-items: center;
  gap: 7px;
  padding: 0 12px;
  border: 1px solid var(--pet-line);
  border-radius: var(--pet-radius);
  background: #fff;
  color: var(--pet-ink);
  text-decoration: none;
  font-size: 13px;
  font-weight: 700;
}

.profile-button {
  max-width: 150px;
  display: flex;
  align-items: center;
  gap: 9px;
  padding: 0;
  border: 0;
  background: transparent;
  color: var(--pet-ink);
  cursor: pointer;
}

.profile-button img {
  width: 40px;
  height: 40px;
  flex-shrink: 0;
  border: 2px solid #fff;
  border-radius: 50%;
  box-shadow: 0 5px 16px rgba(63, 47, 32, 0.14);
}

.profile-copy {
  min-width: 0;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.profile-copy small {
  color: var(--pet-muted);
  font-size: 10px;
}

.profile-copy strong {
  max-width: 92px;
  overflow: hidden;
  font-size: 12px;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.mobile-menu-button {
  display: none;
  width: 42px;
  height: 42px;
  border: 1px solid var(--pet-line);
  border-radius: var(--pet-radius);
  background: #fff;
  color: var(--pet-ink);
  cursor: pointer;
}

.mobile-nav {
  width: var(--pet-container);
  margin: 0 auto;
  display: none;
  padding: 0 0 18px;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 8px;
}

.mobile-nav a {
  padding: 12px 14px;
  border: 1px solid var(--pet-line);
  border-radius: var(--pet-radius);
  background: #fff;
  text-decoration: none;
  font-size: 14px;
  font-weight: 700;
}

.mobile-nav-enter-active,
.mobile-nav-leave-active {
  transition: opacity 180ms ease, transform 180ms ease;
}

.mobile-nav-enter-from,
.mobile-nav-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}

@media (max-width: 1180px) {
  .header-container {
    grid-template-columns: 1fr auto;
  }

  .desktop-nav {
    display: none;
  }

  .mobile-menu-button {
    display: grid;
    place-items: center;
  }

  .mobile-nav {
    display: grid;
  }
}

@media (max-width: 820px) {
  .header-container {
    min-height: 72px;
    grid-template-columns: 1fr auto;
    gap: 16px;
  }

  .icon-action,
  .profile-button {
    display: none;
  }

  .brand-copy small {
    display: none;
  }
}

@media (max-width: 480px) {
  .brand-copy strong {
    font-size: 15px;
  }

  .brand-mark {
    width: 38px;
    height: 38px;
  }

}
</style>

<style>
.user-popover {
  padding: 0 !important;
  border: 1px solid var(--pet-line) !important;
  border-radius: 8px !important;
  box-shadow: var(--pet-shadow) !important;
}

.user-dropdown {
  padding: 18px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.dropdown-avatar {
  width: 52px;
  height: 52px;
  border-radius: 50%;
  background: var(--pet-accent-soft);
}

.info-content {
  min-width: 0;
}

.info-content p {
  margin: 0;
}

.info-content .username {
  color: var(--pet-ink);
  font-weight: 800;
}

.info-content .role-text {
  margin-top: 3px;
  color: var(--pet-muted);
  font-size: 12px;
}

.delivery-note {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 14px;
  color: var(--pet-muted);
  font-size: 13px;
}

.logout-btn {
  width: 100%;
}
</style>
