<template>
  <div class="login-page">
    <div class="login-container">
      <div class="login-left">
        <div class="illustration-wrapper">
          <div class="floating-elements">
            <div class="paw paw-1">🐾</div>
            <div class="paw paw-2">🐾</div>
            <div class="bone bone-1">🦴</div>
            <div class="heart heart-1">❤️</div>
            <div class="star star-1">⭐</div>
          </div>

          <div class="main-character">
            <div class="cat-container">
              <div class="cat-body"></div>
              <div class="cat-head">
                <div class="ear left"></div>
                <div class="ear right"></div>
                <div class="face">
                  <div class="eyes">
                    <div class="eye left">
                      <div class="pupil"></div>
                    </div>
                    <div class="eye right">
                      <div class="pupil"></div>
                    </div>
                  </div>
                  <div class="nose"></div>
                  <div class="mouth"></div>
                  <div class="whiskers left">
                    <div class="whisker"></div>
                    <div class="whisker"></div>
                    <div class="whisker"></div>
                  </div>
                  <div class="whiskers right">
                    <div class="whisker"></div>
                    <div class="whisker"></div>
                    <div class="whisker"></div>
                  </div>
                </div>
              </div>
              <div class="cat-tail"></div>
              <div class="food-bowl">
                <div class="bowl"></div>
                <div class="food"></div>
              </div>
            </div>
          </div>

          <div class="welcome-text">
            <h2>欢迎来到</h2>
            <h1>宠物用品商城 🐱</h1>
            <p>为您的爱宠提供最优质的产品和服务</p>
          </div>
        </div>
      </div>

      <div class="login-right">
        <div class="login-form-container">
          <div class="form-header">
            <h2>账号登录</h2>
            <p>Login to your account</p>
          </div>

          <el-tabs v-model="activeTab" class="login-tabs" stretch>
            <el-tab-pane label="密码登录" name="password">
              <el-form
                ref="passwordFormRef"
                :model="passwordForm"
                :rules="passwordRules"
                @submit.prevent="handlePasswordLogin"
              >
                <el-form-item prop="username">
                  <el-input
                    v-model="passwordForm.username"
                    placeholder="请输入手机号/邮箱"
                    size="large"
                    :prefix-icon="User"
                  />
                </el-form-item>

                <el-form-item prop="password">
                  <el-input
                    v-model="passwordForm.password"
                    type="password"
                    placeholder="请输入密码"
                    size="large"
                    :prefix-icon="Lock"
                    show-password
                  />
                </el-form-item>

                <div class="form-options">
                  <el-checkbox v-model="rememberMe">记住我</el-checkbox>
                  <a href="#" class="forgot-link">忘记密码？</a>
                </div>

                <el-form-item>
                  <el-button
                    type="primary"
                    size="large"
                    class="login-btn"
                    :loading="loading"
                    @click="handlePasswordLogin"
                  >
                    登 录
                  </el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>

            <el-tab-pane label="验证码登录" name="code">
              <el-form
                ref="codeFormRef"
                :model="codeForm"
                :rules="codeRules"
                @submit.prevent="handleCodeLogin"
              >
                <el-form-item prop="phone">
                  <el-input
                    v-model="codeForm.phone"
                    placeholder="请输入手机号"
                    size="large"
                    :prefix-icon="Phone"
                  />
                </el-form-item>

                <el-form-item prop="verifyCode">
                  <div class="code-input-wrapper">
                    <el-input
                      v-model="codeForm.verifyCode"
                      placeholder="请输入验证码"
                      size="large"
                      :prefix-icon="Message"
                    />
                    <el-button
                      type="primary"
                      plain
                      size="large"
                      class="send-code-btn"
                      :disabled="countdown > 0"
                      @click="sendVerifyCode"
                    >
                      {{ countdown > 0 ? `${countdown}s` : "获取验证码" }}
                    </el-button>
                  </div>
                </el-form-item>

                <el-form-item>
                  <el-button
                    type="warning"
                    size="large"
                    class="login-btn code-btn"
                    :loading="loading"
                    @click="handleCodeLogin"
                  >
                    登 录
                  </el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>
          </el-tabs>

          <div class="divider">
            <span>其他登录方式</span>
          </div>

          <div class="social-login">
            <div class="social-btn wechat" title="微信登录">
              <svg viewBox="0 0 24 24" fill="#07C160">
                <path
                  d="M9.5 4C5.36 4 2 6.69 2 10c0 1.89 1.08 3.56 2.78 4.66L4 17l2.5-1.5c.89.31 1.87.5 2.91.5.37 0 .73-.03 1.09-.08C10.17 14.86 10 13.95 10 13c0-3.31 3.13-6 7-6 .34 0 .67.03 1 .08C17.44 5.56 14.31 4 11 4H9.5z"
                />
                <path
                  d="M19 9c-3.31 0-6 2.24-6 5s2.69 5 6 5c.67 0 1.31-.1 1.91-.27L23 20l-1.22-2.45C22.85 16.43 23.5 15.28 23.5 14c0-2.76-2.01-5-4.5-5z"
                />
              </svg>
            </div>
            <div class="social-btn alipay" title="支付宝登录">
              <svg viewBox="0 0 24 24" fill="#1677FF">
                <path
                  d="M21.422 15.358c-3.32-1.316-5.393-2.878-6.047-4.02.654-.47 1.058-.986 1.058-1.77 0-1.316-1.186-2.382-2.64-2.382S11.153 8.252 11.153 9.568c0 .784.404 1.3 1.058 1.77-.654 1.142-2.727 2.704-6.047 4.02L4 16.512c0 2.502 3.51 4.488 7.793 4.488s7.793-1.986 7.793-4.488l1.836-1.154z"
                />
              </svg>
            </div>
            <div class="social-btn qq" title="QQ登录">
              <svg viewBox="0 0 24 24" fill="#12B7F5">
                <path
                  d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 18c-4.41 0-8-3.59-8-8s3.59-8 8-8 8 3.59 8 8-3.59 8-8 8z"
                />
                <circle cx="9" cy="10" r="1.5" />
                <circle cx="15" cy="10" r="1.5" />
                <path
                  d="M12 17.5c-2.33 0-4.31-1.46-5.11-3.5h10.22c-.8 2.04-2.78 3.5-5.11 3.5z"
                />
              </svg>
            </div>
          </div>

          <div class="register-link">
            还没有账号？
            <router-link to="/register" class="link">立即注册 →</router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onUnmounted } from "vue";
