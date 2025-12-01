        <template>
        <div class="container py-4">

            <button class="btn btn-secondary mb-3" @click="$router.back()">← 返回</button>

            <div class="card shadow-sm p-4">
            <h3 class="mb-4 fw-bold">訂單詳細內容</h3>

            <div v-if="order">

                <!-- ===== 訂單基本資訊 ===== -->
                <div class="row mb-3">
                <div class="col-3 fw-bold">訂單 ID：</div>
                <div class="col">{{ order.id }}</div>
                </div>

                <div class="row mb-3">
                <div class="col-3 fw-bold">使用者 ID：</div>
                <div class="col">{{ order.userId }}</div>
                </div>

                <div class="row mb-3">
                <div class="col-3 fw-bold">場次 ID：</div>
                <div class="col">{{ order.showId }}</div>
                </div>

                <div class="row mb-3">
                <div class="col-3 fw-bold">訂單狀態：</div>
                <div class="col">
                    <span
                    :class="{
                        'badge bg-warning text-dark px-3 py-2': order.orderStatus === 'pending',
                        'badge bg-success px-3 py-2': order.orderStatus === 'completed',
                        'badge bg-danger px-3 py-2': order.orderStatus === 'canceled',
                        'badge bg-secondary px-3 py-2': !['pending','completed','canceled'].includes(order.orderStatus)
                    }"
                    >
                    {{ order.orderStatus }}
                    </span>
                </div>
                </div>

                <div class="row mb-3">
                <div class="col-3 fw-bold">訂單金額：</div>
                <div class="col">{{ order.totalAmount }} 元</div>
                </div>

                <div class="row mb-4">
                <div class="col-3 fw-bold">建立時間：</div>
                <div class="col">{{ formatDate(order.orderTime) }}</div>
                </div>

                <!-- ===== 明細表格 ===== -->
                <h5 class="fw-bold mb-3">座位明細</h5>

                <table class="table table-bordered table-striped align-middle text-center">
                <thead class="table-dark">
                    <tr>
                    <th>明細 ID</th>
                    <th>座位 ID</th>
                    <th>票種</th>
                    <th>票價</th>
                    <th>狀態</th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-for="d in order.details" :key="d.id">
                    <td>{{ d.id }}</td>
                    <td>{{ d.seatId }}</td>
                    <td>{{ d.ticketType }}</td>
                    <td>{{ d.ticketPrice }} 元</td>
                    <td>{{ d.status }}</td>
                    </tr>
                </tbody>
                </table>

            </div>

            <div v-else>
                <p>載入中...</p>
            </div>

            </div>
        </div>
        </template>

        <script setup>
    import { useRoute } from "vue-router";
    import { ref, onMounted } from "vue";
    import axios from "axios";

    const route = useRoute();
    const order = ref(null);

    onMounted(async () => {
    const id = route.params.id;

    // 取訂單主檔
    const resOrder = await axios.get(`http://localhost:8080/api/orders/by-id/${id}`);

    // 取訂單明細
    const resDetails = await axios.get(`http://localhost:8080/api/order-details/order/${id}`);

    // 合併成完整資料
    order.value = {
        ...resOrder.data,
        details: resDetails.data
    };
    });

    // 日期格式化
    function formatDate(time) {
    if (!time) return "";
    return new Date(time).toLocaleString("zh-TW", { hour12: false });
    }
    </script>
