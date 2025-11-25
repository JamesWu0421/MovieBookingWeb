<template>
  <el-card>
  <section>
    <h2>座位列表（影廳 ID：{{ screenId }}）</h2>

    <div class="toolbar">
      <button  class="action-btn cancel" @click="goBack">返回影廳列表</button>
      <button class="action-btn secondary" @click="goBatch">批次新增座位</button>
      <button class="action-btn seatmap" @click="goSeatMap">查看座位圖</button>
    </div>

    <p v-if="loading">載入中...</p>
    <p v-else-if="error" style="color: red">{{ error }}</p>

    <table v-else class="seat-table">
      <thead>
        <tr>
          <th>ID</th>
          <th>排</th>
          <th>座號</th>
          <th>是否封鎖</th>
        </tr>
      </thead>
      <tbody>
        <tr
          v-for="seat in seats"
          :key="seat.id"
          :class="{ blockedRow: seat.isBlocked }">
          <td>{{ seat.id }}</td>
          <td>{{ seat.rowLabel }}</td>
          <td>{{ seat.seatNumber }}</td>
          <td>{{ seat.isBlocked ? '是' : '否' }}</td>
        </tr>
      </tbody>
    </table>
  </section>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { seatApi } from '../services/api';
import Swal from 'sweetalert2';

const route = useRoute();
const router = useRouter();

const screenId = Number(route.params.screenId);
const seats = ref([]);
const loading = ref(false);
const error = ref('');

const fetchSeats = async () => {
  loading.value = true;
  error.value = '';
  try {
    const res = await seatApi.getByScreen(screenId);
    seats.value = res.data;
  } catch (err) {
    console.error(err);

    Swal.fire({
      icon: 'error',
      title: '載入失敗',
      text: '無法載入座位資料，請稍後再試。',
    });

    error.value = '載入座位資料失敗';
  } finally {
    loading.value = false;
  }
};

const goBack = () => {
  router.push('/screens');
};

const goBatch = () => {
  router.push({
    path: '/seats/batch',
    query: { screen: screenId },
  });
};

const goSeatMap = () => {
  router.push(`/screens/${screenId}/seat-map`);
};

onMounted(fetchSeats);
</script>


<style scoped>
.toolbar {
  margin: 12px 0;
  display: flex;
  gap: 8px;
}

.seat-table {
  width: 400px;
  border-collapse: collapse;
}

.seat-table th,
.seat-table td {
  border: 1px solid #ddd;
  padding: 6px 8px;
}

.seat-table th {
  width: 60px;
  background: #f5f5f5;
  text-align: left;
}

.action-btn {
  padding: 6px 14px;
  border-radius: 5px;
  border: 1px solid rgb(111, 119, 127);
  cursor: pointer;
  font-size: 14px;
  transition: 0.2s;
  background-color: #fff;
  margin-right: 8px;
}

.action-btn.cancel {
  background-color: rgba(231, 125, 141, 0.5);
  color: #cd4949;
  border-color:#e98787;
}

.action-btn.cancel:hover {
  background-color: #d45f5f;
  color: #ffffff;
}

.action-btn.secondary {
  background-color: rgba(223, 196, 75, 0.5);
  color: #836e2d;
  border-color:#c2a653;
}

.action-btn.secondary:hover {
  background-color: rgb(209, 182, 62);
  color: #ffffff;
}

.action-btn.seatmap {
  background-color: rgba(216, 117, 60, 0.5);
  color: #834c25;
  border-color:#926749;
}

.action-btn.seatmap:hover {
  background-color: #bb7c4f;
  color: #ffffff;
}

.blockedRow {
  background-color: #e0e0e0;
  color: #666;
}

.blockedRow td {
  opacity: 0.7;
}
</style>