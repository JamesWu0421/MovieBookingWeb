<template>
  <!-- loading / error / 找不到 -->
  <div v-if="loading" class="movie-detail-page">
    <div class="loading-container">
      <div class="loading-spinner"></div>
      <p>載入中…</p>
    </div>
  </div>

  <div v-else-if="error" class="movie-detail-page">
    <div class="error-container">
      <p class="error-message">{{ error }}</p>
      <button @click="retryFetch" class="retry-btn">重新載入</button>
    </div>
  </div>

  <div v-else-if="!movie" class="movie-detail-page">
    <div class="not-found-container">
      <p>找不到這部電影</p>
      <button @click="goBack" class="back-btn">返回首頁</button>
    </div>
  </div>

  <!-- 正常內容 -->
  <div v-else class="movie-detail-page">
    <!-- 線上訂票區塊 -->
    <div class="booking-section">
      <div class="div1">
        <div class="div2">線上訂票</div>
      </div>

      <!-- 選取影城標題 -->
      <h3 class="selection-title">選取影城</h3>
      
      <div class="cinema-selection">
        <div v-if="cinemasLoading" class="loading-text">載入影城中...</div>

        <!-- ✅ 只顯示一個固定的「台北館」按鈕 -->
        <div v-else class="cinema-grid">
          <button
            :class="['cinema-btn', { active: selectedCinema === TAIPEI_CINEMA_ID }]"
            @click="selectCinema(TAIPEI_CINEMA_ID)"
          >
            {{ TAIPEI_CINEMA_NAME }}
          </button>
        </div>
      </div>

      <!-- 日期選擇區 -->
      <div v-if="selectedCinema" class="date-selection">
        <div class="date-tabs">
          <button 
            v-for="date in dateOptions" 
            :key="date.value"
            :class="['date-tab', { 'active': selectedDate === date.value }]"
            @click="selectDate(date.value)"
          >
            <div class="date-label">{{ date.label }}</div>
            <div class="date-weekday">{{ date.weekDay }}</div>
          </button>
        </div>
      </div>

      <!-- 廳種篩選 -->
      <div v-if="selectedCinema && selectedDate" class="hall-type-filter">
        <div class="filter-label">選取影廳版本</div>
        <div class="filter-buttons">
          <button 
            v-for="hallType in hallTypeOptions" 
            :key="hallType"
            :class="['filter-btn', { 'active': selectedHallType === hallType }]"
            @click="selectHallType(hallType)"
          >
            {{ hallType }}
          </button>
        </div>
      </div>

      <!-- 場次列表 -->
      <div v-if="selectedCinema && selectedDate" class="showtimes-section">
        <div v-if="showtimesLoading" class="loading-text">載入場次中...</div>
        <div v-else-if="filteredShowtimes.length === 0" class="no-showtimes">
          此日期暫無場次
        </div>
        <div v-else class="showtimes-grid">
          <div 
            v-for="(showtime, index) in filteredShowtimes" 
            :key="index"
            class="showtime-card"
            @click="selectShowtime(showtime)"
          >
            <div class="showtime-header">
              <span class="hall-info">{{ showtime.hall }} | {{ showtime.hallType }}</span>
            </div>
            <div class="showtime-time">
              {{ showtime.startTime }} ~ {{ showtime.endTime }}
            </div>
            <div class="showtime-footer">
              <span v-if="showtime.ticketStatus" class="ticket-status">{{ showtime.ticketStatus }}</span>
              <button class="seat-btn">
                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M5 12h14M12 5l7 7-7 7"/>
                </svg>
                <span>座位圖</span>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 電影簡介標題區塊 -->
    <div class="intro-section">
      <div class="div1">
        <div class="div2">電影簡介</div>
      </div>
    </div>

    <!-- 海報 + 資訊區塊 -->
    <section class="movie-hero">
      <div class="poster-wrap">
        <img
          class="poster"
          :src="movie.posterUrl"
          :alt="movie.title"
        />
      </div>

      <div class="meta-wrap">
        <h1 class="title">
          <a :href="`/movies/${movie.id}`" class="title-link">
            {{ movie.title }}
          </a>
        </h1>
        <p class="eng-title">{{ movie.engTitle }}</p>

        <ul class="info-list">
          <li>
            <span class="label">級別</span>
            <span class="value">
              <img
                v-if="movie.ratingLevel"
                :src="movie.ratingLevel"
                alt="分級"
              />
            </span>
          </li>
          <li>
            <span class="label">片長</span>
            <span class="value">{{ movie.runtimeMinutes || movie.runtime }} 分</span>
          </li>
          <li>
            <span class="label">上映日</span>
            <span class="value">{{ movie.releaseDate }}</span>
          </li>
          <li>
            <span class="label">類型</span>
            <span class="value">{{ movie.genres || movie.genre }}</span>
          </li>
          <li>
            <span class="label">演員</span>
            <span class="value">{{ movie.cast }}</span>
          </li>
          <li>
            <span class="label">導演</span>
            <span class="value">{{ movie.director }}</span>
          </li>
        </ul>
      </div>
    </section>

  </div>
