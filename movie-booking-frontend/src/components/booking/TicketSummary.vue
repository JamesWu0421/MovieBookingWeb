<template>
  <div class="ticket-summary">
    <h2>訂票資訊</h2>
    <p v-if="!booking.selectedMovie">尚未選擇電影</p>
    <div v-else>
      <p>電影：{{ booking.selectedMovie.title }}</p>
      <p v-if="booking.selectedShowtime">
        場次：{{ booking.selectedShowtime.startTime }}
      </p>
      <p>座位數量：{{ booking.seatCount }}</p>
      <p>總金額：{{ booking.totalPrice }}</p>
    </div>
  </div>
</template>

<script setup>
import { storeToRefs } from 'pinia';
import { useBookingStore } from '../../stores/booking';

const store = useBookingStore();
const booking = storeToRefs(store);
</script>
