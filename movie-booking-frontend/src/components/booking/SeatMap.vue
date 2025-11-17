<template>
  <div class="seat-map">
    <!-- 上面的「座位狀況」說明 -->
    <div class="seat-status">
      <span>座位狀況</span>
      <div class="legend">
        <div class="legend-item">
          <span class="legend-color selected"></span>
          <span>已選取</span>
        </div>
        <div class="legend-item">
          <span class="legend-color blocked"></span>
          <span>無法選取</span>
        </div>
      </div>
    </div>

    <!-- 銀幕 -->
    <div class="screen-bar">
      <span>銀 幕</span>
    </div>

    <!-- 座位格子（每一列：左列號＋座位＋右列號） -->
    <div class="seat-grid">
      <div
        v-for="row in rows"
        :key="row.label"
        class="seat-row-line"
      >
        <!-- 左側列號 -->
        <div class="row-label">
          {{ row.display }}
        </div>

        <!-- 座位 -->
        <div class="seat-row">
          <button
            v-for="seat in seatNumbers"
            :key="row.label + '-' + seat"
            class="seat"
            :class="seatClass(row, seat)"
            @click="onSeatClick(row, seat)"
          >
            {{ seat }}
          </button>
        </div>

        <!-- 右側列號 -->
        <div class="row-label">
          {{ row.display }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useBookingStore } from '../../stores/booking';

const bookingStore = useBookingStore();

const rows = [
  { display: 1, label: 'A' },
  { display: 2, label: 'B' },
  { display: 3, label: 'C' },
  { display: 4, label: 'D' },
  { display: 5, label: 'E' },
  { display: 6, label: 'F' },
];

const seatNumbers = [11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1];

const blockedSeats = new Set([
  'C-7', 'C-6', 'D-5', 'D-4', 'F-2', 'F-1',
  'E-7', 'E-8',
  'D-6',
]);

const isBlocked = (row, seat) => blockedSeats.has(`${row.label}-${seat}`);

const isSelected = (row, seat) =>
  bookingStore.selectedSeats?.some(
    (s) => s.rowLabel === row.label && s.seatNumber === seat,
  );

const seatClass = (row, seat) => {
  if (isBlocked(row, seat)) return 'blocked';
  if (isSelected(row, seat)) return 'selected';
  return 'available';
};

const onSeatClick = (row, seat) => {
  if (isBlocked(row, seat)) return;

  bookingStore.toggleSeat({
    rowLabel: row.label,
    seatNumber: seat,
    price: 300,
  });
};
</script>

<style scoped>
.seat-map {
  margin-top: 40px;
  text-align: center;
  color: #333;
}

/* 座位狀況 */
.seat-status {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 24px;
  margin-bottom: 24px;
  font-size: 14px;
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

.legend-color.blocked {
  background: #dedddd;
}

/* 銀幕 */
.screen-bar {
  font-weight: 300;
  margin: 0 auto 50px;
  background: #f3f3f3;
  padding: 8px 0;
  width: 95%;
  font-size: 16px;
  letter-spacing: 4px;
}

/* 座位格 */
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
  width: 16px;
  text-align: center;
  font-size: 14px;
}

.seat-row {
  display: flex;
  gap: 8px;
}

.seat {
  width: 40px;
  height: 40px;
  border: 1px solid #ccc;
  background: #ffffff;
  font-size: 12px;
  cursor: pointer;
}

.seat.available:hover {
  outline: 2px solid #ad9278;
}

.seat.selected {
  background: #ad9278;
  color: #ffffff;
}

.seat.blocked {
  background: #dedddd;
}
</style>
