<template>
  <div class="ticket-selection-page">
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
            <span class="info-value">{{ showtimeInfo.cinemaName }}{{ showtimeInfo.hall }}</span>
          </div>
          
          <div class="info-item">
            <span class="info-label">開演時間</span>
            <span class="info-value">
              {{ formatDate(showtimeInfo.date) }} {{ showtimeInfo.startTime }} 
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
</template>

<script setup>
import { computed, ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useMoviesStore } from '../stores/movies'
import { useShowStore } from '../stores/show'
import { useTicketsPackagesStore } from '../stores/ticketspackages'

const route = useRoute()
const router = useRouter()
const moviesStore = useMoviesStore()
const showStore = useShowStore()
const ticketsPackagesStore = useTicketsPackagesStore()

// 從路由參數取得資料
const movieId = route.params.movieId || route.query.movieId
const cinemaId = parseInt(route.params.cinemaId || route.query.cinemaId)
const showtimeDate = route.params.date || route.query.date
const showtimeStart = route.params.startTime || route.query.startTime
const showtimeEnd = route.params.endTime || route.query.endTime
const hall = route.params.hall || route.query.hall

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

// 影城資訊
const cinemaInfo = computed(() => {
  return showStore.getCinemaById(cinemaId) || { name: '' }
})

// 場次資訊
const showtimeInfo = computed(() => {
  return {
    cinemaName: cinemaInfo.value.name,
    hall: hall,
    date: showtimeDate,
    startTime: showtimeStart,
    endTime: showtimeEnd
  }
})

// 計算剩餘時間
const timeRemaining = computed(() => {
  const now = new Date()
  const showtimeDateTime = new Date(`${showtimeDate} ${showtimeStart}`)
  const diff = showtimeDateTime - now
  
  if (diff < 0) return '已開演'
  
  const minutes = Math.floor(diff / 1000 / 60)
  const hours = Math.floor(minutes / 60)
  const days = Math.floor(hours / 24)
  
  if (days > 0) return `${days} 天後`
  if (hours > 0) return `${hours} 小時後`
  return `${minutes} 分後`
})

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

// 計算總票數
const totalTickets = computed(() => {
  return cart.value.reduce((sum, item) => {
    // 如果是套票,需要計算實際包含的電影票數量
    if (item.items) {
      const movieTickets = item.items.find(i => i.name === '電影票')
      if (movieTickets) {
        return sum + (movieTickets.quantity * item.quantity)
      }
    }
    // 一般票種直接計算數量
    return sum + item.quantity
  }, 0)
})

// 取得票種數量
const getTicketQuantity = (ticketId) => {
  const item = cart.value.find(item => item.id === ticketId)
  return item ? item.quantity : 0
}

// 計算某個票種可選擇的最大數量
const getMaxQuantity = (ticket) => {
  const currentQty = getTicketQuantity(ticket.id)
  
  // 計算如果選擇這個票種,會增加多少張電影票
  let ticketsPerItem = 1 // 預設每個票種包含1張電影票
  
  if (ticket.items) {
    const movieTickets = ticket.items.find(i => i.name === '電影票')
    if (movieTickets) {
      ticketsPerItem = movieTickets.quantity
    }
  }
  
  // 計算其他票種已經佔用的票數
  const otherTicketsCount = cart.value
    .filter(item => item.id !== ticket.id)
    .reduce((sum, item) => {
      if (item.items) {
        const movieTickets = item.items.find(i => i.name === '電影票')
        if (movieTickets) {
          return sum + (movieTickets.quantity * item.quantity)
        }
      }
      return sum + item.quantity
    }, 0)
  
  // 計算還可以選擇多少張
  const remainingSlots = 6 - otherTicketsCount
  const maxForThisTicket = Math.floor(remainingSlots / ticketsPerItem)
  
  // 單一票種最多6張,且總票數不超過6張
  return Math.min(6, maxForThisTicket + currentQty)
}

