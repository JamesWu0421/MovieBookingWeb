<template>
  <div class="showtimes-price-system">
    <!-- 頁面標題 -->
    <div class="page-header">
      <h1>場次價格設定系統</h1>
    </div>

    <!-- 場次價格設定區塊 -->
    <el-card shadow="hover" style="margin-bottom: 20px;">
      <template #header>
        <div class="card-header">
          <span class="card-title">新增場次價格</span>
        </div>
      </template>
      
      <el-form :model="showPriceForm" label-width="120px" label-position="right">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="選擇票種" required>
              <el-select v-model="showPriceForm.selectedTicketId" placeholder="請選擇票種" style="width: 100%;">
                <el-option 
                  v-for="ticket in ticketList" 
                  :key="ticket.id" 
                  :label="`${ticket.packageName} (${ticket.packageCode})`"
                  :value="ticket.id"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="選擇電影場次" required>
              <el-select 
                v-model="showPriceForm.selectedMovieId" 
                placeholder="請選擇電影場次" 
                style="width: 100%;"
                @change="onMovieSelected"
              >
                <el-option 
                  v-for="movie in movieList" 
                  :key="movie.id" 
                  :label="`${movie.movieName} (${formatTime(movie.startTime)} - ${formatTime(movie.endTime)})`"
                  :value="movie.id"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 自動填入的場次資訊（僅顯示） -->
        <el-alert 
          v-if="showPriceForm.selectedMovieId"
          type="info" 
          :closable="false"
          style="margin-bottom: 20px;"
        >
          <template #title>
            <div style="font-size: 14px;">
              <strong>場次資訊：</strong>
              <span style="margin-left: 10px;">電影：{{ selectedMovieInfo.movieName }}</span>
              <span style="margin-left: 10px;">時間：{{ formatTime(selectedMovieInfo.startTime) }} - {{ formatTime(selectedMovieInfo.endTime) }}</span>
              <span style="margin-left: 10px;">片長：{{ selectedMovieInfo.movieDuration }} 分鐘</span>
              <span style="margin-left: 10px;">影廳基準價：{{ selectedMovieInfo.screenBasePrice }} 元</span>
            </div>
          </template>
        </el-alert>

        <!-- 自動顯示票種價格調整 -->
        <el-alert 
          v-if="showPriceForm.selectedTicketId"
          type="success" 
          :closable="false"
          style="margin-bottom: 20px;"
        >
          <template #title>
            <div style="font-size: 14px;">
              <strong>票種價格調整：</strong>
              <span style="margin-left: 10px;">{{ selectedTicketInfo.packageName }}</span>
              <span style="margin-left: 10px;">價格調整：{{ selectedTicketInfo.priceAdjustment >= 0 ? '+' : '' }}{{ selectedTicketInfo.priceAdjustment }} 元</span>
              <span
                v-if="selectedTicketInfo.enableEarlyBird"
                style="margin-left: 10px;"
              >
                早場調整：{{ selectedTicketInfo.earlyBirdAdjustment >= 0 ? '+' : '' }}{{ selectedTicketInfo.earlyBirdAdjustment }} 元
              </span>
            </div>
          </template>
        </el-alert>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="可販售">
              <el-switch v-model="showPriceForm.isAvailable"></el-switch>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 價格計算明細 -->
        <el-alert 
          v-if="showPriceForm.selectedMovieId && showPriceForm.selectedTicketId"
          type="warning" 
          :closable="false"
          style="margin-bottom: 20px;"
        >
          <template #title>
            <div style="font-size: 14px;">
              <strong>價格計算明細：</strong>
              <div style="margin-top: 8px; line-height: 1.8;">
                <div>影廳基準價：{{ selectedMovieInfo.screenBasePrice }} 元</div>
                <div>票種調整：{{ selectedTicketInfo.priceAdjustment >= 0 ? '+' : '' }}{{ selectedTicketInfo.priceAdjustment }} 元</div>
                <div>片長加價：+{{ durationSurcharge }} 元 (片長 {{ selectedMovieInfo.movieDuration }} 分鐘)</div>
                <div v-if="selectedTicketInfo.enableEarlyBird">
                  早場調整：{{ selectedTicketInfo.earlyBirdAdjustment >= 0 ? '+' : '' }}{{ selectedTicketInfo.earlyBirdAdjustment }} 元
                </div>
                <el-divider style="margin: 8px 0;"></el-divider>
                <div style="font-size: 16px; font-weight: 600; color: #409eff;">
                  預估最終價格：{{ calculatedPrice }} 元
                </div>
              </div>
            </div>
          </template>
        </el-alert>

        <el-form-item>
          <el-button type="primary" size="large" @click="handleAddShowPrice">
            新增場次價格
          </el-button>
          <el-button size="large" @click="resetShowPriceForm">
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 場次價格列表 -->
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span class="card-title">場次價格列表</span>
          <div>
            <el-button type="success" size="small" @click="exportShowPrices" :disabled="showPricesList.length === 0">
              📥 匯出資料
            </el-button>
            <el-button type="danger" size="small" @click="clearAllShowPrices" :disabled="showPricesList.length === 0">
              🗑️ 清空全部
            </el-button>
          </div>
        </div>
      </template>

      <div v-if="showPricesList.length === 0" class="empty-state">
        <el-empty description="尚未建立任何場次價格，請在上方設定區塊新增"></el-empty>
      </div>

      <div v-else>
        <!-- 統計資訊 -->
        <el-row :gutter="20" style="margin-bottom: 20px;">
          <el-col :span="8">
            <el-statistic title="總場次數" :value="showPricesList.length">
              <template #suffix>
                <span style="font-size: 14px;">場</span>
              </template>
            </el-statistic>
          </el-col>
          <el-col :span="8">
            <el-statistic title="可販售場次" :value="availableShowsCount">
              <template #suffix>
                <span style="font-size: 14px;">場</span>
              </template>
            </el-statistic>
          </el-col>
          <el-col :span="8">
            <el-statistic title="平均價格" :value="averagePrice">
              <template #suffix>
                <span style="font-size: 14px;">元</span>
              </template>
            </el-statistic>
          </el-col>
        </el-row>

        <el-divider></el-divider>

        <!-- 場次價格表格 -->
        <el-table 
          :data="showPricesList" 
          style="width: 100%"
          :default-sort="{ prop: 'createdAt', order: 'descending' }"
        >
          <el-table-column type="index" label="#" width="60" align="center" />
          
          <el-table-column prop="ticketPackageName" label="票種名稱" min-width="150">
            <template #default="{ row }">
              <div>
                <div style="font-weight: 600;">{{ row.ticketPackageName }}</div>
                <el-tag size="small" type="info" style="margin-top: 4px;">
                  調整：{{ row.ticketAdjustment >= 0 ? '+' : '' }}{{ row.ticketAdjustment }}元
                </el-tag>
                <el-tag v-if="row.isEarlyBird" size="small" type="warning" style="margin-left: 4px; margin-top: 4px;">
                  早場：{{ row.earlyBirdAdjustment >= 0 ? '+' : '' }}{{ row.earlyBirdAdjustment }}元
                </el-tag>
              </div>
            </template>
          </el-table-column>

          <el-table-column prop="movieName" label="電影名稱" min-width="180">
            <template #default="{ row }">
              <div>
                <div style="font-weight: 600;">{{ row.movieName }}</div>
                <div style="font-size: 12px; color: #909399; margin-top: 4px;">
                  {{ formatTime(row.startTime) }} - {{ formatTime(row.endTime) }}
                </div>
              </div>
            </template>
          </el-table-column>

          <el-table-column prop="movieDuration" label="片長" width="100" align="center">
            <template #default="{ row }">
              {{ row.movieDuration }} 分鐘
            </template>
          </el-table-column>

          <el-table-column label="價格組成" min-width="200">
            <template #default="{ row }">
              <div style="font-size: 12px; line-height: 1.8;">
                <div>基準價：{{ row.screenBasePrice }}元</div>
                <div>票種：{{ row.ticketAdjustment >= 0 ? '+' : '' }}{{ row.ticketAdjustment }}元</div>
                <div>片長：+{{ row.durationSurcharge }}元</div>
                <div v-if="row.isEarlyBird">早場：{{ row.earlyBirdAdjustment >= 0 ? '+' : '' }}{{ row.earlyBirdAdjustment }}元</div>
              </div>
            </template>
          </el-table-column>

          <el-table-column prop="finalPrice" label="最終價格" width="120" align="center">
            <template #default="{ row }">
              <el-tag type="success" effect="dark" size="large">
                {{ row.finalPrice }} 元
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column prop="isAvailable" label="販售狀態" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="row.isAvailable ? 'success' : 'info'" size="small">
                {{ row.isAvailable ? '可販售' : '不可售' }}
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column label="操作" width="100" align="center" fixed="right">
            <template #default="{ row }">
              <el-button 
                type="danger" 
                size="small" 
                @click="removeShowPrice(row)"
                link
              >
                刪除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import ticketPackageService from '../services/ticketPackageService'
