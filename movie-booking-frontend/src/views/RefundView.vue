    <template>
    <div class="refund-container py-4">

        <h2 class="fw-bold mb-4 text-center">票券退款</h2>

        <div v-if="details.length" class="card p-4 shadow-lg refund-card">

        <h4 class="fw-bold mb-3">選擇要退款的票券</h4>

        <table class="table table-hover text-center align-middle refund-table">
            <thead class="table-dark">
            <tr>
                <th>座位</th>
                <th>票種</th>
                <th>票價</th>
                <th>狀態</th>
                <th>操作</th>
            </tr>
            </thead>

            <tbody>
            <tr v-for="d in details" :key="d.id">
                <td>{{ d.seatId }}</td>
                <td>{{ d.ticketType }}</td>
                <td class="fw-bold text-primary">{{ d.ticketPrice }} 元</td>

                <td>
                <span
                    :class="{
                    'badge bg-secondary': d.status === 'refunded',
                    'badge bg-success': d.status === 'ACTIVE'
                    }"
                    class="status-badge"
                >
                    {{ d.status === 'ACTIVE' ? '可退款' : '已退款' }}
                </span>
                </td>

                <td>
                <button
                    v-if="d.status !== 'refunded'"
                    class="btn btn-danger btn-sm refund-btn"
                    @click="refund(d.id)"
                >
                    退款
                </button>

                <span v-else class="text-muted">已退款</span>
                </td>
            </tr>
            </tbody>
        </table>

        </div>

        <div v-else>
        <p>正在載入票券...</p>
        </div>

    </div>
    </template>

    <script setup>
    import { ref, onMounted } from "vue";
    import { useRoute } from "vue-router";
    import axios from "axios";

    const route = useRoute();
    const orderId = route.params.orderId;

    const details = ref([]);

    onMounted(async () => {
    const res = await axios.get(
        `http://localhost:8080/api/order-details/order/${orderId}`
    );
    details.value = res.data;
    });

    async function refund(orderDetailId) {
    if (!confirm("確定要退款這張票嗎？")) return;

    try {
        await axios.post(
        `http://localhost:8080/api/refund/single/${orderDetailId}`
        );

        alert("退款成功！");

        const res = await axios.get(
        `http://localhost:8080/api/order-details/order/${orderId}`
        );
        details.value = res.data;

    } catch (err) {
        alert("退款失敗，請查看後端 API！");
    }
    }
    </script>

    <style scoped>
    /* 整個內容區塊加寬到 80% 視窗 */
    .refund-container {
    max-width: 1400px;  /* 原本 900 → 擴大 */
    margin: 0 auto;
    }

    /* 卡片本體寬一點 + 更大留白 */
    .refund-card {
    border-radius: 16px;
    padding: 40px !important;
    }

    /* 表格標題加寬 */
    .refund-table th {
    padding: 20px 32px;  /* 左右空間更多 */
    font-size: 18px;
    }

    /* 表格內容加寬 */
    .refund-table td {
    padding: 24px 32px;  /* 左右空間更多 + 上下高度增加 */
    font-size: 17px;
    }

    /* hover 效果 */
    .refund-table tbody tr:hover {
    background-color: #f8f9fa;
    }

    /* 狀態 badge */
    .status-badge {
    font-size: 15px;
    padding: 7px 16px;
    border-radius: 10px;
    }

    /* 操作按鈕更大、更圓、更顯眼 */
    .refund-btn {
    padding: 7px 22px;
    font-size: 16px;
    border-radius: 12px;
    transition: all 0.2s ease;
    }

    .refund-btn:hover {
    transform: scale(1.08);
    }
    </style>
