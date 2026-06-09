<script setup lang="ts">
import { ref, onMounted, reactive } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { productApi } from "@/api";
import type { Product } from "@/types/api";
import type { UploadFile, UploadRawFile } from "element-plus";
import { Search, Refresh, Plus, Delete } from "@element-plus/icons-vue";
import request from "@/utils/request";

const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const loading = ref(false);
const productList = ref<Product[]>([]);
const categoryOptions = ref<Array<{ id: number; categoryName: string }>>([]);

const searchForm = reactive({
  productName: "",
  categoryId: null as number | null,
  brandId: null as number | null,
  status: "" as string,
});

const editDialogVisible = ref(false);
const isEditMode = ref(false);
const editForm = reactive({
  id: null as number | null,
  productName: "",
  categoryId: null as number | null,
  costPrice: 0,
  price: 0,
  stock: 0,
  imageUrl: "",
  description: "",
  status: 1,
  isDeleted: 0,
  createTime: "",
  updateTime: "",
});

const uploadHeaders = {
  "Content-Type": "multipart/form-data",
};

const handleUploadChange = async (uploadFile: UploadFile) => {
  const rawFile = uploadFile.raw;
  if (!rawFile) {
    return;
  }

  const isImage =
    rawFile.type === "image/jpeg" ||
    rawFile.type === "image/png" ||
    rawFile.type === "image/gif" ||
    rawFile.type === "image/webp";

  if (!isImage) {
    ElMessage.error("只能上传 JPG/PNG/GIF/WebP 格式的图片!");
    return;
  }

  const isLt5M = rawFile.size / 1024 / 1024 < 5;
  if (!isLt5M) {
    ElMessage.error("图片大小不能超过 5MB!");
    return;
  }

  const reader = new FileReader();
  reader.onload = async (e) => {
    editForm.imageUrl = e.target?.result as string;

    try {
      const formData = new FormData();
      formData.append("file", rawFile);

      console.log("🚀 正在上传图片到服务器...");
      const res = await request.post("/admin/common/upload", formData, {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      });

      console.log("✅ 图片上传响应:", res);

      if (res.code === 200 && res.data) {
        editForm.imageUrl = res.data;
        ElMessage.success("图片上传成功");
      } else {
        ElMessage.error(res.msg || "图片上传失败");
        editForm.imageUrl = "";
      }
    } catch (error: any) {
      console.error("❌ 图片上传失败:", error);
      ElMessage.error("图片上传失败");
      editForm.imageUrl = "";
    }
  };
  reader.readAsDataURL(rawFile);
};

const handleRemoveImage = () => {
  editForm.imageUrl = "";
};

const fetchProductList = async () => {
  loading.value = true;
  try {
    console.log("🚀 正在获取商品列表...");
    const params: any = {
      page: currentPage.value,
      pageSize: pageSize.value,
    };

    if (searchForm.productName) {
      params.productName = searchForm.productName;
    }
    if (searchForm.categoryId) {
      params.categoryId = searchForm.categoryId;
    }
    if (searchForm.brandId) {
      params.brandId = searchForm.brandId;
    }
    if (searchForm.status !== "") {
      params.status = Number(searchForm.status);
    }

    console.log("📤 请求参数:", params);
    const res = await productApi.getProductList(params);

    console.log("✅ 收到商品列表响应:", res);

    if (res.code === 200 && res.data) {
      const records = res.data.records || [];
      console.log(
        `📥 后端返回 records 数量: ${records.length}, total: ${res.data.total}`
      );
      productList.value = records;
      total.value = res.data.total || 0;
      console.log(`📦 成功加载 ${productList.value.length} 条商品数据`);
      console.log("📋 商品数据:", productList.value);
    } else {
      console.error(
        "❌ 获取商品列表失败:",
        res.msg || res.message || "未知错误"
      );
      ElMessage.error(res.msg || res.message || "获取商品列表失败");
    }
  } catch (error: any) {
    console.error("❌ 获取商品列表异常:", error);
    ElMessage.error("获取商品列表失败");
  } finally {
    loading.value = false;
  }
};

