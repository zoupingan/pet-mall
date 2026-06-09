<template>
  <div class="confirm-order-page">
    <div class="confirm-order-container">
      <h1 class="page-title">确认订单</h1>

      <div class="order-content">
        <div class="order-left">
          <div class="address-section">
            <h2 class="section-title">收货地址</h2>
            <div v-if="addresses.length > 0" class="address-list">
              <div
                v-for="addr in addresses"
                :key="addr.id"
                class="address-card"
                :class="{ selected: selectedAddress?.id === addr.id }"
                @click="selectedAddress = addr"
              >
                <div class="address-info">
                  <div class="address-header">
                    <span class="address-name">{{ addr.name }}</span>
                    <span class="address-phone">{{ addr.phone }}</span>
                    <el-tag v-if="addr.label" size="small" type="warning">{{
                      addr.label
                    }}</el-tag>
                    <el-tag
                      v-if="addr.isDefault === 1"
                      size="small"
                      type="success"
                      >默认</el-tag
                    >
                  </div>
                  <div class="address-detail">{{ addr.fullAddress }}</div>
                </div>
                <el-icon
                  v-if="selectedAddress?.id === addr.id"
                  class="check-icon"
                  color="#ff9b7a"
                  ><Select
                /></el-icon>
              </div>
              <div class="add-address-btn" @click="openAddressDialog">
                <el-icon><Plus /></el-icon>
                <span>添加新地址</span>
              </div>
            </div>
            <div v-else class="no-address">
              <el-empty description="暂无收货地址" :image-size="80">
                <el-button type="primary" @click="openAddressDialog"
                  >添加地址</el-button
                >
              </el-empty>
            </div>
          </div>

          <div class="products-section">
            <h2 class="section-title">商品信息</h2>
            <div class="product-list">
              <div
                v-for="item in orderItems"
                :key="item.id"
                class="product-item"
              >
                <div class="product-image">
                  <img :src="item.image" :alt="item.name" />
                </div>
                <div class="product-info">
                  <div class="product-name">{{ item.name }}</div>
                  <div class="product-price">¥{{ item.price.toFixed(2) }}</div>
                </div>
                <div class="product-quantity">x{{ item.quantity }}</div>
                <div class="product-subtotal">
                  ¥{{ (item.price * item.quantity).toFixed(2) }}
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="order-right">
          <div class="order-summary">
            <h2 class="summary-title">订单摘要</h2>
            <div class="summary-item">
              <span class="label">商品总数</span>
              <span class="value">{{ totalQuantity }} 件</span>
            </div>
            <div class="summary-item total">
              <span class="label">订单总价</span>
              <span class="value">¥{{ totalPrice.toFixed(2) }}</span>
            </div>
            <el-button
              type="warning"
              size="large"
              class="pay-btn"
              :loading="isPaying"
              @click="handlePay"
            >
              确认下单
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <el-dialog
      v-model="addressDialogVisible"
      title="添加收货地址"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="addressFormRef"
        :model="addressForm"
        :rules="addressRules"
        label-width="80px"
      >
        <el-form-item label="收货人" prop="consignee">
          <el-input
            v-model="addressForm.consignee"
            placeholder="请输入收货人姓名"
            size="large"
          />
        </el-form-item>

        <el-form-item label="性别" prop="sex">
          <el-radio-group v-model="addressForm.sex">
            <el-radio label="1">男</el-radio>
            <el-radio label="2">女</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="手机号" prop="phone">
          <el-input
            v-model="addressForm.phone"
            placeholder="请输入收货人手机号"
            size="large"
          />
        </el-form-item>

        <el-form-item label="详细地址" prop="detail">
          <el-input
            v-model="addressForm.detail"
            type="textarea"
            :rows="3"
            placeholder="请输入详细地址"
            size="large"
          />
        </el-form-item>

        <el-form-item label="设为默认" prop="isDefault">
          <el-switch
            v-model="addressForm.isDefault"
            :active-value="1"
            :inactive-value="0"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="addressDialogVisible = false">取消</el-button>
        <el-button
          type="primary"
          :loading="savingAddress"
          @click="handleSaveAddress"
        >
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, reactive } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { Select, Plus } from "@element-plus/icons-vue";
import { getShoppingCartList } from "../api/shoppingcart";
import { getAddressList, submitOrder, saveAddress } from "../api/address";