import { useRouter } from "vue-router";
import { User, Lock, Phone, Message } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import { useUserStore } from "../stores/user";
import { login } from "../api/user";

const router = useRouter();
const userStore = useUserStore();

const activeTab = ref("password");
const loading = ref(false);
const rememberMe = ref(false);
const countdown = ref(0);
let timer = null;

const passwordFormRef = ref(null);
const codeFormRef = ref(null);

const passwordForm = reactive({
  username: "",
  password: "",
});

const codeForm = reactive({
  phone: "",
  verifyCode: "",
});

const passwordRules = {
  username: [
    { required: true, message: "请输入手机号或邮箱", trigger: "blur" },
  ],
  password: [
    { required: true, message: "请输入密码", trigger: "blur" },
    { min: 6, message: "密码长度不能少于6位", trigger: "blur" },
  ],
};

const codeRules = {
  phone: [
    { required: true, message: "请输入手机号", trigger: "blur" },
    {
      pattern: /^1[3-9]\d{9}$/,
      message: "请输入正确的手机号",
      trigger: "blur",
    },
  ],
  verifyCode: [
    { required: true, message: "请输入验证码", trigger: "blur" },
    { len: 6, message: "验证码为6位数字", trigger: "blur" },
  ],
};

function handlePasswordLogin() {
  if (!passwordFormRef.value) return;

  passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true;
      try {
        const res = await login({
          username: passwordForm.username,
          password: passwordForm.password,
        });

        if (res.data && res.data.token) {
          localStorage.setItem("authentication", res.data.token);
          localStorage.setItem("isLoggedIn", "true");
          localStorage.setItem("userInfo", JSON.stringify(res.data));

          userStore.setUserInfo({
            name: res.data.username || passwordForm.username,
            email: "",
            avatar: "",
          });

          ElMessage({
            message: "登录成功！正在跳转到商城首页... 🎉",
            type: "success",
            duration: 1500,
            showClose: true,
          });

          setTimeout(() => {
            router.replace("/");
          }, 500);
        }
      } catch (error) {
        console.error("登录失败:", error);
        ElMessage.error(error.message || "登录失败，请重试");
      } finally {
        loading.value = false;
      }
    }
  });
}

