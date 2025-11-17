<template>
  <div class="seat-selection-view">
  <div class="div1">
      <div class="div2">選取座位</div>
      </div>
  <section>
    
    <!-- <p>Showtime ID：{{ showtimeId }}</p> -->
    <SeatMap />
  </section>
  </div>
</template>

<script setup>
import { useRoute } from 'vue-router';
import SeatMap from '../components/booking/SeatMap.vue';

const route = useRoute();
const showtimeId = route.params.showtimeId;
</script>

<style scoped>

.seat-selection-view {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px 32px 48px;
}

.div1 {
  width: 100%;
  height: 60px;
  margin-top: 5px;
  border-top: 1px solid rgb(230, 230, 230);
  border-bottom: 1px solid rgb(230, 230, 230);
}

.div2 {
  font-weight: 300;
  letter-spacing: 0.5em;
  color: rgb(51, 51, 51);
  height: 100%;
  width: 145px;
  font-size: 1.5em;
  padding: 0px 2em;
  display: flex;
  -webkit-box-align: center;
  align-items: center;
  border-left: 1px solid rgb(230, 230, 230);
  border-right: 1px solid rgb(230, 230, 230);
  background: repeating-linear-gradient(-45deg, rgba(99, 99, 99, 0.067), rgba(103, 103, 103, 0.067) 2px, rgba(0, 0, 0, 0) 2px, rgba(0, 0, 0, 0) 4px);
}
</style>
