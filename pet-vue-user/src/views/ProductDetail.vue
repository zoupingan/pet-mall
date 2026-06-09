<template>
  <div class="product-detail-page">
    <div class="detail-container" v-if="loading">
      <el-skeleton :rows="10" animated />
    </div>

    <div class="detail-container" v-else>
      <div class="breadcrumb">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item>宠物食品</el-breadcrumb-item>
          <el-breadcrumb-item>{{ product.name }}</el-breadcrumb-item>
        </el-breadcrumb>
        <router-link to="/orders" class="order-link">我的订单</router-link>
      </div>

      <div class="product-main">
        <div class="product-gallery">
          <div class="main-image">
            <img :src="product.image" :alt="product.name" />
            <div class="discount-badge" v-if="product.discount">
              {{ product.discount }}
            </div>
          </div>
        </div>

        <div class="product-info-section">
          <h1 class="product-title">{{ product.name }}</h1>

          <div class="product-meta">
            <span class="brand">{{ product.brand }}</span>
            <span class="weight">{{ product.weight }}</span>
          </div>

          <div class="price-section">
            <div class="current-price">
              <span class="price-label">¥</span>
              <span class="price-value">{{ product.price }}</span>
              <span class="price-unit">/袋</span>
            </div>
            <div class="original-price" v-if="product.originalPrice">
              原价：¥{{ product.originalPrice }}
            </div>
          </div>

          <div class="quantity-section">
            <label>选择数量：</label>
            <el-input-number
              v-model="quantity"
              :min="1"
              :max="product.stock || 99"
              size="large"
            />
          </div>

          <div class="address-section">
            <el-icon><Location /></el-icon>
            <span>{{ product.address }}</span>
            <el-tag type="danger" size="small" style="margin-left: 10px"
              >有货</el-tag
            >
          </div>

          <div class="action-buttons">
            <el-button
              type="warning"
              size="large"
              class="btn-cart"
              @click="handleAddToCart"
            >
              加入购物车
            </el-button>
            <el-button
              type="primary"
              size="large"
              class="btn-buy"
              @click="showPaymentDialog = true"
            >
              立即购买
            </el-button>
            <el-button
              size="large"
              class="btn-service"
              @click="openAISearch"
            >
              咨询助手
            </el-button>
          </div>

          <div class="total-price">
            <span class="total-label">总计：</span>
            <span class="total-value">¥{{ totalPrice }}</span>
          </div>
        </div>
      </div>

      <div class="product-description" v-if="product.description">
        <h3>商品描述</h3>
        <p>{{ product.description }}</p>
      </div>
    </div>

    <PaymentDialog
      v-model:visible="showPaymentDialog"
      :amount="totalPrice"
      @confirm="handlePaymentConfirm"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useRoute } from "vue-router";
import { Location } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import PaymentDialog from "../components/PaymentDialog.vue";
import { getProductDetail } from "../api/product";
import { addToCart } from "../api/shoppingcart";

const route = useRoute();
const loading = ref(true);
const quantity = ref(1);
const showPaymentDialog = ref(false);

const product = ref({
  id: "",
  name: "",
  brand: "",
  weight: "",
  price: "0",
  originalPrice: "",
  discount: "",
  image: "",
  address: "天津市 市辖区 和平区 金皇小区24号楼3单元106",
  description: "",
  stock: 99,
});

const totalPrice = computed(() => {
  return (parseFloat(product.value.price) * quantity.value).toFixed(2);
});

const fetchProductDetail = async () => {
  try {
    const id = route.params.id;
    if (!id) return;

    loading.value = true;
    const res = await getProductDetail(id);

    if (res.data) {
      let data = res.data;
      if (Array.isArray(data)) {
        data = data[0];
      }

      if (data && data.id) {
        product.value = {
          id: data.id,
          name: data.productName || "",
          brand: "",
          weight: "",
          price: data.price ? String(data.price) : "0",
          originalPrice: data.costPrice ? String(data.costPrice) : "",
          discount: "",
          image:
            data.imageUrl ||
            "https://via.placeholder.com/600x600?text=No+Image",
          address: "天津市 市辖区 和平区 金皇小区24号楼3单元106",
          description: data.description || "",
          stock: data.stock || 99,
        };
      }
    }
  } catch (error) {
    console.error("获取商品详情失败:", error);
  } finally {
    loading.value = false;
  }
};

