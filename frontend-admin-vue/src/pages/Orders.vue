    <template>
    <div class="container py-4">

        <h2 class="fw-bold mb-4">訂單管理</h2>

        <!-- 🔍 搜尋 + 篩選 -->
        <div class="d-flex gap-3 mb-4">
        <input type="number" v-model="searchId" placeholder="搜尋訂單 ID" class="form-control w-25" />

        <select v-model="filterStatus" class="form-select w-25">
            <option value="">全部狀態</option>
            <option value="PENDING">PENDING</option>
            <option value="COMPLETED">COMPLETED</option>
            <option value="CANCELED">CANCELED</option>
        </select>

        <button class="btn btn-primary" @click="loadOrders">搜尋</button>
        </div>

        <!-- ========== 📌 訂單表格 ========== -->
        <table class="table align-middle text-center">
        <thead class="table-dark">
            <tr>
            <th>ID</th>
            <th>使用者 ID</th>
            <th>場次 ID</th>
            <th>狀態</th>
            <th>總金額</th>
            <th>建立時間</th>
            <th>操作</th>
            </tr>
        </thead>

        <tbody>
            <tr v-for="o in filteredOrders" :key="o.id">

            <td>{{ o.id }}</td>
            <td>{{ o.userId }}</td>
            <td>{{ o.showId }}</td>

            <!-- ⭐ Badge 狀態 -->
            <td>
                <span :class="['status-badge', o.orderStatus]">
                {{ o.orderStatus }}
                </span>
            </td>

            <td class="fw-bold text-danger">{{ o.totalAmount }} 元</td>
            <td>{{ formatDate(o.orderTime) }}</td>

            <!-- 查看→導向訂單詳細 -->
            <td>
                <button class="btn-view" @click="$router.push('/orders/' + o.id)">
                查看
                </button>
            </td>

            </tr>
        </tbody>
        </table>

    </div>
    </template>


    <script setup>
    import axios from "axios"
    import { ref, computed, onMounted } from "vue"

    const orders = ref([])
    const searchId = ref("")
    const filterStatus = ref("")

    // 取得訂單
    async function loadOrders() {
    const res = await axios.get(`http://localhost:8080/api/orders`)
    orders.value = res.data
    }

    // 初始化載入
    onMounted(loadOrders)

    // 過濾邏輯
    const filteredOrders = computed(() => {
    return orders.value.filter(o => {
        return (!searchId.value || o.id == searchId.value) &&
            (!filterStatus.value || o.orderStatus === filterStatus.value)
    })
    })

    // 時間格式化
    function formatDate(t) {
    return t ? new Date(t).toLocaleString("zh-TW", { hour12: false }) : ""
    }
    </script>


    <style scoped>
    /* ===== 表整體樣式 ===== */
    table {
    width: 100%;
    border-collapse: separate;
    border-spacing: 0 10px;
    }

    tbody tr {
    background: #fff;
    box-shadow: 0 1px 5px rgba(0,0,0,.1);
    border-radius: 10px;
    }

    td {
    padding: 14px 10px !important;
    font-size: 15px;
    }

    /* ===== 狀態 Badge ===== */
    .status-badge {
    padding: 7px 16px;
    font-size: 14px;
    font-weight: 700;
    border-radius: 20px;
    color: white;
    text-transform: uppercase;
    }

    .status-badge.PENDING    { background:#e74c3c; } /* 🔥 未付款/未完成 */
    .status-badge.COMPLETED  { background:#2ecc71; } /* 🟢 已完成 */
    .status-badge.CANCELED   { background:#7f8c8d; } /* ⚫ 已取消 */

    .status-badge:hover { opacity:.85 }

    /* ===== 查看按鈕 ===== */
    .btn-view {
    background:#007bff;
    color:#fff;
    border:none;
    padding:8px 14px;
    border-radius:6px;
    font-size:14px;
    font-weight:600;
    transition:.15s;
    }
    .btn-view:hover { background:#005fcc; }
    </style>