</template>

<script setup>
import { computed, ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useMoviesStore } from '../stores/movies'
import { useShowStore } from '../stores/show'

const route = useRoute()
const router = useRouter()
const moviesStore = useMoviesStore()
const showStore = useShowStore()

// ✅ 固定一個台北館的設定（cinemaId 請改成你後端實際的 id）
const TAIPEI_CINEMA_ID = 1
const TAIPEI_CINEMA_NAME = '台北館'

// 載入狀態
const loading = computed(() => moviesStore.loading)
const error = computed(() => moviesStore.error)
const cinemasLoading = ref(false)   // 現在只是讓 v-if 不報錯，用不到可直接設 false
const showtimesLoading = ref(false)

// 選中的狀態
const selectedCinema = ref(null)
const selectedDate = ref(null)
const selectedHallType = ref('所有')

// 進來頁面時載入電影資料（場次等按下台北館才撈）
onMounted(async () => {
  try {
    if (!moviesStore.movies.length) {
      await moviesStore.fetchMovies()
    }
  } catch (err) {
    console.error('Failed to load initial data:', err)
  }
})

// 依照網址上的 :id 找對應電影
const movie = computed(() =>
  moviesStore.getMovieById(route.params.id)
)

// 產生日期列表 (未來7天)
const dateOptions = computed(() => {
  const dates = []
  const weekDays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
  
  for (let i = 0; i < 7; i++) {
    const date = new Date()
    date.setDate(date.getDate() + i)
    const dateStr = date.toISOString().split('T')[0]
    const month = date.getMonth() + 1
    const day = date.getDate()
    const weekDay = weekDays[date.getDay()]
    
    dates.push({
      value: dateStr,
      label: `${month}月${day}日`,
      weekDay: weekDay,
      isToday: i === 0
    })
  }
  
  return dates
})

// 取得場次（過濾掉已過時間的場次）
const filteredShowtimes = computed(() => {
  if (!selectedCinema.value || !selectedDate.value) return []
  
  // 從 store 取得場次
  let showtimes = showStore.getShowtimes({
    cinemaId: selectedCinema.value,
    movieId: route.params.id,
    date: selectedDate.value,
    hallType: selectedHallType.value
  })

  // 過濾掉已經超過的場次
  const now = new Date()
  const currentDate = now.toISOString().split('T')[0]
  const currentTime = `${String(now.getHours()).padStart(2, '0')}:${String(now.getMinutes()).padStart(2, '0')}`

  showtimes = showtimes.filter(showtime => {
    // 如果選擇的日期是今天,則需要比對時間
    if (selectedDate.value === currentDate) {
      return showtime.startTime > currentTime
    }
    // 如果是未來的日期,全部顯示
    return selectedDate.value > currentDate
  })

  return showtimes
})

// 取得可用的廳種選項
const hallTypeOptions = computed(() => {
  if (!selectedCinema.value || !selectedDate.value) return ['所有']
  
  return showStore.getHallTypes(
    selectedCinema.value,
    route.params.id,
    selectedDate.value
  )
})

// 選擇影城（只有台北館會呼叫到這裡）
const selectCinema = async (cinemaId) => {
  selectedCinema.value = cinemaId
  
  // 自動選擇第一個日期
  if (dateOptions.value.length > 0) {
    selectedDate.value = dateOptions.value[0].value
  }
  
  // 重置廳種篩選
  selectedHallType.value = '所有'
  
  // 載入場次
  await loadShowtimes()
}

// 選擇日期
const selectDate = async (dateValue) => {
  selectedDate.value = dateValue
  selectedHallType.value = '所有'
  
  // 載入場次
  await loadShowtimes()
}

// 選擇廳種
const selectHallType = (hallType) => {
  selectedHallType.value = hallType
}