async function handleAddToCart() {
  try {
    if (!product.value.id) {
      ElMessage.warning("商品信息加载中，请稍候");
      return;
    }

    const res = await addToCart(product.value.id, quantity.value);

    if (res.code === 200 || res.data) {
      ElMessage.success("已成功加入购物车！");
    } else {
      ElMessage.error(res.msg || "加入购物车失败");
    }
  } catch (error) {
    console.error("加入购物车失败:", error);
    ElMessage.error("网络错误，请重试");
  }
}

function openAISearch() {
  window.dispatchEvent(new CustomEvent("open-ai-search"));
}

function handlePaymentConfirm(paymentMethod) {
  console.log("支付方式:", paymentMethod, "金额:", totalPrice.value);
  showPaymentDialog.value = false;
}

onMounted(() => {
  fetchProductDetail();
});
</script>

<style scoped>
.product-detail-page {
  min-height: 100vh;
  background-color: #f5f7fa;
  padding-top: 20px;
}

.detail-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 30px 60px;
}

.breadcrumb {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
  padding: 15px 20px;
  background: #fff;
  border-radius: 8px;
}

.order-link {
  color: #ff6b35;
  text-decoration: none;
  font-size: 14px;
}

.product-main {
  display: flex;
  gap: 40px;
  background: #fff;
  border-radius: 12px;
  padding: 40px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.06);
}

.product-gallery {
  width: 480px;
  flex-shrink: 0;
}

.main-image {
  width: 100%;
  height: 480px;
  background: #f8f9fa;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.main-image img {
  max-width: 90%;
  max-height: 90%;
  object-fit: contain;
}

.discount-badge {
  position: absolute;
  top: 20px;
  left: 20px;
  background: linear-gradient(135deg, #ff5722 0%, #ff7043 100%);
  color: white;
  padding: 8px 16px;
  border-radius: 6px;
  font-weight: bold;
  font-size: 14px;
}

.product-info-section {
  flex: 1;
  padding-top: 10px;
}

.product-title {
  font-size: 26px;
  color: #333;
  font-weight: bold;
  line-height: 1.4;
  margin: 0 0 15px;
}

.product-meta {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
  color: #999;
  font-size: 14px;
}

.price-section {
  background: linear-gradient(135deg, #fff5f0 0%, #ffe8dc 100%);
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 25px;
}

.current-price {
  display: flex;
  align-items: baseline;
  gap: 4px;
  margin-bottom: 8px;
}

.price-label {
  font-size: 22px;
  color: #ff5722;
  font-weight: bold;
}

.price-value {
  font-size: 42px;
  color: #ff5722;
  font-weight: 900;
}

.price-unit {
  font-size: 18px;
  color: #ff5722;
}

.original-price {
  color: #999;
  font-size: 14px;
  text-decoration: line-through;
}

.quantity-section,
.address-section {
  margin-bottom: 25px;
}

.quantity-section label {
  display: block;
  font-size: 15px;
  color: #333;
  font-weight: 500;
  margin-bottom: 12px;
}

.address-section {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 15px;
  background: #fafafa;
  border-radius: 6px;
  font-size: 14px;
  color: #666;
}

.action-buttons {
  display: flex;
  gap: 15px;
  margin-bottom: 25px;
}

.btn-cart,
.btn-buy,
.btn-service {
  flex: 1 !important;
  height: 50px !important;
  font-size: 17px !important;
  font-weight: bold !important;
  border-radius: 8px !important;
}

.btn-cart {
  background-color: #ff9800 !important;
  border-color: #ff9800 !important;
}

.btn-buy {
  background-color: #ff5722 !important;
  border-color: #ff5722 !important;
}

.total-price {
  display: flex;
  align-items: baseline;
  gap: 10px;
  padding-top: 20px;
  border-top: 2px dashed #e0e0e0;
}

.total-label {
  font-size: 18px;
  color: #666;
  font-weight: 500;
}

.total-value {
  font-size: 32px;
  color: #ff5722;
  font-weight: 900;
}

.product-description {
  margin-top: 30px;
  background: #fff;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.06);
}

.product-description h3 {
  font-size: 20px;
  color: #333;
  margin-bottom: 15px;
  padding-left: 12px;
  border-left: 4px solid #ff6b35;
}

.product-description p {
  font-size: 15px;
  color: #666;
  line-height: 1.8;
}
</style>
