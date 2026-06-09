<template>
  <div class="about-container">
    <el-container>
      <el-header>
        <div class="header-content">
          <h1>🐾 Pet Vue User</h1>
          <nav class="nav-menu">
            <router-link to="/">首页</router-link>
            <router-link to="/about">关于</router-link>
          </nav>
        </div>
      </el-header>

      <el-main>
        <el-card class="about-card">
          <template #header>
            <div class="card-header">
              <span>关于项目</span>
            </div>
          </template>
          
          <el-descriptions :column="1" border>
            <el-descriptions-item label="项目名称">Pet Vue User</el-descriptions-item>
            <el-descriptions-item label="技术栈">Vue 3 + Vite</el-descriptions-item>
            <el-descriptions-item label="UI框架">Element Plus</el-descriptions-item>
            <el-descriptions-item label="状态管理">Pinia</el-descriptions-item>
            <el-descriptions-item label="路由管理">Vue Router 4</el-descriptions-item>
          </el-descriptions>

          <el-divider />

          <el-timeline>
            <el-timeline-item timestamp="2026-05-14" placement="top" type="primary">
              <el-card>
                <h4>项目初始化</h4>
                <p>完成 Vue 3 项目搭建，集成 Element Plus、Pinia 和 Vue Router</p>
              </el-card>
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-main>

      <el-footer>
        <p>&copy; 2026 Pet Vue User. All rights reserved.</p>
      </el-footer>
    </el-container>
  </div>
</template>

<script setup>
</script>

<style scoped>
.about-container {
  min-height: 100vh;
  background-color: #f5f7fa;
}

.el-header {
  background-color: #409eff;
  color: white;
  display: flex;
  align-items: center;
  padding: 0 20px;
}

.header-content {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-content h1 {
  font-size: 24px;
  margin: 0;
}

.nav-menu a {
  color: white;
  text-decoration: none;
  margin-left: 20px;
  font-size: 16px;
  transition: opacity 0.3s;
}

.nav-menu a:hover {
  opacity: 0.8;
}

.about-card {
  max-width: 800px;
  margin: 40px auto;
}

.card-header {
  font-size: 18px;
  font-weight: bold;
}

.el-footer {
  text-align: center;
  color: #909399;
  padding: 20px;
  background-color: #fff;
}
</style>
