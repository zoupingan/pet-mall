<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Plus } from "@element-plus/icons-vue";
import request from "@/utils/request";

const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const loading = ref(false);
const userList = ref<any[]>([]);

const dialogVisible = ref(false);
const isEditMode = ref(false);
const userForm = reactive({
  id: null as number | null,
  username: "",
  password: "",
  role: null as number | null,
  status: 1,
});

const searchForm = reactive({
  username: "",
  status: "" as string,
});

const handleAddUser = () => {
  isEditMode.value = false;
  userForm.id = null;
  userForm.username = "";
  userForm.password = "";
  userForm.role = null;
  userForm.status = 1;
  dialogVisible.value = true;
};

const handleEdit = (row: any) => {
  isEditMode.value = true;
  userForm.id = row.id;
  userForm.username = row.username || "";
  userForm.password = "";
  userForm.role = row.role;
  userForm.status = row.status ?? 1;
  dialogVisible.value = true;
};

const handleSubmit = async () => {
  if (!userForm.username.trim()) {
    ElMessage.warning("请输入用户名");
    return;
  }
  if (!isEditMode.value && !userForm.password.trim()) {
    ElMessage.warning("请输入密码");
    return;
  }

  try {
    let res;
    const formData: any = {
      username: userForm.username,
      role: userForm.role,
      status: userForm.status,
    };

    if (isEditMode.value) {
      if (userForm.password) {
        formData.password = userForm.password;
      }
      formData.id = userForm.id;
      res = await request.put("/admin/user/update", formData);
    } else {
      formData.password = userForm.password;
      res = await request.post("/admin/user/add", formData);
    }

    if (res.code === 200) {
      ElMessage.success(isEditMode.value ? "编辑用户成功" : "新增用户成功");
      dialogVisible.value = false;
      fetchUserList();
    } else {
      ElMessage.error(
        res.msg || (isEditMode.value ? "编辑用户失败" : "新增用户失败")
      );
    }
  } catch (error: any) {
    console.error(isEditMode.value ? "编辑用户失败:" : "新增用户失败:", error);
    ElMessage.error(isEditMode.value ? "编辑用户失败" : "新增用户失败");
  }
};

const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm(`确定要删除用户"${row.username}"吗？`, "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    const res = await request.delete(`/admin/user/delete/${row.id}`);
    if (res.code === 200) {
      ElMessage.success("删除成功");
      fetchUserList();
    } else {
      ElMessage.error(res.msg || "删除失败");
    }
  } catch (error: any) {
    if (error !== "cancel") {
      console.error("删除用户失败:", error);
      ElMessage.error("删除用户失败");
    }
  }
};

const fetchUserList = async () => {
  loading.value = true;
  try {
    console.log("🚀 正在获取用户列表...");
    const params: any = {
      page: currentPage.value,
      pageSize: pageSize.value,
    };

    if (searchForm.username) {
      params.username = searchForm.username;
    }
    if (searchForm.status !== "") {
      params.status = Number(searchForm.status);
    }

    console.log("📤 请求参数:", params);
    const res = await request.get("/admin/user/list", { params });

    console.log("✅ 收到用户列表响应:", res);

    if (res.code === 200 && res.data) {
      const records = res.data.records || [];
      userList.value = records;
      total.value = res.data.total || 0;
      console.log(`📦 成功加载 ${userList.value.length} 条用户数据`);
    } else {
      console.error(
        "❌ 获取用户列表失败:",
        res.msg || res.message || "未知错误"
      );
      ElMessage.error(res.msg || res.message || "获取用户列表失败");
    }
  } catch (error: any) {
    console.error("❌ 获取用户列表异常:", error);
    ElMessage.error("获取用户列表失败");
  } finally {
    loading.value = false;
  }
};

const handleSearch = () => {
  currentPage.value = 1;
  fetchUserList();
};

const handleReset = () => {
  searchForm.username = "";
  searchForm.status = "";
  currentPage.value = 1;
  fetchUserList();
};

onMounted(() => {
  console.log("📄 用户管理页面已加载，开始请求数据...");
  fetchUserList();
});
</script>