// 載入場次（打後端 /shows，前端再用 cinemaId / movieId / date 篩選）
const loadShowtimes = async () => {
  if (!selectedCinema.value || !selectedDate.value) return
  
  showtimesLoading.value = true
  try {
    await showStore.fetchShowtimes({
      cinemaId: selectedCinema.value,
      movieId: route.params.id,
      date: selectedDate.value
    })
  } catch (err) {
    console.error('Failed to load showtimes:', err)
  } finally {
    showtimesLoading.value = false
  }
}

// 選擇場次
const selectShowtime = (showtime) => {
  console.log('選擇的場次:', showtime)
  
  // 導航到票種選擇頁面
  router.push({
    name: 'TicketSelection',
    query: {
      movieId: route.params.id,
      cinemaId: showtime.cinemaId,
      showId: showtime.id, // 傳遞場次 ID
      date: showtime.date,
      startTime: showtime.startTime,
      endTime: showtime.endTime,
      hall: showtime.hall
    }
  })
}

// 重新載入
const retryFetch = async () => {
  await moviesStore.fetchMovies()
  if (selectedCinema.value && selectedDate.value) {
    await loadShowtimes()
  }
}

// 返回首頁
const goBack = () => {
  router.push('/')
}
</script>

<style scoped>
.movie-detail-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px 64px 48px;
}

/* Loading 樣式 */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 400px;
  gap: 20px;
}

.loading-spinner {
  width: 50px;
  height: 50px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #3d5266;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.loading-text {
  text-align: center;
  padding: 20px;
  color: #666;
  font-size: 14px;
}

.empty-text {
  text-align: center;
  padding: 20px;
  color: #999;
  font-size: 14px;
}

/* Error 樣式 */
.error-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 400px;
  gap: 20px;
}

.error-message {
  color: #e74c3c;
  font-size: 16px;
}

.retry-btn,
.back-btn {
  padding: 10px 24px;
  font-size: 14px;
  background: #3d5266;
  color: #fff;
  border: none;
  cursor: pointer;
  transition: background 0.3s ease;
}

.retry-btn:hover,
.back-btn:hover {
  background: #2c3e50;
}

/* Not Found 樣式 */
.not-found-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 400px;
  gap: 20px;
}

.not-found-container p {
  font-size: 18px;
  color: #666;
}

/* 電影簡介區塊 */
.intro-section {
  margin-bottom: 0;
}

/* 線上訂票區塊 */
.booking-section {
  margin-bottom: 0;
}

/* 選取影城標題 */
.selection-title {
  font-weight: 300;
  letter-spacing: 0.05em;
  font-size: 18px;
  margin: 24px 0 20px 0;
  padding: 0;
  color: #333;
}

.cinema-selection {
  padding: 0 0 32px 0;
  background-color: #fff;
}

.div1 {
  width: 100%;
  height: 60px;
  margin-bottom: 10px;
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

.cinema-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 12px;
  padding: 0;
}

.cinema-btn {
  padding: 12px 20px;
  font-size: 14px;
  font-weight: 300;
  letter-spacing: 0.05em;
  border: 1px solid #ccc;
  background: #fff;
  color: #333;
  cursor: pointer;
  transition: all 0.2s ease;
  border-radius: 0;
  text-align: center;
}

.cinema-btn:hover {
  border-color: #3d5266;
  background-color: #f0f2f4;
}

.cinema-btn.active {
  background: #3d5266;
  color: #fff;
  border-color: #3d5266;
}

/* 日期選擇區 */
.date-selection {
  margin-top: 32px;
  padding-bottom: 24px;
  border-bottom: 1px solid #e6e6e6;
}

.date-tabs {
  display: flex;
  gap: 0;
  overflow-x: auto;
}

.date-tab {
  min-width: 120px;
  padding: 12px 20px;
  border: 1px solid #d0d0d0;
  border-right: none;
  background: #fff;
  cursor: pointer;
  transition: all 0.2s ease;
  text-align: center;
}

.date-tab:first-child {
  border-radius: 0;
}

.date-tab:last-child {
  border-right: 1px solid #d0d0d0;
  border-radius: 0;
}

.date-tab:hover {
  background-color: #f5f5f5;
}

.date-tab.active {
  background: #3d5266;
  color: #fff;
  border-color: #3d5266;
  position: relative;
  z-index: 1;
}

.date-tab.active + .date-tab {
  border-left-color: #3d5266;
}

.date-label {
  font-size: 15px;
  font-weight: 400;
  letter-spacing: 0.05em;
  margin-bottom: 4px;
  color: inherit;
}

.date-weekday {
  font-size: 12px;
  font-weight: 300;
  letter-spacing: 0.1em;
  color: inherit;
  opacity: 0.8;
}

/* 廳種篩選 */
.hall-type-filter {
  margin-top: 24px;
  padding-bottom: 24px;
}

