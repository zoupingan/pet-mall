<template>
  <div class="register-page">
    <div class="register-container">
      <div class="register-left">
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
            <h2>加入我们</h2>
            <h1>宠物用品商城 🐶</h1>
            <p>为您的爱宠提供最优质的产品和服务</p>
          </div>
        </div>
      </div>

      <div class="register-right">
        <div class="register-form-container">
          <div class="form-header">
            <h2>用户注册</h2>
            <p>Create your account</p>
          </div>

          <el-form
            ref="registerFormRef"
            :model="registerForm"
            :rules="registerRules"
            class="register-form"
          >
            <el-form-item prop="username">
              <el-input
                v-model="registerForm.username"
                placeholder="请输入用户名"
                size="large"
                :prefix-icon="User"
              />
            </el-form-item>

            <el-form-item prop="password">
              <el-input
                v-model="registerForm.password"
                type="password"
                placeholder="请输入密码"
                size="large"
                :prefix-icon="Lock"
                show-password
              />
            </el-form-item>

            <el-form-item prop="confirmPassword">
              <el-input
                v-model="registerForm.confirmPassword"
                type="password"
                placeholder="请确认密码"
                size="large"
                :prefix-icon="Lock"
                show-password
              />
            </el-form-item>

            <el-form-item>
              <el-button
                type="primary"
                size="large"
                class="register-btn"
                :loading="loading"
                @click="handleRegister"
              >
                注 册
              </el-button>
            </el-form-item>
          </el-form>

          <div class="login-link">
            已有账号？
            <router-link to="/login" class="link">立即登录 →</router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from "vue";
import { useRouter } from "vue-router";
import { User, Lock } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import { register } from "../api/user";

const router = useRouter();

const loading = ref(false);
const registerFormRef = ref(null);

const registerForm = reactive({
  username: "",
  password: "",
  confirmPassword: "",
});

const validateConfirmPassword = (rule, value, callback) => {
  if (value === "") {
    callback(new Error("请再次输入密码"));
  } else if (value !== registerForm.password) {
    callback(new Error("两次输入的密码不一致"));
  } else {
    callback();
  }
};

const registerRules = {
  username: [
    { required: true, message: "请输入用户名", trigger: "blur" },
    { min: 3, max: 20, message: "用户名长度为3-20个字符", trigger: "blur" },
  ],
  password: [
    { required: true, message: "请输入密码", trigger: "blur" },
    { min: 6, message: "密码长度不能少于6位", trigger: "blur" },
  ],
  confirmPassword: [
    { required: true, validator: validateConfirmPassword, trigger: "blur" },
  ],
};

