<template>
  <div class="cart-page">
    <div class="cart-container">
      <h1 class="page-title">我的购物车</h1>

      <div v-if="loading" class="loading-cart">
        <el-skeleton :rows="5" animated />
        <el-skeleton :rows="5" animated style="margin-top: 20px" />
        <el-skeleton :rows="3" animated style="margin-top: 20px" />
      </div>

      <div v-else-if="cartItems.length === 0" class="empty-cart">
        <el-empty description="购物车空空如也" :image-size="200">
          <el-button type="primary" @click="$router.push('/')"
            >去逛逛</el-button
          >
        </el-empty>
      </div>

      <div v-else class="cart-content">
        <!-- 购物车头部 -->
        <div class="cart-header">
          <div class="header-product">商品名称</div>
          <div class="header-spec">规格</div>
          <div class="header-price">单价</div>
          <div class="header-quantity">数量</div>
          <div class="header-discount">优惠</div>
          <div class="header-subtotal">小计</div>
          <div class="header-action">操作</div>
        </div>

        <!-- 购物车商品列表 -->
        <div class="cart-list">
          <div v-for="item in cartItems" :key="item.id" class="cart-item">
            <div class="item-product">
              <div class="product-image">
                <img :src="item.image" :alt="item.name" />
              </div>
              <div class="product-name">{{ item.name }}</div>
            </div>

            <div class="item-spec">{{ item.spec }}</div>

            <div class="item-price">¥{{ item.price.toFixed(2) }}</div>

            <div class="item-quantity">
              <el-input-number
                v-model="item.quantity"
                :min="0"
                :max="99"
                size="small"
                @change="
                  (val, oldVal) => handleQuantityChange(item, val, oldVal)
                "
              />
            </div>

            <div class="item-discount">{{ item.discount || "无" }}</div>

            <div class="item-subtotal">
              ¥{{ (item.price * item.quantity).toFixed(2) }}
            </div>

            <div class="item-action">
              <el-button
                type="danger"
                circle
                size="small"
                @click="handleDelete(item)"
              >
                <el-icon><Delete /></el-icon>
              </el-button>
            </div>
          </div>
        </div>

        <!-- 底部结算栏 -->
        <div class="cart-footer">
          <div class="footer-left">
            <span class="select-info">
              共 <em>{{ cartItems.length }}</em> 件商品
            </span>
          </div>

          <div class="footer-right">
            <el-button
              type="danger"
              size="large"
              class="clean-btn"
              :disabled="cartItems.length === 0"
              @click="handleCleanCart"
            >
              清空购物车
            </el-button>
            <div class="total-price">
              合计：<span class="price-value"
                >¥{{ totalPrice.toFixed(2) }}</span
              >
            </div>
            <el-button
              type="warning"
              size="large"
              class="checkout-btn"
              :disabled="cartItems.length === 0"
              @click="handleCheckout"
            >
              去结算
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import { ElMessage, ElMessageBox } from "element-plus";
import { Delete } from "@element-plus/icons-vue";
import {
  getShoppingCartList,
  addToCart,
  subtractFromCart,
  removeProductFromCart,
  cleanShoppingCart,
} from "../api/shoppingcart";

const router = useRouter();
const loading = ref(true);
const cartItems = ref([]);

const selectAll = computed({
  get() {
    return (
      cartItems.value.length > 0 &&
      cartItems.value.every((item) => item.selected)
    );
  },
  set(value) {
    handleSelectAll(value);
  },
});

const selectedCount = computed(() => {
  return cartItems.value.filter((item) => item.selected).length;
});

const totalPrice = computed(() => {
  return cartItems.value.reduce(
    (sum, item) => sum + item.price * item.quantity,
    0
  );
});

function handleSelectAll(checked) {
  cartItems.value.forEach((item) => {
    item.selected = checked;
  });
}

function handleItemSelect() {
  // 触发计算属性更新
}

async function handleQuantityChange(item, newValue, oldValue) {
  if (!oldValue) return;

  try {
    if (newValue > oldValue) {
      await addToCart(item.productId || item.id);
    } else if (newValue < oldValue) {
      await subtractFromCart(item.productId || item.id);
    }

    if (newValue === 0) {
      await fetchCartList();
    }

    ElMessage.success(`${item.name} 数量已更新为 ${newValue}`);
  } catch (error) {
    console.error("数量更新失败:", error);
    ElMessage.error("数量更新失败，请重试");
    item.quantity = oldValue;
  }
}

