<template>
  <el-card>
  <section>
    <h2>{{ isEdit ? '編輯影廳' : '新增影廳' }}</h2>

    <p v-if="loading">載入中...</p>
    <p v-else-if="error" style="color:red;">{{ error }}</p>

    <form v-else @submit.prevent="onSubmit" class="screen-form">
      <div class="form-row">
        <label>影廳名稱</label>
        <input v-model="form.name" required />
      </div>

      <div class="form-row">
        <label>座位總數</label>
        <input type="number" v-model.number="form.totalSeats" min="1" required />
      </div>

      <div class="form-row">
        <label>影廳類型</label>
        <input v-model="form.screenType" placeholder="例如：IMAX、Dolby、一般廳" />
      </div>

      <div class="form-row">
        <label>票價</label>
        <input type="number" v-model.number="form.price" min="0" required />
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
import { screenApi } from '../services/movieApi';
import Swal from 'sweetalert2'; 

const route = useRoute();
const router = useRouter();

const idParam = route.params.id;
const isEdit = computed(() => !!idParam);

const loading = ref(false);
const error = ref('');

const form = ref({
  name: '',
  totalSeats: 0,
  screenType: '',
  price: 0,
});

const loadScreen = async () => {
  if (!isEdit.value) return;
  loading.value = true;
  error.value = '';
  try {
    const res = await screenApi.getById(idParam);
    form.value = {
      name: res.data.name,
      totalSeats: res.data.totalSeats,
      screenType: res.data.screenType,
      price: res.data.price,
    };
  } catch (err) {
    error.value = '載入影廳資料失敗';
    console.error(err);
  } finally {
    loading.value = false;
  }
};

const onSubmit = async () => {
  try {
    if (isEdit.value) {
      await screenApi.update(idParam, form.value);

      //成功提示
      await Swal.fire({
        icon: 'success',
        title: '修改成功',
        text: `影廳「${form.value.name}」已更新。`,
        confirmButtonText: 'OK'
      });

    } else {
      await screenApi.create(form.value);
      await Swal.fire({
        icon: 'success',
        title: '新增成功',
        text: `影廳「${form.value.name}」已建立。`,
        confirmButtonText: 'OK'
      });
    }

    router.push('/screens');

  } catch (err) {
    console.error(err);

    //失敗提示
    Swal.fire({
      icon: 'error',
      title: '儲存失敗',
      text: '無法儲存影廳資料，請稍後再試。',
      confirmButtonText: '好啦'
    });
  }
};

const goBack = () => {
  router.push('/screens');
};

onMounted(() => {
  loadScreen();
});
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

.screen-form {
  max-width: 400px;
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

.form-row input {
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