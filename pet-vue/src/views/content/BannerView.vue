<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Plus, UploadFilled } from "@element-plus/icons-vue";
import type { UploadFile } from "element-plus";
import request from "@/utils/request";

const loading = ref(false);
const bannerList = ref<any[]>([]);

const dialogVisible = ref(false);
const isEditMode = ref(false);

const bannerForm = reactive({
  id: null as number | null,
  title: "",
  imageUrl: "",
  bannerUrl: "",
  linkUrl: "",
  sort: 0,
  status: 1 as number | null,
});

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
    bannerForm.imageUrl = e.target?.result as string;

    try {
      const formData = new FormData();
      formData.append("file", rawFile);

      const res = await request.post("/admin/common/upload", formData, {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      });

      if (res.code === 200 && res.data) {
        bannerForm.imageUrl = res.data;
        bannerForm.bannerUrl = res.data;
        ElMessage.success("上传成功");
      } else {
        ElMessage.error(res.msg || "上传失败");
        bannerForm.imageUrl = "";
        bannerForm.bannerUrl = "";
      }
    } catch (error: any) {
      console.error("上传失败:", error);
      ElMessage.error("上传失败");
      bannerForm.imageUrl = "";
      bannerForm.bannerUrl = "";
    }
  };
  reader.readAsDataURL(rawFile);
};

const handleAddBanner = () => {
  isEditMode.value = false;
  bannerForm.id = null;
  bannerForm.title = "";
  bannerForm.imageUrl = "";
  bannerForm.bannerUrl = "";
  bannerForm.linkUrl = "";
  bannerForm.sort = 0;
  bannerForm.status = 1;
  dialogVisible.value = true;
};

const handleEdit = (row: any) => {
  isEditMode.value = true;
  bannerForm.id = row.id;
  bannerForm.title = row.title || "";
  bannerForm.imageUrl = row.imageUrl || row.bannerUrl || "";
  bannerForm.bannerUrl = row.bannerUrl || row.imageUrl || "";
  bannerForm.linkUrl = row.linkUrl || "";
  bannerForm.sort = row.sort || 0;
  bannerForm.status = row.status ?? 1;
  dialogVisible.value = true;
};

const handleSubmit = async () => {
  if (!bannerForm.bannerUrl.trim() && !bannerForm.imageUrl.trim()) {
    ElMessage.warning("请上传轮播图图片");
    return;
  }
  if (!bannerForm.title.trim()) {
    ElMessage.warning("请输入轮播图标题");
    return;
  }

  try {
    let res;
    const formData: any = {
      title: bannerForm.title,
      bannerUrl: bannerForm.bannerUrl || bannerForm.imageUrl,
      linkUrl: bannerForm.linkUrl,
      sort: bannerForm.sort,
      status: bannerForm.status,
    };

    if (isEditMode.value) {
      formData.id = bannerForm.id;
      res = await request.put("/admin/banner/update", formData);
    } else {
      res = await request.post("/admin/banner/add", formData);
    }

    if (res.code === 200) {
      ElMessage.success(isEditMode.value ? "编辑成功" : "新增成功");
      dialogVisible.value = false;
      fetchBannerList();
    } else {
      ElMessage.error(res.msg || (isEditMode.value ? "编辑失败" : "新增失败"));
    }
  } catch (error: any) {
    console.error(isEditMode.value ? "编辑失败:" : "新增失败:", error);
    ElMessage.error(isEditMode.value ? "编辑失败" : "新增失败");
  }
};

const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm(`确定要删除轮播图"${row.title}"吗？`, "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    const res = await request.delete(`/admin/banner/delete/${row.id}`);
    if (res.code === 200) {
      ElMessage.success("删除成功");
      fetchBannerList();
    } else {
      ElMessage.error(res.msg || "删除失败");
    }
  } catch (error: any) {
    if (error !== "cancel") {
      console.error("删除失败:", error);
      ElMessage.error("删除失败");
    }
  }
};

