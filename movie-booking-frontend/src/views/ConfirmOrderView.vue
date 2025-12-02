<template>
  <div class="page-wrapper">
    <!-- ★ 載入中狀態 (動畫) -->
    <div v-if="loading" class="loading-box">
      <div class="loader"></div>
      <p>讀取訂單資訊中...</p>
    </div>

    <!-- ★ 訂單顯示卡片 (資料載入完成後才顯示) -->
    <div v-else-if="order" class="summary-wrapper">
      <div class="summary-card">
        <!-- Header -->
        <div class="summary-header">
          <h3>訂單摘要 ({{ details.length }}張票)</h3>
        </div>

        <!-- 訂單資訊 -->
        <div class="summary-section">
          <div class="row-item">
            <span>訂單編號</span><span>{{ order.id }}</span>
          </div>
          <div class="row-item">
            <span>場次 ID</span><span>{{ order.showId }}</span>
          </div>
          <div class="row-item">
            <span>訂單時間</span><span>{{ formatDate(order.orderTime) }}</span>
          </div>
        </div>

        <hr />

        <!-- 票券明細 -->
        <div class="ticket-list">
          <h4>🎟 票券明細</h4>

          <div v-for="d in details" :key="d.id" class="ticket-item">
            <div class="ticket-left">
              <div class="seat">座位 {{ d.seatId }}</div>
              <div class="type">票種：{{ d.ticketType }}</div>
            </div>
            <div class="ticket-price">{{ d.ticketPrice }} 元</div>
          </div>
        </div>

        <hr />

        <!-- Total -->
        <div class="total-row">
          <strong>總計</strong>
          <strong class="total-price">{{ order.totalAmount }} 元</strong>
        </div>

        <!-- Pay Button -->
        <button class="pay-btn" @click="$router.push('/payment/' + order.id)">
          前往付款 →
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import axios from "axios";
import { ref, onMounted } from "vue";
import { useRoute } from "vue-router";

const route = useRoute();
const orderId = Number(route.params.orderId); // ⭐直接接參數 orderId

const order = ref(null);
const details = ref([]);
const loading = ref(true);

onMounted(async () => {
  try {
    const resOrder = await axios.get(
      import.meta.env.VITE_API_BASE_URL + `/orders/by-id/${orderId}` ||
        `http://localhost:8080/api/orders/by-id/${orderId}`
    );
    order.value = resOrder.data;

    const resDetails = await axios.get(
      import.meta.env.VITE_API_BASE_URL + `/order-details/order/${orderId}` ||
        `http://localhost:8080/api/order-details/order/${orderId}`
    );
    details.value = resDetails.data;
  } finally {
    loading.value = false;
  }
});

function formatDate(t) {
  return t ? new Date(t).toLocaleString("zh-TW", { hour12: false }) : "";
}
</script>

<style scoped>
/* ===== Page Layout ===== */
.page-wrapper {
  max-width: 450px;
  margin: 60px auto;
}

/* ===== Loading Screen ===== */
.loading-box {
  text-align: center;
  margin-top: 120px;
  opacity: 0.85;
}

.loader {
  width: 38px;
  height: 38px;
  border: 4px solid #ddd;
  border-top-color: #0d6efd;
  border-radius: 50%;
  margin: auto;
  animation: spin 1s linear infinite;
}
@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* ===== Card ===== */
.summary-card {
  background: white;
  border-radius: 12px;
  padding: 26px;
  border: 1px solid #e5e5e5;
}

/* Header */
.summary-header h3 {
  font-size: 20px;
  font-weight: 800;
  margin-bottom: 14px;
}

/* Order Info */
.summary-section .row-item {
  display: flex;
  justify-content: space-between;
  margin: 8px 0;
  font-size: 15px;
}

/* Ticket Detail */
.ticket-list h4 {
  font-size: 17px;
  font-weight: 700;
  margin-bottom: 10px;
}

.ticket-item {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
}

.ticket-left .seat {
  font-size: 15px;
  font-weight: 700;
}

.type {
  font-size: 13px;
  color: #555;
}

.ticket-price {
  font-weight: 700;
  font-size: 15px;
}

/* Total */
.total-row {
  display: flex;
  justify-content: space-between;
  font-size: 17px;
  margin-top: 10px;
}

.total-price {
  color: #d9534f;
  font-size: 18px;
}

/* Pay Button */
.pay-btn {
  width: 100%;
  background: #0d6efd;
  color: #fff;
  border: none;
  font-size: 17px;
  padding: 12px 0;
  margin-top: 20px;
  border-radius: 6px;
  font-weight: 700;
  cursor: pointer;
  transition: 0.15s;
}
.pay-btn:hover {
  background: #0b5ed7;
}
</style>