<template>
  <div class="page-container">
    <div class="search-card">
      <el-form :inline="true" class="search-form">
        <el-form-item label="用户名">
          <el-input
            v-model="searchForm.username"
            placeholder="请输入用户名"
            clearable
            style="width: 240px"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择状态"
            clearable
            style="width: 180px"
          >
            <el-option label="正常" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-card">
      <div class="table-header">
        <el-button type="primary" :icon="Plus" @click="handleAddUser"
          >新增用户</el-button
        >
      </div>
      <el-table
        :data="userList"
        v-loading="loading"
        stripe
        style="width: 100%"
        :header-cell-style="{ fontSize: '15px', fontWeight: '700' }"
        :row-style="{ fontSize: '14px' }"
      >
        <el-table-column
          prop="id"
          label="用户ID"
          min-width="10%"
          align="center"
        />
        <el-table-column prop="username" label="用户名" min-width="15%" />
        <el-table-column
          prop="role"
          label="角色"
          min-width="12%"
          align="center"
        >
          <template #default="{ row }">
            <el-tag
              :type="row.role === 1 ? 'warning' : ''"
              size="small"
              effect="light"
              >{{ row.role === 1 ? "管理员" : "顾客" }}</el-tag
            >
          </template>
        </el-table-column>
        <el-table-column
          prop="status"
          label="状态"
          min-width="12%"
          align="center"
        >
          <template #default="{ row }">
            <el-tag
              :type="row.status === 1 ? 'success' : 'info'"
              size="small"
              effect="light"
              >{{ row.status === 1 ? "正常" : "禁用" }}</el-tag
            >
          </template>
        </el-table-column>
        <el-table-column label="修改时间" min-width="18%" align="center">
          <template #default="{ row }">{{ row.updateTime || "-" }}</template>
        </el-table-column>
        <el-table-column
          prop="action"
          label="操作"
          min-width="15%"
          fixed="right"
          align="center"
        >
          <template #default="{ row }">
            <el-button type="primary" plain @click="handleEdit(row)"
              >编辑</el-button
            >
            <el-button type="danger" plain @click="handleDelete(row)"
              >删除</el-button
            >
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          background
          @current-change="fetchUserList"
          @size-change="
            () => {
              currentPage = 1;
              fetchUserList();
            }
          "
        />
      </div>
    </div>

    <el-dialog
      v-model="dialogVisible"
      :title="isEditMode ? '编辑用户' : '新增用户'"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form :model="userForm" label-width="100px">
        <el-form-item v-if="isEditMode" label="用户ID">
          <el-input v-model.number="userForm.id" disabled />
        </el-form-item>
        <el-form-item label="用户名">
          <el-input v-model="userForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input
            v-model="userForm.password"
            :placeholder="isEditMode ? '不修改密码请留空' : '请输入密码'"
            show-password
          />
        </el-form-item>
        <el-form-item label="角色">
          <el-select
            v-model.number="userForm.role"
            placeholder="请选择角色"
            style="width: 100%"
          >
            <el-option label="顾客" :value="0" />
            <el-option label="管理员" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model.number="userForm.status"
            placeholder="请选择状态"
            style="width: 100%"
          >
            <el-option label="正常" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.page-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.search-card {
  background: #fff;
  border-radius: 14px;
  padding: 28px 32px;
  box-shadow: 0 1px 6px rgba(0, 0, 0, 0.04);
  border: 1px solid #f0f2f5;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.search-form :deep(.el-form-item__label) {
  color: #4a5568;
  font-weight: 600;
  font-size: 14px;
}

.search-form :deep(.el-input__wrapper) {
  height: 46px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  border-radius: 8px;
  transition: all 0.3s ease;
}

.search-form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.search-form :deep(.el-input__inner) {
  font-size: 15px;
}

.search-form :deep(.el-select .el-input__wrapper) {
  height: 46px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  border-radius: 8px;
}

