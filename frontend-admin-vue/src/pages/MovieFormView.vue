<template>
  <el-card>
  <section>
    <h2>{{ isEdit ? '編輯電影' : '新增電影' }}</h2>

    <p v-if="loading">載入中...</p>
    <p v-else-if="error" style="color:red;">{{ error }}</p>



    <MovieForm
      v-else
      v-model="movie"
      :submit-text="isEdit ? '儲存更新' : '新增'"
      @submit="handleSubmit"
      @cancel="goBack"
    />
  </section>
  </el-card>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { movieApi } from '../services/api';
import MovieForm from './MovieForm.vue';
import Swal from 'sweetalert2';

const route = useRoute();
const router = useRouter();

const id = route.params.id;
const isEdit = computed(() => !!id);

const movie = ref({});
const loading = ref(false);
const error = ref(null);

const loadMovie = async () => {
  if (!isEdit.value) return;
  loading.value = true;
  error.value = null;
  try {
    const res = await movieApi.getById(id);
    movie.value = res.data;
  } catch (err) {
    error.value = '載入電影資料失敗';
  } finally {
    loading.value = false;
  }
};

const handleSubmit = async (data) => {
  try {
    if (isEdit.value) {
      await movieApi.update(id, data);

      //修改成功提示
      await Swal.fire({
        icon: 'success',
        title: '修改成功',
        text: `電影資料已更新。`,
      });

    } else {
      await movieApi.create(data);

      await Swal.fire({
        icon: 'success',
        title: '新增成功',
        text: `電影已成功新增。`,
      });
    }

    router.push({ name: 'movies' });

  } catch (err) {
    console.error(err);

    //失敗提示
    Swal.fire({
      icon: 'error',
      title: '儲存失敗',
      text: '無法儲存電影資料，請稍後再試。',
    });
  }
};

const goBack = () => {
  router.push({ name: 'movies' });
};

onMounted(loadMovie);
</script>


<style scoped>

h2{
  width: 100%;
  height: 60px;
  font-weight: 500;
  color: rgb(51, 51, 51);
  letter-spacing: 0.2em;
  margin-top: 20px;
  margin-bottom: 5px;
  border-bottom: 1px solid rgb(230, 230, 230);
}
</style>
