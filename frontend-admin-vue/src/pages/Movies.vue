<template>
  <el-card>
    <section>
    <h2>電影列表</h2>

    <div style="margin: 12px 0;">
      <button class="action-btn new" @click="goNew">+ 新增電影</button>
    </div>

    <p v-if="loading">載入中...</p>
    <p v-else-if="error" style="color:red;">{{ error }}</p>

    <table v-else class="movie-table">
      <thead>
        <tr>
          <th>ID</th>
          <th>電影海報</th>
          <th>中文片名</th>
          <th>英文片名</th>
          <th>分級</th>
          <th>片長</th>
          <th>上架</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="m in movies" :key="m.id">
          <td>{{ m.id }}</td>
          <td><img :src="m.posterUrl" :alt="m.title" class="poster-img" /></td>
          <td>{{ m.title }}</td>
          <td>{{ m.engTitle }}</td>
          <td><img :src="m.ratingLevel" alt="" class="rating-img" /></td>
          <td>{{ m.runtimeMinutes }} 分</td>
          <td>{{ m.isPublished ? '是' : '否' }}</td>
          <td>
            <button class="action-btn edit" @click="goEdit(m.id)">編輯</button>
            <button class="action-btn delete" @click="removeMovie(m.id)">刪除</button>
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
import { movieApi } from '../services/api';
import Swal from 'sweetalert2';

const router = useRouter();

const movies = ref([]);
const loading = ref(false);
const error = ref(null);

const loadMovies = async () => {
  loading.value = true;
  error.value = null;
  try {
    const res = await movieApi.getAll();
    movies.value = res.data;
  } catch (err) {
    error.value = '載入電影列表失敗';
  } finally {
    loading.value = false;
  }
};

const goNew = () => {
  router.push({ name: 'movie-new' });
};

const goEdit = (id) => {
  router.push({ name: 'movie-edit', params: { id } });
};

const removeMovie = async (id) => {
  const movie = movies.value.find(m => m.id === id);
  const movieName = movie?.title ?? `#${id}`;

  //確認刪除提示
  const result = await Swal.fire({
    title: '確定刪除？',
    text: `電影「${movieName}」將會永久刪除！`,
    icon: 'warning',
    showCancelButton: true,
    confirmButtonText: '刪除',
    cancelButtonText: '取消',
    confirmButtonColor: '#d33',
  });

  if (!result.isConfirmed) return;

  try {
    await movieApi.remove(id);
    await loadMovies();

    //成功提示
    Swal.fire({
      icon: 'success',
      title: '刪除成功',
      text: `電影「${movieName}」已成功刪除。`,
    });

  } catch (err) {
    console.error(err);

    //失敗提示
    Swal.fire({
      icon: 'error',
      title: '刪除失敗',
      text: '刪除電影時發生錯誤，請稍後再試。',
    });
  }
};

onMounted(loadMovies);
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

.movie-table {
  width: 100%;
  border-collapse: collapse;
}
.movie-table th,
.movie-table td {
  border: 1px solid #ddd;
  padding: 8px;
}
.movie-table th {
  background: #f5f5f5;
  text-align: left;
}

.poster-img {
  width: 70px; 
  height: 100px;
  object-fit: cover;
}

.rating-img{
  width: 40px; 
  height: 40px;
  object-fit: cover;
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

</style>