// 更新票種數量
const updateQuantity = (ticket, quantity) => {
  const qty = parseInt(quantity)
  const currentQty = getTicketQuantity(ticket.id)
  
  // 計算這個票種包含多少張電影票
  let ticketsPerItem = 1
  if (ticket.items) {
    const movieTickets = ticket.items.find(i => i.name === '電影票')
    if (movieTickets) {
      ticketsPerItem = movieTickets.quantity
    }
  }
  
  // 計算選擇這個數量後的總票數
  const otherTicketsCount = cart.value
    .filter(item => item.id !== ticket.id)
    .reduce((sum, item) => {
      if (item.items) {
        const movieTickets = item.items.find(i => i.name === '電影票')
        if (movieTickets) {
          return sum + (movieTickets.quantity * item.quantity)
        }
      }
      return sum + item.quantity
    }, 0)
  
  const newTotal = otherTicketsCount + (qty * ticketsPerItem)
  
  // 檢查是否超過6張
  if (newTotal > 6) {
    alert('單次買票最多6張')
    return
  }
  
  // 更新數量
  ticketsPackagesStore.updateCartItemQuantity(ticket.id, qty)
  
  // 如果數量大於0但購物車中沒有,則新增
  if (qty > 0 && !cart.value.find(item => item.id === ticket.id)) {
    ticketsPackagesStore.addToCart(ticket, qty)
  }
}

// 切換票種類別
const selectCategory = (categoryId) => {
  ticketsPackagesStore.setCategory(categoryId)
}

// 格式化日期
const formatDate = (dateStr) => {
  const date = new Date(dateStr)
  const year = date.getFullYear()
  const month = date.getMonth() + 1
  const day = date.getDate()
  return `${year}/${month}/${day}`
}

// 計算票種顯示文字（和明細一樣的格式）
const ticketSummaryText = computed(() => {
  return cart.value.map(item => {
    const count = getItemTicketCount(item)
    return `${item.name} × ${count}`
  }).join(', ')
})

// 計算明細顯示文字（和明細一樣的格式）
const detailSummaryText = computed(() => {
  const details = []
  cart.value.forEach(item => {
    if (item.items) {
      item.items.forEach(subItem => {
        details.push(`${subItem.name} × ${subItem.quantity * item.quantity}`)
      })
    } else {
      details.push(`電影票 × ${item.quantity}`)
    }
  })
  return details.join(', ')
})

// 計算每個項目包含的票數
const getItemTicketCount = (item) => {
  if (item.items) {
    const movieTickets = item.items.find(i => i.name === '電影票')
    if (movieTickets) {
      return movieTickets.quantity * item.quantity
    }
  }
  return item.quantity
}

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
  
  // 導航到座位選擇頁面 (之後實作)
  router.push({
    name: 'SeatSelection',
    params: {
      movieId,
      cinemaId,
      date: showtimeDate,
      startTime: showtimeStart,
      endTime: showtimeEnd,
      hall
    }
  })
}

// 初始化
onMounted(async () => {
  if (!moviesStore.movies.length) {
    await moviesStore.fetchMovies()
  }
  
  // 清空購物車
  ticketsPackagesStore.clearCart()
})
</script>

<style scoped>
.ticket-selection-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px 64px 48px;
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

.value img {
  height: 20px;
}

/* 票種明細 (在電影資訊區塊中) */
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

.summary-row .summary-label {
  font-weight: 400;
  color: #333;
  font-size: 14px;
}

.summary-content {
  color: #666;
  font-size: 14px;
  line-height: 1.8;
  display: block;
  white-space: normal;
}

.ticket-item {
  display: inline;
  white-space: nowrap;
}

.ticket-item:not(:last-child)::after {
  content: ', ';
  white-space: pre;
}

.detail-item {
  display: inline;
  white-space: nowrap;
}

.detail-item:not(:last-child)::after {
  content: ', ';
  white-space: pre;
}

.ticket-summary .close-icon {
  color: #9b59b6;
  stroke: #9b59b6;
  width: 14px;
  height: 14px;
  flex-shrink: 0;
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