import showtimeService from '../services/showtimeService'

// 場次價格表單
const showPriceForm = ref({
  selectedTicketId: '',
  selectedMovieId: '',
  isAvailable: true
})

// 資料列表
const ticketList = ref([])
const movieList = ref([])
const showPricesList = ref([])

// ID 計數器
const showPriceIdCounter = ref(1)

// 選中的電影資訊
const selectedMovieInfo = computed(() => {
  if (!showPriceForm.value.selectedMovieId) {
    return {
      movieName: '',
      startTime: '',
      endTime: '',
      movieDuration: 0,
      screenBasePrice: 0
    }
  }
  const movie = movieList.value.find(m => m.id === showPriceForm.value.selectedMovieId)
  return movie || {
    movieName: '',
    startTime: '',
    endTime: '',
    movieDuration: 0,
    screenBasePrice: 0
  }
})

// 選中的票種資訊
const selectedTicketInfo = computed(() => {
  if (!showPriceForm.value.selectedTicketId) {
    return {
      packageName: '',
      priceAdjustment: 0,
      earlyBirdAdjustment: 0,
      enableEarlyBird: false
    }
  }
  const ticket = ticketList.value.find(t => t.id === showPriceForm.value.selectedTicketId)
  return ticket || {
    packageName: '',
    priceAdjustment: 0,
    earlyBirdAdjustment: 0,
    enableEarlyBird: false
  }
})

