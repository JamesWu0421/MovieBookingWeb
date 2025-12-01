<template>
  <div class="seat-map">

    <!-- 座位狀況 -->
    <div class="seat-status">
      <span>座位狀況</span>
      <div class="legend">
        <div class="legend-item">
          <span class="legend-color selected"></span>
          <span>已選取</span>
        </div>
        <!-- 只有一種不可選狀態 -->
        <div class="legend-item">
          <span class="legend-color blocked"></span>
          <span>無法選取</span>
        </div>
      </div>
    </div>

    <!-- 銀幕 -->
    <div class="screen-bar">
      銀 幕
    </div>

    <!-- 座位格 -->
    <div class="seat-grid" v-if="!loading">
      <div
        v-for="row in rows"
        :key="row.label"
        class="seat-row-line"
      >
        <!-- 左列號 -->
        <div class="row-label">{{ row.label }}</div>

        <!-- 座位 -->
        <div class="seat-row">
          <button
            v-for="seat in row.seats"
            :key="seat.id"
            class="seat"
            :class="seatClass(seat)"
            @click="onSeatClick(seat)"
          >
            {{ seat.seatNumber }}
          </button>
        </div>

        <!-- 右列號 -->
        <div class="row-label">{{ row.label }}</div>
      </div>
    </div>

    <p v-else>座位載入中…</p>
    <p v-if="error" style="color:red">{{ error }}</p>

  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import { useBookingStore } from '../../stores/booking';
import { seatApi, ticketApi, seatLockApi } from '../../services/movieApi';

const props = defineProps({
  screenId: {
    type: Number,
    required: true,
  },
  showId: {
    type: Number,
    required: true,
  },
});

const bookingStore = useBookingStore();

const seats = ref([]);
const soldSeatIds = ref([]);
const lockedSeatIds = ref([]);
const loading = ref(false);
const error = ref('');

// 撈座位
const fetchSeats = async () => {
  if (!props.screenId) return;
  try {
    const res = await seatApi.getByScreen(props.screenId);
    seats.value = res.data || [];
  } catch (err) {
    console.error(err);
    error.value = '載入座位失敗';
  }
};

// 撈已售出座位
const fetchSoldSeats = async () => {
  if (!props.showId) return;
  try {
    const res = await ticketApi.getSold(props.showId);
    soldSeatIds.value = (res.data || []).map(t => t.seatId);
  } catch (err) {
    console.error(err);
  }
};

// 撈 seat_locks active
const fetchLockedSeats = async () => {
  if (!props.showId) return;
  try {
    const res = await seatLockApi.getActive(props.showId);
    lockedSeatIds.value = (res.data || []).map(l => l.seatId);
  } catch (err) {
    console.error(err);
  }
};

// 分組 row
const rows = computed(() => {
  const map = new Map();

  seats.value.forEach(s => {
    if (!map.has(s.rowLabel)) map.set(s.rowLabel, []);
    map.get(s.rowLabel).push(s);
  });

  const sorted = [...map.keys()].sort();

  return sorted.map((rowLabel, index) => ({
    label: rowLabel,
    display: index + 1,
    seats: map.get(rowLabel).sort((a, b) => a.seatNumber - b.seatNumber),
  }));
});

// 合併：封鎖 + 已售出 + seat_locks
const isUnavailable = seat =>
  seat.isBlocked === true ||
  soldSeatIds.value.includes(seat.id) ||
  lockedSeatIds.value.includes(seat.id);

const isSelected = seat =>
  bookingStore.selectedSeats?.some(s => s.seatId === seat.id);

const seatClass = seat => {
  if (isUnavailable(seat)) return 'blocked';
  if (isSelected(seat)) return 'selected';
  return 'available';
};

const onSeatClick = seat => {
  if (isUnavailable(seat)) return;

  bookingStore.toggleSeat({
    seatId: seat.id,
    rowLabel: seat.rowLabel,
    seatNumber: seat.seatNumber,
    price: 300,
  });
};

const loadAll = async () => {
  loading.value = true;
  error.value = '';
  try {
    await Promise.all([
      fetchSeats(),
      fetchSoldSeats(),
      fetchLockedSeats(), //  一起撈 lock
    ]);
  } finally {
    loading.value = false;
  }
};

onMounted(loadAll);

// 如果父層 showId/screenId 變更（切換場次）就重撈
watch(
  () => [props.screenId, props.showId],
  () => loadAll()
);
</script>

<style scoped>
.seat-map {
  margin-top: 40px;
  text-align: center;
}

.seat-status {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 24px;
  margin-bottom: 24px;
}

.legend {
  display: flex;
  gap: 16px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 6px;
}

.legend-color {
  width: 20px;
  height: 20px;
  border-radius: 2px;
  border: 1px solid #ccc;
}

.legend-color.selected {
  background: #ad9278;
}

/* 封鎖 + 已售出 統一 */
.legend-color.blocked {
  background: #dedddd;
}

/* 銀幕 */
.screen-bar {
  margin: 0 auto 50px;
  padding: 10px;
  width: 90%;
  background: #f3f3f3;
  letter-spacing: 4px;
}

/* 格線 */
.seat-grid {
  display: inline-flex;
  flex-direction: column;
  gap: 8px;
}

.seat-row-line {
  display: flex;
  align-items: center;
  gap: 10px;
}

.row-label {
  width: 18px;
  text-align: center;
}

.seat-row {
  display: flex;
  gap: 8px;
}

/* 座位 */
.seat {
  width: 40px;
  height: 40px;
  border: 1px solid #ccc;
  background: #fff;
  cursor: pointer;
}

.seat.available:hover {
  outline: 2px solid #ad9278;
}

.seat.selected {
  background: #ad9278;
  color: #fff;
}

/* 封鎖 + 已售出 都用這個 */
.seat.blocked {
  background: #dedddd;
  cursor: not-allowed;
}
</style>
