<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Plus } from "@element-plus/icons-vue";
import request from "@/utils/request";

const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const loading = ref(false);
const brandList = ref<any[]>([]);

const dialogVisible = ref(false);
const isEditMode = ref(false);
const brandForm = reactive({
  id: null as number | null,
  brandName: "",
  description: "",
});

const handleAddBrand = () => {
  isEditMode.value = false;
  brandForm.id = null;
  brandForm.brandName = "";
  brandForm.description = "";
  dialogVisible.value = true;
};

const handleEdit = (row: any) => {
  isEditMode.value = true;
  brandForm.id = row.id;
  brandForm.brandName = row.brandName || "";
  brandForm.description = row.description || "";
  dialogVisible.value = true;
};

const handleSubmit = async () => {
  if (!brandForm.brandName.trim()) {
    ElMessage.warning("请输入品牌名称");
    return;
  }

  try {
    let res;
    if (isEditMode.value) {
      res = await request.put("/admin/brand/update", {
        id: brandForm.id,
        brandName: brandForm.brandName,
        description: brandForm.description,
      });
    } else {
      res = await request.post("/admin/brand/add", {
        brandName: brandForm.brandName,
        description: brandForm.description,
      });
    }

    if (res.code === 200) {
      ElMessage.success(isEditMode.value ? "编辑品牌成功" : "新增品牌成功");
      dialogVisible.value = false;
      fetchBrandList();
    } else {
      ElMessage.error(
        res.msg || (isEditMode.value ? "编辑品牌失败" : "新增品牌失败")
      );
    }
  } catch (error: any) {
    console.error(isEditMode.value ? "编辑品牌失败:" : "新增品牌失败:", error);
    ElMessage.error(isEditMode.value ? "编辑品牌失败" : "新增品牌失败");
  }
};

const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm(`确定要删除品牌"${row.brandName}"吗？`, "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    const res = await request.delete(`/admin/brand/delete/${row.id}`);
    if (res.code === 200) {
      ElMessage.success("删除成功");
      fetchBrandList();
    } else {
      ElMessage.error(res.msg || "删除失败");
    }
  } catch (error: any) {
    if (error !== "cancel") {
      console.error("删除品牌失败:", error);
      ElMessage.error("删除品牌失败");
    }
  }
};

const fetchBrandList = async () => {
  loading.value = true;
  try {
    console.log("🚀 正在获取品牌列表...");
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value,
    };
    const res = await request.get("/admin/brand/list", { params });

    console.log("✅ 收到品牌列表响应:", res);

    if (res.code === 200 && res.data) {
      const records = res.data.records || [];
      brandList.value = records;
      total.value = res.data.total || 0;
      console.log(`📦 成功加载 ${brandList.value.length} 条品牌数据`);
    } else {
      console.error(
        "❌ 获取品牌列表失败:",
        res.msg || res.message || "未知错误"
      );
      ElMessage.error(res.msg || res.message || "获取品牌列表失败");
    }
  } catch (error: any) {
    console.error("❌ 获取品牌列表异常:", error);
    ElMessage.error("获取品牌列表失败");
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  console.log("📄 品牌管理页面已加载，开始请求数据...");
  fetchBrandList();
});
</script>

<template>
  <div class="page-container">
    <div class="table-card">
      <div class="table-header">
        <el-button type="primary" :icon="Plus" @click="handleAddBrand"
          >新增品牌</el-button
        >
      </div>
      <el-table
        :data="brandList"
        v-loading="loading"
        stripe
        style="width: 100%"
        :header-cell-style="{ fontSize: '15px', fontWeight: '700' }"
        :row-style="{ fontSize: '14px' }"
      >
        <el-table-column
          prop="id"
          label="品牌ID"
          min-width="10%"
          align="center"
        />
        <el-table-column prop="brandName" label="品牌名称" min-width="20%" />
        <el-table-column prop="description" label="品牌描述" min-width="35%" />
        <el-table-column label="修改时间" min-width="18%" align="center">
          <template #default="{ row }">{{ row.updateTime || "-" }}</template>
        </el-table-column>
        <el-table-column
          prop="action"
          label="操作"
          min-width="17%"
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
          @current-change="fetchBrandList"
          @size-change="
            () => {
              currentPage = 1;
              fetchBrandList();
            }
          "
        />
      </div>
    </div>

    <el-dialog
      v-model="dialogVisible"
      :title="isEditMode ? '编辑品牌' : '新增品牌'"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form :model="brandForm" label-width="100px">
        <el-form-item v-if="isEditMode" label="品牌ID">
          <el-input v-model.number="brandForm.id" disabled />
        </el-form-item>
        <el-form-item label="品牌名称">
          <el-input
            v-model="brandForm.brandName"
            placeholder="请输入品牌名称"
          />
        </el-form-item>
        <el-form-item label="品牌描述">
          <el-input
            v-model="brandForm.description"
            type="textarea"
            :rows="4"
            placeholder="请输入品牌描述（可选）"
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