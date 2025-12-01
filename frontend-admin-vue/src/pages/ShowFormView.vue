<template>
  <el-card>
  <section>
    <h2>{{ isEdit ? '編輯場次' : '新增場次' }}</h2>

    <p v-if="loading">載入中...</p>
    <p v-else-if="error" style="color:red;">{{ error }}</p>

    <form v-else @submit.prevent="onSubmit" class="show-form">
      <div class="form-row">
        <label>電影</label>
        <select v-model.number="form.movieId" required>
          <option value="" disabled>請選擇電影</option>
          <option v-for="m in movies" :key="m.id" :value="m.id">
            {{ m.id }} - {{ m.title }}
          </option>
        </select>
      </div>

      <div class="form-row">
        <label>影廳</label>
        <select v-model.number="form.screenId" required>
          <option value="" disabled>請選擇影廳</option>
          <option v-for="s in screens" :key="s.id" :value="s.id">
            {{ s.id }} - {{ s.name }}
          </option>
        </select>
      </div>

      <div class="form-row">
        <label>日期</label>
        <input type="date" v-model="form.showDate" required />
      </div>

      <div class="form-row">
        <label>開始時間</label>
        <input type="time" v-model="form.showTime" required />
      </div>

      <div class="form-row">
        <label>結束時間</label>
        <input type="time" v-model="form.endTime" required />
      </div>

      <div class="btn-row">
        <button type="submit" class="action-btn save">{{ isEdit ? '儲存修改' : '新增' }}</button>
        <button type="button" class="action-btn cancel" @click="goBack">返回列表</button>
      </div>
    </form>
  </section>
  </el-card>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { showApi, movieApi, screenApi } from '../services/movieApi';
import Swal from 'sweetalert2';

const route = useRoute();
const router = useRouter();

const idParam = route.params.id;
const isEdit = computed(() => !!idParam);

const loading = ref(false);
const error = ref('');

const movies = ref([]);
const screens = ref([]);

const form = ref({
  movieId: '',
  screenId: '',
  showDate: '',
  showTime: '',
  endTime: '',
});

const loadMoviesAndScreens = async () => {
  try {
    const [mRes, sRes] = await Promise.all([
      movieApi.getAll(),
      screenApi.getAll(),
    ]);
    movies.value = mRes.data;
    screens.value = sRes.data;
  } catch (err) {
    console.error(err);
    error.value = '載入電影或影廳資料失敗';
  }
};

// 後端時間轉 input 格式
const toInputTime = (t) => {
  if (!t) return '';
  if (typeof t === 'string' && t.length >= 5) return t.substring(0, 5);
  return t;
};

// input 格式轉後端格式
const toApiTime = (t) => {
  if (!t) return '';
  if (t.length === 5) return t + ':00';
  return t;
};

const loadShow = async () => {
  if (!isEdit.value) return;
  loading.value = true;
  error.value = '';
  try {
    const res = await showApi.getById(idParam);
    const data = res.data;
    form.value = {
      movieId: data.movieId,
      screenId: data.screenId,
      showDate: data.showDate,
      showTime: toInputTime(data.showTime),
      endTime: toInputTime(data.endTime),
    };
  } catch (err) {
    console.error(err);
    error.value = '載入場次資料失敗';
  } finally {
    loading.value = false;
  }
};

const onSubmit = async () => {
  try {
    const payload = {
      movieId: form.value.movieId,
      screenId: form.value.screenId,
      showDate: form.value.showDate,
      showTime: toApiTime(form.value.showTime),
      endTime: toApiTime(form.value.endTime),
    };

    if (isEdit.value) {
      await showApi.update(idParam, payload);

      await Swal.fire({
        icon: 'success',
        title: '修改成功',
        text: '場次資料已成功更新。',
      });
    } else {
      await showApi.create(payload);

      await Swal.fire({
        icon: 'success',
        title: '新增成功',
        text: '場次已成功建立。',
      });
    }

    router.push('/shows');

  } catch (err) {
    console.error(err);

    Swal.fire({
      icon: 'error',
      title: '儲存失敗',
      text: '無法儲存場次資料，請稍後再試。',
    });
  }
};

const goBack = () => {
  router.push('/shows');
};

onMounted(async () => {
  loading.value = true;
  await loadMoviesAndScreens();
  await loadShow();
  loading.value = false;
});
</script>

<style scoped>
.show-form {
  max-width: 420px;
}

.form-row {
  margin-bottom: 12px;
  display: flex;
  flex-direction: column;
}

.form-row label {
  margin-bottom: 4px;
  font-weight: bold;
}

.form-row input,
.form-row select {
  padding: 6px 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.btn-row {
  margin-top: 16px;
  display: flex;
  gap: 8px;
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

.action-btn.save{
  font-weight: 400;
  letter-spacing: 2px;
  background-color: rgba(116, 175, 231, 0.5);
  color: #2980bf;
  border-color: #3fa5ee;
}

.action-btn.save:hover {
  background-color: #3683d0;
  color: #ffffff;
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
</style>