<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Plus } from "@element-plus/icons-vue";
import request from "@/utils/request";

const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const loading = ref(false);
const addressList = ref<any[]>([]);

const dialogVisible = ref(false);
const isEditMode = ref(false);
const addressForm = reactive({
  id: null as number | null,
  userId: null as number | null,
  consignee: "",
  sex: "",
  phone: "",
  provinceCode: "",
  provinceName: "",
  cityCode: "",
  cityName: "",
  districtCode: "",
  districtName: "",
  detail: "",
  isDefault: 0,
  label: "",
});

const searchForm = reactive({
  userId: "" as string,
  consignee: "" as string,
});

const handleAddAddress = () => {
  isEditMode.value = false;
  addressForm.id = null;
  addressForm.userId = null;
  addressForm.consignee = "";
  addressForm.sex = "";
  addressForm.phone = "";
  addressForm.provinceCode = "";
  addressForm.provinceName = "";
  addressForm.cityCode = "";
  addressForm.cityName = "";
  addressForm.districtCode = "";
  addressForm.districtName = "";
  addressForm.detail = "";
  addressForm.isDefault = 0;
  addressForm.label = "";
  dialogVisible.value = true;
};

const handleEdit = (row: any) => {
  isEditMode.value = true;
  addressForm.id = row.id;
  addressForm.userId = row.userId || null;
  addressForm.consignee = row.consignee || "";
  addressForm.sex = row.sex || "";
  addressForm.phone = row.phone || "";
  addressForm.provinceCode = row.provinceCode || "";
  addressForm.provinceName = row.provinceName || "";
  addressForm.cityCode = row.cityCode || "";
  addressForm.cityName = row.cityName || "";
  addressForm.districtCode = row.districtCode || "";
  addressForm.districtName = row.districtName || "";
  addressForm.detail = row.detail || "";
  addressForm.isDefault = row.isDefault ?? 0;
  addressForm.label = row.label || "";
  dialogVisible.value = true;
};

const handleSubmit = async () => {
  if (!addressForm.consignee.trim()) {
    ElMessage.warning("请输入收货人姓名");
    return;
  }
  if (!addressForm.phone.trim()) {
    ElMessage.warning("请输入收货人手机号");
    return;
  }
  if (!addressForm.detail.trim()) {
    ElMessage.warning("请输入详细地址");
    return;
  }

  try {
    let res;
    const formData: any = {
      userId: addressForm.userId,
      consignee: addressForm.consignee,
      sex: addressForm.sex,
      phone: addressForm.phone,
      provinceCode: addressForm.provinceCode,
      provinceName: addressForm.provinceName,
      cityCode: addressForm.cityCode,
      cityName: addressForm.cityName,
      districtCode: addressForm.districtCode,
      districtName: addressForm.districtName,
      detail: addressForm.detail,
      isDefault: addressForm.isDefault,
      label: addressForm.label,
    };

    if (isEditMode.value) {
      formData.id = addressForm.id;
      res = await request.put("/admin/address/update", formData);
    } else {
      res = await request.post("/admin/address/add", formData);
    }

    if (res.code === 200) {
      ElMessage.success(isEditMode.value ? "编辑地址簿成功" : "新增地址簿成功");
      dialogVisible.value = false;
      fetchAddressList();
    } else {
      ElMessage.error(
        res.msg || (isEditMode.value ? "编辑地址簿失败" : "新增地址簿失败")
      );
    }
  } catch (error: any) {
    console.error(
      isEditMode.value ? "编辑地址簿失败:" : "新增地址簿失败:",
      error
    );
    ElMessage.error(isEditMode.value ? "编辑地址簿失败" : "新增地址簿失败");
  }
};

