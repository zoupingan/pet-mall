<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Plus } from "@element-plus/icons-vue";
import request from "@/utils/request";

const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const loading = ref(false);
const categoryList = ref<any[]>([]);

const dialogVisible = ref(false);
const isEditMode = ref(false);
const categoryForm = reactive({
  id: null as number | null,
  categoryName: "",
  icon: "",
});

const handleAddCategory = () => {
  isEditMode.value = false;
  categoryForm.id = null;
  categoryForm.categoryName = "";
  categoryForm.icon = "";
  dialogVisible.value = true;
};

const handleEdit = (row: any) => {
  isEditMode.value = true;
  categoryForm.id = row.id;
  categoryForm.categoryName = row.categoryName || "";
  categoryForm.icon = row.icon || "";
  dialogVisible.value = true;
};

const handleSubmit = async () => {
  if (!categoryForm.categoryName.trim()) {
    ElMessage.warning("请输入分类名称");
    return;
  }

  try {
    let res;
    if (isEditMode.value) {
      res = await request.put("/admin/category/update", {
        id: categoryForm.id,
        categoryName: categoryForm.categoryName,
        icon: categoryForm.icon,
      });
    } else {
      res = await request.post("/admin/category/add", {
        categoryName: categoryForm.categoryName,
        icon: categoryForm.icon,
      });
    }

    if (res.code === 200) {
      ElMessage.success(isEditMode.value ? "编辑分类成功" : "新增分类成功");
      dialogVisible.value = false;
      fetchCategoryList();
    } else {
      ElMessage.error(
        res.msg || (isEditMode.value ? "编辑分类失败" : "新增分类失败")
      );
    }
  } catch (error: any) {
    console.error(isEditMode.value ? "编辑分类失败:" : "新增分类失败:", error);
    ElMessage.error(isEditMode.value ? "编辑分类失败" : "新增分类失败");
  }
};

const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除分类"${row.categoryName}"吗？`,
      "提示",
      {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }
    );

    const res = await request.delete(`/admin/category/delete/${row.id}`);
    if (res.code === 200) {
      ElMessage.success("删除成功");
      fetchCategoryList();
    } else {
      ElMessage.error(res.msg || "删除失败");
    }
  } catch (error: any) {
    if (error !== "cancel") {
      console.error("删除分类失败:", error);
      ElMessage.error("删除分类失败");
    }
  }
};

const fetchCategoryList = async () => {
  loading.value = true;
  try {
    console.log("🚀 正在获取分类列表...");
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value,
    };
    const res = await request.get("/admin/category/list", { params });

    console.log("✅ 收到分类列表响应:", res);

    if (res.code === 200 && res.data) {
      const records = res.data.records || [];
      categoryList.value = records;
      total.value = res.data.total || 0;
      console.log(`📦 成功加载 ${categoryList.value.length} 条分类数据`);
    } else {
      console.error(
        "❌ 获取分类列表失败:",
        res.msg || res.message || "未知错误"
      );
      ElMessage.error(res.msg || res.message || "获取分类列表失败");
    }
  } catch (error: any) {
    console.error("❌ 获取分类列表异常:", error);
    ElMessage.error("获取分类列表失败");
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  console.log("📄 分类管理页面已加载，开始请求数据...");
  fetchCategoryList();
});
</script>

<template>
  <div class="page-container">
    <div class="table-card">
      <div class="table-header">
        <el-button type="primary" :icon="Plus" @click="handleAddCategory"
          >新增分类</el-button
        >
      </div>
      <el-table
        :data="categoryList"
        v-loading="loading"
        stripe
        style="width: 100%"
        :header-cell-style="{ fontSize: '15px', fontWeight: '700' }"
        :row-style="{ fontSize: '14px' }"
      >
        <el-table-column
          prop="id"
          label="分类ID"
          min-width="8%"
          align="center"
        />
        <el-table-column
          prop="icon"
          label="分类图标"
          min-width="18%"
          align="center"
        >
          <template #default>
            <div class="category-icon-wrapper">
              <el-icon><Goods /></el-icon>
            </div>
          </template>
        </el-table-column>
        <el-table-column
          prop="categoryName"
          label="分类名称"
          min-width="22%"
          align="center"
        />
        <el-table-column label="修改时间" min-width="26%" align="center">
          <template #default="{ row }">{{ row.updateTime || "-" }}</template>
        </el-table-column>
        <el-table-column
          prop="action"
          label="操作"
          min-width="16%"
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
          @current-change="fetchCategoryList"
          @size-change="
            () => {
              currentPage = 1;
              fetchCategoryList();
            }
          "
        />
      </div>
    </div>

    <el-dialog
      v-model="dialogVisible"
      :title="isEditMode ? '编辑分类' : '新增分类'"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form :model="categoryForm" label-width="100px">
        <el-form-item v-if="isEditMode" label="分类ID">
          <el-input v-model.number="categoryForm.id" disabled />
        </el-form-item>
        <el-form-item label="分类名称">
          <el-input
            v-model="categoryForm.categoryName"
            placeholder="请输入分类名称"
          />
        </el-form-item>
        <el-form-item label="分类图标">
          <el-input
            v-model="categoryForm.icon"
            placeholder="请输入分类图标URL（可选）"
          />
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

.icon-placeholder {
  width: 60px;
  height: 60px;
  background: linear-gradient(135deg, #f0f2f5, #e8ecf1);
  border-radius: 12px;
  margin: 6px auto;
}

.icon-image {
  width: 60px;
  height: 60px;
  margin: 6px auto;
  overflow: hidden;
  border-radius: 12px;
}

.icon-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.category-icon-wrapper {
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 6px auto;
  background: linear-gradient(135deg, #f8f9fa, #e9ecef);
  border-radius: 12px;
}

.category-icon-wrapper .el-icon {
  color: var(--pm-primary);
  font-size: 26px;
}

.icon-text {
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  color: #666;
  background: linear-gradient(135deg, #f8f9fa, #e9ecef);
  border-radius: 12px;
  margin: 6px auto;
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
  border-radius: 6px;
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
  border-radius: 6px;
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
  border-radius: 6px;
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