const handleStatusChange = async (row: any) => {
  try {
    const newStatus = row.status === 1 ? 0 : 1;
    const res = await request.put(`/admin/banner/update`, {
      ...row,
      status: newStatus,
    });
    if (res.code === 200) {
      row.status = newStatus;
      ElMessage.success(newStatus === 1 ? "上架成功" : "下架成功");
    } else {
      ElMessage.error(res.msg || "操作失败");
    }
  } catch (error: any) {
    console.error("状态更新失败:", error);
    ElMessage.error("操作失败");
  }
};

const fetchBannerList = async () => {
  loading.value = true;
  try {
    const res = await request.get("/admin/banner/list");
    if (res.code === 200 && res.data) {
      bannerList.value = Array.isArray(res.data)
        ? res.data
        : res.data.records || [];
    } else {
      ElMessage.error(res.msg || "获取轮播图列表失败");
    }
  } catch (error: any) {
    console.error("获取轮播图列表失败:", error);
    ElMessage.error("获取轮播图列表失败");
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  fetchBannerList();
});
</script>

<template>
  <div class="page-container">
    <div class="content-header">
      <el-button type="primary" :icon="Plus" @click="handleAddBanner"
        >新增轮播图</el-button
      >
    </div>

    <div v-loading="loading" class="banner-grid">
      <div
        v-for="(item, index) in bannerList"
        :key="item.id"
        class="banner-card"
      >
        <div class="banner-image">
          <img
            v-if="item.imageUrl || item.bannerUrl"
            :src="item.imageUrl || item.bannerUrl"
            :alt="item.title"
            class="banner-img"
          />
          <div v-else class="img-placeholder"></div>
          <div class="banner-overlay">
            <el-tag size="small" effect="dark" type="warning">{{
              index + 1 <= 3
                ? "首页推荐"
                : index + 1 <= 5
                ? "活动促销"
                : "新品上架"
            }}</el-tag>
          </div>
        </div>
        <div class="banner-info">
          <h4>{{ item.title || `轮播图 ${index + 1}` }}</h4>
          <p class="banner-desc">跳转链接: {{ item.linkUrl || "-" }}</p>
          <div class="banner-meta">
            <el-tag
              :type="item.status === 1 ? 'success' : 'info'"
              size="small"
              effect="light"
              >{{ item.status === 1 ? "展示中" : "已下架" }}</el-tag
            >
            <span class="sort">排序: {{ item.sort || index + 1 }}</span>
          </div>
        </div>
        <div class="banner-actions">
          <el-button type="primary" size="small" plain @click="handleEdit(item)"
            >编辑</el-button
          >
          <el-button
            :type="item.status === 1 ? 'info' : 'success'"
            size="small"
            plain
            @click="handleStatusChange(item)"
            >{{ item.status === 1 ? "下架" : "上架" }}</el-button
          >
          <el-button
            type="danger"
            size="small"
            plain
            @click="handleDelete(item)"
            >删除</el-button
          >
        </div>
      </div>
    </div>

    <el-dialog
      v-model="dialogVisible"
      :title="isEditMode ? '编辑轮播图' : '新增轮播图'"
      width="600px"
      :close-on-click-modal="false"
      class="banner-dialog"
    >
      <el-form
        :model="bannerForm"
        label-width="100px"
        class="banner-form"
        label-position="top"
      >
        <el-form-item label="轮播图图片">
          <el-upload
            class="banner-uploader"
            :auto-upload="false"
            :show-file-list="false"
            :on-change="handleUploadChange"
            accept=".jpg,.jpeg,.png,.gif,.webp"
          >
            <img
              v-if="bannerForm.imageUrl"
              :src="bannerForm.imageUrl"
              class="banner-uploaded-img"
            />
            <div v-else class="upload-placeholder">
              <el-icon class="banner-uploader-icon"><UploadFilled /></el-icon>
              <span>点击或拖拽上传图片</span>
            </div>
          </el-upload>
          <div class="upload-tip">
            支持 JPG、PNG、GIF、WebP 格式，建议尺寸 1200×400px，大小不超过 5MB
          </div>
        </el-form-item>

        <el-form-item label="图片URL（可选）">
          <el-input
            v-model="bannerForm.bannerUrl"
            placeholder="或直接输入图片URL地址"
            clearable
            @input="(val: string) => { if (val) bannerForm.imageUrl = val; }"
          />
        </el-form-item>

        <el-form-item label="轮播图标题">
          <el-input
            v-model="bannerForm.title"
            placeholder="请输入轮播图标题"
            maxlength="50"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="跳转链接">
          <el-input
            v-model="bannerForm.linkUrl"
            placeholder="请输入点击轮播图后的跳转链接"
            clearable
          >
            <template #prepend>https://</template>
          </el-input>
        </el-form-item>

        <el-form-item label="排序值">
          <el-input-number
            v-model.number="bannerForm.sort"
            :min="0"
            :max="999"
            placeholder="数值越小越靠前"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="状态">
          <el-radio-group v-model.number="bannerForm.status">
            <el-radio :value="1">展示中</el-radio>
            <el-radio :value="0">已下架</el-radio>
          </el-radio-group>
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
  gap: 24px;
}

