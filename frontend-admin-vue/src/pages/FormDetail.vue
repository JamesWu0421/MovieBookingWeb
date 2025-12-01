    <template>
    <div class="container py-4">

        <button class="btn btn-secondary mb-3" @click="$router.back()">← 返回</button>

        <div class="card shadow-sm p-4">

        <h3 class="mb-4 fw-bold">工單詳細內容</h3>

        <div v-if="ticket">
            <div class="row mb-3">
            <div class="col-3 fw-bold">工單 ID：</div>
            <div class="col">{{ ticket.id }}</div>
            </div>

            <div class="row mb-3">
            <div class="col-3 fw-bold">訂單編號：</div>
            <div class="col">{{ ticket.orderId }}</div>
            </div>

            <div class="row mb-3">
            <div class="col-3 fw-bold">問題類型：</div>
            <div class="col">{{ ticket.issueType }}</div>
            </div>

            <div class="row mb-3">
            <div class="col-3 fw-bold">標題：</div>
            <div class="col">{{ ticket.title }}</div>
            </div>

            <div class="row mb-3">
            <div class="col-3 fw-bold">內容描述：</div>
            <div class="col">
                <div class="description-box">{{ ticket.description }}</div>
            </div>
            </div>

            <div class="row mb-3">
            <div class="col-3 fw-bold">狀態：</div>
            <div class="col">
                <span class="badge bg-warning text-dark" v-if="ticket.status === 'pending'">待處理</span>
                <span class="badge bg-success" v-else-if="ticket.status === 'resolved'">已完成</span>
                <span class="badge bg-secondary" v-else>{{ ticket.status }}</span>
            </div>
            </div>

            <div class="row mb-3">
            <div class="col-3 fw-bold">處理人員：</div>
            <div class="col">{{ ticket.handledBy || "未指派" }}</div>
            </div>

            <div class="row mb-3">
            <div class="col-3 fw-bold">建立時間：</div>
            <div class="col">{{ formatDate(ticket.createdAt) }}</div>
            </div>

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
    const ticket = ref(null);

    onMounted(async () => {
    const id = route.params.id;
    const res = await axios.get(`http://localhost:8080/api/tickets/${id}`);
    ticket.value = res.data;
    });

    // 日期格式化
    function formatDate(time) {
    return new Date(time).toLocaleString("zh-TW", { hour12: false });
    }
    </script>

    <style scoped>
    .description-box {
    padding: 12px;
    border: 1px solid #ccc;
    border-radius: 8px;
    background: #fafafa;
    white-space: pre-wrap;
    }
    </style>
