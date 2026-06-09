<script setup lang="ts">
import { reactive, ref } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { userApi } from "@/api";

const router = useRouter();

const registerForm = reactive({
  username: "",
  password: "",
  confirmPassword: "",
  agree: false,
});

const loading = ref(false);

const handleRegister = async () => {
  if (!registerForm.username) {
    ElMessage.warning("请输入用户名");
    return;
  }
  if (!registerForm.password) {
    ElMessage.warning("请输入密码");
    return;
  }
  if (!registerForm.confirmPassword) {
    ElMessage.warning("请再次输入密码");
    return;
  }

  if (registerForm.password !== registerForm.confirmPassword) {
    ElMessage.error("两次输入的密码不一致！");
    return;
  }

  loading.value = true;
  try {
    const res = await userApi.register({
      username: registerForm.username,
      password: registerForm.password,
    });

    if (res.code === 200) {
      ElMessage.success("注册成功！即将跳转到登录页...");
      setTimeout(() => {
        router.push("/login");
      }, 1500);
    } else {
      ElMessage.error(res.message || "注册失败");
    }
  } catch (error: any) {
    console.error("注册失败:", error);
  } finally {
    loading.value = false;
  }
};
</script>

<template>
  <div class="register-container">
    <div class="register-left">
      <div class="brand-content">
        <div class="brand-icon">
          <svg viewBox="0 0 100 100" xmlns="http://www.w3.org/2000/svg">
            <circle cx="50" cy="50" r="45" fill="#FFF3E0" />
            <ellipse cx="35" cy="40" rx="8" ry="10" fill="#5D4037" />
            <ellipse cx="65" cy="40" rx="8" ry="10" fill="#5D4037" />
            <ellipse cx="50" cy="58" rx="6" ry="4" fill="#5D4037" />
            <path
              d="M 30 55 Q 50 70 70 55"
              stroke="#5D4037"
              stroke-width="3"
              fill="none"
              stroke-linecap="round"
            />
            <path d="M 20 25 L 28 35 L 18 38 Z" fill="#E67E22" />
            <path d="M 80 25 L 72 35 L 82 38 Z" fill="#E67E22" />
          </svg>
        </div>
        <h1 class="brand-title">PetShop</h1>
        <h2 class="brand-subtitle">宠物商城管理系统</h2>
        <p class="brand-desc">加入我们，开启您的宠物商城管理之旅</p>
        <div class="brand-features">
          <div class="feature-item">
            <el-icon class="feature-icon"><Lock /></el-icon>
            <span>安全可靠</span>
          </div>
          <div class="feature-item">
            <el-icon class="feature-icon"><Lightning /></el-icon>
            <span>快速注册</span>
          </div>
          <div class="feature-item">
            <el-icon class="feature-icon"><Aim /></el-icon>
            <span>专业服务</span>
          </div>
          <div class="feature-item">
            <el-icon class="feature-icon"><Briefcase /></el-icon>
            <span>高效管理</span>
          </div>
        </div>
      </div>
    </div>

    <div class="register-right">
      <div class="register-form-wrapper">
        <div class="form-header">
          <h2>创建账号</h2>
          <p>填写以下信息完成注册</p>
        </div>

        <form @submit.prevent="handleRegister" class="register-form">
          <div class="form-item">
            <label for="username">用户名</label>
            <div class="input-wrapper">
              <span class="input-icon">
                <svg
                  viewBox="0 0 24 24"
                  width="18"
                  height="18"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="2"
                >
                  <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                  <circle cx="12" cy="7" r="4"></circle>
                </svg>
              </span>
              <input
                id="username"
                v-model="registerForm.username"
                type="text"
                placeholder="请输入用户名"
                autocomplete="username"
              />
            </div>
          </div>

          <div class="form-item">
            <label for="password">密码</label>
            <div class="input-wrapper">
              <span class="input-icon">
                <svg
                  viewBox="0 0 24 24"
                  width="18"
                  height="18"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="2"
                >
                  <rect
                    x="3"
                    y="11"
                    width="18"
                    height="11"
                    rx="2"
                    ry="2"
                  ></rect>
                  <path d="M7 11V7a5 5 0 0 1 10 0v4"></path>
                </svg>
              </span>
              <input
                id="password"
                v-model="registerForm.password"
                type="password"
                placeholder="请输入密码"
                autocomplete="new-password"
              />
            </div>
          </div>

          <div class="form-item">
            <label for="confirmPassword">确认密码</label>
            <div class="input-wrapper">
              <span class="input-icon">
                <svg
                  viewBox="0 0 24 24"
                  width="18"
                  height="18"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="2"
                >
                  <rect
                    x="3"
                    y="11"
                    width="18"
                    height="11"
                    rx="2"
                    ry="2"
                  ></rect>
                  <path d="M7 11V7a5 5 0 0 1 10 0v4"></path>
                </svg>
              </span>
              <input
                id="confirmPassword"
                v-model="registerForm.confirmPassword"
                type="password"
                placeholder="请再次输入密码"
                autocomplete="new-password"
              />
            </div>
          </div>

          <div class="form-options">
            <label class="agree-term">
              <input type="checkbox" v-model="registerForm.agree" />
              <span
                >我已阅读并同意<a href="javascript:;" class="term-link"
                  >服务条款</a
                ></span
              >
            </label>
          </div>

          <button
            type="submit"
            class="register-btn-submit"
            :class="{ loading }"
            :disabled="loading"
          >
            <span v-if="!loading">注 册</span>
            <span v-else class="loading-spinner"></span>
          </button>
        </form>

        <div class="login-link">
          <span>已有账号？</span>
          <a
            href="javascript:;"
            @click="router.push('/login')"
            class="login-btn-text"
            >立即登录</a
          >
        </div>

        <div class="form-footer">
          <p>© 2026 PetShop 宠物商城 · 管理后台</p>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.register-container {
  display: flex;
  min-height: 100vh;
  width: 100%;
}