// 計算片長加價
const durationSurcharge = computed(() => {
  if (!showPriceForm.value.selectedMovieId) return 0
  return calculateDurationSurcharge(selectedMovieInfo.value.movieDuration)
})

// 計算最終價格
const calculatedPrice = computed(() => {
  if (!showPriceForm.value.selectedMovieId || !showPriceForm.value.selectedTicketId) {
    return 0
  }
  
  const movieInfo = selectedMovieInfo.value
  const ticketInfo = selectedTicketInfo.value
  
  let total = movieInfo.screenBasePrice
  total += ticketInfo.priceAdjustment
  total += durationSurcharge.value
  
  if (ticketInfo.enableEarlyBird) {
    total += ticketInfo.earlyBirdAdjustment
  }
  
  return Math.max(0, total)
})

// 統計資訊
const availableShowsCount = computed(() => {
  return showPricesList.value.filter(sp => sp.isAvailable).length
})

const averagePrice = computed(() => {
  if (showPricesList.value.length === 0) return 0
  const total = showPricesList.value.reduce((sum, sp) => sum + sp.finalPrice, 0)
  return Math.round(total / showPricesList.value.length)
})

// 計算片長加價
function calculateDurationSurcharge(duration) {
  if (duration <= 180) {
    return 0
  }
  const extraMinutes = duration - 180
  const extraBlocks = Math.ceil(extraMinutes / 30)
  return extraBlocks * 50
}

// 載入票種列表
async function fetchTicketList() {
  try {
    const res = await ticketPackageService.list()
    if (res && res.data) {
      ticketList.value = res.data.filter(ticket => ticket.isActive)
      ElMessage.success('票種資料載入成功')
    } else {
      ticketList.value = []
    }
  } catch (error) {
    console.error('Failed to fetch ticket packages:', error)
    ElMessage.error('載入票種列表失敗')
    ticketList.value = []
  }
}

// 載入電影場次列表
async function fetchMovieList() {
  try {
    const res = await showtimeService.list()
    if (res && res.data) {
      movieList.value = res.data.map(showtime => ({
        id: showtime.id,
        movieName: showtime.movieName,
        startTime: showtime.startTime,
        endTime: showtime.endTime,
        movieDuration: showtime.movieDuration,
        screenBasePrice: showtime.screenBasePrice
      }))
      ElMessage.success('電影場次資料載入成功')
    } else {
      movieList.value = []
    }
  } catch (error) {
    console.error('Failed to fetch showtimes:', error)
    ElMessage.error('載入電影場次資料失敗')
    movieList.value = []
  }
}

function onMovieSelected(movieId) {
  const movie = movieList.value.find(m => m.id === movieId)
  if (movie) {
    ElMessage.success('已載入電影場次資訊')
  }
}

