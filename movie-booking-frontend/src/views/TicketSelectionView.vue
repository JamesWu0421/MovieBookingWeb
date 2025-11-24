<template>
  <div class="ticket-selection-page">
    <!-- Loading 狀態 -->
    <div v-if="loading" class="loading-container">
      <div class="loading-spinner"></div>
      <p>載入中…</p>
    </div>

    <!-- Error 狀態 -->
    <div v-else-if="error" class="error-container">
      <p class="error-message">{{ error }}</p>
      <button @click="retryFetch" class="retry-btn">重新載入</button>
    </div>

    <!-- 正常內容 -->
    <div v-else>
      <!-- 電影資訊區塊 -->
      <div class="movie-info-section">
        <div class="movie-poster">
          <img :src="movieInfo.posterUrl" :alt="movieInfo.title" />
        </div>
        
        <div class="movie-details">
          <h1 class="movie-title">{{ movieInfo.title }}</h1>
          <p class="movie-subtitle">{{ movieInfo.engTitle }}</p>
          
          <div class="info-grid">
            <div class="info-item">
              <span class="info-label">級別</span>
              <span class="info-value">
                <img v-if="movieInfo.ratingLevel" :src="movieInfo.ratingLevel" alt="分級" class="rating-icon" />
              </span>
            </div>
            
            <div class="info-item">
              <span class="info-label">地點</span>
              <span class="info-value">{{ locationText }}</span>
            </div>
            
            <div class="info-item">
              <span class="info-label">開演時間</span>
              <span class="info-value">
                {{ formatDateTime(showtimeInfo.date, showtimeInfo.startTime) }}
                <span class="time-remaining">({{ timeRemaining }})</span>
              </span>
            </div>
          </div>

          <!-- 票種明細 (只在有選票時顯示) -->
          <div v-if="cart.length > 0" class="ticket-summary">
            <div class="summary-row">
              <span class="summary-label">票種</span>
              <span class="summary-content">{{ ticketSummaryText }}</span>
            </div>

            <div class="summary-row">
              <span class="summary-label">明細</span>
              <span class="summary-content">{{ detailSummaryText }}</span>
            </div>

            <div class="summary-row total-row">
              <span class="summary-label">總計</span>
              <span class="summary-total">${{ totalAmount }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 票種選擇區塊 -->
      <div class="ticket-selection-section">
        <div class="section-header">
          <h2 class="section-title">選取票種</h2>
          <button 
            class="btn-select-seats" 
            @click="goToSeatSelection"
            :disabled="totalAmount === 0"
          >
            已選取 {{ totalTickets }} 張票；開始選取座位
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M5 12h14M12 5l7 7-7 7"/>
            </svg>
          </button>
        </div>
        
        <!-- 票種類別 Tab -->
        <div class="ticket-tabs">
          <button
            v-for="category in ticketCategories"
            :key="category.id"
            :class="['tab-btn', { 'active': selectedCategory === category.id }]"
            @click="selectCategory(category.id)"
          >
            {{ category.name }}
          </button>
        </div>

        <!-- 票種列表 -->
        <div class="tickets-list">
          <div class="tickets-header">
            <div class="header-col">票種</div>
            <div class="header-col">單價</div>
            <div class="header-col">數量</div>
            <div class="header-col">小計</div>
          </div>

          <div
            v-for="ticket in currentTickets"
            :key="ticket.id"
            class="ticket-item"
          >
            <div class="ticket-info">
              <div v-if="ticket.image" class="ticket-image">
                <img :src="ticket.image" :alt="ticket.name" />
              </div>
              <div class="ticket-text">
                <h3 class="ticket-name">
                  {{ ticket.name }}
                  <button class="info-btn" @click="showTicketInfo(ticket)">ⓘ</button>
                </h3>
                <p class="ticket-description">{{ ticket.description }}</p>
              </div>
            </div>

            <div class="ticket-price">
              ${{ ticket.price }}
            </div>

            <div class="ticket-quantity">
              <select
                :value="getTicketQuantity(ticket.id)"
                @change="updateQuantity(ticket, $event.target.value)"
                class="quantity-select"
              >
                <option 
                  v-for="n in (getMaxQuantity(ticket) + 1)" 
                  :key="n - 1" 
                  :value="n - 1"
                >
                  {{ n - 1 }}
                </option>
              </select>
            </div>

            <div class="ticket-subtotal">
              ${{ getTicketQuantity(ticket.id) * ticket.price }}
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, onMounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useMoviesStore } from '../stores/movies'
import { useShowStore } from '../stores/show'
import { useTicketsPackagesStore } from '../stores/ticketspackages'
// ✨ 新增：匯入整合服務
import ticketIntegrationService from '../services/ticketIntegrationService'

