<template>
  <el-card>
    <section>
    <h2>影廳列表</h2>

    <div style="margin: 12px 0;">
      <button class="action-btn new" @click="goNew">+ 新增影廳</button>
    </div>

    <p v-if="loading">載入中...</p>
    <p v-else-if="error" style="color:red;">{{ error }}</p>

    <table v-else class="screen-table">
      <thead>
        <tr>
          <th>ID</th>
          <th>名稱</th>
          <th>座位數</th>
          <th>類型</th>
          <th>票價</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="s in screens" :key="s.id">
          <td>{{ s.id }}</td>
          <td>{{ s.name }}</td>
          <td>{{ s.totalSeats }}</td>
          <td>{{ s.screenType }}</td>
          <td>{{ s.price }}</td>
          <td>
            <button class="action-btn edit" @click="goEdit(s.id)">編輯</button>
            <button class="action-btn delete" @click="onDelete(s.id)">刪除</button>
            <button class="action-btn secondary" @click="goSeatBatch(s.id)">
              批次新增座位
            </button>
            <button class="action-btn seatlist" @click="goSeatList(s.id)">
              查看座位
            </button>
            <button class="action-btn seatmap" @click="goSeatMap(s.id)">
              座位圖
            </button>
          </td>
        </tr>
      </tbody>
    </table>
  </section>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { screenApi } from '../services/api';
import Swal from 'sweetalert2';

const router = useRouter();

const screens = ref([]);
const loading = ref(false);
const error = ref('');

const fetchScreens = async () => {
  loading.value = true;
  error.value = '';
  try {
    const res = await screenApi.getAll();
    screens.value = res.data;
  } catch (err) {
    error.value = '載入影廳資料失敗';
    console.error(err);
  } finally {
    loading.value = false;
  }
};

const goNew = () => {
  router.push('/screens/new');
};

const goEdit = (id) => {
  router.push(`/screens/${id}/edit`);
};

// 刪除確認提示
const onDelete = async (id) => {
  const result = await Swal.fire({
    title: '確定刪除？',
    text: '此操作無法復原！',
    icon: 'warning',
    showCancelButton: true,
    confirmButtonText: '刪除',
    cancelButtonText: '取消',
    confirmButtonColor: '#d33',
  });

  if (!result.isConfirmed) return;

  try {
    await screenApi.remove(id);
    await fetchScreens();

    // 刪除成功提示
    await Swal.fire({
      icon: 'success',
      title: '刪除成功',
      text: '影廳已成功移除。',
    });
  } catch (err) {
    console.error(err);

    // 刪除失敗提示
    Swal.fire({
      icon: 'error',
      title: '刪除失敗',
      text: '無法刪除影廳，可能已有相關資料。',
    });
  }
};

// 跳到批次新增座位頁，帶上 screenId
const goSeatBatch = (screenId) => {
  router.push({
    path: '/seats/batch',
    query: { screen: screenId },
  });
};

const goSeatList = (screenId) => {
  router.push(`/screens/${screenId}/seats`);
};

const goSeatMap = (screenId) => {
  router.push(`/screens/${screenId}/seat-map`);
};

onMounted(() => {
  fetchScreens();
});
</script>


<style scoped>

h2{
  width: 100%;
  height: 60px;
  font-weight: 300;
  color: rgb(51, 51, 51);
  letter-spacing: 0.5em;
  margin-top: 20px;
  margin-bottom: 5px;
  border-bottom: 1px solid rgb(230, 230, 230);
}

.screen-table {
  width: 100%;
  border-collapse: collapse;
}

.screen-table th,
.screen-table td {
  border: 1px solid #ddd;
  padding: 8px 10px;
}

.screen-table th {
  background-color: #f5f5f5;
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

.action-btn:hover {
  background-color: #f0f0f0;
}

.action-btn.new {
  font-weight: 400;
  font-size: 16px;
  letter-spacing: 2px;
  background-color: rgba(116, 175, 231, 0.5);
  color: #2980bf;
  border-color: #3fa5ee;
}

.action-btn.new:hover {
  background-color: #3683d0;
  color: #ffffff;
}

.action-btn.edit {
  background-color: rgba(116, 175, 231, 0.5);
  color: #2980bf;
  border-color: #3fa5ee;
}

.action-btn.edit:hover {
  background-color: #3683d0;
  color: #ffffff;
}

.action-btn.delete {
  background-color: rgba(231, 125, 141, 0.5);
  color: #cd4949;
  border-color:#e98787;
}

.action-btn.delete:hover {
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

.action-btn.seatlist {
  background-color: rgba(91, 193, 92, 0.5);
  color: #397b23;
  border-color:#4d823b;
}

.action-btn.seatlist:hover {
  background-color: #429a25;
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
</style>
