        <template>
    <div class="order-container">

        <!-- 等資料載入再顯示 -->
        <div v-if="order">

        <button class="back-btn" @click="$router.back()">← 返回訂單列表</button>

        <div class="order-card">

            <h2>📄 訂單明細</h2>

            <div class="order-info">
            <div><b>訂單編號：</b>{{ order.id }}</div>
            <div><b>使用者 ID：</b>{{ order.userId }}</div>
            <div><b>場次 ID：</b>{{ order.showId }}</div>

            <div>
                <b>訂單狀態：</b>
                <span :class="['status-badge', order.orderStatus]">{{ order.orderStatus }}</span>
            </div>

            <div><b>總金額：</b>{{ order.totalAmount }} 元</div>
            <div><b>建立時間：</b>{{ formatDate(order.orderTime) }}</div>
            </div>

            <h3 class="section-title">🎟 座位明細</h3>

            <table class="seat-table">
            <thead>
                <tr><th>明細ID</th><th>座位</th><th>票種</th><th>價格</th><th>狀態</th></tr>
            </thead>
            
            <tbody>
                <tr v-for="d in orderDetails" :key="d.id">
                <td>{{ d.id }}</td>
                <td>{{ d.seatId }}</td>
                <td>{{ d.ticketType }}</td>
                <td class="price">{{ d.ticketPrice }} 元</td>
                <td>{{ d.status }}</td>
                </tr>
            </tbody>
            </table>

        </div>
        </div>

        <!-- 載入中 UI -->
        <div v-else class="loading">載入中...</div>

    </div>
    </template>


        <script setup>
        import axios from "axios"
        import { ref, onMounted } from "vue"
        import { useRoute } from "vue-router"

        const route = useRoute()
        const order = ref(null)
        const orderDetails = ref([])

        onMounted(async () => {
        const id = route.params.id
        order.value = (await axios.get(`http://localhost:8080/api/orders/by-id/${id}`)).data
        orderDetails.value = (await axios.get(`http://localhost:8080/api/order-details/order/${id}`)).data
        })

        function formatDate(t){
        return t ? new Date(t).toLocaleString("zh-TW",{hour12:false}) : ""
        }
        </script>

        <style scoped>
.order-container {
  display: flex;
  justify-content: center;
  padding-top: 40px;
}

/* 外框卡片 */
.order-card {
  width: 550px;
  background: #fff;
  border-radius: 14px;
  padding: 35px 45px;
  box-shadow: 0 4px 14px rgba(0,0,0,0.08);
  margin: auto;
}

/* 返回按鈕置中 */
.back-btn {
  display: block;
  margin: 0 auto 25px;
  padding: 8px 14px;
  background: #f0f0f0;
  border-radius: 6px;
  font-weight: 600;
  transition: .2s;
}
.back-btn:hover{
  background:#dcdcdc;
}

/* 標題 */
.order-card h2 {
  font-size: 22px;
  margin-bottom: 18px;
  display: flex;
  align-items:center;
  gap:6px;
}

/* 區塊標題 */
.section-title {
  margin-top: 28px;
  font-size:18px;
  font-weight:bold;
}

/* 訂單信息 */
.order-info {
  line-height: 28px;
  font-size:16px;
}

/* 狀態 badge 顏色 */
.status-badge {
  padding:4px 12px;
  border-radius:20px;
  color:#fff;
  font-weight:600;
}
.status-badge.COMPLETED { background:#2ecc71; }
.status-badge.PENDING { background:#e74c3c; }
.status-badge.CANCELED { background:#7f8c8d; }

/* 表格 */
.seat-table {
  width:100%;
  border-collapse:separate;
  border-spacing:0 8px;
  margin-top:10px;
}

.seat-table tbody tr {
  background:#fff;
  box-shadow:0 2px 8px rgba(0,0,0,0.06);
  border-radius:8px;
}

.seat-table td, .seat-table th {
  padding:12px;
  font-size:15px;
}

.price { color:#e74c3c;font-weight:bold; }
.loading{ text-align:center;font-size:18px;padding-top:40px; }
</style>
