<template>
  <div class="orders-page">
    <div class="orders-container">
      <el-tabs
        v-model="activeTab"
        class="order-tabs"
        @tab-change="handleTabChange"
      >
        <el-tab-pane label="全部订单" name="all"></el-tab-pane>
        <el-tab-pane label="待付款" name="0"></el-tab-pane>
        <el-tab-pane label="待发货" name="1"></el-tab-pane>
        <el-tab-pane label="已发货" name="2"></el-tab-pane>
        <el-tab-pane label="已收货" name="3"></el-tab-pane>
      </el-tabs>

      <div class="orders-list">
        <div v-for="order in orders" :key="order.id" class="order-card">
          <div class="card-header">
            <div class="header-left">
              <span class="order-no">订单编号：{{ order.orderNo }}</span>
              <span class="create-time">{{ order.createTime }}</span>
            </div>
            <el-tag
              :type="getStatusType(order.status)"
              size="large"
              effect="dark"
            >
              {{ getStatusText(order.status) }}
            </el-tag>
          </div>

          <div class="card-body">
            <div class="product-info">
              <div class="info-item product-name-item">
                <span class="label">商品名称</span>
                <span class="value product-name">{{ order.productName }}</span>
              </div>
              <div class="info-item">
                <span class="label">收货地址</span>
                <span class="value address">{{ order.address }}</span>
              </div>
            </div>

            <div class="price-info">
              <div class="payment-method">
                <el-icon><Wallet /></el-icon>
                <span>{{ order.paymentMethod }}</span>
              </div>
              <div class="amount">
                <span class="amount-label">付款金额</span>
                <span class="amount-value">¥{{ order.amount }}</span>
              </div>
            </div>

            <div class="action-area">
              <!-- 待付款：取消订单、支付订单 -->
              <div v-if="order.status === 0" class="btn-group">
                <el-button
                  type="danger"
                  plain
                  round
                  @click="cancelOrder(order)"
                >
                  取消订单
                </el-button>
                <el-button type="warning" round @click="payOrder(order)">
                  支付订单
                </el-button>
              </div>

              <!-- 待发货：取消订单、催发货 -->
              <div v-if="order.status === 1" class="btn-group">
                <el-button
                  type="danger"
                  plain
                  round
                  @click="cancelOrder(order)"
                >
                  取消订单
                </el-button>
                <el-button type="primary" round @click="urgeDelivery(order)">
                  催发货
                </el-button>
              </div>

              <!-- 已发货：取消订单、确认收货 -->
              <div v-if="order.status === 2" class="btn-group">
                <el-button
                  type="danger"
                  plain
                  round
                  @click="cancelOrder(order)"
                >
                  取消订单
                </el-button>
                <el-button type="warning" round @click="confirmReceive(order)">
                  确认收货
                </el-button>
              </div>

              <!-- 已收货：退款、申请发票 -->
              <div v-if="order.status === 3" class="btn-group">
                <el-button type="info" plain round @click="refundOrder(order)">
                  退款
                </el-button>
                <el-button type="primary" round @click="applyInvoice(order)">
                  申请发票
                </el-button>
              </div>
            </div>
          </div>
        </div>

        <el-empty v-if="orders.length === 0" description="暂无订单数据" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Wallet } from "@element-plus/icons-vue";
import { getOrderList, deleteOrder } from "@/api/order";

const activeTab = ref("all");

const orders = ref([]);

const fetchOrders = async () => {
  try {
    const params = {
      page: 1,
      pageSize: 10,
    };

    if (activeTab.value !== "all") {
      params.status = activeTab.value;
    }

    const res = await getOrderList(params);
    if (res.data && res.data.records) {
      orders.value = res.data.records.map((order) => ({
        ...order,
        productNo: order.orderNo,
        address: order.orderAddress,
        productName:
          order.orderDetailList && order.orderDetailList.length > 0
            ? order.orderDetailList.map((item) => item.productName).join("、")
            : "",
        amount: order.totalAmount,
        status: order.status,
      }));
    }
  } catch (error) {
    console.error("获取订单列表失败:", error);
    ElMessage.error("获取订单列表失败");
  }
};

const handleTabChange = () => {
  fetchOrders();
};

onMounted(() => {
  fetchOrders();
});

function canShowInvoice(order) {
  return order.status !== 5 && order.status !== 6;
}

function getStatusText(status) {
  const map = {
    0: "待付款",
    1: "待发货",
    2: "已发货",
    3: "已收货",
    4: "待评价",
    5: "已完成",
    6: "已取消",
  };
  return map[status] || status;
}