const route = useRoute()
const router = useRouter()
const moviesStore = useMoviesStore()
const showStore = useShowStore()
const ticketsPackagesStore = useTicketsPackagesStore()

// 狀態
const loading = ref(true)
const error = ref(null)

// 從路由參數取得資料
const showId = route.query.showId
const movieId = route.query.movieId

// 場次資料
const showtimeInfo = ref({
  date: '',
  startTime: '',
  endTime: '',
  hall: '',
  hallType: '',
  cinemaName: '台北館'
})

// 電影資訊
const movieInfo = computed(() => {
  const movie = moviesStore.getMovieById(movieId)
  return movie || {
    title: '載入中...',
    engTitle: '',
    posterUrl: '',
    ratingLevel: ''
  }
})

// 地點文字（影城 + 影廳）
const locationText = computed(() => {
  return `${showtimeInfo.value.cinemaName}${showtimeInfo.value.hall}`
})

// 計算剩餘時間
const timeRemaining = computed(() => {
  if (!showtimeInfo.value.date || !showtimeInfo.value.startTime) {
    return ''
  }
  
  const now = new Date()
  const showtimeDateTime = new Date(`${showtimeInfo.value.date} ${showtimeInfo.value.startTime}`)
  const diff = showtimeDateTime - now
  
  if (diff < 0) return '已開演'
  
  const minutes = Math.floor(diff / 1000 / 60)
  const hours = Math.floor(minutes / 60)
  const days = Math.floor(hours / 24)
  
  if (days > 0) return `${days} 天後`
  if (hours > 0) return `${hours} 小時後`
  return `${minutes} 分後`
})

// 格式化日期時間
const formatDateTime = (dateStr, timeStr) => {
  if (!dateStr || !timeStr) return ''
  
  const date = new Date(dateStr)
  const year = date.getFullYear()
  const month = date.getMonth() + 1
  const day = date.getDate()
  
  return `${year}/${month}/${day} ${timeStr}`
}

// 票種類別
const ticketCategories = computed(() => ticketsPackagesStore.ticketCategories)
const selectedCategory = computed(() => ticketsPackagesStore.selectedCategory)

// 當前類別的票種
const currentTickets = computed(() => {
  return ticketsPackagesStore.getTicketsByCategory(selectedCategory.value)
})

// 購物車
const cart = computed(() => ticketsPackagesStore.cart)
const totalAmount = computed(() => ticketsPackagesStore.totalAmount)

// ✨ 修正：計算總票數 - 正確計算實際電影票張數
const totalTickets = computed(() => {
  let total = 0
  
  cart.value.forEach(item => {
    // 使用整合服務計算每個套票/票種包含的電影票數
    const ticketsPerItem = ticketIntegrationService.getMovieTicketCount(item)
    // 乘以購買數量
    total += ticketsPerItem * item.quantity
  })
  
  console.log('計算總票數:', {
    cart: cart.value,
    total: total
  })
  
  return total
})

// 取得票種數量
const getTicketQuantity = (ticketId) => {
  const item = cart.value.find(item => item.id === ticketId)
  return item ? item.quantity : 0
}

// ✨ 修改：計算某個票種可選擇的最大數量
const getMaxQuantity = (ticket) => {
  const currentQty = getTicketQuantity(ticket.id)
  
  // 使用整合服務計算每個票種包含的電影票數
  const ticketsPerItem = ticketIntegrationService.getMovieTicketCount(ticket)
  
  // 計算其他票種已經佔用的票數
  const otherTicketsCount = cart.value
    .filter(item => item.id !== ticket.id)
    .reduce((sum, item) => {
      const count = ticketIntegrationService.getMovieTicketCount(item)
      return sum + (count * item.quantity)
    }, 0)
  
  // 計算還可以選擇多少張
  const remainingSlots = 6 - otherTicketsCount
  const maxForThisTicket = Math.floor(remainingSlots / ticketsPerItem)
  
  // 單一票種最多6張,且總票數不超過6張
  return Math.min(6, maxForThisTicket + currentQty)
}

