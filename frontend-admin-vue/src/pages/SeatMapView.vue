<template>
  <el-card>
    <section>
      <h2>
        座位圖（影廳：{{ screenName }}
        <span v-if="showId">，場次 ID：{{ showId }}</span>
        ）
      </h2>

      <div class="toolbar">
        <button class="action-btn" @click="goBackShows">場次列表</button>
        <button class="action-btn" @click="goBack">座位列表</button>
        <button class="action-btn" @click="goBackScreens">影廳列表</button>
      </div>

      <p v-if="loading">載入中...</p>
      <p v-else-if="error" style="color: red">{{ error }}</p>

      <div v-else>
        <!-- 銀幕 -->
        <div class="screen-label">銀幕</div>

        <!-- 座位圖 -->
        <div class="seat-map">
          <div
            v-for="row in groupedRows"
            :key="row.rowLabel"
            class="seat-row"
          >
            <!-- 左邊顯示排號 -->
            <div class="row-label">
              {{ row.rowLabel }}
            </div>

            <!-- 右邊一整排座位 -->
            <div class="row-seats">
              <div
                v-for="seat in row.seats"
                :key="seat.id"
                class="seat"
                :class="{
                  blocked: seat.localBlocked,
                  sold: soldSeatIds.includes(seat.id)
                }"
                @click="onSeatClick(seat)"
              >
                {{ seat.seatNumber }}
              </div>
            </div>
          </div>
        </div>

        <!-- 下方操作區（顯示有沒有未儲存變更） -->
        <div class="bottom-actions">
          <span v-if="hasChanges">
            有 {{ dirtySeats.length }} 個座位封鎖狀態尚未儲存
          </span>
          <span v-else>
            目前沒有尚未儲存的變更
          </span>

          <button
            class="save-btn"
            :disabled="!hasChanges || saving"
            @click="saveChanges"
          >
            {{ saving ? '儲存中...' : '儲存變更' }}
          </button>
        </div>

        <!-- 圖例 -->
        <div class="legend">
          <div class="legend-item">
            <span class="box normal"></span> 一般座位
          </div>
          <div class="legend-item">
            <span class="box blocked"></span> 封鎖座位
          </div>
          <!-- 只有有 showId（從場次進來）才顯示已售出座位 -->
          <div class="legend-item" v-if="showId">
            <span class="box sold"></span> 已售出座位
          </div>
        </div>
      </div>
    </section>
  </el-card>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { seatApi, ticketApi, screenApi } from '../services/api';
import Swal from 'sweetalert2';

const route = useRoute();
const router = useRouter();

const screenId = Number(route.params.screenId);
const showId = route.query.showId ? Number(route.query.showId) : null;

const seats = ref([]);
const soldSeatIds = ref([]);
const loading = ref(false);
const error = ref('');
const screenName = ref('');
const saving = ref(false);

// 取得座位
const fetchSeats = async () => {
  try {
    const res = await seatApi.getByScreen(screenId);
    // 加上 localBlocked 當「目前選取狀態」
    seats.value = res.data.map(s => ({
      ...s,
      localBlocked: s.isBlocked, // 一開始一樣
    }));
  } catch (err) {
    console.error(err);
    error.value = '載入座位資料失敗';
  }
};

// 取得已售出座位（依場次）
const fetchSoldSeats = async () => {
  if (!showId) {
    soldSeatIds.value = [];
    return;
  }
  try {
    const res = await ticketApi.getSold(showId);
    soldSeatIds.value = res.data.map(t => t.seatId);
  } catch (err) {
    console.error(err);
  }
};

// 取得影廳名稱
const fetchScreenInfo = async () => {
  try {
    const res = await screenApi.getById(screenId);
    screenName.value = res.data.name;
  } catch (err) {
    console.error('影廳資訊載入失敗', err);
  }
};

// 依 rowLabel 分組，並依座號排序
const groupedRows = computed(() => {
  const map = new Map();

  for (const s of seats.value) {
    const row = s.rowLabel;
    if (!map.has(row)) {
      map.set(row, []);
    }
    map.get(row).push(s);
  }

  const rows = [];
  for (const [rowLabel, list] of map.entries()) {
    list.sort((a, b) => a.seatNumber - b.seatNumber);
    rows.push({ rowLabel, seats: list });
  }

  rows.sort((a, b) => {
    const ra = a.rowLabel;
    const rb = b.rowLabel;
    if (ra < rb) return -1;
    if (ra > rb) return 1;
    return 0;
  });

  return rows;
});

// 有變更的座位（localBlocked 和 isBlocked 不同）
const dirtySeats = computed(() =>
  seats.value.filter(s => s.localBlocked !== s.isBlocked)
);

const hasChanges = computed(() => dirtySeats.value.length > 0);