const router = useRouter();
const loading = ref(true);
const isPaying = ref(false);
const orderItems = ref([]);
const selectedAddress = ref(null);
const addresses = ref([]);

const addressDialogVisible = ref(false);
const addressFormRef = ref(null);
const savingAddress = ref(false);

const addressForm = reactive({
  consignee: "",
  sex: "1",
  phone: "",
  detail: "",
  isDefault: 0,
});

const addressRules = {
  consignee: [{ required: true, message: "请输入收货人姓名", trigger: "blur" }],
  sex: [{ required: true, message: "请选择性别", trigger: "change" }],
  phone: [
    { required: true, message: "请输入手机号", trigger: "blur" },
    {
      pattern: /^1[3-9]\d{9}$/,
      message: "请输入正确的手机号",
      trigger: "blur",
    },
  ],
  detail: [{ required: true, message: "请输入详细地址", trigger: "blur" }],
};

const totalQuantity = computed(() => {
  return orderItems.value.reduce((sum, item) => sum + item.quantity, 0);
});

const totalPrice = computed(() => {
  return orderItems.value.reduce(
    (sum, item) => sum + item.price * item.quantity,
    0
  );
});

const fetchAddresses = async () => {
  try {
    const res = await getAddressList();

    if (res.data && Array.isArray(res.data)) {
      addresses.value = res.data.map((item) => ({
        id: item.id,
        name: item.consignee,
        phone: item.phone,
        provinceName: item.provinceName,
        cityName: item.cityName,
        districtName: item.districtName,
        detail: item.detail,
        fullAddress: `${item.provinceName || ""}${item.cityName || ""}${
          item.districtName || ""
        }${item.detail || ""}`,
        isDefault: item.isDefault,
        label: item.label,
      }));

      const defaultAddress = addresses.value.find(
        (addr) => addr.isDefault === 1
      );
      if (defaultAddress) {
        selectedAddress.value = defaultAddress;
      } else if (addresses.value.length > 0) {
        selectedAddress.value = addresses.value[0];
      }
    }
  } catch (error) {
    console.error("获取收货地址失败:", error);
    ElMessage.error("获取收货地址失败");
  }
};

const fetchOrderItems = async () => {
  try {
    loading.value = true;
    const res = await getShoppingCartList();

    if (res.data && Array.isArray(res.data)) {
      orderItems.value = res.data.map((item) => ({
        id: item.id,
        productId: item.productId || item.id,
        name: item.name || "",
        image: item.image || "",
        price: item.amount || 0,
        quantity: item.number || 1,
      }));
    }

    if (orderItems.value.length > 0) {
      selectedAddress.value = addresses.value[0];
    }
  } catch (error) {
    console.error("获取订单商品失败:", error);
    ElMessage.error("获取订单商品失败");
  } finally {
    loading.value = false;
  }
};

const handlePay = async () => {
  if (!selectedAddress.value) {
    ElMessage.warning("请选择收货地址");
    return;
  }

  if (orderItems.value.length === 0) {
    ElMessage.warning("订单中没有商品");
    return;
  }

  try {
    isPaying.value = true;
    const res = await submitOrder(selectedAddress.value.id);

    if (res.code === 200 || res.data) {
      ElMessage.success("支付成功！");
      router.push("/orders");
    } else {
      ElMessage.error(res.msg || "支付失败，请重试");
    }
  } catch (error) {
    console.error("支付失败:", error);
    ElMessage.error("支付失败，请重试");
  } finally {
    isPaying.value = false;
  }
};

const openAddressDialog = () => {
  addressForm.consignee = "";
  addressForm.sex = "1";
  addressForm.phone = "";
  addressForm.detail = "";
  addressForm.isDefault = 0;
  addressDialogVisible.value = true;
};