async function handleDelete(item) {
  try {
    await ElMessageBox.confirm(`确定要删除 ${item.name} 吗？`, "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    await removeProductFromCart(item.productId || item.id);
    await fetchCartList();
    ElMessage.success("已删除");
  } catch (error) {
    if (error !== "cancel" && error !== "close") {
      console.error("删除购物车商品失败:", error);
      ElMessage.error("删除失败，请重试");
    }
  }
}

async function handleCleanCart() {
  try {
    await ElMessageBox.confirm(
      "确定要清空购物车吗？此操作不可恢复！",
      "清空购物车",
      {
        confirmButtonText: "确定清空",
        cancelButtonText: "取消",
        type: "warning",
      }
    );

    await cleanShoppingCart();
    cartItems.value = [];
    ElMessage.success("购物车已清空");
  } catch (error) {
    if (error !== "cancel") {
      console.error("清空购物车失败:", error);
      ElMessage.error("清空购物车失败，请重试");
    }
  }
}

function handleCheckout() {
  if (cartItems.value.length === 0) {
    ElMessage.warning("购物车为空");
    return;
  }

  router.push("/confirm-order");
}

const fetchCartList = async () => {
  try {
    loading.value = true;
    const res = await getShoppingCartList();

    if (res.data && Array.isArray(res.data)) {
      cartItems.value = res.data.map((item) => ({
        id: item.id,
        productId: item.productId || item.id,
        name: item.name || "",
        image: item.image || "",
        spec: "件",
        price: item.amount || 0,
        quantity: item.number || 1,
        discount: "无",
        selected: false,
      }));
    }
  } catch (error) {
    console.error("获取购物车列表失败:", error);
    ElMessage.error("获取购物车数据失败");
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  fetchCartList();
});
</script>

<style scoped>
.cart-page {
  min-height: 100vh;
  background-color: #f5f7fa;
  padding-top: 30px;
}

.cart-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 30px 60px;
}

.page-title {
  font-size: 24px;
  color: #333;
  font-weight: bold;
  margin-bottom: 25px;
  padding-left: 10px;
  border-left: 4px solid #ff6b35;
}

.empty-cart {
  background: #fff;
  border-radius: 12px;
  padding: 80px 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.loading-cart {
  background: #fff;
  border-radius: 12px;
  padding: 40px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.cart-content {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.cart-header {
  display: grid;
  grid-template-columns: 80px 3fr 1fr 1fr 1fr 1fr 1fr 100px;
  gap: 15px;
  align-items: center;
  padding: 18px 24px;
  background: linear-gradient(to right, #fafafa, #fff);
  border-bottom: 2px solid #f0f0f0;
  font-size: 14px;
  font-weight: 600;
  color: #666;
}

.header-checkbox {
  text-align: center;
}

.header-product,
.header-spec,
.header-price,
.header-quantity,
.header-discount,
.header-subtotal,
.header-action {
  text-align: center;
}

.cart-list {
  max-height: 600px;
  overflow-y: auto;
}

.cart-item {
  display: grid;
  grid-template-columns: 80px 3fr 1fr 1fr 1fr 1fr 1fr 100px;
  gap: 15px;
  align-items: center;
  padding: 24px;
  border-bottom: 1px solid #f5f5f5;
  transition: all 0.3s;
}

.cart-item:hover {
  background-color: #fafafa;
}

.item-checkbox {
  text-align: center;
}

.item-product {
  display: flex;
  align-items: center;
  gap: 16px;
}

.product-image {
  width: 90px;
  height: 90px;
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
  border: 1px solid #f0f0f0;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-name {
  font-size: 14px;
  color: #333;
  font-weight: 500;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.item-spec,
.item-price,
.item-quantity,
.item-discount,
.item-subtotal,
.item-action {
  text-align: center;
  font-size: 14px;
  color: #666;
}

.item-price {
  color: #ff5722;
  font-weight: 600;
}

.item-subtotal {
  color: #ff5722;
  font-weight: bold;
  font-size: 15px;
}

.item-discount {
  color: #999;
}

.cart-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  background: linear-gradient(to right, #fff9f5, #fff);
  border-top: 2px solid #ffe0d0;
}

.footer-left .select-info {
  font-size: 14px;
  color: #666;
}

.footer-left em {
  color: #ff5722;
  font-style: normal;
  font-weight: bold;
  margin: 0 3px;
}

.footer-right {
  display: flex;
  align-items: center;
  gap: 30px;
}

.total-price {
  font-size: 16px;
  color: #666;
}

.price-value {
  font-size: 28px;
  color: #ff5722;
  font-weight: bold;
  margin-left: 8px;
}

.clean-btn {
  min-width: 120px !important;
  height: 48px !important;
  font-size: 15px !important;
  font-weight: bold !important;
  border-radius: 8px !important;
}

.clean-btn:hover:not(:disabled) {
  opacity: 0.9;
}

.checkout-btn {
  min-width: 140px !important;
  height: 48px !important;
  font-size: 17px !important;
  font-weight: bold !important;
  border-radius: 8px !important;
  background-color: #ff9b7a !important;
  border-color: #ff9b7a !important;
}

.checkout-btn:hover:not(:disabled) {
  background-color: #ff854f !important;
  border-color: #ff854f !important;
}

.checkout-btn.is-disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

:deep(.el-input-number--small) {
  width: 110px;
}

:deep(.el-input-number__increase),
:deep(.el-input-number__decrease) {
  background-color: #f5f7fa;
  border-color: #e4e7ed;
}

:deep(.el-input-number__increase:hover),
:deep(.el-input-number__decrease:hover) {
  color: #ff6b35;
}

:deep(.el-checkbox__input.is-checked .el-checkbox__inner) {
  background-color: #ff6b35;
  border-color: #ff6b35;
}

:deep(.el-checkbox__input.is-checked + .el-checkbox__label) {
  color: #ff6b35;
}

:deep(.el-table th) {
  background-color: #fafafa !important;
}
</style>
