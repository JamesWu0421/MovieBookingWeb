        <template>
        <div class="payment-success">

            <!-- 付款成功標題 -->
            <h1>💰付款完成</h1>
            <p>訂單已成立，感謝您的購買！</p>

            <!-- 有 order 才顯示摘要 -->
            <div v-if="order" class="summary-wrapper">
            <div class="summary-card">

                <!-- Header -->
                <div class="summary-header">
                <h3>訂單摘要 ({{ details.length }}張票)</h3>
                </div>

                <!-- 訂單資訊 -->
                <div class="summary-section">
                <div class="row-item"><span>訂單編號</span><span>{{ order.id }}</span></div>
                <div class="row-item"><span>場次 ID</span><span>{{ order.showId }}</span></div>
                <div class="row-item"><span>訂單時間</span><span>{{ formatDate(order.orderTime) }}</span></div>
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

                

                <button class="pay-btn outline" @click="completeOrder">
                    查看電影票 →
                </button>


            </div>
            </div>

        </div>
        </template>
    <script setup>
    import axios from "axios";
    import { ref, onMounted } from "vue";
    import { useRoute, useRouter } from "vue-router";   // ⭐ 必須加這個

    const route = useRoute();
    const router = useRouter();                         // ⭐ 建立 router

    const orderId = Number(route.params.id);

    const order = ref(null);
    const details = ref([]);
    const loading = ref(true);

    onMounted(async () => {
    try {
        const resOrder = await axios.get(`http://localhost:8080/api/orders/by-id/${orderId}`);
        order.value = resOrder.data;

        const resDetails = await axios.get(`http://localhost:8080/api/order-details/order/${orderId}`);
        details.value = resDetails.data;

    } finally {
        loading.value = false;
    }
    });

    // ⭐ 你的 formatDate 必須存在，不然 template 會錯
    function formatDate(t) {
    return t ? new Date(t).toLocaleString("zh-TW", { hour12: false }) : "";
    }

    async function completeOrder() {
    try {
        // 1. 完成訂單
        await axios.put(`http://localhost:8080/api/orders/${orderId}/complete`);

        // 2. 產生票券
        await axios.post(`http://localhost:8080/api/tickets/order/${orderId}/generate`);

        // 3. 跳轉
        router.push(`/tickets/order/${orderId}`);       // ⭐ 現在 router 可用了
    } catch (err) {
        console.error("建立票券失敗❌", err);
    }
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
    margin-top:120px;
    opacity:0.85;
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
    @keyframes spin { to { transform: rotate(360deg);} }

    /* ===== Card ===== */
    .summary-card {
    background:white;
    border-radius:12px;
    padding:26px;
    border:1px solid #e5e5e5;
    }

    /* Header */
    .summary-header h3 {
    font-size:20px;
    font-weight:800;
    margin-bottom:14px;
    }

    /* Order Info */
    .summary-section .row-item {
    display:flex;
    justify-content:space-between;
    margin:8px 0;
    font-size:15px;
    }

    /* Ticket Detail */
    .ticket-list h4 {
    font-size:17px;
    font-weight:700;
    margin-bottom:10px;
    }

    .ticket-item {
    display:flex;
    justify-content:space-between;
    padding:8px 0;
    }

    .ticket-left .seat {
    font-size:15px;
    font-weight:700;
    }

    .type { font-size:13px; color:#555; }

    .ticket-price {
    font-weight:700;
    font-size:15px;
    }

    /* Total */
    .total-row {
    display:flex;
    justify-content:space-between;
    font-size:17px;
    margin-top:10px;
    }

    .total-price { color:#d9534f; font-size:18px; }

    /* Pay Button */
    .pay-btn {
    width:100%;
    background:#0d6efd;
    color:#fff;
    border:none;
    font-size:17px;
    padding:12px 0;
    margin-top:20px;
    border-radius:6px;
    font-weight:700;
    cursor:pointer;
    transition:.15s;
    }
    .pay-btn:hover { background:#0b5ed7; }

    .pay-btn {
        width: 60%;        /* 🔥從100%改小 */
        padding: 10px 0;
        font-size: 16px;
        margin: 20px auto; /* 自動置中 */
        display: block;
        }
    </style>
    