.register-left {
  flex: 1;
  background: linear-gradient(135deg, #2c3e50 0%, #1a252f 50%, #0d1519 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px;
  position: relative;
  overflow: hidden;
}

.register-left::before {
  content: "";
  position: absolute;
  top: -50%;
  right: -20%;
  width: 80%;
  height: 200%;
  background: radial-gradient(
    ellipse,
    rgba(230, 126, 34, 0.15) 0%,
    transparent 70%
  );
  pointer-events: none;
}

.register-left::after {
  content: "";
  position: absolute;
  bottom: -30%;
  left: -10%;
  width: 60%;
  height: 150%;
  background: radial-gradient(
    ellipse,
    rgba(230, 126, 34, 0.1) 0%,
    transparent 70%
  );
  pointer-events: none;
}

.brand-content {
  position: relative;
  z-index: 1;
  color: #fff;
  text-align: center;
}

.brand-icon {
  width: 120px;
  height: 120px;
  margin: 0 auto 24px;
  animation: float 3s ease-in-out infinite;
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

.brand-title {
  font-size: 48px;
  font-weight: 700;
  margin: 0 0 8px;
  background: linear-gradient(135deg, #e67e22, #f39c12);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: 2px;
}

.brand-subtitle {
  font-size: 20px;
  font-weight: 400;
  color: rgba(255, 255, 255, 0.85);
  margin: 0 0 16px;
  letter-spacing: 4px;
}

.brand-desc {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.6);
  margin: 0 0 48px;
  line-height: 1.8;
}

.brand-features {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  max-width: 320px;
  margin: 0 auto;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 14px 18px;
  background: rgba(255, 255, 255, 0.08);
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;
  cursor: default;
}

.feature-item:hover {
  background: rgba(230, 126, 34, 0.15);
  border-color: rgba(230, 126, 34, 0.3);
  transform: translateY(-2px);
}

.feature-icon {
  color: #f2a46d;
  font-size: 19px;
}

.feature-item span:last-child {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.9);
}

.register-right {
  flex: 0 0 520px;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px;
}

.register-form-wrapper {
  width: 100%;
  max-width: 380px;
}

.form-header {
  margin-bottom: 32px;
}

.form-header h2 {
  font-size: 28px;
  font-weight: 700;
  color: #2c3e50;
  margin: 0 0 8px;
}

.form-header p {
  font-size: 14px;
  color: #95a5a6;
  margin: 0;
}

.register-form {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.form-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-item label {
  font-size: 14px;
  font-weight: 600;
  color: #34495e;
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.input-icon {
  position: absolute;
  left: 14px;
  color: #bdc3c7;
  display: flex;
  align-items: center;
  transition: color 0.3s;
}

.input-wrapper input {
  width: 100%;
  padding: 12px 14px 12px 44px;
  border: 2px solid #ecf0f1;
  border-radius: 12px;
  font-size: 15px;
  color: #2c3e50;
  background: #f8f9fa;
  transition: all 0.3s ease;
  outline: none;
  box-sizing: border-box;
}

.input-wrapper input::placeholder {
  color: #bdc3c7;
}

.input-wrapper input:focus {
  border-color: #e67e22;
  background: #fff;
  box-shadow: 0 0 0 4px rgba(230, 126, 34, 0.1);
}

.input-wrapper input:focus ~ .input-icon {
  color: #e67e22;
}

.form-options {
  display: flex;
  justify-content: flex-start;
  align-items: center;
}

.agree-term {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  font-size: 13px;
  color: #7f8c8d;
}

.agree-term input[type="checkbox"] {
  width: 16px;
  height: 16px;
  accent-color: #e67e22;
  cursor: pointer;
}

.term-link {
  color: #e67e22;
  text-decoration: none;
}

.term-link:hover {
  opacity: 0.8;
}

.register-btn-submit {
  width: 100%;
  padding: 14px;
  background: linear-gradient(135deg, #e67e22, #d35400);
  color: #fff;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 4px;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  margin-top: 4px;
}

.register-btn-submit::before {
  content: "";
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(
    90deg,
    transparent,
    rgba(255, 255, 255, 0.2),
    transparent
  );
  transition: left 0.5s;
}

.register-btn-submit:hover:not(:disabled)::before {
  left: 100%;
}

.register-btn-submit:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(230, 126, 34, 0.4);
}

.register-btn-submit:active:not(:disabled) {
  transform: translateY(0);
}

.register-btn-submit:disabled {
  opacity: 0.8;
  cursor: not-allowed;
}

.loading-spinner {
  display: inline-block;
  width: 20px;
  height: 20px;
  border: 3px solid rgba(255, 255, 255, 0.3);
  border-top-color: #fff;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.login-link {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
  margin-top: 24px;
  font-size: 14px;
  color: #7f8c8d;
}

.login-btn-text {
  color: #e67e22;
  text-decoration: none;
  font-weight: 600;
  transition: all 0.3s ease;
}

.login-btn-text:hover {
  color: #d35400;
  transform: translateX(2px);
}

.form-footer {
  text-align: center;
  margin-top: 32px;
}

.form-footer p {
  font-size: 12px;
  color: #bdc3c7;
  margin: 0;
}

@media (max-width: 1024px) {
  .register-left {
    display: none;
  }

  .register-right {
    flex: 1;
  }
}

@media (max-width: 480px) {
  .register-right {
    padding: 40px 24px;
  }

  .form-header h2 {
    font-size: 24px;
  }
}
</style>