function handleCodeLogin() {
  if (!codeFormRef.value) return;

  codeFormRef.value.validate((valid) => {
    if (valid) {
      loading.value = true;
      setTimeout(() => {
        userStore.setUserInfo({
          name: codeForm.phone,
          email: "",
          avatar: "",
        });
        ElMessage({
          message: "登录成功！正在跳转到商城首页... 🎉",
          type: "success",
          duration: 1500,
          showClose: true,
        });
        loading.value = false;
        setTimeout(() => {
          router.replace("/");
        }, 500);
      }, 1000);
    }
  });
}

function sendVerifyCode() {
  if (!codeForm.phone) {
    ElMessage.warning("请先输入手机号");
    return;
  }

  const phoneReg = /^1[3-9]\d{9}$/;
  if (!phoneReg.test(codeForm.phone)) {
    ElMessage.error("请输入正确的手机号");
    return;
  }

  ElMessage.success("验证码已发送！");

  countdown.value = 60;
  timer = setInterval(() => {
    countdown.value--;
    if (countdown.value <= 0) {
      clearInterval(timer);
      timer = null;
    }
  }, 1000);
}

onUnmounted(() => {
  if (timer) {
    clearInterval(timer);
  }
});
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  position: relative;
  overflow: hidden;
}

.login-page::before {
  content: "";
  position: absolute;
  width: 200%;
  height: 200%;
  background: radial-gradient(
    circle,
    rgba(255, 255, 255, 0.1) 1px,
    transparent 1px
  );
  background-size: 20px 20px;
  animation: moveBackground 20s linear infinite;
}

@keyframes moveBackground {
  0% {
    transform: translate(0, 0);
  }
  100% {
    transform: translate(-50px, -50px);
  }
}

.login-container {
  width: 100%;
  max-width: 1100px;
  min-height: 650px;
  background: white;
  border-radius: 24px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  display: flex;
  overflow: hidden;
  position: relative;
  z-index: 1;
}

.login-left {
  flex: 1;
  background: linear-gradient(135deg, #ff9966 0%, #ffcc99 100%);
  padding: 60px 40px;
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}

.floating-elements {
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
  pointer-events: none;
}

.paw,
.bone,
.heart,
.star {
  position: absolute;
  font-size: 28px;
  opacity: 0.6;
  animation: float 4s ease-in-out infinite;
}

.paw-1 {
  top: 15%;
  left: 10%;
  animation-delay: 0s;
}

.paw-2 {
  top: 70%;
  right: 15%;
  animation-delay: 1.5s;
}

.bone-1 {
  top: 25%;
  right: 20%;
  font-size: 24px;
  animation-delay: 0.8s;
}

.heart-1 {
  bottom: 20%;
  left: 15%;
  font-size: 22px;
  animation-delay: 2s;
}

.star-1 {
  top: 60%;
  left: 25%;
  font-size: 20px;
  animation-delay: 1s;
}

@keyframes float {
  0%,
  100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(10deg);
  }
}

.main-character {
  position: relative;
  z-index: 2;
  margin-bottom: 40px;
}

.cat-container {
  position: relative;
  width: 280px;
  height: 280px;
  margin: 0 auto;
}