.content-header {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 20px;
}

.content-header :deep(.el-button) {
  height: 40px;
  padding: 12px 28px;
  font-size: 14px;
  font-weight: 600;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.content-header :deep(.el-button--primary) {
  background: linear-gradient(135deg, #e8792e 0%, #d96920 100%);
  border: none;
  box-shadow: 0 4px 15px rgba(232, 121, 46, 0.35);
}

.content-header :deep(.el-button--primary:hover) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(232, 121, 46, 0.45);
}

.banner-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
}

.banner-card {
  background: #fff;
  border-radius: 14px;
  overflow: hidden;
  box-shadow: 0 1px 6px rgba(0, 0, 0, 0.04);
  border: 1px solid #f0f2f5;
  transition: transform 0.25s, box-shadow 0.25s;
}

.banner-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
}

.banner-image {
  position: relative;
  height: 180px;
  overflow: hidden;
  background: linear-gradient(135deg, #e67e22, #d35400);
}

.banner-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.img-placeholder {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #e67e22, #d35400);
}

.banner-overlay {
  position: absolute;
  top: 12px;
  right: 12px;
}

.banner-info {
  padding: 20px;
}

.banner-info h4 {
  font-size: 15px;
  font-weight: 600;
  color: #334155;
  margin: 0 0 8px;
}

.banner-desc {
  font-size: 13px;
  color: #999;
  margin: 0 0 14px;
  word-break: break-all;
}

.banner-meta {
  display: flex;
  align-items: center;
  gap: 12px;
}

.sort {
  font-size: 13px;
  color: #bbb;
}

.banner-actions {
  padding: 0 20px 20px;
  display: flex;
  gap: 8px;
}

@media (max-width: 1200px) {
  .banner-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .banner-grid {
    grid-template-columns: 1fr;
  }
}

.banner-card :deep(.el-button--primary.is-plain) {
  border-color: #e8792e;
  color: #e8792e;
  background: #f0f4ff;
  font-weight: 600;
  border-radius: 6px;
  transition: all 0.3s ease;
}

.banner-card :deep(.el-button--primary.is-plain:hover) {
  background: linear-gradient(135deg, #e8792e 0%, #d96920 100%);
  color: #fff;
  border-color: transparent;
  transform: translateY(-1px);
  box-shadow: 0 3px 10px rgba(232, 121, 46, 0.35);
}

.banner-card :deep(.el-button--danger.is-plain) {
  border-color: #ff6b6b;
  color: #ff6b6b;
  background: #fff5f5;
  font-weight: 600;
  border-radius: 6px;
  transition: all 0.3s ease;
}

.banner-card :deep(.el-button--danger.is-plain:hover) {
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a5a 100%);
  color: #fff;
  border-color: transparent;
  transform: translateY(-1px);
  box-shadow: 0 3px 10px rgba(255, 107, 107, 0.35);
}

.banner-card :deep(.el-button--info.is-plain) {
  border-color: #909399;
  color: #909399;
  background: #f4f4f5;
  font-weight: 600;
  border-radius: 6px;
  transition: all 0.3s ease;
}

.banner-card :deep(.el-button--info.is-plain:hover) {
  background: linear-gradient(135deg, #909399 0%, #6a6d73 100%);
  color: #fff;
  border-color: transparent;
  transform: translateY(-1px);
  box-shadow: 0 3px 10px rgba(144, 147, 153, 0.35);
}

.banner-card :deep(.el-button--warning.is-plain) {
  border-color: #e6a23c;
  color: #e6a23c;
  background: #fdf6ec;
  font-weight: 600;
  border-radius: 6px;
  transition: all 0.3s ease;
}

.banner-card :deep(.el-button--warning.is-plain:hover) {
  background: linear-gradient(135deg, #e6a23c 0%, #cf8e2e 100%);
  color: #fff;
  border-color: transparent;
  transform: translateY(-1px);
  box-shadow: 0 3px 10px rgba(230, 162, 60, 0.35);
}

.banner-card :deep(.el-button--success.is-plain) {
  border-color: #67c23a;
  color: #67c23a;
  background: #f0f9eb;
  font-weight: 600;
  border-radius: 6px;
  transition: all 0.3s ease;
}

.banner-card :deep(.el-button--success.is-plain:hover) {
  background: linear-gradient(135deg, #67c23a 0%, #529b2e 100%);
  color: #fff;
  border-color: transparent;
  transform: translateY(-1px);
  box-shadow: 0 3px 10px rgba(103, 194, 58, 0.35);
}
</style>

<style>
.banner-dialog .el-dialog__body {
  padding: 24px 28px;
}

.banner-form .el-form-item__label {
  font-weight: 600;
  color: #34495e;
  font-size: 14px;
  margin-bottom: 8px;
}

.banner-uploader {
  width: 100%;
}

.banner-uploader .el-upload {
  width: 100%;
  height: 240px;
  border: 2px dashed #dcdfe6;
  border-radius: 10px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fafafa;
}

.banner-uploader .el-upload:hover {
  border-color: #e8792e;
  background: #f0f4ff;
}

.banner-uploader-icon {
  font-size: 48px;
  color: #c0c4cc;
  text-align: center;
}

.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
}

.upload-placeholder span {
  font-size: 14px;
  color: #909399;
}

.banner-uploaded-img {
  width: 100%;
  height: 240px;
  object-fit: cover;
  display: block;
}

.upload-tip {
  margin-top: 8px;
  font-size: 12px;
  color: #909399;
  line-height: 1.5;
}

.banner-form .el-input__wrapper {
  height: 44px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.banner-form .el-input__wrapper:focus-within {
  box-shadow: 0 0 0 2px rgba(232, 121, 46, 0.2);
}

.banner-form .el-textarea__inner {
  min-height: 80px;
  border-radius: 8px;
}

.banner-form .el-input-number {
  width: 100%;
}

.banner-form .el-input-number .el-input__wrapper {
  height: 44px;
}

.banner-dialog .el-dialog__footer {
  padding: 16px 28px 24px;
  border-top: 1px solid #f0f2f5;
}

.banner-dialog .el-dialog__footer .el-button {
  height: 40px;
  padding: 12px 32px;
  font-size: 14px;
  font-weight: 600;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.banner-dialog .el-dialog__footer .el-button--primary {
  background: linear-gradient(135deg, #e8792e 0%, #d96920 100%);
  border: none;
  box-shadow: 0 4px 15px rgba(232, 121, 46, 0.35);
}

.banner-dialog .el-dialog__footer .el-button--primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(232, 121, 46, 0.45);
}
</style>
