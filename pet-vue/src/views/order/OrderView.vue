<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Plus } from "@element-plus/icons-vue";
import request from "@/utils/request";

const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const loading = ref(false);
const orderList = ref<any[]>([]);

const dialogVisible = ref(false);
const isEditMode = ref(false);
const orderForm = reactive({
  id: null as number | null,
  orderNo: "",
  userId: 0,
  totalAmount: 0,
  payMethod: "",
  status: 1 as number | null,
});

const fetchOrderList = async () => {
  loading.value = true;
  try {
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value,
    };
    const res = await request.get("/admin/order/list", { params });

    if (res.code === 200 && res.data) {
      orderList.value = res.data.records || [];
      total.value = res.data.total || 0;
    } else {
      ElMessage.error(res.msg || res.message || "获取订单列表失败");
    }
  } catch (error: any) {
    console.error("获取订单列表失败:", error);
    ElMessage.error("获取订单列表失败");
  } finally {
    loading.value = false;
  }
};

const getStatusText = (status: number | string) => {
  const map: Record<string, string> = {
    0: "待付款",
    1: "待发货",
    2: "待收货",
    3: "已完成",
    4: "已取消",
  };
  return map[String(status)] || "未知状态";
};

const handleEdit = (row: any) => {
  isEditMode.value = true;
  orderForm.id = row.id;
  orderForm.orderNo = row.orderNo || "";
  orderForm.userId = row.userId || 0;
  orderForm.totalAmount = row.totalAmount || 0;
  orderForm.payMethod = row.payMethod || "";
  orderForm.status = row.status;
  dialogVisible.value = true;
};

const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm(`确定要删除订单"${row.orderNo}"吗？`, "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    const res = await request.delete(`/admin/order/delete/${row.id}`);
    if (res.code === 200) {
      ElMessage.success("删除成功");
      fetchOrderList();
    } else {
      ElMessage.error(res.msg || "删除失败");
    }
  } catch (error: any) {
    if (error !== "cancel") {
      console.error("删除订单失败:", error);
      ElMessage.error("删除失败");
    }
  }
};

const getStatusType = (status: number | string) => {
  const typeMap: Record<string, string> = {
    0: "info", // 待付款
    1: "warning", // 待发货
    2: "primary", // 待收货
    3: "success", // 已完成
    4: "danger", // 已取消
  };
  return typeMap[String(status)] || "info";
};

const handleAddOrder = () => {
  isEditMode.value = false;
  orderForm.id = null;
  orderForm.orderNo = "";
  orderForm.userId = 0;
  orderForm.totalAmount = 0;
  orderForm.payMethod = "";
  orderForm.status = 1;
  dialogVisible.value = true;
};

const handleSubmit = async () => {
  if (!orderForm.orderNo.trim()) {
    ElMessage.warning("请输入订单编号");
    return;
  }

  try {
    let res;
    if (isEditMode.value) {
      res = await request.put("/admin/order/update", {
        id: orderForm.id,
        orderNo: orderForm.orderNo,
        userId: orderForm.userId,
        totalAmount: orderForm.totalAmount,
        payMethod: orderForm.payMethod,
        status: orderForm.status,
      });
    } else {
      res = await request.post("/admin/order/add", {
        orderNo: orderForm.orderNo,
        userId: orderForm.userId,
        totalAmount: orderForm.totalAmount,
        payMethod: orderForm.payMethod,
        status: orderForm.status,
      });
    }

    if (res.code === 200) {
      ElMessage.success(isEditMode.value ? "编辑订单成功" : "新增订单成功");
      dialogVisible.value = false;
      fetchOrderList();
    } else {
      ElMessage.error(
        res.msg || (isEditMode.value ? "编辑订单失败" : "新增订单失败")
      );
    }
  } catch (error: any) {
    console.error(isEditMode.value ? "编辑订单失败:" : "新增订单失败:", error);
    ElMessage.error(isEditMode.value ? "编辑订单失败" : "新增订单失败");
  }
};

onMounted(() => {
  fetchOrderList();
});
</script>