const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除收货人"${row.consignee}"的地址吗？`,
      "提示",
      {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }
    );

    const res = await request.delete(`/admin/address/delete/${row.id}`);
    if (res.code === 200) {
      ElMessage.success("删除成功");
      fetchAddressList();
    } else {
      ElMessage.error(res.msg || "删除失败");
    }
  } catch (error: any) {
    if (error !== "cancel") {
      console.error("删除地址簿失败:", error);
      ElMessage.error("删除地址簿失败");
    }
  }
};

const fetchAddressList = async () => {
  loading.value = true;
  try {
    console.log("🚀 正在获取地址簿列表...");
    const params: any = {
      page: currentPage.value,
      pageSize: pageSize.value,
    };

    if (searchForm.userId) {
      params.userId = Number(searchForm.userId);
    }
    if (searchForm.consignee) {
      params.consignee = searchForm.consignee;
    }

    console.log("📤 请求参数:", params);
    const res = await request.get("/admin/address/list", { params });

    console.log("✅ 收到地址簿列表响应:", res);

    if (res.code === 200 && res.data) {
      const records = res.data.records || [];
      addressList.value = records;
      total.value = res.data.total || 0;
      console.log(`📦 成功加载 ${addressList.value.length} 条地址簿数据`);
    } else {
      console.error(
        "❌ 获取地址簿列表失败:",
        res.msg || res.message || "未知错误"
      );
      ElMessage.error(res.msg || res.message || "获取地址簿列表失败");
    }
  } catch (error: any) {
    console.error("❌ 获取地址簿列表异常:", error);
    ElMessage.error("获取地址簿列表失败");
  } finally {
    loading.value = false;
  }
};

const handleSearch = () => {
  currentPage.value = 1;
  fetchAddressList();
};

const handleReset = () => {
  searchForm.userId = "";
  searchForm.consignee = "";
  currentPage.value = 1;
  fetchAddressList();
};

onMounted(() => {
  console.log("📄 地址管理页面已加载，开始请求数据...");
  fetchAddressList();
});
</script>

<template>
  <div class="page-container">
    <div class="search-card">
      <el-form :inline="true" class="search-form">
        <el-form-item label="用户ID">
          <el-input
            v-model="searchForm.userId"
            placeholder="请输入用户ID"
            clearable
            style="width: 180px"
          />
        </el-form-item>
        <el-form-item label="收货人">
          <el-input
            v-model="searchForm.consignee"
            placeholder="请输入收货人姓名"
            clearable
            style="width: 180px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-card">
      <div class="table-header">
        <el-button type="primary" :icon="Plus" @click="handleAddAddress"
          >新增地址</el-button
        >
      </div>
      <el-table
        :data="addressList"
        v-loading="loading"
        stripe
        style="width: 100%"
        :header-cell-style="{ fontSize: '15px', fontWeight: '700' }"
        :row-style="{ fontSize: '14px' }"
      >
        <el-table-column prop="id" label="ID" min-width="6%" align="center" />
        <el-table-column
          prop="userId"
          label="用户ID"
          min-width="8%"
          align="center"
        />
        <el-table-column
          prop="consignee"
          label="收货人"
          min-width="10%"
          align="center"
        />
        <el-table-column prop="sex" label="性别" min-width="6%" align="center">
          <template #default="{ row }">
            {{ row.sex === "男" ? "男" : row.sex === "女" ? "女" : "-" }}
          </template>
        </el-table-column>
        <el-table-column
          prop="phone"
          label="手机号"
          min-width="12%"
          align="center"
        />
        <el-table-column label="省市区" min-width="18%" align="center">
          <template #default="{ row }">
            {{ row.provinceName }} {{ row.cityName }} {{ row.districtName }}
          </template>
        </el-table-column>
        <el-table-column
          prop="detail"
          label="详细地址"
          min-width="20%"
          show-overflow-tooltip
        />
        <el-table-column
          prop="isDefault"
          label="默认"
          min-width="7%"
          align="center"
        >
          <template #default="{ row }">
            <el-tag
              :type="row.isDefault === 1 ? 'success' : 'info'"
              size="small"
              effect="light"
            >
              {{ row.isDefault === 1 ? "是" : "否" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          prop="label"
          label="标签"
          min-width="10%"
          align="center"
        >
          <template #default="{ row }">
            <el-tag v-if="row.label" size="small" effect="plain">{{
              row.label
            }}</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column
          prop="action"
          label="操作"
          min-width="14%"
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
          @current-change="fetchAddressList"
          @size-change="
            () => {
              currentPage = 1;
              fetchAddressList();
            }
          "
        />
      </div>
    </div>

    <el-dialog
      v-model="dialogVisible"
      :title="isEditMode ? '编辑地址' : '新增地址'"
      width="650px"
      :close-on-click-modal="false"
    >
      <el-form :model="addressForm" label-width="90px">
        <el-row :gutter="16">
          <el-col :span="10">
            <el-form-item label="收货人姓名">
              <el-input
                v-model="addressForm.consignee"
                placeholder="请输入收货人姓名"
              />
            </el-form-item>
          </el-col>
          <el-col :span="7">
            <el-form-item label="性别">
              <el-select
                v-model="addressForm.sex"
                placeholder="请选择"
                style="width: 100%"
              >
                <el-option label="男" value="男" />
                <el-option label="女" value="女" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="7">
            <el-form-item label="手机号">
              <el-input
                v-model="addressForm.phone"
                placeholder="手机号"
                maxlength="11"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">地区信息</el-divider>

        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="省份">
              <el-input
                v-model="addressForm.provinceName"
                placeholder="省份名称"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="城市">
              <el-input
                v-model="addressForm.cityName"
                placeholder="城市名称"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="区县">
              <el-input
                v-model="addressForm.districtName"
                placeholder="区县名称"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="详细地址">
          <el-input
            v-model="addressForm.detail"
            type="textarea"
            :rows="3"
            placeholder="请输入详细地址，如街道、楼牌号等"
          />
        </el-form-item>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="是否默认">
              <el-select
                v-model.number="addressForm.isDefault"
                placeholder="请选择"
                style="width: 100%"
              >
                <el-option label="否" :value="0" />
                <el-option label="是" :value="1" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="标签">
              <el-input
                v-model="addressForm.label"
                placeholder="如：家、公司、学校"
              />
            </el-form-item>
          </el-col>
        </el-row>
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