// ✨✨✨ 重點修改：更新票種數量 - 超過6張時自動歸零最後一次選擇
const updateQuantity = async (ticket, quantity) => {
  const qty = parseInt(quantity)
  const currentQty = getTicketQuantity(ticket.id)
  
  // 使用整合服務計算該票種包含的電影票數
  const ticketsPerItem = ticketIntegrationService.getMovieTicketCount(ticket)
  
  // 計算其他票種已經佔用的票數
  const otherTicketsCount = cart.value
    .filter(item => item.id !== ticket.id)
    .reduce((sum, item) => {
      const count = ticketIntegrationService.getMovieTicketCount(item)
      return sum + (count * item.quantity)
    }, 0)
  
  const newTotal = otherTicketsCount + (qty * ticketsPerItem)
  
  // 檢查是否超過6張
  if (newTotal > 6) {
    // 顯示提示訊息
    alert('單次買票最多6張電影票，已自動重置此票種數量')
    
    // 將這個票種的數量歸零
    ticketsPackagesStore.updateCartItemQuantity(ticket.id, 0)
    
    // 等待 DOM 更新後，重新觸發選擇器的更新
    await nextTick()
    
    // 強制更新選擇器的值（防止視覺上顯示錯誤的數字）
    const selectElements = document.querySelectorAll('.quantity-select')
    selectElements.forEach(select => {
      if (select.closest('.ticket-item') && 
          select.closest('.ticket-item').querySelector('.ticket-name')?.textContent.includes(ticket.name)) {
        select.value = '0'
      }
    })
    
    return
  }
  
  // 如果沒有超過，正常更新數量
  ticketsPackagesStore.updateCartItemQuantity(ticket.id, qty)
  
  console.log('更新票種數量:', {
    ticketName: ticket.name,
    newQuantity: qty,
    ticketsPerItem: ticketsPerItem,
    totalTickets: newTotal
  })
}

// 切換票種類別
const selectCategory = (categoryId) => {
  ticketsPackagesStore.setCategory(categoryId)
}

// ✨ 修改：計算票種顯示文字（修正計算邏輯）
const ticketSummaryText = computed(() => {
  return cart.value.map(item => {
    // 計算每個 item 包含的電影票數
    const ticketsPerItem = ticketIntegrationService.getMovieTicketCount(item)
    // 總票數 = 每個item的票數 × 購買數量
    const totalTickets = ticketsPerItem * item.quantity
    
    // 如果是套票且購買數量 > 1，顯示套票數量和總票數
    if (ticketsPerItem > 1 || item.items?.length > 1) {
      return `${item.name} × ${item.quantity} (共 ${totalTickets} 張票)`
    }
    // 單張票就直接顯示總數
    return `${item.name} × ${totalTickets}`
  }).join(', ')
})

// ✨ 修改：計算明細顯示文字（顯示套票內容物）
const detailSummaryText = computed(() => {
  const details = []
  
  cart.value.forEach(item => {
    if (item.items && item.items.length > 0) {
      // 有內容物的套票
      item.items.forEach(subItem => {
        const totalQty = subItem.quantity * item.quantity
        const spec = subItem.spec ? ` ${subItem.spec}` : ''
        details.push(`${subItem.name}${spec} × ${totalQty}`)
      })
    } else {
      // 單一票種
      details.push(`電影票 × ${item.quantity}`)
    }
  })
  
  return details.join(', ')
})

// 顯示票種資訊
const showTicketInfo = (ticket) => {
  alert(ticket.description)
}

// 前往座位選擇頁面
const goToSeatSelection = () => {
  if (totalAmount.value === 0) {
    alert('請選擇至少一種票種')
    return
  }
  
  // 導航到座位選擇頁面
  router.push({
    name: 'SeatSelection',
    query: {
      movieId: movieId,
      showId: showId,
      date: showtimeInfo.value.date,
      startTime: showtimeInfo.value.startTime,
      endTime: showtimeInfo.value.endTime,
      hall: showtimeInfo.value.hall
    }
  })
}

