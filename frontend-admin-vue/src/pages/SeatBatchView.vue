<template>
  <el-card>
  <section>
    <h2>批次新增座位</h2>

    <div v-if="loading">載入中...</div>
    <div v-else-if="error" style="color: red">{{ error }}</div>

    <form v-else class="seat-form" @submit.prevent="onSubmit">

      <!-- 選影廳 -->
      <div class="form-row">
        <label>影廳</label>
        <select v-model.number="form.screenId" required>
          <option value="" disabled>請選擇影廳</option>
          <option v-for="s in screens" :key="s.id" :value="s.id">
            {{ s.id }} - {{ s.name }}
          </option>
        </select>
      </div>

      <!-- 排號起訖 -->
      <div class="form-row">
        <label>排號（起）</label>
        <input v-model="form.startRow" maxlength="1" required />
      </div>

      <div class="form-row">
        <label>排號（迄）</label>
        <input v-model="form.endRow" maxlength="1" required />
      </div>

      <!-- 座號起訖 -->
      <div class="form-row">
        <label>座號（起）</label>
        <input type="number" v-model.number="form.startSeat" min="1" required />
      </div>

      <div class="form-row">
        <label>座號（迄）</label>
        <input type="number" v-model.number="form.endSeat" min="1" required />
      </div>

      <div class="btn-row">
        <button class="action-btn secondary" type="submit">批次新增</button>
        <button class="action-btn cancel" type="button" @click="goBack">返回影廳列表</button>
      </div>

    </form>
  </section>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { screenApi, seatApi } from '../services/movieApi';
import { useRouter, useRoute } from 'vue-router';
import Swal from 'sweetalert2'; 

const router = useRouter();
const route = useRoute();

const loading = ref(false);
const error = ref('');
const screens = ref([]);

const form = ref({
  screenId: '',
  startRow: '',
  endRow: '',
  startSeat: 1,
  endSeat: 1
});

const loadScreens = async () => {
  loading.value = true;
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

const onSubmit = async () => {
  const startR = form.value.startRow.toUpperCase();
  const endR = form.value.endRow.toUpperCase();

  // 驗證只能 A~Z 一個字母
  if (!/^[A-Z]$/.test(startR) || !/^[A-Z]$/.test(endR)) {
    Swal.fire({
      icon: 'error',
      title: '格式錯誤',
      text: '排號必須是 A~Z 的單一字母',
    });
    return;
  }

  if (startR > endR) {
    Swal.fire({
      icon: 'warning',
      title: '排號錯誤',
      text: '排號起值不能大於迄值',
    });
    return;
  }

  // 生成 rowLabels（A~D → ["A","B","C","D"]）
  const rowLabels = [];
  for (let c = startR.charCodeAt(0); c <= endR.charCodeAt(0); c++) {
    rowLabels.push(String.fromCharCode(c));
  }

  const payload = {
    screenId: form.value.screenId,
    rowLabels,
    startSeatNumber: form.value.startSeat,
    endSeatNumber: form.value.endSeat
  };

  try {
    await seatApi.batchCreate(payload);

    // 成功提示
    await Swal.fire({
      icon: 'success',
      title: '批次新增成功！',
      text: `${rowLabels.join(', ')} 排，座號 ${form.value.startSeat}~${form.value.endSeat}`,
      confirmButtonText: 'OK'
    });

    router.push('/screens');
  } catch (err) {
    console.error(err);

    // 失敗提示
    Swal.fire({
      icon: 'error',
      title: '新增失敗',
      text: '批次新增座位過程發生錯誤，請稍後再試。',
    });
  }
};

const goBack = () => {
  router.push('/screens');
};

onMounted(async () => {
  await loadScreens();

  const screenQuery = route.query.screen;
  if (screenQuery) {
    form.value.screenId = Number(screenQuery);
  }
});
</script>

<style scoped>
.seat-form {
  max-width: 420px;
}

.form-row {
  margin-bottom: 12px;
  display: flex;
  flex-direction: column;
}

.form-row label {
  font-weight: bold;
  margin-bottom: 4px;
}

.form-row input,
.form-row select {
  padding: 6px 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.btn-row {
  display: flex;
  gap: 10px;
  margin-top: 16px;
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
</style>
