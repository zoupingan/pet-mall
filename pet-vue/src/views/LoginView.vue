<script setup lang="ts">
import { reactive, ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { userApi } from "@/api";
import { tokenManager } from "@/utils/token";

const router = useRouter();

const loginForm = reactive({
  username: "",
  password: "",
  remember: false,
});

const loading = ref(false);

onMounted(() => {
  const savedUsername = tokenManager.getUsername();
  if (savedUsername) {
    loginForm.username = savedUsername;
    loginForm.remember = true;
  }
});

const handleLogin = async () => {
  if (!loginForm.username) {
    ElMessage.warning("请输入用户名");
    return;
  }
  if (!loginForm.password) {
    ElMessage.warning("请输入密码");
    return;
  }

  loading.value = true;

  try {
    const res = await userApi.login({
      username: loginForm.username,
      password: loginForm.password,
    });

    if (res.code === 200 && res.data) {
      const token = typeof res.data === 'string' ? res.data : (res.data as any).token;

      if (!token) {
        ElMessage.error("登录失败：未获取到token");
        return;
      }

      tokenManager.setToken(token);

      if (loginForm.remember) {
        tokenManager.setUsername(loginForm.username);
      } else {
        tokenManager.removeUsername();
      }

      ElMessage.success("登录成功！");

      router.push("/");
    } else {
      ElMessage.error(res.message || res.msg || "登录失败，请检查用户名和密码");
    }
  } catch (error: any) {
    console.error("登录失败:", error);
    ElMessage.error(
      error.response?.data?.message || error.message || "登录失败"
    );
  } finally {
    loading.value = false;
  }
};
</script>

<template>
  <div class="login-container">
    <div class="login-left">
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
        <p class="brand-desc">高效管理 · 精准运营 · 用心服务每一位宠物主人</p>
        <div class="brand-features">
          <div class="feature-item">
            <el-icon class="feature-icon"><Goods /></el-icon>
            <span>商品管理</span>
          </div>
          <div class="feature-item">
            <el-icon class="feature-icon"><Document /></el-icon>
            <span>订单处理</span>
          </div>
          <div class="feature-item">
            <el-icon class="feature-icon"><User /></el-icon>
            <span>用户中心</span>
          </div>
          <div class="feature-item">
            <el-icon class="feature-icon"><DataLine /></el-icon>
            <span>数据统计</span>
          </div>
        </div>
      </div>
    </div>

    <div class="login-right">
      <div class="login-form-wrapper">
        <div class="form-header">
          <h2>欢迎回来</h2>
          <p>请登录您的管理员账号</p>
        </div>

        <form @submit.prevent="handleLogin" class="login-form">
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
                v-model="loginForm.username"
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
                v-model="loginForm.password"
                type="password"
                placeholder="请输入密码"
                autocomplete="current-password"
              />
            </div>
          </div>

          <div class="form-options">
            <label class="remember-me">
              <input type="checkbox" v-model="loginForm.remember" />
              <span>记住密码</span>
            </label>
            <a href="javascript:;" class="forgot-password">忘记密码？</a>
          </div>

          <button
            type="submit"
            class="login-btn"
            :class="{ loading }"
            :disabled="loading"
          >
            <span v-if="!loading">登 录</span>
            <span v-else class="loading-spinner"></span>
          </button>

          <div class="register-link">
            <span>还没有账号？</span>
            <a
              href="javascript:;"
              @click="router.push('/register')"
              class="register-btn"
              >立即注册</a
            >
          </div>
        </form>

        <div class="form-footer">
          <p>© 2026 PetShop 宠物商城 · 管理后台</p>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.login-container {
  display: flex;
  min-height: 100vh;
  width: 100%;
}

.login-left {
  flex: 1;
  background: linear-gradient(135deg, #2c3e50 0%, #1a252f 50%, #0d1519 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px;
  position: relative;
  overflow: hidden;
}

.login-left::before {
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

.login-left::after {
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

.login-right {
  flex: 0 0 520px;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px;
}

.login-form-wrapper {
  width: 100%;
  max-width: 380px;
}

.form-header {
  margin-bottom: 40px;
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

.login-form {
  display: flex;
  flex-direction: column;
  gap: 24px;
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
  padding: 14px 14px 14px 44px;
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

.input-wrapper input:focus + .input-icon,
.input-wrapper input:focus ~ .input-icon {
  color: #e67e22;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.remember-me {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  font-size: 14px;
  color: #7f8c8d;
}

.remember-me input[type="checkbox"] {
  width: 16px;
  height: 16px;
  accent-color: #e67e22;
  cursor: pointer;
}

.forgot-password {
  font-size: 14px;
  color: #e67e22;
  text-decoration: none;
  transition: opacity 0.3s;
}

.forgot-password:hover {
  opacity: 0.8;
}

.login-btn {
  width: 100%;
  padding: 16px;
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
  margin-top: 8px;
}

.login-btn::before {
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

.login-btn:hover:not(:disabled)::before {
  left: 100%;
}

.login-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(230, 126, 34, 0.4);
}

.login-btn:active:not(:disabled) {
  transform: translateY(0);
}

.login-btn:disabled {
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

.form-footer {
  text-align: center;
  margin-top: 40px;
}

.register-link {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
  margin-top: 20px;
  font-size: 14px;
  color: #7f8c8d;
}

.register-btn {
  color: #e67e22;
  text-decoration: none;
  font-weight: 600;
  transition: all 0.3s ease;
}

.register-btn:hover {
  color: #d35400;
  transform: translateX(2px);
}

.form-footer p {
  font-size: 12px;
  color: #bdc3c7;
  margin: 0;
}

@media (max-width: 1024px) {
  .login-left {
    display: none;
  }

  .login-right {
    flex: 1;
  }
}

@media (max-width: 480px) {
  .login-right {
    padding: 40px 24px;
  }

  .form-header h2 {
    font-size: 24px;
  }
}
</style>
