<template>
  <footer class="app-footer">
    <div class="footer-inner pet-page-shell">
      <div class="footer-brand">
        <span class="brand-mark">
          <el-icon><Goods /></el-icon>
        </span>
        <div>
          <strong>宠物用品商城</strong>
          <p>认真选择每一件进入宠物日常生活的用品。</p>
        </div>
      </div>

      <nav class="footer-links" aria-label="页脚导航">
        <router-link to="/">商城首页</router-link>
        <router-link to="/all-products">全部商品</router-link>
        <router-link to="/orders">订单中心</router-link>
        <router-link to="/cart">购物车</router-link>
      </nav>

      <div class="footer-service">
        <span>客服热线</span>
        <strong>020 520 1314</strong>
        <small>每日 09:00 - 22:00</small>
      </div>
    </div>

    <div class="footer-bottom pet-page-shell">
      <span>Copyright © 2026 宠物用品商城</span>
      <span>正品保障 · 安心支付 · 售后支持</span>
    </div>
  </footer>
</template>

<script setup>
import { Goods } from "@element-plus/icons-vue";
</script>

<style scoped>
.app-footer {
  margin-top: 0;
  padding: 72px 0 24px;
  background: #292720;
  color: #fff;
}

.footer-inner {
  display: grid;
  grid-template-columns: minmax(320px, 1.4fr) 0.8fr auto;
  gap: 70px;
  align-items: start;
  padding-bottom: 54px;
}

.footer-brand {
  display: flex;
  align-items: flex-start;
  gap: 16px;
}

.brand-mark {
  width: 48px;
  height: 48px;
  flex-shrink: 0;
  display: grid;
  place-items: center;
  border-radius: 50%;
  background: var(--pet-accent);
  color: #fff;
}

.footer-brand strong {
  font-size: 22px;
}

.footer-brand p {
  max-width: 38ch;
  margin: 9px 0 0;
  color: rgba(255, 255, 255, 0.58);
  font-size: 13px;
  line-height: 1.7;
}

.footer-links {
  display: grid;
  grid-template-columns: repeat(2, minmax(90px, 1fr));
  gap: 16px 28px;
}

.footer-links a {
  color: rgba(255, 255, 255, 0.72);
  font-size: 13px;
  font-weight: 700;
  text-decoration: none;
}

.footer-links a:hover {
  color: #fff;
}

.footer-service {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.footer-service span,
.footer-service small {
  color: rgba(255, 255, 255, 0.5);
  font-size: 11px;
}

.footer-service strong {
  margin: 8px 0;
  color: #f1b38f;
  font-size: 22px;
}

.footer-bottom {
  display: flex;
  justify-content: space-between;
  gap: 20px;
  padding-top: 22px;
  border-top: 1px solid rgba(255, 255, 255, 0.12);
  color: rgba(255, 255, 255, 0.42);
  font-size: 11px;
}

@media (max-width: 860px) {
  .footer-inner {
    grid-template-columns: 1fr 1fr;
    gap: 42px;
  }

  .footer-service {
    align-items: flex-start;
  }
}

@media (max-width: 620px) {
  .footer-inner {
    grid-template-columns: 1fr;
  }

  .footer-bottom {
    flex-direction: column;
  }
}
</style>