// 新增場次價格
function handleAddShowPrice() {
  const selectedTicket = ticketList.value.find(t => t.id === showPriceForm.value.selectedTicketId)
  const selectedMovie = movieList.value.find(m => m.id === showPriceForm.value.selectedMovieId)
  
  if (!selectedTicket) {
    ElMessage.warning('請先選擇票種')
    return
  }

  if (!selectedMovie) {
    ElMessage.warning('請先選擇電影場次')
    return
  }

  // 檢查是否已存在相同的組合
  const exists = showPricesList.value.some(sp => 
    sp.ticketPackageId === showPriceForm.value.selectedTicketId && 
    sp.movieId === selectedMovie.id
  )

  if (exists) {
    ElMessage.warning('此票種與場次組合已存在，請勿重複新增')
    return
  }

  const surcharge = calculateDurationSurcharge(selectedMovie.movieDuration)

  const newShowPrice = {
    id: showPriceIdCounter.value++,
    ticketPackageId: showPriceForm.value.selectedTicketId,
    ticketPackageName: selectedTicket.packageName,
    movieId: selectedMovie.id,
    movieName: selectedMovie.movieName,
    startTime: selectedMovie.startTime,
    endTime: selectedMovie.endTime,
    movieDuration: selectedMovie.movieDuration,
    screenBasePrice: selectedMovie.screenBasePrice,
    ticketAdjustment: selectedTicket.priceAdjustment,
    isEarlyBird: selectedTicket.enableEarlyBird,
    earlyBirdAdjustment: selectedTicket.enableEarlyBird ? selectedTicket.earlyBirdAdjustment : 0,
    durationSurcharge: surcharge,
    finalPrice: calculatedPrice.value,
    isAvailable: showPriceForm.value.isAvailable,
    createdAt: new Date().toISOString()
  }

  showPricesList.value.push(newShowPrice)
  
  resetShowPriceForm()
  ElMessage.success('場次價格新增成功！')
}

function resetShowPriceForm() {
  showPriceForm.value = {
    selectedTicketId: '',
    selectedMovieId: '',
    isAvailable: true
  }
}

// 刪除場次價格
async function removeShowPrice(showPrice) {
  try {
    await ElMessageBox.confirm(
      `確定要刪除「${showPrice.ticketPackageName} - ${showPrice.movieName}」的場次價格嗎？`,
      '確認刪除',
      {
        confirmButtonText: '確定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )
    
    showPricesList.value = showPricesList.value.filter(sp => sp.id !== showPrice.id)
    ElMessage.success('場次價格已刪除')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to delete show price:', error)
    }
  }
}

// 清空全部場次價格
async function clearAllShowPrices() {
  try {
    await ElMessageBox.confirm(
      `確定要清空全部 ${showPricesList.value.length} 筆場次價格資料嗎？此操作無法復原！`,
      '警告',
      {
        confirmButtonText: '確定清空',
        cancelButtonText: '取消',
        type: 'error',
      }
    )
    
    showPricesList.value = []
    showPriceIdCounter.value = 1
    ElMessage.success('已清空全部場次價格')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('Failed to clear show prices:', error)
    }
  }
}

// 匯出資料
function exportShowPrices() {
  const data = JSON.stringify(showPricesList.value, null, 2)
  const blob = new Blob([data], { type: 'application/json' })
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = `場次價格_${new Date().toISOString().split('T')[0]}.json`
  link.click()
  URL.revokeObjectURL(url)
  ElMessage.success('資料已匯出')
}

function formatTime(time) {
  if (!time) return ''
  if (typeof time === 'string') {
    if (time.includes('T')) {
      return time.split('T')[1].slice(0, 5)
    }
    if (time.includes(' ')) {
      return time.split(' ')[1].slice(0, 5)
    }
    return time
  }
  const hours = time.getHours().toString().padStart(2, '0')
  const minutes = time.getMinutes().toString().padStart(2, '0')
  return `${hours}:${minutes}`
}

onMounted(() => {
  fetchTicketList()
  fetchMovieList()
})
</script>

<style scoped>
.showtimes-price-system {
  padding: 20px;
  background: #f5f7fa;
  min-height: 100vh;
}

.page-header {
  margin-bottom: 24px;
  display: flex;
  align-items: center;
}

.page-header h1 {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.empty-state {
  padding: 40px;
  text-align: center;
}

@media (max-width: 768px) {
  .showtimes-price-system {
    padding: 10px;
  }

  .page-header h1 {
    font-size: 20px;
  }
}
</style>