function getStatusType(status) {
  const map = {
    0: "warning",
    1: "",
    2: "success",
    3: "info",
    4: "success",
    5: "success",
    6: "danger",
  };
  return map[status] || "info";
}

function applyInvoice(order) {
  ElMessageBox.confirm(`确定要为订单 ${order.orderNo} 申请发票吗？`, "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "info",
  })
    .then(() => {
      ElMessage.success("发票申请已提交，请等待商家处理");
    })
    .catch(() => {});
}

async function cancelOrder(order) {
  try {
    await ElMessageBox.confirm(`确定要取消订单 ${order.orderNo} 吗？`, "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    const res = await deleteOrder(order.id);
    if (res.code === 200 || res.code === 0) {
      ElMessage.success("订单已取消");
      fetchOrders();
    } else {
      ElMessage.error(res.message || "取消订单失败");
    }
  } catch (error) {
    if (error !== "cancel") {
      console.error("取消订单失败:", error);
      ElMessage.error("取消订单失败，请重试");
    }
  }
}

function payOrder(order) {
  ElMessageBox.confirm(`确定要支付订单 ${order.orderNo} 吗？`, "提示", {
    confirmButtonText: "确定支付",
    cancelButtonText: "取消",
    type: "info",
  })
    .then(() => {
      ElMessage.success("支付成功");
      fetchOrders();
    })
    .catch(() => {});
}

function urgeDelivery(order) {
  ElMessage.success(`已向商家发送催促提醒，订单 ${order.orderNo} 会尽快发货`);
}

function confirmReceive(order) {
  ElMessageBox.confirm("确定已收到商品吗？", "提示", {
    confirmButtonText: "确认收货",
    cancelButtonText: "取消",
    type: "info",
  })
    .then(() => {
      order.status = "received";
      ElMessage.success("确认收货成功");
    })
    .catch(() => {});
}

function refundOrder(order) {
  ElMessageBox.confirm("确定要申请退款吗？", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(() => {
      ElMessage.success("退款申请已提交，请等待商家处理");
    })
    .catch(() => {});
}
</script>

<style scoped>
.orders-page {
  min-height: 100vh;
  background-color: #f5f7fa;
  padding-top: 30px;
}

.orders-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 30px 60px;
}

.order-tabs {
  background: #fff;
  border-radius: 12px;
  padding: 20px 25px 0;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.06);
  margin-bottom: 20px;
}

:deep(.el-tabs__header) {
  margin-bottom: 0;
}

:deep(.el-tabs__item) {
  font-size: 15px !important;
  font-weight: 500 !important;
  padding: 0 28px !important;
  height: 45px !important;
  line-height: 45px !important;
}

:deep(.el-tabs__item.is-active) {
  color: #409eff !important;
  font-weight: bold !important;
}

:deep(.el-tabs__active-bar) {
  height: 3px !important;
}

.orders-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.order-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: all 0.3s;
}

.order-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
  transform: translateY(-2px);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 18px 24px;
  background: linear-gradient(to right, #fafafa, #fff);
  border-bottom: 1px solid #f0f0f0;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 24px;
}

.order-no {
  font-size: 14px;
  color: #333;
  font-weight: 600;
}

.create-time {
  font-size: 13px;
  color: #999;
}

.card-body {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr;
  gap: 24px;
  padding: 24px;
  align-items: center;
}

.product-info {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.info-item {
  display: flex;
  align-items: baseline;
  gap: 8px;
}

.label {
  font-size: 13px;
  color: #999;
  white-space: nowrap;
  min-width: 70px;
}

.value {
  font-size: 14px;
  color: #333;
}

.product-name {
  font-weight: 500;
  color: #409eff;
  word-wrap: break-word;
  word-break: break-all;
  line-height: 1.5;
  max-width: 100%;
}

.address {
  color: #666;
}

.price-info {
  display: flex;
  flex-direction: column;
  gap: 12px;
  align-items: center;
  justify-content: center;
  padding: 0 20px;
  border-left: 1px solid #f0f0f0;
  border-right: 1px solid #f0f0f0;
}

.payment-method {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #666;
}

.amount {
  text-align: center;
}

.amount-label {
  display: block;
  font-size: 13px;
  color: #999;
  margin-bottom: 4px;
}

.amount-value {
  font-size: 26px;
  color: #ff5722;
  font-weight: bold;
}

.action-area {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
}

.btn-group {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  justify-content: center;
}

.btn-group .el-button {
  min-width: 100px;
  padding: 12px 20px;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.btn-group .el-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}
</style>
