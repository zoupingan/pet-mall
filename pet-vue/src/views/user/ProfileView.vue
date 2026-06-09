<script setup lang="ts">
import { reactive } from "vue";

const profileForm = reactive({
  username: "admin",
  realName: "系统管理员",
  email: "admin@petshop.com",
  phone: "138****8888",
  role: "超级管理员",
});

const passwordForm = reactive({
  oldPassword: "",
  newPassword: "",
  confirmPassword: "",
});
</script>

<template>
  <div class="page-container">
    <div class="profile-layout">
      <div class="profile-left">
        <div class="profile-card">
          <div class="profile-avatar">
            <el-avatar
              :size="80"
              style="
                background: linear-gradient(135deg, #e67e22, #d35400);
                font-size: 28px;
                font-weight: 700;
              "
              >管</el-avatar
            >
          </div>
          <h3 class="profile-name">{{ profileForm.realName }}</h3>
          <p class="profile-role">{{ profileForm.role }}</p>
          <el-divider />
          <div class="profile-stats">
            <div class="stat-item">
              <span class="stat-num">256</span>
              <span class="stat-label">登录次数</span>
            </div>
            <div class="stat-item">
              <span class="stat-num">86</span>
              <span class="stat-label">操作记录</span>
            </div>
          </div>
        </div>
      </div>

      <div class="profile-right">
        <div class="form-section">
          <h3 class="section-title">
            <el-icon><UserFilled /></el-icon> 基本信息
          </h3>
          <el-form label-width="100px" class="info-form">
            <el-row :gutter="24">
              <el-col :span="12">
                <el-form-item label="用户名">
                  <el-input :model-value="profileForm.username" disabled />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="真实姓名">
                  <el-input
                    v-model="profileForm.realName"
                    placeholder="请输入真实姓名"
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="24">
              <el-col :span="12">
                <el-form-item label="邮箱地址">
                  <el-input
                    v-model="profileForm.email"
                    placeholder="请输入邮箱地址"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="手机号码">
                  <el-input
                    v-model="profileForm.phone"
                    placeholder="请输入手机号码"
                  />
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
          <div class="form-actions">
            <el-button type="primary">保存修改</el-button>
          </div>
        </div>

        <div class="form-section">
          <h3 class="section-title">
            <el-icon><Lock /></el-icon> 修改密码
          </h3>
          <el-form label-width="100px" class="info-form">
            <el-row :gutter="24">
              <el-col :span="24">
                <el-form-item label="当前密码">
                  <el-input
                    v-model="passwordForm.oldPassword"
                    type="password"
                    placeholder="请输入当前密码"
                    show-password
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="24">
              <el-col :span="12">
                <el-form-item label="新密码">
                  <el-input
                    v-model="passwordForm.newPassword"
                    type="password"
                    placeholder="请输入新密码"
                    show-password
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="确认新密码">
                  <el-input
                    v-model="passwordForm.confirmPassword"
                    type="password"
                    placeholder="请再次输入新密码"
                    show-password
                  />
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
          <div class="form-actions">
            <el-button type="primary">修改密码</el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.page-container {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.profile-layout {
  display: grid;
  grid-template-columns: 300px 1fr;
  gap: 24px;
}

.profile-left {
  position: sticky;
  top: 76px;
  height: fit-content;
}

.profile-card {
  background: #fff;
  border-radius: 14px;
  padding: 36px 28px;
  text-align: center;
  box-shadow: 0 1px 6px rgba(0, 0, 0, 0.04);
  border: 1px solid #f0f2f5;
}

.profile-avatar {
  margin-bottom: 18px;
  display: flex;
  justify-content: center;
}

.profile-name {
  font-size: 18px;
  font-weight: 600;
  color: #334155;
  margin: 0 0 8px;
}

.profile-role {
  font-size: 13px;
  color: #999;
  margin: 0;
}

.profile-stats {
  display: flex;
  justify-content: center;
  gap: 40px;
}

.stat-item {
  text-align: center;
}

.stat-num {
  display: block;
  font-size: 24px;
  font-weight: 700;
  color: #e67e22;
}

.stat-label {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
  display: block;
}

.form-section {
  background: #fff;
  border-radius: 14px;
  padding: 28px 32px;
  box-shadow: 0 1px 6px rgba(0, 0, 0, 0.04);
  border: 1px solid #f0f2f5;
  margin-bottom: 24px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #334155;
  margin: 0 0 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f2f5;
  display: flex;
  align-items: center;
  gap: 8px;
}

.info-form {
  margin-top: 8px;
}

.form-actions {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
}

@media (max-width: 768px) {
  .profile-layout {
    grid-template-columns: 1fr;
  }
}
</style>
