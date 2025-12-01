<template>
  <div class="bg-light min-vh-100 py-4">
    <div class="container">

      <div class="d-flex justify-content-between align-items-center mb-4">
        <h3 class="fw-bold">訂單管理</h3>

        <!-- 搜尋 + 狀態選單 -->
        <div class="d-flex gap-2">
          <input
            v-model="searchId"
            type="number"
            class="form-control"
            placeholder="搜尋訂單 ID"
            style="width: 150px;"
          />

          <select v-model="filterStatus" class="form-select" style="width: 150px;">
            <option value="">全部狀態</option>
            <option value="pending">待處理</option>
            <option value="completed">已完成</option>
            <option value="canceled">已取消</option>
          </select>
        </div>
      </div>

      <div class="card shadow-sm border-0 rounded-4">
        <div class="card-body p-0">

          <table class="table table-hover table-striped table-bordered mb-0 text-center align-middle">
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

                <td>
                  <span
                    class="status-badge"
                    :style="{
                      background: o.orderStatus === 'completed'
                        ? '#2E7D32'
                        : o.orderStatus === 'pending'
                        ? '#FBC02D'
                        : '#D32F2F',
                      color: o.orderStatus === 'pending' ? 'black' : 'white'
                    }"
                  >
                    {{ o.orderStatus }}
                  </span>
                </td>

                <td>{{ o.totalAmount }}</td>

                <td>{{ formatDate(o.orderTime) }}</td>

                <td>
                  <RouterLink
                    :to="'/orders/' + o.id"
                    class="btn btn-primary btn-sm rounded-pill px-3"
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
  </div>
</template>

<script setup>
import axios from "axios";
import { ref, computed } from "vue";

const orders = ref([]);

const searchId = ref("");
const filterStatus = ref("");

// 取得全部訂單
axios.get("http://localhost:8080/api/orders").then(res => {
  orders.value = res.data;
});

// 過濾後結果
const filteredOrders = computed(() => {
  return orders.value.filter(o => {
    const matchId =
      searchId.value === "" || o.id === Number(searchId.value);

    const matchStatus =
      filterStatus.value === "" || o.orderStatus === filterStatus.value;

    return matchId && matchStatus;
  });
});

// 時間格式
function formatDate(time) {
  if (!time) return "";
  return new Date(time).toLocaleString("zh-TW", { hour12: false });
}
</script>
<style scoped>
.status-badge {
  display: inline-block;
  width: 110px;       
  text-align: center;
  padding: 6px 0;
  font-weight: 600;
  border-radius: 8px;
  font-size: 14px;
}
</style>