<template>
  <div class="seat-selection-view">
    <div class="div1">
      <div class="div2">選取座位</div>
    </div>

    <section>
      <p v-if="loadingShow">載入場次資料中...</p>
      <p v-else-if="error" style="color:red;">{{ error }}</p>

      <div v-else>
        <!-- 座位圖 -->
        <SeatMap
          v-if="screenId"
          :screen-id="screenId"
          :show-id="Number(showtimeId)"
        />

        <!-- 下方確認區 -->
        <div class="confirm-bar">
          <div>
            已選座位
            <span v-if="maxSeats > 0">（最多 {{ maxSeats }} 席）</span>：
            <span v-if="!selectedSeats.length">尚未選取</span>
            <span v-else>
              {{ selectedSeatsText }}
            </span>
          </div>

          <button
            class="confirm-btn"
            :disabled="!selectedSeats.length || locking"
            @click="confirmSeats"
          >
            {{ locking ? '處理中...' : '確認座位' }}
          </button>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import axios from "axios";
import { ref, computed, onMounted, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useBookingStore } from '../stores/booking';
import SeatMap from '../components/booking/SeatMap.vue';
import { showApi, seatLockApi } from '../services/movieApi';
import Swal from 'sweetalert2';

const route = useRoute();
const router = useRouter();
const showtimeId = route.params.showtimeId; // = showId

const screenId = ref(null);
const loadingShow = ref(false);
const error = ref('');
const locking = ref(false);

const bookingStore = useBookingStore();

const selectedSeats = computed(() => bookingStore.selectedSeats || []);

const selectedSeatsText = computed(() =>
  selectedSeats.value.map(s => `${s.rowLabel}${s.seatNumber}`).join('、')
);

// 新增：本次最多可選幾個座位（從前一頁 query 帶過來）
const maxSeats = computed(() => {
  const v = Number(route.query.ticketCount);
  return isNaN(v) ? 0 : v; // 0 代表不限制（防呆）
});

// 監看 selectedSeats，一旦超過 maxSeats 就砍掉多出來的並跳 Swal
watch(
  selectedSeats,
  async (newVal) => {
    if (!maxSeats.value) return; // 沒設定上限就不管
    if (newVal.length <= maxSeats.value) return;

    // 把前 maxSeats 個保留，其餘視為超過
    const trimmed = newVal.slice(0, maxSeats.value);
    const exceeded = newVal.slice(maxSeats.value);

    bookingStore.selectedSeats = trimmed;

    await Swal.fire(
      '提醒',
      `本次最多只能選擇 ${maxSeats.value} 個座位。\n若要更換，請先取消其他座位。`,
      'warning'
    );
  },
  { deep: true }
);

// 撈場次資料，取得 screenId
const loadShow = async () => {
  loadingShow.value = true;
  error.value = '';

  try {
    const res = await showApi.getById(showtimeId);
    screenId.value = res.data.screenId;

    if (!screenId.value) {
      error.value = '此場次尚未設定影廳';
      await Swal.fire('錯誤', '此場次尚未設定影廳', 'error');
    }
  } catch (err) {
    console.error(err);
    error.value = '載入場次資料失敗';
    await Swal.fire('錯誤', '載入場次資料失敗', 'error');
  } finally {
    loadingShow.value = false;
  }
};

// 確認座位 → 呼叫 seat_locks
const confirmSeats = async () => {
  if (!selectedSeats.value.length) {
    await Swal.fire('提示', '請先選擇座位', 'info');
    return;
  }

  if (maxSeats.value > 0 && selectedSeats.value.length < maxSeats.value) {
    await Swal.fire(
      '提醒',
      `本次購買為 ${maxSeats.value} 張票，請選滿 ${maxSeats.value} 個座位後再確認。`,
      'warning'
    )
    return;
  }

  locking.value = true;
  error.value = '';

  try {
    const payload = {
      showId: Number(showtimeId),
      seatIds: selectedSeats.value.map(s => s.seatId),
      userId: 1,
      lockSeconds: 600,
    };

    const res = await seatLockApi.lockSeats(payload);
    const { failedSeatIds } = res.data || {};

    if (failedSeatIds && failedSeatIds.length) {
      bookingStore.selectedSeats = bookingStore.selectedSeats
        .filter(s => !failedSeatIds.includes(s.seatId));

      await Swal.fire(
        '部分座位已被選取',
        `有 ${failedSeatIds.length} 個座位已被其他人選取，已自動移除，請重新確認座位。`,
        'warning'
      );
    } else {
      await Swal.fire(
        '成功！',
        `座位 ${selectedSeatsText.value} 已暫時鎖定！請於 10 分鐘內完成購票!`,
        'success'
      ).then(async () => {

        // ⭐ 立即建立訂單 (呼叫 from-seatlock)
        const res = await axios.post(import.meta.env.VITE_API_BASE_URL+`orders/from-seatlock`||`http://localhost:8080/api/orders/from-seatlock`, null, {
          params: { userId: 1, showId: Number(showtimeId) }
        });

        const orderId = res.data.id;

        // ⭐ 跳至訂單確認頁並帶 orderId
        router.push(`/checkout/${orderId}`);
      });
    }

  } catch (err) {
    console.error(err);
    await Swal.fire(
      '鎖定失敗',
      '座位鎖定失敗，可能已被他人選取',
      'error'
    );
  } finally {
    locking.value = false;
  }
};

onMounted(loadShow);
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
  padding: 0 2em;
  display: flex;
  align-items: center;
  border-left: 1px solid rgb(230, 230, 230);
  border-right: 1px solid rgb(230, 230, 230);
  background: repeating-linear-gradient(
    -45deg,
    rgba(99, 99, 99, 0.067),
    rgba(103, 103, 103, 0.067) 2px,
    rgba(0, 0, 0, 0) 2px,
    rgba(0, 0, 0, 0) 4px
  );
}

.confirm-bar {
  margin-top: 32px;
  padding-top: 16px;
  border-top: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.confirm-btn {
  padding: 8px 20px;
  border-radius: 4px;
  border: none;
  background: #ad9278;
  color: #fff;
  cursor: pointer;
}

.confirm-btn:hover {
  background: #a17c5a;
  color: #fff;

}

.confirm-btn:disabled {
  opacity: 0.5;
  cursor: default;
}
</style>