const fetchCategoryOptions = async () => {
  try {
    const res = await request.get("/admin/category/list", {
      params: {
        page: 1,
        pageSize: 1000,
      },
    });

    if (res.code === 200 && res.data) {
      categoryOptions.value = res.data.records || [];
    }
  } catch (error) {
    console.error("获取商品分类失败:", error);
  }
};

const getCategoryName = (categoryId?: number | null) => {
  if (categoryId == null) {
    return "未分类";
  }

  const category = categoryOptions.value.find(
    (item) => Number(item.id) === Number(categoryId)
  );
  return category?.categoryName || `分类 ${categoryId}`;
};

const handleSizeChange = () => {
  currentPage.value = 1;
  fetchProductList();
};

const handleSearch = () => {
  currentPage.value = 1;
  fetchProductList();
};

const handleReset = () => {
  searchForm.productName = "";
  searchForm.categoryId = null;
  searchForm.brandId = null;
  searchForm.status = "";
  currentPage.value = 1;
  fetchProductList();
};

const handleEdit = (row: Product) => {
  isEditMode.value = true;
  Object.assign(editForm, {
    id: row.id,
    productName: row.productName || "",
    categoryId: row.categoryId,
    costPrice: row.costPrice || 0,
    price: row.price,
    stock: row.stock,
    imageUrl: row.imageUrl || "",
    description: row.description || "",
    status: row.status,
    isDeleted: row.isDeleted || 0,
    createTime: row.createTime || "",
    updateTime: row.updateTime || "",
  });
  editDialogVisible.value = true;
};

const handleAdd = () => {
  isEditMode.value = false;
  Object.assign(editForm, {
    id: null,
    productName: "",
    categoryId: null,
    costPrice: 0,
    price: 0,
    stock: 0,
    imageUrl: "",
    description: "",
    status: 1,
    isDeleted: 0,
    createTime: "",
    updateTime: "",
  });
  editDialogVisible.value = true;
};

const handleSubmit = async () => {
  try {
    let res;
    if (isEditMode.value) {
      res = await request.put("/admin/product/update", editForm);
    } else {
      res = await request.post("/admin/product/add", editForm);
    }

    if (res.code === 200) {
      ElMessage.success(isEditMode.value ? "更新成功" : "新增成功");
      editDialogVisible.value = false;
      fetchProductList();
    } else {
      ElMessage.error(res.msg || (isEditMode.value ? "更新失败" : "新增失败"));
    }
  } catch (error: any) {
    console.error(isEditMode.value ? "更新商品失败:" : "新增商品失败:", error);
    ElMessage.error(isEditMode.value ? "更新商品失败" : "新增商品失败");
  }
};