.search-form :deep(.el-button) {
  height: 46px;
  padding: 14px 32px;
  font-size: 15px;
  font-weight: 600;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.search-form :deep(.el-button--primary) {
  background: linear-gradient(135deg, #e8792e 0%, #d96920 100%);
  border: none;
  box-shadow: 0 4px 15px rgba(232, 121, 46, 0.35);
}

.search-form :deep(.el-button--primary:hover) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(232, 121, 46, 0.45);
}

.search-form :deep(.el-button--default) {
  border: 2px solid #e2e8f0;
  background: #fff;
  color: #4a5568;
}

.search-form :deep(.el-button--default:hover) {
  border-color: #e8792e;
  color: #e8792e;
  transform: translateY(-2px);
}
.table-card {
  background: #fff;
  border-radius: 14px;
  overflow: hidden;
  box-shadow: 0 1px 6px rgba(0, 0, 0, 0.04);
  border: 1px solid #f0f2f5;
}

.table-header {
  padding: 28px 32px;
  display: flex;
  justify-content: flex-end;
  border-bottom: 1px solid #f0f2f5;
}

.table-header :deep(.el-button) {
  height: 46px;
  padding: 14px 32px;
  font-size: 15px;
  font-weight: 600;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.table-header :deep(.el-button--primary) {
  background: linear-gradient(135deg, #e8792e 0%, #d96920 100%);
  border: none;
  box-shadow: 0 4px 15px rgba(232, 121, 46, 0.35);
}

.table-header :deep(.el-button--primary:hover) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(232, 121, 46, 0.45);
}
:deep(.el-table) {
  --el-table-border-color: #f2f4f7;
  --el-table-header-bg-color: #fafbfc;
  --el-table-row-hover-bg-color: #fdfdfd;
  font-size: 14px;
}
:deep(.el-table th.el-table__cell) {
  background-color: #fafbfc !important;
  color: #555;
  font-weight: 700;
  font-size: 15px;
  padding: 18px 0;
}
:deep(.el-table td.el-table__cell) {
  padding: 20px 0;
  color: #444;
}

.pagination-wrapper {
  padding: 24px 28px;
  display: flex;
  justify-content: flex-end;
  border-top: 1px solid #f0f2f5;
}

.pagination-wrapper :deep(.el-pagination) {
  font-size: 14px;
}

.pagination-wrapper :deep(.el-pagination .el-pager li) {
  font-size: 14px;
  font-weight: 500;
}

:deep(.el-table .el-button--primary.is-plain) {
  border-color: #e8792e;
  color: #e8792e;
  background: #f0f4ff;
  font-weight: 600;
  font-size: 14px;
  padding: 10px 20px;
  height: 38px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

:deep(.el-table .el-button--primary.is-plain:hover) {
  background: linear-gradient(135deg, #e8792e 0%, #d96920 100%);
  color: #fff;
  border-color: transparent;
  transform: translateY(-1px);
  box-shadow: 0 3px 10px rgba(232, 121, 46, 0.35);
}

:deep(.el-table .el-button--danger.is-plain) {
  border-color: #ff6b6b;
  color: #ff6b6b;
  background: #fff5f5;
  font-weight: 600;
  font-size: 14px;
  padding: 10px 20px;
  height: 38px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

:deep(.el-table .el-button--danger.is-plain:hover) {
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a5a 100%);
  color: #fff;
  border-color: transparent;
  transform: translateY(-1px);
  box-shadow: 0 3px 10px rgba(255, 107, 107, 0.35);
}

:deep(.el-table .el-button--info.is-plain) {
  border-color: #909399;
  color: #909399;
  background: #f4f4f5;
  font-weight: 600;
  border-radius: 8px;
  transition: all 0.3s ease;
}

:deep(.el-table .el-button--info.is-plain:hover) {
  background: linear-gradient(135deg, #909399 0%, #6a6d73 100%);
  color: #fff;
  border-color: transparent;
  transform: translateY(-1px);
  box-shadow: 0 3px 10px rgba(144, 147, 153, 0.35);
}

:deep(.el-table .el-button--warning.is-plain) {
  border-color: #e6a23c;
  color: #e6a23c;
  background: #fdf6ec;
  font-weight: 600;
  border-radius: 8px;
  transition: all 0.3s ease;
}

:deep(.el-table .el-button--warning.is-plain:hover) {
  background: linear-gradient(135deg, #e6a23c 0%, #cf8e2e 100%);
  color: #fff;
  border-color: transparent;
  transform: translateY(-1px);
  box-shadow: 0 3px 10px rgba(230, 162, 60, 0.35);
}

:deep(.el-table .el-button--success.is-plain) {
  border-color: #67c23a;
  color: #67c23a;
  background: #f0f9eb;
  font-weight: 600;
  border-radius: 8px;
  transition: all 0.3s ease;
}

:deep(.el-table .el-button--success.is-plain:hover) {
  background: linear-gradient(135deg, #67c23a 0%, #529b2e 100%);
  color: #fff;
  border-color: transparent;
  transform: translateY(-1px);
  box-shadow: 0 3px 10px rgba(103, 194, 58, 0.35);
}
</style>