// 點座位 → 只改 localBlocked
const onSeatClick = (seat) => {
  // 如果是已售出座位，就不要讓他改封鎖，避免誤操作
  if (showId && soldSeatIds.value.includes(seat.id)) {
    return;
  }
  seat.localBlocked = !seat.localBlocked;
};

// 按下「儲存變更」 → 逐一打 API 更新
const saveChanges = async () => {
  if (!hasChanges.value) return;

  saving.value = true;
  error.value = '';

  const targets = dirtySeats.value;

  try {
    await Promise.all(
      targets.map(s =>
        seatApi.updateBlocked(s.id, s.localBlocked)
      )
    );

    // 後端成功 → 同步原始狀態
    targets.forEach(s => {
      s.isBlocked = s.localBlocked;
    });

    // 成功提示
    await Swal.fire({
      icon: 'success',
      title: '已儲存',
      text: `已更新 ${targets.length} 個座位的封鎖狀態`,
      confirmButtonText: 'OK'
    });
  } catch (err) {
    console.error('批次更新座位封鎖狀態失敗', err);

    // 失敗提示
    Swal.fire({
      icon: 'error',
      title: '更新失敗',
      text: '更新座位封鎖狀態失敗，請稍後再試',
      confirmButtonText: '好啦'
    });
  } finally {
    saving.value = false;
  }
};


const goBack = () => {
  router.push(`/screens/${screenId}/seats`);
};

const goBackScreens = () => {
  router.push('/screens');
};

const goBackShows = () => {
  router.push('/shows');
};

onMounted(async () => {
  loading.value = true;
  error.value = '';
  try {
    await Promise.all([
      fetchScreenInfo(),
      fetchSeats(),
      fetchSoldSeats()
    ]);
  } finally {
    loading.value = false;
  }
});
</script>

<style scoped>
.toolbar {
  margin: 12px 0;
  display: flex;
  gap: 8px;
}

.screen-label {
  width: 780px;
  text-align: center;
  margin-bottom: 12px;
  padding: 6px 0;
  border-radius: 6px;
  border: 1px solid #aaa;
  background: #f5f5f5;
  font-weight: bold;
}

/* 整個座位圖 */
.seat-map {
  width: 760px;
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding: 12px;
  border-radius: 8px;
  border: 1px solid #ddd;
  background: #fafafa;
}

/* 一排 */
.seat-row {
  display: flex;
  align-items: center;
  gap: 8px;
}

/* 左邊排號 */
.row-label {
  width: 30px;
  text-align: center;
  font-weight: bold;
}

/* 右邊座位格子列 */
.row-seats {
  flex: 1; /* 吃掉 row-label 以外的寬度 */
  display: grid;
  grid-auto-flow: column;
  grid-auto-columns: minmax(28px, 32px);
  gap: 4px;
  justify-content: center;
}

/* 單一座位 */
.seat {
  height: 28px;
  border-radius: 4px;
  border: 1px solid #ccc;
  background: #ffffff;
  font-size: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

/* 封鎖座位顏色 */
.seat.blocked {
  background: #9e9e9e;
  color: #fff;
  border-color: #757575;
}

/* 已售出座位顏色 */
.seat.sold {
  background: #d32f2f;
  color: #fff;
  border-color: #b71c1c;
}

/* 下方操作區 */
.bottom-actions {
  width: 760px;
  margin-top: 10px;
  padding: 8px 12px;
  border-radius: 6px;
  border: 1px solid #ddd;
  background: #f4f6fa;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 13px;
}

/* 圖例 */
.legend {
  margin-top: 12px;
  display: flex;
  gap: 16px;
  font-size: 13px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.box {
  width: 18px;
  height: 18px;
  border-radius: 4px;
  border: 1px solid #ccc;
}

.box.normal {
  background: #ffffff;
}

.box.blocked {
  background: #9e9e9e;
  border-color: #757575;
}

.box.sold {
  background: #d32f2f;
  border-color: #b71c1c;
}

.action-btn {
  padding: 6px 14px;
  border-radius: 5px;
  border: 1px solid rgb(111, 119, 127);
  cursor: pointer;
  font-size: 14px;
  transition: 0.2s;
  margin-right: 8px;
  background-color: rgba(69, 132, 173, 0.3);
  color: #466ea4;
  border-color: #3fa5ee;
}

.action-btn:hover {
  background-color: #518fcd;
  color: #ffffff;
}

.save-btn {
  padding: 6px 14px;
  border-radius: 5px;
  border: 1px solid #4a88c8;
  background-color: #4a88c8;
  color: #fff;
  font-size: 13px;
  cursor: pointer;
  transition: 0.2s;
}

.save-btn:disabled {
  opacity: 0.6;
  cursor: default;
}

.save-btn:not(:disabled):hover {
  background-color: #386eaa;
}
</style>
