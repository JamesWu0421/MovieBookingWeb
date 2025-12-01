<template>
  <div class="showtimes-price-system">
    <!-- 頁面標題 -->
    <div class="page-header">
      <h1>場次票種設定系統</h1>
    </div>

    <!-- 場次價格設定區塊 -->
    <el-card shadow="hover" style="margin-bottom: 20px;">
      <template #header>
        <div class="card-header">
          <span class="card-title">新增場次票種</span>
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
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="選擇電影" required>
              <el-select 
                v-model.number="showPriceForm.selectedMovieId" 
                placeholder="請選擇電影" 
                style="width: 100%;"
                @change="onMovieSelected"
                filterable
              >
                <el-option 
                  v-for="m in movies" 
                  :key="m.id" 
                  :label="`${m.id} - ${m.title}`"
                  :value="m.id"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          
          <el-col :span="12">
            <el-form-item label="選擇場次" required>
              <el-select 
                v-model.number="showPriceForm.selectedShowId" 
                placeholder="請先選擇電影" 
                style="width: 100%;"
                :disabled="!showPriceForm.selectedMovieId || filteredShowList.length === 0"
                @change="onShowSelected"
              >
                <el-option 
                  v-for="show in filteredShowList" 
                  :key="show.id" 
                  :label="`${formatDate(show.showDate)} ${formatTime(show.showTime)} - ${formatTime(show.endTime)} (影廳${show.screenId})`"
                  :value="show.id"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 自動填入的電影資訊 -->
        <el-alert 
          v-if="showPriceForm.selectedMovieId"
          type="info" 
          :closable="false"
          style="margin-bottom: 20px;"
        >
          <template #title>
            <div style="font-size: 14px;">
              <strong>電影資訊:</strong>
              <span style="margin-left: 10px;">片名: {{ selectedMovieName }}</span>
              <span style="margin-left: 10px;">場次數量: {{ filteredShowList.length }} 場</span>
            </div>
          </template>
        </el-alert>

        <!-- 自動填入的場次資訊 -->
        <el-alert 
          v-if="showPriceForm.selectedShowId"
          type="success" 
          :closable="false"
          style="margin-bottom: 20px;"
        >
          <template #title>
            <div style="font-size: 14px;">
              <strong>場次資訊:</strong>
              <span style="margin-left: 10px;">日期: {{ formatDate(selectedShowInfo.showDate) }}</span>
              <span style="margin-left: 10px;">時間: {{ formatTime(selectedShowInfo.showTime) }} - {{ formatTime(selectedShowInfo.endTime) }}</span>
              <span style="margin-left: 10px;">影廳: {{ selectedShowInfo.screenId }}</span>
              <span style="margin-left: 10px; color: #E6A23C; font-weight: 600;">
                影廳基準價: {{ currentBasePrice }} 元
              </span>
            </div>
          </template>
        </el-alert>

        <!-- 自動顯示票種價格調整 -->
        <el-alert 
          v-if="showPriceForm.selectedTicketId"
          type="warning" 
          :closable="false"
          style="margin-bottom: 20px;"
        >
          <template #title>
            <div style="font-size: 14px;">
              <strong>票種價格調整:</strong>
              <span style="margin-left: 10px;">{{ selectedTicketInfo.packageName }}</span>
              <span style="margin-left: 10px;">價格調整: {{ selectedTicketInfo.priceAdjustment >= 0 ? '+' : '' }}{{ selectedTicketInfo.priceAdjustment }} 元</span>
              <span
                v-if="selectedTicketInfo.enableEarlyBird"
                style="margin-left: 10px;"
              >
                早場調整: {{ selectedTicketInfo.earlyBirdAdjustment >= 0 ? '+' : '' }}{{ selectedTicketInfo.earlyBirdAdjustment }} 元
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
          v-if="showPriceForm.selectedShowId && showPriceForm.selectedTicketId"
          type="error" 
          :closable="false"
          style="margin-bottom: 20px;"
        >
          <template #title>
            <div style="font-size: 14px;">
              <strong>價格計算明細:</strong>
              <div style="margin-top: 8px; line-height: 1.8;">
                <div>影廳基準價: {{ currentBasePrice }} 元</div>
                <div>票種調整: {{ selectedTicketInfo.priceAdjustment >= 0 ? '+' : '' }}{{ selectedTicketInfo.priceAdjustment }} 元</div>
                <div v-if="selectedTicketInfo.enableEarlyBird">
                  早場調整: {{ selectedTicketInfo.earlyBirdAdjustment >= 0 ? '+' : '' }}{{ selectedTicketInfo.earlyBirdAdjustment }} 元
                </div>
                <el-divider style="margin: 8px 0;"></el-divider>
                <div style="font-size: 16px; font-weight: 600; color: #f56c6c;">
                  預估最終價格: {{ calculatedPrice }} 元
                </div>
              </div>
            </div>
          </template>
        </el-alert>

        <el-form-item>
          <el-button 
            type="primary" 
            size="large" 
            @click="handleAddShowPrice"
            :loading="submitLoading"
          >
            {{ submitLoading ? '提交中...' : '新增場次票種' }}
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
          <span class="card-title">場次票種價格列表</span>
          <el-button 
            type="primary" 
            size="small" 
            @click="loadShowPricesList"
            :loading="listLoading"
          >
            重新整理列表
          </el-button>
        </div>
      </template>

      <div v-if="enrichedShowPricesList.length === 0" class="empty-state">
        <el-empty description="尚未建立任何場次價格"></el-empty>
      </div>

      <div v-else>
        <!-- 統計資訊 -->
        <el-row :gutter="20" style="margin-bottom: 20px;">
          <el-col :span="8">
            <el-statistic title="總場次數" :value="enrichedShowPricesList.length">
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
          :data="enrichedShowPricesList" 
          style="width: 100%"
          :default-sort="{ prop: 'id', order: 'descending' }"
          v-loading="listLoading"
        >
          <el-table-column type="index" label="#" width="60" align="center" />
          
          <!-- 電影名稱 -->
          <el-table-column label="電影名稱" min-width="180">
            <template #default="{ row }">
              <div>
                <div style="font-weight: 600; font-size: 15px; color: #303133;">
                  {{ row.movieTitle || '未知電影' }}
                </div>
                <el-tag size="small" type="info" style="margin-top: 4px;">
                  場次ID: {{ row.showId }}
                </el-tag>
              </div>
            </template>
          </el-table-column>

          <!-- 場次時間 -->
          <el-table-column label="場次時間" min-width="160">
            <template #default="{ row }">
              <div style="line-height: 1.8;">
                <div style="font-size: 13px; color: #606266;">
                  <i class="el-icon-calendar"></i>
                  {{ formatDate(row.showDate) }}
                </div>
                <div style="font-size: 13px; color: #909399; margin-top: 4px;">
                  <i class="el-icon-time"></i>
                  {{ formatTime(row.startTime) }} - {{ formatTime(row.endTime) }}
                </div>
                <el-tag size="small" style="margin-top: 4px;">
                  影廳{{ row.screenId }}
                </el-tag>
              </div>
            </template>
          </el-table-column>
          
          <!-- 票種名稱 -->
          <el-table-column label="票種名稱" min-width="150">
            <template #default="{ row }">
              <div>
                <div style="font-weight: 600; color: #409EFF;">
                  {{ row.ticketPackageName || '未知票種' }}
                </div>
                <el-tag size="small" type="success" style="margin-top: 4px;">
                  {{ row.ticketPackageCode || 'N/A' }}
                </el-tag>
              </div>
            </template>
          </el-table-column>

          <!-- 價格組成 -->
          <el-table-column label="價格組成" min-width="200">
            <template #default="{ row }">
              <div style="font-size: 12px; line-height: 1.8;">
                <div>基準價: <strong>{{ row.screenBasePrice }}</strong> 元</div>
                <div>
                  票種調整: 
                  <span :style="{ color: row.ticketAdjustment >= 0 ? '#67C23A' : '#F56C6C' }">
                    {{ row.ticketAdjustment >= 0 ? '+' : '' }}{{ row.ticketAdjustment }}
                  </span> 元
                </div>
                <div v-if="row.earlyBird">
                  早場優惠: 
                  <span :style="{ color: row.earlyBirdAdjustment >= 0 ? '#67C23A' : '#F56C6C' }">
                    {{ row.earlyBirdAdjustment >= 0 ? '+' : '' }}{{ row.earlyBirdAdjustment }}
                  </span> 元
                  <el-tag size="small" type="warning" style="margin-left: 4px;">早場</el-tag>
                </div>
                <div v-if="row.durationSurcharge > 0">
                  片長加價: <span style="color: #E6A23C;">+{{ row.durationSurcharge }}</span> 元
                </div>
              </div>
            </template>
          </el-table-column>

          <!-- 最終價格 -->
          <el-table-column prop="finalPrice" label="最終價格" width="120" align="center">
            <template #default="{ row }">
              <el-tag type="success" effect="dark" size="large">
                {{ row.finalPrice }} 元
              </el-tag>
            </template>
          </el-table-column>

          <!-- 販售狀態 -->
          <el-table-column prop="available" label="販售狀態" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="row.available ? 'success' : 'info'" size="small">
                {{ row.available ? '✅ 可售' : '❌ 停售' }}
              </el-tag>
            </template>
          </el-table-column>

          <!-- 計算時間 -->
          <el-table-column label="計算時間" width="180" align="center">
            <template #default="{ row }">
              <div style="font-size: 12px; color: #909399;">
                {{ formatDateTime(row.calculatedAt) }}
              </div>
            </template>
          </el-table-column>

          <!-- 操作 -->
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
import { ref, computed, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import ticketPackageService from '../services/ticketPackageService'
import showTicketPriceService from '../services/showTicketPriceService'
import { showApi, movieApi, screenApi } from '../services/movieApi'

// 場次價格表單
const showPriceForm = ref({
  selectedTicketId: '',
  selectedMovieId: '',
  selectedShowId: '',
  isAvailable: true
})

// 載入狀態
const submitLoading = ref(false)
const listLoading = ref(false)

// 資料列表
const ticketList = ref([])
const movies = ref([])
const allShowList = ref([])
const showPricesList = ref([])
const screenList = ref([]) // ✅ 新增：影廳列表

// ✅ 修改：動態基準價格，根據選擇的場次取得對應影廳的價格
const currentBasePrice = computed(() => {
  if (!showPriceForm.value.selectedShowId) {
    return 0
  }
  
  // 找到選擇的場次
  const show = allShowList.value.find(s => s.id === showPriceForm.value.selectedShowId)
  if (!show) {
    return 0
  }
  
  // 根據場次的 screenId 找到對應的影廳
  const screen = screenList.value.find(s => s.id === show.screenId)
  if (!screen) {
    console.warn(`找不到影廳 ID: ${show.screenId}`)
    return 0
  }
  
  // 返回影廳的基準價格 (後端欄位名稱是 price)
  return screen.price || 0
})

// 根據選擇的電影 ID 過濾場次
const filteredShowList = computed(() => {
  if (!showPriceForm.value.selectedMovieId) {
    return []
  }
  
  const movieId = showPriceForm.value.selectedMovieId
  const filtered = allShowList.value.filter(show => {
    return show.movieId === movieId
  })
  
  return filtered
})

// 選中的電影名稱
const selectedMovieName = computed(() => {
  if (!showPriceForm.value.selectedMovieId) {
    return ''
  }
  
  const movie = movies.value.find(m => m.id === showPriceForm.value.selectedMovieId)
  return movie ? movie.title : ''
})

// 選中的場次資訊
const selectedShowInfo = computed(() => {
  if (!showPriceForm.value.selectedShowId) {
    return {
      showDate: null,
      showTime: null,
      endTime: null,
      screenId: ''
    }
  }
  
  const show = allShowList.value.find(s => s.id === showPriceForm.value.selectedShowId)
  if (!show) {
    return {
      showDate: null,
      showTime: null,
      endTime: null,
      screenId: ''
    }
  }
  
  return {
    showDate: show.showDate,
    showTime: show.showTime,
    endTime: show.endTime,
    screenId: show.screenId
  }
})

// 選中的票種資訊
const selectedTicketInfo = computed(() => {
  if (!showPriceForm.value.selectedTicketId) {
    return {
      packageName: '',
      packageCode: '',
      priceAdjustment: 0,
      earlyBirdAdjustment: 0,
      enableEarlyBird: false
    }
  }
  const ticket = ticketList.value.find(t => t.id === showPriceForm.value.selectedTicketId)
  return ticket || {
    packageName: '',
    packageCode: '',
    priceAdjustment: 0,
    earlyBirdAdjustment: 0,
    enableEarlyBird: false
  }
})

// 豐富場次價格列表
const enrichedShowPricesList = computed(() => {
  console.log('🔍 開始處理場次價格列表')
  console.log('原始列表數量:', showPricesList.value.length)
  console.log('票種列表:', ticketList.value)

  return showPricesList.value.map((sp, index) => {
    const plainSp = JSON.parse(JSON.stringify(sp))
    console.log(`\n處理第 ${index + 1} 項:`, plainSp)

    // 場次資訊
    const showId = plainSp.showId ?? plainSp.show_id
    const show = allShowList.value.find(s => s.id === showId)

    // 票種 id
    const ticketPackageId =
      plainSp.ticketPackageId ??
      plainSp.ticket_package_id ??
      plainSp.ticket_id

    console.log('  ticketPackageId =', ticketPackageId)

    // 用 ticketList 找對應的票種
    const ticket = ticketList.value.find(t => t.id == ticketPackageId)

    console.log('  匹配到的票種 =', ticket)

    // 組成前端要用的物件
    const enriched = {
      ...plainSp,
      movieTitle: show?.movieTitle || plainSp.movieTitle || '未知電影',
      showDate: show?.showDate || plainSp.showDate || null,
      screenId: plainSp.screenId || show?.screenId || 'N/A',
      ticketPackageName: ticket?.packageName || '未知票種',
      ticketPackageCode: ticket?.packageCode || 'N/A'
    }

    console.log('  豐富後:', enriched)

    return enriched
  })
})

// ✅ 修改：使用動態基準價格計算最終價格
const calculatedPrice = computed(() => {
  if (!showPriceForm.value.selectedShowId || !showPriceForm.value.selectedTicketId) {
    return 0
  }
  
  const ticketInfo = selectedTicketInfo.value
  
  // 使用動態取得的影廳基準價格
  let total = currentBasePrice.value
  total += ticketInfo.priceAdjustment
  
  if (ticketInfo.enableEarlyBird) {
    total += ticketInfo.earlyBirdAdjustment
  }
  
  return Math.max(0, total)
})

// 統計資訊
const availableShowsCount = computed(() => {
  return enrichedShowPricesList.value.filter(sp => sp.available).length
})

const averagePrice = computed(() => {
  if (enrichedShowPricesList.value.length === 0) return 0
  const total = enrichedShowPricesList.value.reduce((sum, sp) => sum + sp.finalPrice, 0)
  return Math.round(total / enrichedShowPricesList.value.length)
})

// 監聽電影選擇變化
watch(() => showPriceForm.value.selectedMovieId, (newMovieId, oldMovieId) => {
  if (oldMovieId !== undefined && newMovieId !== oldMovieId) {
    showPriceForm.value.selectedShowId = ''
  }
})

// ✅ 新增：載入影廳列表
async function fetchScreenList() {
  try {
    const res = await screenApi.getAll()
    if (res && res.data) {
      screenList.value = res.data
      console.log('✅ 影廳資料載入成功:', screenList.value.length)
      console.log('影廳資料:', screenList.value)
    }
  } catch (error) {
    console.error('❌ 載入影廳列表失敗:', error)
    ElMessage.error('載入影廳列表失敗')
  }
}

// 載入票種列表
async function fetchTicketList() {
  try {
    const res = await ticketPackageService.list()
    if (res && res.data) {
      ticketList.value = res.data.filter(ticket => ticket.isActive || ticket.is_active)
      console.log('✅ 票種資料載入成功:', ticketList.value.length)
    }
  } catch (error) {
    console.error('❌ 載入票種列表失敗:', error)
    ElMessage.error('載入票種列表失敗')
  }
}

// 載入電影列表
async function fetchMovies() {
  try {
    const res = await movieApi.getAll()
    if (res && res.data) {
      movies.value = res.data
      console.log('✅ 電影資料載入成功:', movies.value.length)
    }
  } catch (error) {
    console.error('❌ 載入電影列表失敗:', error)
    ElMessage.error('載入電影列表失敗')
  }
}

// 載入所有場次列表
async function fetchAllShows() {
  try {
    const [showsRes, moviesRes] = await Promise.all([
      showApi.getAll(),
      movieApi.getAll(),
    ])

    const rawShows = showsRes.data || []
    const moviesData = moviesRes.data || []

    // 建立電影 ID 到名稱的映射
    const movieMap = {}
    moviesData.forEach(m => {
      movieMap[m.id] = m.title
    })

    // 為每個場次添加電影名稱
    allShowList.value = rawShows.map(s => ({
      id: s.id,
      movieId: s.movieId,
      movieTitle: movieMap[s.movieId] || `未知電影(ID:${s.movieId})`,
      screenId: s.screenId,
      showDate: s.showDate,
      showTime: s.showTime,
      endTime: s.endTime,
    }))

    console.log('✅ 場次資料載入成功:', allShowList.value.length)
  } catch (error) {
    console.error('❌ 載入場次列表失敗:', error)
    ElMessage.error('載入場次列表失敗')
  }
}

// 載入場次價格列表
async function loadShowPricesList() {
  listLoading.value = true
  try {
    const res = await showTicketPriceService.list()
    showPricesList.value = res.data || []
    
    console.log('✅ 載入場次價格列表:', showPricesList.value.length, '筆')
    console.log('第一筆資料結構:', showPricesList.value[0])
    
    // 確保有足夠的關聯資料
    if (ticketList.value.length === 0) {
      await fetchTicketList()
    }
    if (allShowList.value.length === 0) {
      await fetchAllShows()
    }
    if (screenList.value.length === 0) {
      await fetchScreenList()
    }
    
    ElMessage.success(`成功載入 ${showPricesList.value.length} 筆場次價格`)
  } catch (error) {
    console.error('❌ 載入場次價格列表失敗:', error)
    ElMessage.error('載入場次價格列表失敗')
    showPricesList.value = []
  } finally {
    listLoading.value = false
  }
}

function onMovieSelected(movieId) {
  const movie = movies.value.find(m => m.id === movieId)
  if (movie) {
    const showCount = filteredShowList.value.length
    if (showCount > 0) {
      ElMessage.success(`已選擇電影: ${movie.title} (共 ${showCount} 個場次)`)
    } else {
      ElMessage.warning(`已選擇電影: ${movie.title},但該電影暫無場次`)
    }
  }
}

function onShowSelected(showId) {
  const show = allShowList.value.find(s => s.id === showId)
  if (show) {
    // ✅ 修改：顯示影廳基準價格 (後端欄位是 price)
    const screen = screenList.value.find(sc => sc.id === show.screenId)
    const basePriceInfo = screen ? `基準價: ${screen.price} 元` : ''
    ElMessage.success(`已選擇場次: ${formatDate(show.showDate)} ${formatTime(show.showTime)} ${basePriceInfo}`)
  }
}

// 新增場次價格（提交到後端）
async function handleAddShowPrice() {
  if (!showPriceForm.value.selectedTicketId) {
    ElMessage.warning('請先選擇票種')
    return
  }

  if (!showPriceForm.value.selectedMovieId) {
    ElMessage.warning('請先選擇電影')
    return
  }

  if (!showPriceForm.value.selectedShowId) {
    ElMessage.warning('請先選擇場次')
    return
  }

  // 檢查是否已存在
  const exists = showPricesList.value.some(sp => 
    sp.ticketPackage?.id === showPriceForm.value.selectedTicketId && 
    sp.showId === showPriceForm.value.selectedShowId
  )

  if (exists) {
    ElMessage.warning('此票種與場次組合已存在')
    return
  }

  submitLoading.value = true
  
  try {
    // 準備資料
    const data = {
      showId: showPriceForm.value.selectedShowId,
      ticketPackageId: showPriceForm.value.selectedTicketId,
      isAvailable: showPriceForm.value.isAvailable
    }

    console.log('📤 提交資料:', data)

    // 呼叫 API
    const res = await showTicketPriceService.create(data)
    
    console.log('✅ 返回結果:', res.data)
    
    ElMessage.success('場次價格新增成功!')
    
    // 重新載入列表
    await loadShowPricesList()
    
    // 重置表單
    resetShowPriceForm()
    
  } catch (error) {
    console.error('❌ 新增失敗:', error)
    
    let errorMsg = '新增場次價格失敗'
    if (error.response && error.response.data) {
      if (error.response.data.error) {
        errorMsg = error.response.data.error
      } else if (typeof error.response.data === 'string') {
        errorMsg = error.response.data
      }
    }
    
    ElMessage.error(errorMsg)
  } finally {
    submitLoading.value = false
  }
}

function resetShowPriceForm() {
  showPriceForm.value = {
    selectedTicketId: '',
    selectedMovieId: '',
    selectedShowId: '',
    isAvailable: true
  }
}

// 刪除場次價格
async function removeShowPrice(showPrice) {
  try {
    await ElMessageBox.confirm(
      `確定要刪除「${showPrice.movieTitle}」的「${showPrice.ticketPackageName}」價格設定嗎?`,
      '確認刪除',
      {
        confirmButtonText: '確定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )
    
    listLoading.value = true
    
    try {
      await showTicketPriceService.delete(showPrice.id)
      ElMessage.success('場次價格已刪除')
      await loadShowPricesList()
    } catch (error) {
      console.error('❌ 刪除失敗:', error)
      ElMessage.error('刪除場次價格失敗')
    } finally {
      listLoading.value = false
    }
  } catch {
    // 使用者取消
  }
}

function formatDate(date) {
  if (!date) return ''
  if (typeof date === 'string') return date.split(' ')[0]
  
  const d = new Date(date)
  if (isNaN(d.getTime())) return date
  
  const year = d.getFullYear()
  const month = (d.getMonth() + 1).toString().padStart(2, '0')
  const day = d.getDate().toString().padStart(2, '0')
  
  return `${year}/${month}/${day}`
}

function formatTime(time) {
  if (!time) return ''
  if (typeof time === 'string') return time.substring(0, 5)
  
  const date = new Date(time)
  if (isNaN(date.getTime())) return time
  
  const hours = date.getHours().toString().padStart(2, '0')
  const minutes = date.getMinutes().toString().padStart(2, '0')
  
  return `${hours}:${minutes}`
}

function formatDateTime(datetime) {
  if (!datetime) return ''
  
  const date = new Date(datetime)
  if (isNaN(date.getTime())) return datetime
  
  const year = date.getFullYear()
  const month = (date.getMonth() + 1).toString().padStart(2, '0')
  const day = date.getDate().toString().padStart(2, '0')
  const hours = date.getHours().toString().padStart(2, '0')
  const minutes = date.getMinutes().toString().padStart(2, '0')
  
  return `${year}/${month}/${day} ${hours}:${minutes}`
}

onMounted(async () => {
  console.log('🚀 組件掛載，開始載入資料...')
  
  // ✅ 修改：依序載入基礎資料，包含影廳列表
  await Promise.all([
    fetchTicketList(),
    fetchMovies(),
    fetchAllShows(),
    fetchScreenList() // 新增
  ])
  
  // 載入場次價格列表
  await loadShowPricesList()
  
  console.log('✅ 所有資料載入完成')
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
</style>