// ✨ 修改：載入資料
const loadData = async () => {
  loading.value = true
  error.value = null
  
  try {
    // 1. 載入電影資料（如果還沒載入）
    if (!moviesStore.movies.length) {
      await moviesStore.fetchMovies()
    }
    
    // 2. 載入場次資訊
    if (showId) {
      // 先確保場次資料已載入
      if (!showStore.shows.length) {
        await showStore.fetchShowtimes({
          movieId: movieId,
          date: route.query.date
        })
      }
      
      // 從 store 取得場次資訊
      const show = showStore.getShowById(showId)
      
      if (show) {
        const screenId = show.screenId || show.screen_id
        const screen = showStore.screens[screenId]
        
        showtimeInfo.value = {
          date: show.showDate || show.show_date || route.query.date,
          startTime: show.showTime || show.show_time || route.query.startTime,
          endTime: show.endTime || show.end_time || route.query.endTime,
          hall: screen?.name || route.query.hall || `${screenId}廳`,
          hallType: screen?.screenType || screen?.screen_type || 'NORMAL_2D',
          cinemaName: '台北館'
        }
      } else {
        // 如果找不到場次，使用 query 參數作為備用
        showtimeInfo.value = {
          date: route.query.date,
          startTime: route.query.startTime,
          endTime: route.query.endTime,
          hall: route.query.hall,
          hallType: 'NORMAL_2D',
          cinemaName: '台北館'
        }
      }
    } else {
      // 沒有 showId，使用 query 參數
      showtimeInfo.value = {
        date: route.query.date,
        startTime: route.query.startTime,
        endTime: route.query.endTime,
        hall: route.query.hall,
        hallType: 'NORMAL_2D',
        cinemaName: '台北館'
      }
    }
    
    // ✨ 新增：3. 載入該場次的票種資訊
    console.log('開始載入場次票種，showId:', showId)
    
    if (showId) {
      await ticketsPackagesStore.fetchTicketsByShowId(showId)
      console.log('票種載入完成:', ticketsPackagesStore.tickets.length)
    } else {
      console.warn('缺少 showId，無法載入票種')
      error.value = '缺少場次資訊，無法載入票種'
    }
    
    console.log('場次資訊已載入:', showtimeInfo.value)
    
  } catch (err) {
    console.error('Failed to load data:', err)
    error.value = '載入資料失敗，請稍後再試'
  } finally {
    loading.value = false
  }
}

// 重新載入
const retryFetch = async () => {
  await loadData()
}

// 初始化
onMounted(async () => {
  // 清空購物車
  ticketsPackagesStore.clearCart()
  
  // 載入資料
  await loadData()
})
</script>

<style scoped>
.ticket-selection-page {
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

.retry-btn {
  padding: 10px 24px;
  font-size: 14px;
  background: #3d5266;
  color: #fff;
  border: none;
  cursor: pointer;
  transition: background 0.3s ease;
}

.retry-btn:hover {
  background: #2c3e50;
}

/* 電影資訊區塊 */
.movie-info-section {
  display: flex;
  gap: 24px;
  padding: 24px;
  background: #fff;
  border: 1px solid #e6e6e6;
  margin-bottom: 32px;
}

.movie-poster {
  flex-shrink: 0;
  width: 200px;
}

.movie-poster img {
  width: 100%;
  aspect-ratio: 2 / 3;
  object-fit: cover;
}

.movie-details {
  flex: 1;
}

.movie-title {
  font-size: 24px;
  font-weight: 400;
  letter-spacing: 0.1em;
  margin: 0 0 8px 0;
  color: #333;
}

.movie-subtitle {
  font-size: 16px;
  font-weight: 300;
  letter-spacing: 0.1em;
  color: #666;
  margin: 0 0 24px 0;
}

.info-grid {
  display: grid;
  gap: 16px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 16px;
}

.info-label {
  font-weight: 400;
  color: #333;
  min-width: 80px;
}

.info-value {
  color: #666;
}

.rating-icon {
  height: 24px;
}

.time-remaining {
  color: #e74c3c;
  font-weight: 400;
}

/* 票種明細 */
.ticket-summary {
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid #e6e6e6;
}

.summary-row {
  display: grid;
  grid-template-columns: 80px 1fr;
  padding: 8px 0;
  align-items: flex-start;
}

.summary-label {
  font-weight: 400;
  color: #333;
  font-size: 14px;
}

.summary-content {
  color: #666;
  font-size: 14px;
  line-height: 1.8;
}

.total-row {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #e6e6e6;
}

.summary-total {
  font-size: 24px;
  font-weight: 500;
  color: #333;
}

/* 票種選擇區塊 */
.ticket-selection-section {
  margin-bottom: 32px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 32px;
  background: repeating-linear-gradient(
    -45deg,
    rgba(240, 240, 240, 0.5),
    rgba(240, 240, 240, 0.5) 2px,
    rgba(255, 255, 255, 0) 2px,
    rgba(255, 255, 255, 0) 4px
  );
  border-top: 1px solid #e6e6e6;
  border-bottom: 1px solid #e6e6e6;
  margin-bottom: 0;
}

.section-title {
  font-size: 24px;
  font-weight: 300;
  letter-spacing: 0.5em;
  margin: 0;
  color: #333;
}

.btn-select-seats {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 24px;
  font-size: 15px;
  font-weight: 400;
  letter-spacing: 0.05em;
  background: #fff;
  color: #3d5266;
  border: 1px solid #3d5266;
  cursor: pointer;
  transition: all 0.3s ease;
  white-space: nowrap;
}

.btn-select-seats:hover:not(:disabled) {
  background: #3d5266;
  color: #fff;
}

.btn-select-seats:disabled {
  background: #f5f5f5;
  color: #ccc;
  border-color: #e6e6e6;
  cursor: not-allowed;
}

.btn-select-seats svg {
  width: 18px;
  height: 18px;
}

/* 票種類別 Tab */
.ticket-tabs {
  display: flex;
  gap: 0;
  margin-bottom: 24px;
  border-bottom: 2px solid #e6e6e6;
}

.tab-btn {
  padding: 12px 24px;
  font-size: 15px;
  font-weight: 300;
  letter-spacing: 0.05em;
  background: #fff;
  border: none;
  border-bottom: 3px solid transparent;
  color: #666;
  cursor: pointer;
  transition: all 0.2s ease;
}

.tab-btn:hover {
  color: #3d5266;
  background: #f5f5f5;
}

.tab-btn.active {
  color: #3d5266;
  font-weight: 400;
  border-bottom-color: #3d5266;
  background: #fff;
}

/* 票種列表 */
.tickets-list {
  background: #fff;
  border: 1px solid #e6e6e6;
}

.tickets-header {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr;
  gap: 16px;
  padding: 16px 24px;
  background: #f5f5f5;
  border-bottom: 1px solid #e6e6e6;
  font-weight: 400;
  color: #333;
}

.header-col {
  text-align: center;
}

.header-col:first-child {
  text-align: left;
}

.ticket-item {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr;
  gap: 16px;
  padding: 20px 24px;
  border-bottom: 1px solid #f0f0f0;
  align-items: center;
}

.ticket-item:last-child {
  border-bottom: none;
}

.ticket-info {
  display: flex;
  gap: 16px;
  align-items: flex-start;
}

.ticket-image {
  flex-shrink: 0;
  width: 80px;
  height: 80px;
}

.ticket-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 4px;
}