.filter-label {
  font-size: 16px;
  font-weight: 400;
  letter-spacing: 0.1em;
  margin-bottom: 16px;
  color: #333;
}

.filter-buttons {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.filter-btn {
  padding: 8px 24px;
  font-size: 14px;
  font-weight: 300;
  letter-spacing: 0.05em;
  border: 1px solid #ccc;
  background: #fff;
  color: #333;
  cursor: pointer;
  transition: all 0.2s ease;
  border-radius: 0;
}

.filter-btn:hover {
  border-color: #3d5266;
  background-color: #f0f2f4;
}

.filter-btn.active {
  background: #3d5266;
  color: #fff;
  border-color: #3d5266;
}

/* 場次區塊 */
.showtimes-section {
  margin-top: 24px;
  padding-bottom: 40px;
}

.no-showtimes {
  text-align: center;
  padding: 40px 20px;
  color: #999;
  font-size: 16px;
  letter-spacing: 0.1em;
}

.showtimes-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}

.showtime-card {
  border: 1px solid #e0e0e0;
  background: #fafafa;
  padding: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.showtime-card:hover {
  border-color: #3d5266;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  transform: translateY(-2px);
}

.showtime-header {
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid #e6e6e6;
}

.hall-info {
  font-size: 13px;
  font-weight: 400;
  letter-spacing: 0.05em;
  color: #333;
}

.showtime-time {
  font-size: 16px;
  font-weight: 400;
  letter-spacing: 0.1em;
  color: #3d5266;
  margin-bottom: 12px;
}

.showtime-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 12px;
}

.ticket-status {
  font-size: 12px;
  font-weight: 400;
  letter-spacing: 0.05em;
  color: #e74c3c;
  background: #ffe8e6;
  padding: 4px 12px;
  border-radius: 3px;
}

.seat-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  font-weight: 300;
  letter-spacing: 0.05em;
  color: #3d5266;
  background: none;
  border: none;
  cursor: pointer;
  padding: 4px 8px;
  transition: all 0.2s ease;
}

.seat-btn:hover {
  color: #2c3e50;
}

.seat-btn svg {
  width: 18px;
  height: 18px;
}

/* 海報 + 資訊區塊 */
.movie-hero {
  display: grid;
  grid-template-columns: 280px minmax(0, 1fr);
  gap: 24px;
  align-items: flex-start;
  margin-bottom: 40px;
}

.poster-wrap {
  max-width: 100%;
}

.poster {
  width: 100%;
  aspect-ratio: 2 / 3;
  object-fit: cover;
  display: block;
}

.meta-wrap {
  border-left: 1px solid #eee;
  padding-left: 24px;
}

.title {
  font-weight: 400;
  letter-spacing: 0.1em;
  font-size: 28px;
  margin: 0;
  color: #333;
}

.title-link {
  color: #333;
  text-decoration: none;
  transition: color 0.3s ease;
}

.title-link:hover {
  color: #6b4bb8;
  text-decoration: underline;
}

.eng-title {
  font-weight: 300;
  font-size: 16px;
  letter-spacing: 0.2em;
  color: #666;
  margin: 4px 0 16px;
}

.info-list {
  list-style: none;
  padding: 0;
  margin: 0 0 24px;
}

.info-list li {
  display: grid;
  grid-template-columns: 80px minmax(0, 1fr);
  padding: 2px 0;
  font-weight: 300;
  letter-spacing: 0.1em;
  font-size: 14px;
}

.label {
  font-weight: 400;
  color: #020202;
}

.value {
  color: #333;
}

.value img {
  height: 20px;
}

/* 響應式 */
@media (max-width: 768px) {
  .movie-detail-page {
    padding: 16px 20px 32px;
  }

  .movie-hero {
    grid-template-columns: 1fr;
  }

  .meta-wrap {
    border-left: none;
    padding-left: 0;
  }

  .poster-wrap {
    max-width: 260px;
  }

  .poster-wrap,
  .meta-wrap {
    margin: 0 auto;
  }

  .cinema-grid {
    grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
  }

  .date-tabs {
    overflow-x: scroll;
    -webkit-overflow-scrolling: touch;
  }

  .date-tab {
    min-width: 90px;
    padding: 10px 12px;
  }

  .showtimes-grid {
    grid-template-columns: 1fr;
  }

  .showtime-card {
    padding: 14px;
  }

  .filter-buttons {
    gap: 6px;
  }

  .filter-btn {
    padding: 6px 16px;
    font-size: 13px;
  }
}
</style>
