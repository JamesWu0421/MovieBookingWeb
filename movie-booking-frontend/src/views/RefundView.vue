    <template>
    <div class="container py-4">

        <h2 class="fw-bold mb-4">票券退款</h2>

        <div v-if="details.length" class="card p-4 shadow">

        <h4 class="fw-bold mb-3">選擇要退款的票券</h4>

        <table class="table text-center align-middle">
            <thead>
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
                <td>{{ d.ticketPrice }} 元</td>
                <td>
                <span
                    :class="d.status === 'refunded'
                    ? 'badge bg-secondary'
                    : 'badge bg-success'"
                >
                    {{ d.status }}
                </span>
                </td>
                <td>
                <button
                    v-if="d.status !== 'refunded'"
                    class="btn btn-danger btn-sm"
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
    import { useRoute, useRouter } from "vue-router";
    import axios from "axios";

    const route = useRoute();
    const router = useRouter();

    const orderId = route.params.orderId; // /refund/123
    const details = ref([]);

    // 取得明細
    onMounted(async () => {
    const res = await axios.get(
        `http://localhost:8080/api/order-details/order/${orderId}`
    );
    details.value = res.data;
    });

    // 單張退款
    async function refund(orderDetailId) {
    if (!confirm("確定要退款這張票嗎？")) return;

    await axios.post(
        `http://localhost:8080/api/refunds/single/${orderDetailId}`
    );

    alert("退款成功！");
    router.push("/refund-success");
    }
    </script>

    <style scoped>
    .container {
    max-width: 800px;
    }
    </style>
