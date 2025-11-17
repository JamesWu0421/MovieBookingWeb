<template>
  <section>
    <h1>結帳資訊</h1>
    <TicketSummary />
    <form @submit.prevent="submitOrder">
      <div>
        <label>姓名</label>
        <input v-model="form.name" required />
      </div>
      <div>
        <label>電話</label>
        <input v-model="form.phone" required />
      </div>
      <div>
        <label>Email</label>
        <input v-model="form.email" type="email" required />
      </div>
      <button type="submit">確認送出訂單</button>
    </form>
  </section>
</template>

<script setup>
import { reactive } from 'vue';
import { useRouter } from 'vue-router';
import { useBookingStore } from '../stores/booking';
import TicketSummary from '../components/booking/TicketSummary.vue';

const bookingStore = useBookingStore();
const router = useRouter();

const form = reactive({
  name: '',
  phone: '',
  email: '',
});

const submitOrder = async () => {
  bookingStore.setContactInfo(form);

  // 之後這邊改成呼叫後端建立訂單，拿 orderId
  bookingStore.setOrderId('TEST123456');

  router.push({ name: 'booking-result' });
};
</script>