const handleRegister = async () => {
  if (!registerFormRef.value) return;

  try {
    const valid = await registerFormRef.value.validate();
    if (!valid) return;

    loading.value = true;
    const res = await register({
      username: registerForm.username,
      password: registerForm.password,
    });

    ElMessage({
      message: "注册成功！正在跳转到登录页面... 🎉",
      type: "success",
      duration: 1500,
      showClose: true,
    });

    setTimeout(() => {
      router.replace("/login");
    }, 500);
  } catch (error) {
    console.error("注册失败:", error);
    if (error.message) {
      ElMessage.error(error.message);
    } else {
      ElMessage.error("注册失败，请重试");
    }
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.register-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #ff8c42 0%, #ff6b35 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
}

.register-container {
  display: flex;
  width: 100%;
  max-width: 1100px;
  min-height: 600px;
  background: white;
  border-radius: 24px;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(255, 107, 53, 0.3);
}

.register-left {
  flex: 1;
  background: linear-gradient(135deg, #ff8c42 0%, #ff6b35 100%);
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}

.illustration-wrapper {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  position: relative;
  padding: 40px;
}

.floating-elements {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
}

.paw,
.bone,
.heart,
.star {
  position: absolute;
  font-size: 32px;
  animation: float 6s ease-in-out infinite;
}

.paw-1 {
  top: 15%;
  left: 15%;
  animation-delay: 0s;
}

.paw-2 {
  top: 25%;
  right: 20%;
  animation-delay: 1s;
}

.bone-1 {
  bottom: 30%;
  left: 20%;
  animation-delay: 2s;
}

.heart-1 {
  top: 20%;
  right: 15%;
  animation-delay: 1.5s;
}

.star-1 {
  bottom: 25%;
  right: 25%;
  animation-delay: 0.5s;
}

@keyframes float {
  0%,
  100% {
    transform: translateY(0px) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(10deg);
  }
}

.main-character {
  margin-bottom: 30px;
}

.cat-container {
  position: relative;
  width: 200px;
  height: 240px;
}

.cat-body {
  position: absolute;
  bottom: 40px;
  left: 50%;
  transform: translateX(-50%);
  width: 120px;
  height: 100px;
  background: linear-gradient(180deg, #ff8c42 0%, #ff6b35 100%);
  border-radius: 60px 60px 50px 50px;
}

.cat-head {
  position: absolute;
  top: 20px;
  left: 50%;
  transform: translateX(-50%);
  width: 100px;
  height: 90px;
  background: linear-gradient(180deg, #ff8c42 0%, #ff6b35 100%);
  border-radius: 50%;
}

.ear {
  position: absolute;
  top: -15px;
  width: 0;
  height: 0;
  border-left: 18px solid transparent;
  border-right: 18px solid transparent;
  border-bottom: 30px solid #ff8c42;
}

.ear.left {
  left: 5px;
  transform: rotate(-15deg);
}

.ear.right {
  right: 5px;
  transform: rotate(15deg);
}

.face {
  position: absolute;
  top: 20px;
  left: 50%;
  transform: translateX(-50%);
  width: 80px;
  height: 60px;
}

.eyes {
  display: flex;
  justify-content: space-between;
  padding: 0 5px;
}

.eye {
  width: 20px;
  height: 24px;
  background: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.pupil {
  width: 10px;
  height: 12px;
  background: #333;
  border-radius: 50%;
  animation: blink 4s infinite;
}

@keyframes blink {
  0%,
  90%,
  100% {
    transform: scaleY(1);
  }
  95% {
    transform: scaleY(0.1);
  }
}

.nose {
  width: 12px;
  height: 10px;
  background: #ff4500;
  border-radius: 50% 50% 50% 50% / 30% 30% 70% 70%;
  margin: 8px auto 0;
}

.mouth {
  width: 20px;
  height: 10px;
  border-bottom: 3px solid #333;
  border-left: 3px solid transparent;
  border-right: 3px solid transparent;
  margin: 2px auto 0;
}

.whiskers {
  position: absolute;
  top: 30px;
}

.whiskers.left {
  left: -25px;
}

.whiskers.right {
  right: -25px;
}

.whisker {
  width: 25px;
  height: 2px;
  background: #333;
  margin: 5px 0;
  border-radius: 1px;
}

.whiskers.left .whisker:nth-child(1) {
  transform: rotate(-15deg);
}

.whiskers.left .whisker:nth-child(2) {
  transform: rotate(0deg);
}

.whiskers.left .whisker:nth-child(3) {
  transform: rotate(15deg);
}

.whiskers.right .whisker:nth-child(1) {
  transform: rotate(15deg);
}

.whiskers.right .whisker:nth-child(2) {
  transform: rotate(0deg);
}

.whiskers.right .whisker:nth-child(3) {
  transform: rotate(-15deg);
}

.cat-tail {
  position: absolute;
  bottom: 50px;
  right: 20px;
  width: 60px;
  height: 20px;
  background: linear-gradient(90deg, #ff8c42 0%, #ff6b35 100%);
  border-radius: 10px;
  transform: rotate(30deg);
  transform-origin: left center;
  animation: tailWag 2s ease-in-out infinite;
}

@keyframes tailWag {
  0%,
  100% {
    transform: rotate(30deg);
  }
  50% {
    transform: rotate(50deg);
  }
}

.food-bowl {
  position: absolute;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
}

.bowl {
  width: 50px;
  height: 20px;
  background: linear-gradient(180deg, #ff4500 0%, #cc3700 100%);
  border-radius: 0 0 25px 25px;
}

.food {
  width: 40px;
  height: 10px;
  background: #8b4513;
  border-radius: 50%;
  margin: 0 auto;
}

.welcome-text {
  text-align: center;
  color: white;
  z-index: 1;
}

.welcome-text h2 {
  font-size: 28px;
  font-weight: 300;
  margin-bottom: 10px;
}

.welcome-text h1 {
  font-size: 36px;
  font-weight: 700;
  margin-bottom: 15px;
}

.welcome-text p {
  font-size: 16px;
  opacity: 0.9;
}

.register-right {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  background: white;
}

.register-form-container {
  width: 100%;
  max-width: 360px;
}

.form-header {
  margin-bottom: 40px;
  text-align: center;
}

.form-header h2 {
  font-size: 32px;
  font-weight: 700;
  color: #303133;
  margin-bottom: 8px;
}

.form-header p {
  font-size: 14px;
  color: #909399;
  letter-spacing: 1px;
}

.register-form {
  margin-bottom: 30px;
}

.register-form :deep(.el-input__wrapper) {
  padding: 12px 16px;
  border-radius: 8px;
}

.register-form :deep(.el-input__inner) {
  font-size: 15px;
}

.register-form :deep(.el-form-item) {
  margin-bottom: 24px;
}

.register-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #ff8c42 0%, #ff6b35 100%);
  border: none;
  border-radius: 8px;
  color: white;
  cursor: pointer;
  transition: all 0.3s ease;
  letter-spacing: 4px;
}

.register-btn:hover {
  background: linear-gradient(135deg, #ff6b35 0%, #ff5722 100%);
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 107, 53, 0.4);
}

.login-link {
  text-align: center;
  color: #909399;
  font-size: 14px;
}

.login-link .link {
  color: #ff6b35;
  text-decoration: none;
  font-weight: 600;
  margin-left: 5px;
  transition: color 0.3s;
}

.login-link .link:hover {
  color: #ff5722;
  text-decoration: underline;
}

@media (max-width: 768px) {
  .register-container {
    flex-direction: column;
    max-width: 450px;
  }

  .register-left {
    min-height: 250px;
    flex: none;
  }

  .register-right {
    padding: 30px 20px;
  }

  .illustration-wrapper {
    padding: 20px;
  }

  .cat-container {
    transform: scale(0.7);
  }

  .welcome-text h1 {
    font-size: 28px;
  }

  .floating-elements {
    display: none;
  }
}
</style>