.ticket-text {
  flex: 1;
}

.ticket-name {
  font-size: 16px;
  font-weight: 400;
  letter-spacing: 0.05em;
  margin: 0 0 8px 0;
  color: #333;
  display: flex;
  align-items: center;
  gap: 8px;
}

.info-btn {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  border: 1px solid #999;
  background: #fff;
  color: #999;
  font-size: 12px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0;
}

.info-btn:hover {
  border-color: #3d5266;
  color: #3d5266;
}

.ticket-description {
  font-size: 13px;
  font-weight: 300;
  color: #666;
  margin: 0;
  line-height: 1.5;
}

.ticket-price,
.ticket-quantity,
.ticket-subtotal {
  text-align: center;
  font-size: 16px;
  color: #333;
}

.quantity-select {
  padding: 8px 12px;
  font-size: 14px;
  border: 1px solid #ccc;
  border-radius: 4px;
  background: #fff;
  cursor: pointer;
  width: 80px;
}

.quantity-select:focus {
  outline: none;
  border-color: #3d5266;
}

/* 響應式 */
@media (max-width: 768px) {
  .ticket-selection-page {
    padding: 16px 20px 32px;
  }

  .movie-info-section {
    flex-direction: column;
  }

  .movie-poster {
    width: 150px;
    margin: 0 auto;
  }

  .section-header {
    flex-direction: column;
    gap: 16px;
    padding: 16px 20px;
    align-items: stretch;
  }

  .section-title {
    font-size: 20px;
    letter-spacing: 0.3em;
    text-align: center;
  }

  .btn-select-seats {
    width: 100%;
    justify-content: center;
    font-size: 14px;
    padding: 12px 16px;
  }

  .tickets-header {
    display: none;
  }

  .ticket-item {
    grid-template-columns: 1fr;
    gap: 12px;
  }

  .ticket-price,
  .ticket-quantity,
  .ticket-subtotal {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .ticket-price::before {
    content: '單價:';
  }

  .ticket-quantity::before {
    content: '數量:';
  }

  .ticket-subtotal::before {
    content: '小計:';
  }
}
</style>