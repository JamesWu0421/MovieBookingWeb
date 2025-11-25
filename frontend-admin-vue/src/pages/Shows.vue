<template>
  <el-card>
    <section>
    <h2>場次列表</h2>

    <div style="margin: 12px 0;">
      <button class="action-btn new" @click="goNew">+ 新增場次</button>
    </div>

    <p v-if="loading">載入中...</p>
    <p v-else-if="error" style="color:red;">{{ error }}</p>

    <table v-else class="show-table">
      <thead>
        <tr>
          <th>ID</th>
          <th>電影名稱</th>
          <th>影廳ID</th>
          <th>日期</th>
          <th>開始時間</th>
          <th>結束時間</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="s in shows" :key="s.id">
          <td>{{ s.id }}</td>
          <td>{{ s.movieTitle }}</td>
          <td>{{ s.screenId }}</td>
          <td>{{ s.showDate }}</td>
          <td>{{ formatTime(s.showTime) }}</td>
          <td>{{ formatTime(s.endTime) }}</td>
          <td>
            <button class="action-btn edit" @click="goEdit(s.id)">編輯</button>
            <button class="action-btn delete" @click="onDelete(s.id)">刪除</button>
            <button class="action-btn seatmap" @click="goSeatMap(s)">座位圖</button>
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
import { showApi, movieApi } from '../services/api';
import Swal from 'sweetalert2';

const router = useRouter();

const shows = ref([]);
const loading = ref(false);
const error = ref('');

const fetchShows = async () => {
  loading.value = true;
  error.value = '';
  try {
    const [showsRes, moviesRes] = await Promise.all([
      showApi.getAll(),
      movieApi.getAll(),
    ]);

    const rawShows = showsRes.data || [];
    const movies = moviesRes.data || [];

    const movieMap = {};
    movies.forEach(m => {
      movieMap[m.id] = m.title;
    });

    shows.value = rawShows.map(s => ({
      ...s,
      movieTitle: movieMap[s.movieId] || `ID:${s.movieId}`,
    }));
  } catch (err) {
    console.error(err);
    error.value = '載入場次資料失敗';
  } finally {
    loading.value = false;
  }
};

const goNew = () => {
  router.push('/shows/new');
};

const goEdit = (id) => {
  router.push(`/shows/${id}/edit`);
};

//刪除確認提示
const onDelete = async (id) => {
  const result = await Swal.fire({
    title: '確定刪除？',
    text: '這個場次將被永久刪除！',
    icon: 'warning',
    showCancelButton: true,
    confirmButtonText: '刪除',
    cancelButtonText: '取消',
    confirmButtonColor: '#d33',
  });

  if (!result.isConfirmed) return;

  try {
    await showApi.remove(id);
    await fetchShows();

    Swal.fire({
      icon: 'success',
      title: '刪除成功',
      text: '場次已成功移除。',
    });

  } catch (err) {
    console.error(err);

    Swal.fire({
      icon: 'error',
      title: '刪除失敗',
      text: '刪除場次時發生錯誤，請稍後再試。',
    });
  }
};

// 帶 showId，顯示已售出座位
const goSeatMap = (show) => {
  router.push({
    path: `/screens/${show.screenId}/seat-map`,
    query: { showId: show.id },
  });
};

const formatTime = (t) => {
  if (!t) return '';
  if (typeof t === 'string' && t.length >= 5) {
    return t.substring(0, 5);
  }
  return t;
};

onMounted(fetchShows);
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

.show-table {
  width: 100%;
  border-collapse: collapse;
}

.show-table th,
.show-table td {
  border: 1px solid #ddd;
  padding: 8px 10px;
}

.show-table th {
  background-color: #f5f5f5;
  text-align: left;
}


.action-btn {
  padding: 6px 14px;
  border-radius: 5px;
  border: 1px solid #ccc;
  cursor: pointer;
  font-size: 14px;
  transition: 0.2s;
  background-color: #fff;
  margin-right: 8px;
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