.cat-body {
  width: 200px;
  height: 180px;
  background: linear-gradient(135deg, #ffb74d 0%, #ffa726 100%);
  border-radius: 50% 50% 45% 45%;
  position: absolute;
  bottom: 40px;
  left: 50%;
  transform: translateX(-50%);
  box-shadow: inset 0 -20px 40px rgba(0, 0, 0, 0.1);
  animation: breathe 3s ease-in-out infinite;
}

.cat-head {
  width: 180px;
  height: 160px;
  background: linear-gradient(135deg, #ffe0b2 0%, #ffcc80 100%);
  border-radius: 50% 50% 45% 45%;
  position: absolute;
  top: 30px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 3;
}

.ear {
  width: 55px;
  height: 70px;
  background: linear-gradient(135deg, #ffb74d 0%, #ffa726 100%);
  position: absolute;
  top: -20px;
  clip-path: polygon(50% 0%, 0% 100%, 100% 100%);
  z-index: 2;
}

.ear.left {
  left: 25px;
  transform: rotate(-15deg);
  animation: earWiggle 2s ease-in-out infinite;
}

.ear.right {
  right: 25px;
  transform: rotate(15deg);
  animation: earWiggle 2s ease-in-out infinite 0.3s;
}

@keyframes earWiggle {
  0%,
  100% {
    transform: rotate(-15deg);
  }
  50% {
    transform: rotate(-20deg);
  }
}

.face {
  position: absolute;
  top: 60px;
  left: 50%;
  transform: translateX(-50%);
  width: 120px;
  height: 80px;
}

.eyes {
  display: flex;
  gap: 35px;
  justify-content: center;
  margin-bottom: 15px;
}

.eye {
  width: 26px;
  height: 30px;
  background: #333;
  border-radius: 50%;
  position: relative;
  animation: blink 4s ease-in-out infinite;
}

.pupil {
  width: 10px;
  height: 10px;
  background: white;
  border-radius: 50%;
  position: absolute;
  top: 6px;
  left: 6px;
  animation: lookAround 5s ease-in-out infinite;
}

@keyframes blink {
  0%,
  96%,
  100% {
    transform: scaleY(1);
  }
  98% {
    transform: scaleY(0.1);
  }
}

@keyframes lookAround {
  0%,
  100% {
    left: 6px;
  }
  25% {
    left: 12px;
  }
  50% {
    left: 6px;
  }
  75% {
    left: 2px;
  }
}

.nose {
  width: 18px;
  height: 13px;
  background: #ff6b6b;
  border-radius: 50%;
  position: absolute;
  top: 42px;
  left: 50%;
  transform: translateX(-50%);
}

.mouth {
  width: 35px;
  height: 18px;
  border: 3px solid #333;
  border-top: none;
  border-radius: 0 0 50% 50%;
  position: absolute;
  top: 52px;
  left: 50%;
  transform: translateX(-50%);
}

.whiskers {
  position: absolute;
  top: 38px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.whiskers.left {
  left: -35px;
}

.whiskers.right {
  right: -35px;
}

.whisker {
  width: 30px;
  height: 2px;
  background: #666;
  border-radius: 1px;
}

.whiskers.left .whisker:nth-child(1),
.whiskers.right .whisker:nth-child(1) {
  transform: rotate(-10deg);
  width: 28px;
}

.whiskers.left .whisker:nth-child(2) {
  width: 32px;
}

.whiskers.left .whisker:nth-child(3),
.whiskers.right .whisker:nth-child(3) {
  transform: rotate(10deg);
  width: 28px;
}

.whiskers.right .whisker:nth-child(1) {
  transform: rotate(10deg);
}

.whiskers.right .whisker:nth-child(2) {
  width: 32px;
}

.whiskers.right .whisker:nth-child(3) {
  transform: rotate(-10deg);
}

.cat-tail {
  width: 20px;
  height: 90px;
  background: linear-gradient(to bottom, #ffa726 0%, #ff9800 100%);
  border-radius: 10px;
  position: absolute;
  bottom: 30px;
  right: 60px;
  transform-origin: bottom center;
  animation: tailWag 1.5s ease-in-out infinite;
  z-index: 1;
}

@keyframes tailWag {
  0%,
  100% {
    transform: rotate(-15deg);
  }
  50% {
    transform: rotate(15deg);
  }
}

.food-bowl {
  position: absolute;
  bottom: 10px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 4;
}

.bowl {
  width: 80px;
  height: 35px;
  background: linear-gradient(135deg, #e91e63 0%, #f06292 100%);
  border-radius: 0 0 40px 40px;
  position: relative;
}

.bowl::before {
  content: "";
  position: absolute;
  top: -8px;
  left: -5px;
  right: -5px;
  height: 12px;
  background: #ec407a;
  border-radius: 6px 6px 0 0;
}

.food {
  width: 60px;
  height: 20px;
  background: radial-gradient(ellipse at center, #d4a574 0%, #c49563 100%);
  border-radius: 50%;
  position: absolute;
  top: -12px;
  left: 50%;
  transform: translateX(-50%);
  animation: foodBounce 2s ease-in-out infinite;
}

@keyframes foodBounce {
  0%,
  100% {
    transform: translateX(-50%) translateY(0);
  }
  50% {
    transform: translateX(-50%) translateY(-3px);
  }
}

@keyframes breathe {
  0%,
  100% {
    transform: translateX(-50%) scaleY(1);
  }
  50% {
    transform: translateX(-50%) scaleY(1.02);
  }
}

.welcome-text {
  text-align: center;
  color: white;
  position: relative;
  z-index: 2;
}

.welcome-text h2 {
  font-size: 24px;
  font-weight: 400;
  margin-bottom: 10px;
  opacity: 0.95;
}

.welcome-text h1 {
  font-size: 36px;
  font-weight: 900;
  margin-bottom: 15px;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
}

.welcome-text p {
  font-size: 16px;
  opacity: 0.9;
  line-height: 1.6;
}

.login-right {
  flex: 1;
  padding: 60px 50px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-form-container {
  width: 100%;
  max-width: 420px;
}

.form-header {
  text-align: center;
  margin-bottom: 35px;
}

.form-header h2 {
  font-size: 32px;
  color: #333;
  font-weight: bold;
  margin-bottom: 8px;
}

.form-header p {
  color: #999;
  font-size: 15px;
}

.login-tabs {
  margin-bottom: 25px;
}

:deep(.el-tabs__item) {
  font-size: 16px !important;
  font-weight: 500 !important;
}

:deep(.el-tabs__item.is-active) {
  color: #ff6b35 !important;
  font-weight: bold !important;
}

:deep(.el-tabs__active-bar) {
  background-color: #ff6b35 !important;
}

.code-input-wrapper {
  display: flex;
  gap: 12px;
  width: 100%;
}

.code-input-wrapper .el-input {
  flex: 1;
}

.send-code-btn {
  min-width: 130px !important;
  white-space: nowrap;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.forgot-link {
  color: #ff6b35;
  text-decoration: none;
  font-size: 14px;
  transition: all 0.3s;
}

.forgot-link:hover {
  color: #ff5722;
  text-decoration: underline;
}

.login-btn {
  width: 100% !important;
  height: 48px !important;
  font-size: 18px !important;
  font-weight: bold !important;
  border-radius: 10px !important;
  letter-spacing: 4px !important;
  transition: all 0.3s !important;
}

.login-btn:not(.code-btn) {
  background: linear-gradient(135deg, #ff6b35 0%, #ff8c42 100%) !important;
  border: none !important;
  box-shadow: 0 4px 15px rgba(255, 107, 53, 0.3) !important;
}

.login-btn:not(.code-btn):hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 107, 53, 0.4) !important;
}

.code-btn {
  background: linear-gradient(135deg, #ffc107 0%, #ff9800 100%) !important;
  border: none !important;
  box-shadow: 0 4px 15px rgba(255, 193, 7, 0.3) !important;
}

.code-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 193, 7, 0.4) !important;
}

.divider {
  text-align: center;
  position: relative;
  margin: 30px 0;
}

.divider::before,
.divider::after {
  content: "";
  position: absolute;
  top: 50%;
  width: 40%;
  height: 1px;
  background: #e0e0e0;
}

.divider::before {
  left: 0;
}

.divider::after {
  right: 0;
}

.divider span {
  background: white;
  padding: 0 15px;
  color: #999;
  font-size: 14px;
  position: relative;
  z-index: 1;
}

.social-login {
  display: flex;
  justify-content: center;
  gap: 25px;
  margin-bottom: 30px;
}

.social-btn {
  width: 52px;
  height: 52px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s;
  background: #f5f7fa;
  border: 2px solid transparent;
}

.social-btn svg {
  width: 28px;
  height: 28px;
}

.social-btn:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 15px rgba(0, 0, 0, 0.15);
}

.social-btn.wechat:hover {
  background: #e8f8ee;
  border-color: #07c160;
}

.social-btn.alipay:hover {
  background: #e6f4ff;
  border-color: #1677ff;
}

.social-btn.qq:hover {
  background: #e6f7ff;
  border-color: #12b7f5;
}

.register-link {
  text-align: center;
  color: #666;
  font-size: 15px;
}

.register-link .link {
  color: #ff6b35;
  text-decoration: none;
  font-weight: bold;
  margin-left: 5px;
  transition: all 0.3s;
}

.register-link .link:hover {
  color: #ff5722;
  text-decoration: underline;
}
</style>
