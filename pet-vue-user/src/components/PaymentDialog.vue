<template>
  <el-dialog
    :model-value="visible"
    title="支付"
    width="450px"
    :close-on-click-modal="false"
    @update:model-value="$emit('update:visible', $event)"
  >
    <div class="payment-dialog">
      <div class="payment-amount">
        <span class="amount-label">支付金额：</span>
        <span class="amount-value">¥{{ amount }}</span>
      </div>

      <div class="payment-methods">
        <p class="method-label">支付方式：</p>
        <div class="method-options">
          <div
            class="method-item"
            :class="{ active: selectedMethod === 'wechat' }"
            @click="selectedMethod = 'wechat'"
          >
            <img src="data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='%2307C160'%3E%3Cpath d='M9.5 4C5.36 4 2 6.69 2 10c0 1.89 1.08 3.56 2.78 4.66L4 17l2.5-1.5c.89.31 1.87.5 2.91.5.37 0 .73-.03 1.09-.08C10.17 14.86 10 13.95 10 13c0-3.31 3.13-6 7-6 .34 0 .67.03 1 .08C17.44 5.56 14.31 4 11 4H9.5z'/%3E%3Cpath d='M19 9c-3.31 0-6 2.24-6 5s2.69 5 6 5c.67 0 1.31-.1 1.91-.27L23 20l-1.22-2.45C22.85 16.43 23.5 15.28 23.5 14c0-2.76-2.01-5-4.5-5z'/%3E%3C/svg%3E" alt="微信支付" />
            <span>微信支付</span>
          </div>
          <div
            class="method-item"
            :class="{ active: selectedMethod === 'alipay' }"
            @click="selectedMethod = 'alipay'"
          >
            <img src="data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='%201677FF'%3E%3Cpath d='M21.422 15.358c-3.32-1.316-5.393-2.878-6.047-4.02.654-.47 1.058-.986 1.058-1.77 0-1.316-1.186-2.382-2.64-2.382S11.153 8.252 11.153 9.568c0 .784.404 1.3 1.058 1.77-.654 1.142-2.727 2.704-6.047 4.02L4 16.512c0 2.502 3.51 4.488 7.793 4.488s7.793-1.986 7.793-4.488l1.836-1.154zM12 3a9 9 0 100 18 9 9 0 000-18z'/%3E%3C/svg%3E" alt="支付宝" />
            <span>支付宝</span>
          </div>
        </div>
      </div>

      <div class="dialog-footer">
        <el-button type="primary" size="large" class="confirm-btn" @click="handleConfirm">
          确认支付
        </el-button>
      </div>
    </div>
  </el-dialog>
</template>

<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  amount: {
    type: [String, Number],
    default: '0.00'
  }
})

const emit = defineEmits(['update:visible', 'confirm'])

const selectedMethod = ref('wechat')

watch(() => props.visible, (newVal) => {
  if (newVal) {
    selectedMethod.value = 'wechat'
  }
})

function handleConfirm() {
  emit('confirm', selectedMethod.value)
}
</script>

<style scoped>
.payment-dialog {
  padding: 10px 0;
}

.payment-amount {
  text-align: center;
  margin-bottom: 30px;
  padding: 20px;
  background: linear-gradient(135deg, #fff5f0 0%, #ffe8dc 100%);
  border-radius: 8px;
}

.amount-label {
  font-size: 16px;
  color: #666;
  margin-right: 10px;
}

.amount-value {
  font-size: 32px;
  color: #ff5722;
  font-weight: 900;
}

.payment-methods {
  margin-bottom: 30px;
}

.method-label {
  font-size: 15px;
  color: #333;
  font-weight: 500;
  margin-bottom: 15px;
}

.method-options {
  display: flex;
  gap: 15px;
  justify-content: center;
}

.method-item {
  flex: 1;
  padding: 20px;
  border: 2px solid #e0e0e0;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.method-item:hover {
  border-color: #409eff;
  transform: translateY(-2px);
}

.method-item.active {
  border-color: #409eff;
  background-color: #ecf5ff;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
}

.method-item img {
  width: 48px;
  height: 48px;
}

.method-item span {
  font-size: 15px;
  color: #333;
  font-weight: 500;
}

.dialog-footer {
  text-align: center;
  padding-top: 10px;
}

.confirm-btn {
  width: 200px !important;
  height: 46px !important;
  font-size: 17px !important;
  font-weight: bold !important;
  border-radius: 8px !important;
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%) !important;
  border: none !important;
}

.confirm-btn:hover {
  transform: scale(1.02);
  box-shadow: 0 6px 16px rgba(64, 158, 255, 0.3);
}
</style>