const handleDelete = async (row: Product) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除商品"${row.productName || row.name}"吗？`,
      "提示",
      {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }
    );

    const res = await request.delete(`/admin/product/delete/${row.id}`);
    if (res.code === 200) {
      ElMessage.success("删除成功");
      fetchProductList();
    } else {
      ElMessage.error(res.msg || "删除失败");
    }
  } catch (error: any) {
    if (error !== "cancel") {
      console.error("删除商品失败:", error);
      ElMessage.error("删除商品失败");
    }
  }
};

onMounted(() => {
  console.log("📄 商品列表页面已加载，开始请求数据...");
  Promise.all([fetchCategoryOptions(), fetchProductList()]);
});
</script>

<template>
  <div class="page-container">
    <div class="search-card">
      <el-row :gutter="20">
        <el-col :span="5">
          <div class="search-item">
            <label>商品名称</label>
            <el-input
              v-model="searchForm.productName"
              placeholder="请输入商品名称"
              clearable
            />
          </div>
        </el-col>
        <el-col :span="5">
          <div class="search-item">
            <label>商品分类</label>
            <el-select
              v-model="searchForm.categoryId"
              placeholder="全部分类"
              style="width: 100%"
            >
              <el-option label="全部分类" :value="null" />
              <el-option
                v-for="category in categoryOptions"
                :key="category.id"
                :label="category.categoryName"
                :value="category.id"
              />
            </el-select>
          </div>
        </el-col>
        <el-col :span="5">
          <div class="search-item">
            <label>商品品牌</label>
            <el-select
              v-model="searchForm.brandId"
              placeholder="全部品牌"
              style="width: 100%"
            >
              <el-option label="全部品牌" :value="null" />
            </el-select>
          </div>
        </el-col>
        <el-col :span="3">
          <div class="search-item">
            <label>上架状态</label>
            <el-select
              v-model="searchForm.status"
              placeholder="全部"
              style="width: 100%"
            >
              <el-option label="全部" value="" />
              <el-option label="上架" value="1" />
              <el-option label="下架" value="0" />
            </el-select>
          </div>
        </el-col>
        <el-col :span="5" class="search-btns">
          <el-button type="primary" @click="handleSearch"
            ><el-icon><Search /></el-icon> 查询</el-button
          >
          <el-button @click="handleReset"
            ><el-icon><Refresh /></el-icon> 重置</el-button
          >
          <el-button type="primary" :icon="Plus" @click="handleAdd"
            >新增商品</el-button
          >
        </el-col>
      </el-row>
    </div>

    <div class="table-card">
      <el-table
        :data="productList"
        v-loading="loading"
        stripe
        style="width: 100%"
        row-class-name="table-row"
      >
        <el-table-column type="selection" width="54" align="center" />
        <el-table-column prop="id" label="编号" width="72" align="center" />
        <el-table-column prop="imageUrl" label="图片" width="96" align="center">
          <template #default="{ row }">
            <div v-if="row.imageUrl" class="product-image">
              <img :src="row.imageUrl" :alt="row.productName" />
            </div>
            <div v-else class="img-placeholder"></div>
          </template>
        </el-table-column>
        <el-table-column
          prop="productName"
          label="商品名称"
          min-width="240"
        >
          <template #default="{ row }">
            <div class="product-name">{{ row.productName }}</div>
          </template>
        </el-table-column>
        <el-table-column label="分类" min-width="150">
          <template #default="{ row }">
            <span class="category-name">{{
              getCategoryName(row.categoryId)
            }}</span>
          </template>
        </el-table-column>
        <el-table-column
          prop="costPrice"
          label="进价"
          width="120"
          align="center"
        >
          <template #default="{ row }">¥{{ row.costPrice || "0.00" }}</template>
        </el-table-column>
        <el-table-column
          prop="price"
          label="售价"
          width="120"
          align="center"
        >
          <template #default="{ row }">¥{{ row.price || "0.00" }}</template>
        </el-table-column>
        <el-table-column
          prop="status"
          label="状态"
          width="100"
          align="center"
        >
          <template #default="{ row }">
            <el-tag
              :type="row.status === 1 ? 'success' : 'info'"
              size="small"
              effect="light"
            >
              {{ row.status === 1 ? "上架" : "下架" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          prop="stock"
          label="库存"
          width="100"
          align="center"
        >
          <template #default="{ row }">{{ row.stock }}</template>
        </el-table-column>
        <el-table-column label="修改时间" width="180" align="center">
          <template #default="{ row }">
            <div>{{ row.updateTime?.split(" ")[0] || "-" }}</div>
            <div>{{ row.updateTime?.split(" ")[1] || "" }}</div>
          </template>
        </el-table-column>
        <el-table-column
          prop="action"
          label="操作"
          width="190"
          fixed="right"
          align="center"
        >
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              plain
              @click="handleEdit(row)"
              >编辑详情</el-button
            >
            <el-button
              type="danger"
              size="small"
              plain
              @click="handleDelete(row)"
              >删除</el-button
            >
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          background
          @current-change="fetchProductList"
          @size-change="handleSizeChange"
        />
      </div>
    </div>

    <el-dialog
      v-model="editDialogVisible"
      :title="isEditMode ? '编辑商品详情' : '新增商品'"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form :model="editForm" label-width="100px">
        <el-form-item v-if="isEditMode" label="商品ID">
          <el-input v-model.number="editForm.id" disabled />
        </el-form-item>
        <el-form-item label="商品名称">
          <el-input
            v-model="editForm.productName"
            placeholder="请输入商品名称"
          />
        </el-form-item>
        <el-form-item label="分类ID">
          <el-input-number v-model.number="editForm.categoryId" :min="0" />
        </el-form-item>
        <el-form-item label="进价">
          <el-input-number
            v-model.number="editForm.costPrice"
            :min="0"
            :precision="2"
          />
        </el-form-item>
        <el-form-item label="售价">
          <el-input-number
            v-model.number="editForm.price"
            :min="0"
            :precision="2"
          />
        </el-form-item>
        <el-form-item label="库存">
          <el-input-number v-model.number="editForm.stock" :min="0" />
        </el-form-item>
        <el-form-item label="商品图片">
          <el-upload
            class="image-uploader"
            :auto-upload="false"
            :show-file-list="false"
            :on-change="handleUploadChange"
            accept=".jpg,.jpeg,.png,.gif,.webp"
          >
            <div v-if="editForm.imageUrl" class="image-preview">
              <img :src="editForm.imageUrl" alt="商品图片" />
              <div class="image-actions">
                <el-icon @click.stop="handleRemoveImage"><Delete /></el-icon>
              </div>
            </div>
            <div v-else class="upload-placeholder">
              <el-icon :size="32"><Plus /></el-icon>
              <span>上传图片</span>
            </div>
          </el-upload>
        </el-form-item>
        <el-form-item label="描述">
          <el-input
            v-model="editForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入商品描述"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model.number="editForm.status" style="width: 100%">
            <el-option label="上架" :value="1" />
            <el-option label="下架" :value="0" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
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

.search-item {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.search-item label {
  font-size: 14px;
  color: #4a5568;
  font-weight: 600;
}

.search-btns {
  display: flex;
  align-items: flex-end;
  gap: 12px;
  padding-bottom: 2px;
}

.search-btns :deep(.el-button) {
  height: 40px;
  padding: 12px 24px;
  font-size: 14px;
  font-weight: 600;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.table-card {
  background: #fff;
  border-radius: 14px;
  padding: 28px 32px;
  box-shadow: 0 1px 6px rgba(0, 0, 0, 0.04);
  border: 1px solid #f0f2f5;
}

.table-card :deep(.el-table) {
  min-width: 1180px;
}

.table-card :deep(.el-table td.el-table__cell) {
  height: 76px;
}

.table-card :deep(.el-table .cell) {
  padding-right: 16px;
  padding-left: 16px;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 24px;
}

.img-placeholder {
  width: 56px;
  height: 56px;
  background: linear-gradient(135deg, #f0f2f5, #e8ecf1);
  border-radius: 8px;
  margin: 4px auto;
}

.product-image {
  width: 56px;
  height: 56px;
  margin: 4px auto;
  overflow: hidden;
  border-radius: 8px;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-name {
  font-weight: 600;
  color: #334155;
  font-size: 14px;
  line-height: 1.55;
}

.category-name {
  display: inline-flex;
  align-items: center;
  min-height: 30px;
  padding: 4px 12px;
  border: 1px solid var(--pm-primary-muted);
  border-radius: 999px;
  color: var(--pm-primary-hover);
  background: var(--pm-primary-soft);
  font-size: 13px;
  font-weight: 600;
}

.product-no {
  color: #aaa;
  font-size: 12px;
  margin-bottom: 3px;
}

.product-desc {
  color: #94a3b8;
  font-size: 12px;
  line-height: 1.4;
}

.image-uploader {
  width: 300px;
}

.image-uploader :deep(.el-upload) {
  width: 300px;
  border: 2px dashed #d9d9d9;
  border-radius: 8px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 180px;
}

.image-uploader :deep(.el-upload:hover) {
  border-color: #e8792e;
}

.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 10px;
  color: #8c939d;
  padding: 30px;
  min-height: 180px;
}

.upload-placeholder span {
  font-size: 14px;
  color: #8c939d;
}

.image-preview {
  position: relative;
  width: 100%;
  height: auto;
  min-height: 180px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.image-preview img {
  max-width: 100%;
  max-height: 300px;
  object-fit: contain;
  border-radius: 6px;
}

.image-actions {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
  border-radius: 6px;
}

.image-preview:hover .image-actions {
  opacity: 1;
}

.image-actions .el-icon {
  font-size: 24px;
  color: #fff;
  cursor: pointer;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  padding: 8px;
  transition: all 0.3s ease;
}

.image-actions .el-icon:hover {
  background: rgba(255, 77, 79, 0.8);
  transform: scale(1.1);
}
</style>
