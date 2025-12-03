    <template>
    <div class="container mt-4">
        <h3 class="fw-bold mb-3">客服工單管理</h3>

        <div class="card shadow-sm">
        <div class="card-body p-0">

            <table class="table table-hover table-striped mb-0">
            <thead class="table-dark text-center">
                <tr>
                <th>ID</th>
                <th>訂單編號</th>
                <th>類型</th>
                <th>狀態</th>
                <th>處理人員</th>
                <th>建立時間</th>
                <th>操作</th>
                </tr>
            </thead>

            <tbody>
                <tr v-for="t in tickets" :key="t.id" class="align-middle text-center">
                <td>{{ t.id }}</td>
                <td>{{ t.orderId }}</td>

                <!-- 類型 badge -->
                <td> {{ t.issueType }}</td>

                <!-- 狀態 badge -->
                <td>
                    <span
                    :class="{
                        'badge bg-warning text-dark': t.status === 'order pending',
                        'badge bg-success': t.status === 'resolved',
                        'badge bg-secondary': t.status !== 'order pending' && t.status !== 'resolved'
                    }"
                    >
                    {{ t.status }}
                    </span>
                </td>

                <!-- 處理人員 -->
                <td>
                    <span v-if="t.handledBy" class="badge bg-primary">
                    {{ t.handledBy }}
                    </span>
                    <span v-else class="badge bg-secondary">未指派</span>
                </td>

                <!-- 建立時間格式化 -->
                <td>{{ formatDate(t.createdAt) }}</td>

                <td>
                    <RouterLink
                    :to="'/tickets/' + t.id"
                    class="btn btn-sm btn-outline-primary"
                    >
                    查看
                    </RouterLink>
                </td>
                </tr>
            </tbody>
            </table>

        </div>
        </div>
    </div>
    </template>

    <script setup>
    import axios from "axios";
    import { ref } from "vue";

    const tickets = ref([]);

    axios.get(import.meta.env.VITE_BASE_URL+"/api/customer-service"||"http://localhost:8080/api/customer-service").then(res => {
    tickets.value = res.data;
    });

    // 時間格式化
    function formatDate(time) {
    if (!time) return "";
    return new Date(time).toLocaleString("zh-TW", {
        year: "numeric",
        month: "2-digit",
        day: "2-digit",
        hour: "2-digit",
        minute: "2-digit",
        second: "2-digit"
    });
    }
    </script>