const handleSaveAddress = async () => {
  if (!addressFormRef.value) return;

  try {
    const valid = await addressFormRef.value.validate();
    if (!valid) return;

    savingAddress.value = true;

    const userInfo = JSON.parse(localStorage.getItem("userInfo") || "{}");
    const userId = userInfo.id;

    const data = {
      userId: userId,
      consignee: addressForm.consignee,
      sex: addressForm.sex,
      phone: addressForm.phone,
      detail: addressForm.detail,
      isDefault: addressForm.isDefault,
    };

    const res = await saveAddress(data);

    if (res.code === 200 || res.data) {
      ElMessage.success("地址添加成功！");
      addressDialogVisible.value = false;
      fetchAddresses();
    } else {
      ElMessage.error(res.msg || "添加地址失败，请重试");
    }
  } catch (error) {
    console.error("添加地址失败:", error);
    ElMessage.error("添加地址失败，请重试");
  } finally {
    savingAddress.value = false;
  }
};

onMounted(() => {
  fetchAddresses();
  fetchOrderItems();
});
</script>

<style scoped>
.confirm-order-page {
  min-height: 100vh;
  background-color: #f5f7fa;
  padding: 20px 0;
}

.confirm-order-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 30px;
}

.page-title {
  font-size: 28px;
  color: #333;
  margin-bottom: 30px;
  font-weight: bold;
}

.order-content {
  display: flex;
  gap: 30px;
}

.order-left {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.address-section,
.products-section {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.section-title {
  font-size: 18px;
  color: #333;
  margin-bottom: 15px;
  font-weight: bold;
  padding-bottom: 10px;
  border-bottom: 2px solid #ff9b7a;
}

.address-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.add-address-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 15px;
  border: 2px dashed #dcdfe6;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  color: #909399;
  font-size: 14px;
}

.add-address-btn:hover {
  border-color: #ff9b7a;
  color: #ff9b7a;
  background-color: #fff5f2;
}

.add-address-btn .el-icon {
  font-size: 18px;
}

.address-card {
  padding: 15px;
  border: 2px solid #e8e8e8;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.address-card:hover {
  border-color: #ff9b7a;
}

.address-card.selected {
  border-color: #ff9b7a;
  background-color: #fff5f2;
}

.address-info {
  flex: 1;
}

.address-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
  flex-wrap: wrap;
}

.address-name {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.address-phone {
  font-size: 14px;
  color: #666;
}

.address-detail {
  font-size: 14px;
  color: #999;
}

.check-icon {
  font-size: 20px;
  margin-left: 10px;
}

.no-address {
  padding: 20px;
  text-align: center;
}

.product-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.product-item {
  display: flex;
  align-items: center;
  padding: 15px;
  background: #fafafa;
  border-radius: 8px;
}

.product-image {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  overflow: hidden;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
}

.product-image img {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}

.product-info {
  flex: 1;
  margin-left: 15px;
}

.product-name {
  font-size: 15px;
  color: #333;
  margin-bottom: 5px;
}

.product-price {
  font-size: 14px;
  color: #ff5722;
}

.product-quantity {
  font-size: 14px;
  color: #666;
  margin: 0 30px;
}

.product-subtotal {
  font-size: 16px;
  font-weight: bold;
  color: #333;
  min-width: 80px;
  text-align: right;
}

.order-right {
  width: 320px;
}

.order-summary {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  position: sticky;
  top: 20px;
}

.summary-title {
  font-size: 18px;
  color: #333;
  margin-bottom: 20px;
  font-weight: bold;
  padding-bottom: 10px;
  border-bottom: 2px solid #ff9b7a;
}

.summary-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  font-size: 14px;
  color: #666;
}

.summary-item.total {
  border-top: 1px solid #eee;
  margin-top: 10px;
  padding-top: 15px;
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.summary-item .value {
  color: #ff5722;
  font-size: 18px;
}

.pay-btn {
  width: 100%;
  margin-top: 20px;
  height: 48px;
  font-size: 16px;
  font-weight: bold;
  background-color: #ff9b7a !important;
  border-color: #ff9b7a !important;
}

.pay-btn:hover:not(:disabled) {
  background-color: #ff854f !important;
  border-color: #ff854f !important;
}
</style>
