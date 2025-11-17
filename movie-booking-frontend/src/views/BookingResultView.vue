<template>
  <section>
    <h1>訂票完成</h1>
    <p v-if="orderId">訂單編號：{{ orderId }}</p>
    <p v-else>目前沒有訂單資訊。</p>
    <button @click="goHome">回首頁</button>
  </section>
</template>

<script setup>
import { useRouter } from 'vue-router';
import { useBookingStore } from '../stores/booking';

const bookingStore = useBookingStore();
const router = useRouter();

const orderId = bookingStore.orderId;

const goHome = () => {
  bookingStore.resetAll();
  router.push('/');
};
</script>