<template>
  <div class="page-container">
    <div class="table-card">
      <div class="table-header">
        <el-button type="primary" :icon="Plus" @click="handleAddOrder"
          >新增订单</el-button
        >
      </div>
      <el-table
        class="order-table"
        :data="orderList"
        v-loading="loading"
        stripe
        style="width: 100%"
        :header-cell-style="{ fontSize: '15px', fontWeight: '700' }"
        :row-style="{ fontSize: '14px' }"
      >
        <el-table-column
          prop="id"
          label="订单ID"
          width="120"
          align="center"
        />
        <el-table-column
          prop="orderNo"
          label="订单编号"
          min-width="280"
        >
          <template #default="{ row }">
            <span class="order-number">{{ row.orderNo || "-" }}</span>
          </template>
        </el-table-column>
        <el-table-column
          prop="userId"
          label="用户ID"
          width="150"
          align="center"
        />
        <el-table-column
          prop="totalAmount"
          label="订单金额"
          width="170"
          align="center"
        >
          <template #default="{ row }">
            <span class="order-amount">¥{{ row.totalAmount || "0.00" }}</span>
          </template>
        </el-table-column>
        <el-table-column
          prop="status"
          label="订单状态"
          width="160"
          align="center"
        >
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" effect="light">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          prop="createTime"
          label="下单时间"
          min-width="230"
          align="center"
        >
          <template #default="{ row }">
            <div v-if="row.createTime" class="order-time">
              <span>{{ String(row.createTime).split(" ")[0] }}</span>
              <small>{{ String(row.createTime).split(" ")[1] || "" }}</small>
            </div>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column
          prop="action"
          label="操作"
          width="220"
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
          @current-change="fetchOrderList"
          @size-change="
            () => {
              currentPage = 1;
              fetchOrderList();
            }
          "
        />
      </div>
    </div>

    <el-dialog
      v-model="dialogVisible"
      :title="isEditMode ? '编辑订单' : '新增订单'"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form :model="orderForm" label-width="100px">
        <el-form-item v-if="isEditMode" label="订单ID">
          <el-input v-model.number="orderForm.id" disabled />
        </el-form-item>
        <el-form-item label="订单编号">
          <el-input v-model="orderForm.orderNo" placeholder="请输入订单编号" />
        </el-form-item>
        <el-form-item label="用户ID">
          <el-input
            v-model.number="orderForm.userId"
            type="number"
            placeholder="请输入用户ID"
          />
        </el-form-item>
        <el-form-item label="订单金额">
          <el-input
            v-model.number="orderForm.totalAmount"
            type="number"
            placeholder="请输入订单金额"
          >
            <template #prepend>¥</template>
          </el-input>
        </el-form-item>
        <el-form-item label="支付方式">
          <el-select v-model="orderForm.payMethod" placeholder="请选择支付方式">
            <el-option label="微信支付" value="wechat" />
            <el-option label="支付宝" value="alipay" />
            <el-option label="刷卡" value="card" />
          </el-select>
        </el-form-item>
        <el-form-item label="订单状态">
          <el-select v-model="orderForm.status" placeholder="请选择订单状态">
            <el-option label="待付款" :value="0" />
            <el-option label="待发货" :value="1" />
            <el-option label="待收货" :value="2" />
            <el-option label="已完成" :value="3" />
            <el-option label="已取消" :value="4" />
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

.order-table {
  min-width: 1280px;
}

:deep(.el-table th.el-table__cell) {
  background-color: #fafbfc !important;
  color: #555;
  font-weight: 700;
  font-size: 15px;
  padding: 20px 0;
}

:deep(.el-table td.el-table__cell) {
  padding: 22px 0;
  color: #444;
}

.order-number {
  color: #2f3a4c;
  font-weight: 600;
  letter-spacing: 0.2px;
}

.order-amount {
  color: #e66b1f;
  font-size: 15px;
  font-weight: 700;
}

.order-time {
  display: inline-flex;
  flex-direction: column;
  gap: 4px;
  line-height: 1.3;
}

.order-time span {
  color: #374151;
  font-weight: 500;
}

.order-time small {
  color: #8a94a6;
  font-size: 13px;
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
</style>
