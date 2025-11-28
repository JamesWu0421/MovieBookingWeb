<template>
  <div class="batch-tickets-page">
    <!-- 頁面標題 + 返回按鈕 -->
    <div class="page-header">
      <div class="header-left">
        <button @click="goBack" class="btn-back">
          ← 返回批次列表
        </button>
        <h1>🎫 票券管理</h1>
        <span class="batch-info">批次 #{{ batchId }}</span>
      </div>
      <div class="header-right">
        <button @click="showBatchAddDialog = true" class="btn-primary-large">
          ⚡ 批量新增票券
        </button>
      </div>
    </div>

    <!-- 操作工具列 -->
    <div class="toolbar">
      <div class="toolbar-left">
        <button @click="goToSessions" class="btn-navigate">
          📋 返回場次管理
        </button>
        <select
          v-model="filterSessionId"
          @change="loadTickets"
          class="filter-select"
        >
          <option value="">全部場次</option>
          <option
            v-for="session in sessions"
            :key="session.id"
            :value="session.id"
          >
            場次 #{{ session.id }} - {{ getMovieName(session.movieId) }} ({{ formatDateTime(session.showDate, session.showTime) }})
          </option>
        </select>
        <select
          v-model="filterStatus"
          @change="loadTickets"
          class="filter-select"
        >
          <option value="">全部狀態</option>
          <option value="pending">待處理</option>
          <option value="processing">處理中</option>
          <option value="success">成功</option>
          <option value="failed">失敗</option>
        </select>
      </div>
      <div class="toolbar-right">
        <button @click="loadTickets" class="btn-refresh">
          🔄 重新整理
        </button>
      </div>
    </div>

    <!-- 載入中 / 錯誤 / 空狀態 -->
    <div v-if="loading" class="loading">⏳ 載入票券中...</div>
    <div v-else-if="error" class="error-box">❌ {{ error }}</div>
    <div v-else-if="tickets.length === 0" class="empty-state">
      <p>🔭 此批次尚未新增票券</p>
      <button @click="showBatchAddDialog = true" class="btn-secondary">
        開始批量新增票券
      </button>
    </div>

    <!-- 票券列表 -->
    <div v-else class="tickets-table-container">
      <table class="tickets-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>場次 ID</th>
            <th>電影</th>
            <th>場次時間</th>
            <th>套票包 ID</th>
            <th>狀態</th>
            <th>錯誤訊息</th>
            <th>建立時間</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="ticket in tickets"
            :key="ticket.id"
            :class="getRowClass(ticket)"
          >
            <td class="id-cell">{{ ticket.id }}</td>
            <td class="session-cell">{{ ticket.batchSessionId || '-' }}</td>
            <td class="movie-cell">{{ getSessionMovieName(ticket.batchSessionId) }}</td>
            <td class="datetime-cell">{{ getSessionTime(ticket.batchSessionId) }}</td>
            <td class="package-cell">{{ ticket.ticketPackagesId || '-' }}</td>
            <td>
              <span
                class="status-badge"
                :class="getStatusBadgeClass(ticket.status)"
              >
                {{ getStatusText(ticket.status) }}
              </span>
            </td>
            <td class="error-cell">
              <span v-if="ticket.errorMessage" class="error-text">
                {{ ticket.errorMessage }}
              </span>
              <span v-else class="no-error">-</span>
            </td>
            <td class="datetime-cell">
              {{ formatDateTime(ticket.createdAt) }}
            </td>
            <td class="action-cell">
              <button
                @click="editTicket(ticket)"
                class="btn-icon btn-edit"
                title="編輯"
              >
                ✏️
              </button>
              <button
                @click="deleteTicket(ticket)"
                class="btn-icon btn-delete"
                title="刪除"
              >
                🗑️
              </button>
            </td>
          </tr>
        </tbody>
      </table>

      <!-- 統計資訊 -->
      <div class="summary">
        <div class="stat-item">
          <span class="stat-label">總票券數：</span>
          <strong class="stat-value">{{ tickets.length }}</strong>
        </div>
        <div class="stat-item">
          <span class="stat-label">待處理：</span>
          <strong class="stat-value pending">{{ statusCount.pending }}</strong>
        </div>
        <div class="stat-item">
          <span class="stat-label">處理中：</span>
          <strong class="stat-value processing">{{ statusCount.processing }}</strong>
        </div>
        <div class="stat-item">
          <span class="stat-label">成功：</span>
          <strong class="stat-value success">{{ statusCount.success }}</strong>
        </div>
        <div class="stat-item">
          <span class="stat-label">失敗：</span>
          <strong class="stat-value error">{{ statusCount.failed }}</strong>
        </div>
      </div>
    </div>

    <!-- 批量新增票券對話框 -->
    <div
      v-if="showBatchAddDialog"
      class="modal-overlay"
      @click.self="closeDialogs"
    >
      <div class="modal modal-large">
        <div class="modal-header">
          <h2>⚡ 批量新增票券</h2>
          <button @click="closeDialogs" class="btn-close">×</button>
        </div>
        <div class="modal-body">
          <!-- 步驟 1: 選擇電影 -->
          <div class="step-section">
            <div class="step-header">
              <span class="step-number">1</span>
              <h3>選擇電影</h3>
            </div>
            <div class="form-group">
              <label>電影 <span class="required">*</span></label>
              <select 
                v-model="batchFormData.selectedMovieId" 
                @change="onMovieChange"
                class="form-input"
              >
                <option value="">請選擇電影</option>
                <option
                  v-for="movie in movies"
                  :key="movie.id"
                  :value="movie.id"
                >
                  {{ movie.title }}
                </option>
              </select>
            </div>
          </div>

          <!-- 步驟 2: 選擇場次 -->
          <div class="step-section" v-if="batchFormData.selectedMovieId">
            <div class="step-header">
              <span class="step-number">2</span>
              <h3>選擇場次</h3>
              <span class="help-text">（可多選）</span>
            </div>
            <div v-if="filteredSessions.length === 0" class="empty-hint">
              <p>⚠️ 該電影在此批次中沒有可用場次</p>
            </div>
            <div v-else class="sessions-grid">
              <div class="select-all-row">
                <label class="checkbox-label">
                  <input
                    type="checkbox"
                    :checked="isAllSessionsSelected"
                    @change="toggleAllSessions"
                  />
                  <span>全選 ({{ filteredSessions.length }} 場)</span>
                </label>
              </div>
              <label
                v-for="session in filteredSessions"
                :key="session.id"
                class="session-checkbox"
              >
                <input
                  type="checkbox"
                  :value="session.id"
                  v-model="batchFormData.selectedSessionIds"
                />
                <div class="session-info">
                  <div class="session-main">
                    <span class="session-id">#{{ session.id }}</span>
                    <span class="session-screen">{{ getScreenName(session.screenId) }}</span>
                  </div>
                  <div class="session-time">
                    {{ formatSessionDateTime(session.showDate, session.showTime) }}
                  </div>
                </div>
              </label>
            </div>
          </div>

          <!-- 步驟 3: 選擇票種 -->
          <div class="step-section" v-if="batchFormData.selectedSessionIds.length > 0">
            <div class="step-header">
              <span class="step-number">3</span>
              <h3>選擇票種</h3>
              <span class="help-text">（可多選）</span>
            </div>
            <div class="ticket-types-grid">
              <div class="select-all-row">
                <label class="checkbox-label">
                  <input
                    type="checkbox"
                    :checked="isAllTicketTypesSelected"
                    @change="toggleAllTicketTypes"
                  />
                  <span>全選 ({{ ticketPackages.length }} 種)</span>
                </label>
              </div>
              <label
  v-for="pkg in ticketPackages"
  :key="pkg.id"
  class="ticket-type-checkbox"
>
  <input
    type="checkbox"
    :value="pkg.id"
    v-model="batchFormData.selectedTicketPackageIds"
  />
  <div class="ticket-type-info">
    <!-- ✅ 顯示票種名稱 -->
    <div class="ticket-type-name">{{ pkg.packageName }}</div>
    <!-- ✅ 顯示票種代碼 -->
    <div class="ticket-type-code">{{ pkg.packageCode }}</div>
    <!-- ✅ 顯示價格調整（如果有） -->
    <div class="ticket-type-price" v-if="pkg.priceAdjustment !== undefined">
      {{ pkg.priceAdjustment >= 0 ? '+' : '' }}{{ pkg.priceAdjustment }}元
    </div>
  </div>
</label>
            </div>
          </div>

          <!-- 預覽統計 -->
          <div 
            class="preview-section" 
            v-if="batchFormData.selectedSessionIds.length > 0 && batchFormData.selectedTicketPackageIds.length > 0"
          >
            <div class="preview-header">
              <h3>📊 預覽</h3>
            </div>
            <div class="preview-stats">
              <div class="preview-stat">
                <span class="preview-label">選擇場次：</span>
                <strong class="preview-value">{{ batchFormData.selectedSessionIds.length }}</strong>
              </div>
              <div class="preview-stat">
                <span class="preview-label">選擇票種：</span>
                <strong class="preview-value">{{ batchFormData.selectedTicketPackageIds.length }}</strong>
              </div>
              <div class="preview-stat highlight">
                <span class="preview-label">將建立票券：</span>
                <strong class="preview-value-large">{{ totalTicketsToCreate }}</strong>
                <span class="preview-unit">張</span>
              </div>
            </div>
            <div class="preview-detail">
              <p class="preview-formula">
                = {{ batchFormData.selectedSessionIds.length }} 場次 × {{ batchFormData.selectedTicketPackageIds.length }} 票種
              </p>
            </div>
          </div>
        </div>

        <div class="modal-footer">
          <button @click="closeDialogs" class="btn-secondary">取消</button>
          <button
            @click="batchAddTickets"
            class="btn-primary-large"
            :disabled="!isBatchFormValid || submitting"
          >
            <span v-if="submitting">⏳ 建立中...</span>
            <span v-else>✅ 確認批量新增 ({{ totalTicketsToCreate }} 張)</span>
          </button>
        </div>
      </div>
    </div>

    <!-- 編輯票券對話框 -->
    <div
      v-if="showEditDialog"
      class="modal-overlay"
      @click.self="closeDialogs"
    >
      <div class="modal">
        <div class="modal-header">
          <h2>編輯票券</h2>
          <button @click="closeDialogs" class="btn-close">×</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>場次 <span class="required">*</span></label>
            <select v-model="formData.batchSessionId" class="form-input">
              <option value="">請選擇場次</option>
              <option
                v-for="session in sessions"
                :key="session.id"
                :value="session.id"
              >
                場次 #{{ session.id }} - {{ getMovieName(session.movieId) }} - 
                {{ formatSessionDateTime(session.showDate, session.showTime) }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label>套票包 ID</label>
            <select v-model.number="formData.ticketPackagesId" class="form-input">
              <option :value="null">請選擇套票包</option>
              <option
                v-for="pkg in ticketPackages"
                :key="pkg.id"
                :value="pkg.id"
              >
                {{ pkg.name }} - NT$ {{ pkg.price }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label>狀態</label>
            <select v-model="formData.status" class="form-input">
              <option value="pending">待處理</option>
              <option value="processing">處理中</option>
              <option value="success">成功</option>
              <option value="failed">失敗</option>
            </select>
          </div>
          <div class="form-group">
            <label>錯誤訊息（選填）</label>
            <textarea
              v-model="formData.errorMessage"
              placeholder="如果有錯誤,請輸入錯誤訊息"
              class="form-textarea"
              rows="3"
            ></textarea>
          </div>
        </div>
        <div class="modal-footer">
          <button @click="closeDialogs" class="btn-secondary">取消</button>
          <button
            @click="updateTicket()"
            class="btn-primary"
            :disabled="!isFormValid"
          >
            更新
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Swal from 'sweetalert2'
import { batchTicketTempService } from '../services/batchTicketTempService'
import { batchSessionTempService } from '../services/batchSessionTempService'
import { movieApi } from '../services/api'
import ticketPackageService from '../services/ticketPackageService'

const route = useRoute()
const router = useRouter()

const batchId = computed(() => route.params.batchId)

const tickets = ref([])
const sessions = ref([])
const movies = ref([])
const ticketPackages = ref([])
const loading = ref(false)
const error = ref('')
const filterSessionId = ref('')
const filterStatus = ref('')
const submitting = ref(false)

const showEditDialog = ref(false)
const showBatchAddDialog = ref(false)
const editingTicket = ref(null)

// 編輯表單
const formData = ref({
  batchSessionId: null,
  ticketPackagesId: null,
  status: 'pending',
  errorMessage: '',
})

// 批量新增表單
const batchFormData = ref({
  selectedMovieId: '',
  selectedSessionIds: [],
  selectedTicketPackageIds: [],
})

const isFormValid = computed(
  () => !!formData.value.batchSessionId
)

// 批量表單驗證
const isBatchFormValid = computed(() => {
  return (
    batchFormData.value.selectedSessionIds.length > 0 &&
    batchFormData.value.selectedTicketPackageIds.length > 0
  )
})

// 根據選擇的電影過濾場次
const filteredSessions = computed(() => {
  if (!batchFormData.value.selectedMovieId) return []
  return sessions.value.filter(
    session => session.movieId === batchFormData.value.selectedMovieId
  )
})

// 總共將建立的票券數量
const totalTicketsToCreate = computed(() => {
  return batchFormData.value.selectedSessionIds.length * 
         batchFormData.value.selectedTicketPackageIds.length
})

// 是否全選場次
const isAllSessionsSelected = computed(() => {
  return filteredSessions.value.length > 0 &&
         batchFormData.value.selectedSessionIds.length === filteredSessions.value.length
})

// 是否全選票種
const isAllTicketTypesSelected = computed(() => {
  return ticketPackages.value.length > 0 &&
         batchFormData.value.selectedTicketPackageIds.length === ticketPackages.value.length
})

// 切換全選場次
const toggleAllSessions = (event) => {
  if (event.target.checked) {
    batchFormData.value.selectedSessionIds = filteredSessions.value.map(s => s.id)
  } else {
    batchFormData.value.selectedSessionIds = []
  }
}

// 切換全選票種
const toggleAllTicketTypes = (event) => {
  if (event.target.checked) {
    batchFormData.value.selectedTicketPackageIds = ticketPackages.value.map(p => p.id)
  } else {
    batchFormData.value.selectedTicketPackageIds = []
  }
}

// 電影改變時清空場次選擇
const onMovieChange = () => {
  batchFormData.value.selectedSessionIds = []
}

// 計算各狀態票券數量
const statusCount = computed(() => {
  if (!Array.isArray(tickets.value)) {
    return { pending: 0, processing: 0, success: 0, failed: 0 }
  }
  return {
    pending: tickets.value.filter(t => t.status === 'pending').length,
    processing: tickets.value.filter(t => t.status === 'processing').length,
    success: tickets.value.filter(t => t.status === 'success').length,
    failed: tickets.value.filter(t => t.status === 'failed').length,
  }
})

// 取得電影名稱
const getMovieName = (movieId) => {
  const movie = movies.value.find(m => m.id === movieId)
  return movie ? movie.title : `未知電影(${movieId})`
}

// 取得影廳名稱
const getScreenName = (screenId) => {
  return `影廳 ${screenId}`
}

// 取得場次的電影名稱
const getSessionMovieName = (sessionId) => {
  const session = sessions.value.find(s => s.id === sessionId)
  return session ? getMovieName(session.movieId) : '-'
}

// 取得場次時間
const getSessionTime = (sessionId) => {
  const session = sessions.value.find(s => s.id === sessionId)
  return session ? formatSessionDateTime(session.showDate, session.showTime) : '-'
}

// 格式化場次日期時間
const formatSessionDateTime = (date, time) => {
  if (!date || !time) return '-'
  return `${date} ${time}`
}

// 取得狀態文字
const getStatusText = (status) => {
  const statusMap = {
    pending: '⏳ 待處理',
    processing: '🔄 處理中',
    success: '✅ 成功',
    failed: '❌ 失敗',
  }
  return statusMap[status] || status || '-'
}

// 取得狀態徽章樣式
const getStatusBadgeClass = (status) => {
  return `badge-${status || 'default'}`
}

// 取得表格行樣式
const getRowClass = (ticket) => {
  if (ticket.status === 'failed' || ticket.errorMessage) {
    return 'has-error'
  }
  if (ticket.status === 'success') {
    return 'has-success'
  }
  return ''
}

// 載入電影、票種資料
const loadMoviesAndTicketPackages = async () => {
  try {
    const [moviesRes, packagesRes] = await Promise.all([
      movieApi.getAll(),
      ticketPackageService.list(),
    ])
    
    if (moviesRes && moviesRes.data) {
      movies.value = moviesRes.data
      console.log('✅ 電影資料載入成功:', movies.value.length)
    } else {
      movies.value = []
    }
    
    if (packagesRes && packagesRes.data) {
      ticketPackages.value = packagesRes.data.filter(
        ticket => ticket.isActive || ticket.is_active
      )
      console.log('✅ 票種資料載入成功:', ticketPackages.value.length)
    } else {
      ticketPackages.value = []
      console.warn('⚠️ 票種資料格式異常')
    }
    
  } catch (err) {
    console.error('❌ 載入電影或票種資料失敗:', err)
    Swal.fire({
      icon: 'error',
      title: '載入失敗',
      text: '無法載入電影或票種資料',
    })
    movies.value = []
    ticketPackages.value = []
  }
}

// 載入場次列表
const loadSessions = async () => {
  if (!batchId.value) return
  try {
    const response = await batchSessionTempService.getByBatchId(batchId.value)

    let list = response.data
    if (!Array.isArray(list)) {
      if (list.success && Array.isArray(list.data)) {
        list = list.data
      } else if (Array.isArray(list.content)) {
        list = list.content
      } else {
        console.warn('sessions 回傳不是陣列，實際資料為：', list)
        list = []
      }
    }

    sessions.value = list
  } catch (err) {
    console.error('載入場次失敗:', err)
  }
}

// 載入票券列表
const loadTickets = async () => {
  if (!batchId.value) return

  loading.value = true
  error.value = ''

  try {
    let response

    if (filterStatus.value) {
      response = await batchTicketTempService.getByStatus(filterStatus.value)
    } else if (filterSessionId.value) {
      response = await batchTicketTempService.getBySession(filterSessionId.value)
    } else {
      response = await batchTicketTempService.getByBatchId(batchId.value)
    }

    let list = response.data
    
    if (list && typeof list === 'object' && !Array.isArray(list)) {
      if (Array.isArray(list.data)) {
        list = list.data
      } else if (Array.isArray(list.content)) {
        list = list.content
      } else {
        console.warn('tickets 回傳格式異常，實際資料為：', list)
        list = []
      }
    }

    if (!Array.isArray(list)) {
      console.warn('tickets 最終不是陣列，設為空陣列')
      list = []
    }

    tickets.value = list
  } catch (err) {
    console.error(err)
    error.value = err.response?.data?.message || err.message
  } finally {
    loading.value = false
  }
}

// 批量新增票券
const batchAddTickets = async () => {
  if (!isBatchFormValid.value) {
    Swal.fire({
      icon: 'warning',
      title: '資料不完整',
      text: '請選擇場次和票種',
    })
    return
  }

  const result = await Swal.fire({
    icon: 'question',
    title: '確認批量新增',
    html: `
      <div style="text-align: left; padding: 10px;">
        <p><strong>電影：</strong>${getMovieName(batchFormData.value.selectedMovieId)}</p>
        <p><strong>場次數量：</strong>${batchFormData.value.selectedSessionIds.length} 場</p>
        <p><strong>票種數量：</strong>${batchFormData.value.selectedTicketPackageIds.length} 種</p>
        <hr>
        <p style="color: #2563eb; font-size: 18px;"><strong>總共將建立：${totalTicketsToCreate.value} 張票券</strong></p>
      </div>
    `,
    showCancelButton: true,
    confirmButtonText: '確定新增',
    cancelButtonText: '取消',
    confirmButtonColor: '#2563eb',
  })

  if (!result.isConfirmed) return

  submitting.value = true

  try {
    let successCount = 0
    let failCount = 0
    const errors = []

    for (const sessionId of batchFormData.value.selectedSessionIds) {
      for (const packageId of batchFormData.value.selectedTicketPackageIds) {
        try {
          const response = await batchTicketTempService.create({
            batchId: parseInt(batchId.value, 10),
            batchSessionId: sessionId,
            ticketPackagesId: packageId,
            status: 'pending',
            errorMessage: null,
          })

          if (response.data.success) {
            successCount++
          } else {
            failCount++
            errors.push(`場次 #${sessionId} - 票種 #${packageId}: ${response.data.message}`)
          }
        } catch (err) {
          failCount++
          errors.push(`場次 #${sessionId} - 票種 #${packageId}: ${err.message}`)
        }
      }
    }

    if (failCount === 0) {
      await Swal.fire({
        icon: 'success',
        title: '批量新增成功',
        html: `成功建立 <strong>${successCount}</strong> 張票券`,
      })
    } else {
      await Swal.fire({
        icon: 'warning',
        title: '部分新增失敗',
        html: `
          <p>成功：${successCount} 張</p>
          <p>失敗：${failCount} 張</p>
          ${errors.length > 0 ? `<div style="text-align: left; max-height: 200px; overflow-y: auto; margin-top: 10px; padding: 10px; background: #fee; border-radius: 4px;">
            <strong>錯誤詳情：</strong><br>
            ${errors.slice(0, 5).map(e => `• ${e}`).join('<br>')}
            ${errors.length > 5 ? `<br>...還有 ${errors.length - 5} 個錯誤` : ''}
          </div>` : ''}
        `,
      })
    }

    closeDialogs()
    await loadTickets()

  } catch (err) {
    console.error(err)
    Swal.fire({
      icon: 'error',
      title: '批量新增失敗',
      text: err.message,
    })
  } finally {
    submitting.value = false
  }
}

// 編輯票券
const editTicket = (ticket) => {
  editingTicket.value = ticket
  formData.value = {
    batchSessionId: ticket.batchSessionId,
    ticketPackagesId: ticket.ticketPackagesId || null,
    status: ticket.status || 'pending',
    errorMessage: ticket.errorMessage || '',
  }
  showEditDialog.value = true
}

// 更新票券
const updateTicket = async () => {
  try {
    const payload = {
      batchId: parseInt(batchId.value, 10),
      batchSessionId: formData.value.batchSessionId,
      ticketPackagesId: formData.value.ticketPackagesId || null,
      status: formData.value.status || 'pending',
      errorMessage: formData.value.errorMessage || null,
    }

    await batchTicketTempService.update(editingTicket.value.id, payload)

    closeDialogs()
    await loadTickets()

    Swal.fire({ icon: 'success', title: '更新成功' })
  } catch (err) {
    console.error(err)
    Swal.fire({
      icon: 'error',
      title: '更新失敗',
      text: err.response?.data?.message || err.message,
    })
  }
}

// 刪除票券
const deleteTicket = async (ticket) => {
  const result = await Swal.fire({
    icon: 'warning',
    title: `確定刪除票券 #${ticket.id}？`,
    text: `場次: ${ticket.batchSessionId || 'N/A'}, 套票包: ${ticket.ticketPackagesId || 'N/A'}`,
    showCancelButton: true,
    confirmButtonText: '刪除',
    cancelButtonText: '取消',
    confirmButtonColor: '#dc2626',
  })
  if (!result.isConfirmed) return

  try {
    await batchTicketTempService.remove(ticket.id)
    await loadTickets()
    Swal.fire({ icon: 'success', title: '刪除成功' })
  } catch (err) {
    console.error(err)
    Swal.fire({
      icon: 'error',
      title: '刪除失敗',
      text: err.response?.data?.message || err.message,
    })
  }
}

// 導向場次管理頁
const goToSessions = () => {
  router.push({
    name: 'BatchSessionTemp',
    params: { batchId: batchId.value },
  })
}

// 返回批次列表
const goBack = () => {
  router.push({ name: 'BatchOperationList' })
}

// 關閉對話框
const closeDialogs = () => {
  showEditDialog.value = false
  showBatchAddDialog.value = false
  editingTicket.value = null
  
  formData.value = {
    batchSessionId: null,
    ticketPackagesId: null,
    status: 'pending',
    errorMessage: '',
  }
  
  batchFormData.value = {
    selectedMovieId: '',
    selectedSessionIds: [],
    selectedTicketPackageIds: [],
  }
}

// 時間格式工具
const formatDateTime = (datetime) => {
  if (!datetime) return '-'
  return new Date(datetime).toLocaleString('zh-TW', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  })
}

// 初始化
onMounted(async () => {
  if (!batchId.value) {
    router.push({ name: 'BatchOperationList' })
    return
  }
  await Promise.all([
    loadMoviesAndTicketPackages(),
    loadSessions(),
    loadTickets()
  ])
})
</script>

<style scoped>
.batch-tickets-page {
  max-width: 1800px;
  margin: 0 auto;
  padding: 24px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-right {
  display: flex;
  gap: 12px;
}

.btn-back {
  background: white;
  color: #475569;
  border: 1px solid #e2e8f0;
  padding: 10px 20px;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-back:hover {
  background: #f8fafc;
  border-color: #cbd5e1;
}

.page-header h1 {
  font-size: 28px;
  font-weight: 700;
  color: #1a1a1a;
  margin: 0;
}

.batch-info {
  background: #e0e7ff;
  color: #3730a3;
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 600;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding: 16px;
  background: #f8fafc;
  border-radius: 12px;
}

.toolbar-left, .toolbar-right {
  display: flex;
  gap: 12px;
}

.btn-navigate {
  background: #2563eb;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-navigate:hover {
  background: #1d4ed8;
  transform: translateY(-1px);
}

.filter-select {
  padding: 10px 16px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  font-size: 14px;
  background: white;
  cursor: pointer;
  min-width: 250px;
}

.btn-refresh {
  background: white;
  color: #475569;
  border: 1px solid #e2e8f0;
  padding: 10px 20px;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-refresh:hover {
  background: #f8fafc;
}

.btn-primary {
  background: #2563eb;
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 8px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-primary:hover:not(:disabled) {
  background: #1d4ed8;
  transform: translateY(-1px);
}

.btn-primary:disabled {
  background: #94a3b8;
  cursor: not-allowed;
}

.btn-primary-large {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  padding: 14px 28px;
  border-radius: 10px;
  font-size: 16px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

.btn-primary-large:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.5);
}

.btn-primary-large:disabled {
  background: #94a3b8;
  cursor: not-allowed;
  box-shadow: none;
}

.btn-secondary {
  background: white;
  color: #475569;
  border: 1px solid #e2e8f0;
  padding: 10px 20px;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-secondary:hover {
  background: #f8fafc;
}

.tickets-table-container {
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  overflow: hidden;
}

.tickets-table {
  width: 100%;
  border-collapse: collapse;
}

.tickets-table thead {
  background: #f8fafc;
}

.tickets-table th {
  padding: 16px;
  text-align: left;
  font-size: 13px;
  font-weight: 700;
  color: #475569;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  border-bottom: 2px solid #e2e8f0;
}

.tickets-table td {
  padding: 16px;
  border-bottom: 1px solid #f1f5f9;
  font-size: 14px;
  color: #1e293b;
}

.tickets-table tbody tr:hover {
  background: #f8fafc;
}

.tickets-table tbody tr.has-error {
  background: #fef2f2;
}

.tickets-table tbody tr.has-success {
  background: #f0fdf4;
}

.tickets-table tbody tr:last-child td {
  border-bottom: none;
}

.id-cell {
  font-weight: 700;
  color: #2563eb;
}

.session-cell {
  font-weight: 600;
  color: #7c3aed;
}

.movie-cell {
  font-weight: 600;
  color: #059669;
}

.package-cell {
  font-weight: 600;
  color: #0891b2;
}

.datetime-cell {
  color: #64748b;
  font-size: 13px;
}

.status-badge {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 700;
}

.badge-pending {
  background: #fef3c7;
  color: #92400e;
}

.badge-processing {
  background: #dbeafe;
  color: #1e40af;
}

.badge-success {
  background: #d1fae5;
  color: #065f46;
}

.badge-failed {
  background: #fee2e2;
  color: #991b1b;
}

.badge-default {
  background: #f1f5f9;
  color: #64748b;
}

.error-cell {
  max-width: 300px;
}

.error-text {
  color: #dc2626;
  font-size: 13px;
  display: block;
  word-break: break-word;
}

.no-error {
  color: #cbd5e1;
}

.action-cell {
  display: flex;
  gap: 8px;
}

.btn-icon {
  background: none;
  border: none;
  font-size: 18px;
  cursor: pointer;
  padding: 6px;
  border-radius: 6px;
  transition: all 0.2s;
}

.btn-edit:hover {
  background: #dbeafe;
}

.btn-delete:hover {
  background: #fee2e2;
}

.summary {
  display: flex;
  justify-content: center;
  gap: 40px;
  padding: 20px;
  background: #f8fafc;
  border-top: 1px solid #e2e8f0;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.stat-label {
  color: #64748b;
  font-size: 14px;
}

.stat-value {
  font-size: 20px;
  font-weight: 700;
}

.stat-value.pending {
  color: #d97706;
}

.stat-value.processing {
  color: #2563eb;
}

.stat-value.success {
  color: #10b981;
}

.stat-value.error {
  color: #ef4444;
}

.loading, .empty-state {
  text-align: center;
  padding: 80px 20px;
  color: #64748b;
}

.empty-state p {
  font-size: 18px;
  margin-bottom: 20px;
}

.error-box {
  background: #fee2e2;
  color: #991b1b;
  padding: 16px 20px;
  border-radius: 8px;
  border-left: 4px solid #dc2626;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  backdrop-filter: blur(4px);
}

.modal {
  background: white;
  border-radius: 16px;
  width: 90%;
  max-width: 600px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  max-height: 90vh;
  display: flex;
  flex-direction: column;
}

.modal-large {
  max-width: 900px;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px;
  border-bottom: 1px solid #e2e8f0;
}

.modal-header h2 {
  font-size: 20px;
  font-weight: 700;
  margin: 0;
}

.btn-close {
  background: none;
  border: none;
  font-size: 32px;
  color: #64748b;
  cursor: pointer;
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 6px;
}

.btn-close:hover {
  background: #f1f5f9;
}

.modal-body {
  padding: 24px;
  overflow-y: auto;
  flex: 1;
}

.step-section {
  margin-bottom: 32px;
  padding: 20px;
  background: #f8fafc;
  border-radius: 12px;
  border: 2px solid #e2e8f0;
}

.step-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.step-number {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 16px;
}

.step-header h3 {
  font-size: 18px;
  font-weight: 700;
  margin: 0;
  color: #1e293b;
}

.help-text {
  color: #64748b;
  font-size: 14px;
  font-weight: 400;
}

.empty-hint {
  text-align: center;
  padding: 40px 20px;
  color: #64748b;
}

.empty-hint p {
  margin: 0;
  font-size: 15px;
}

.sessions-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 12px;
  margin-top: 12px;
}

.select-all-row {
  grid-column: 1 / -1;
  padding: 12px;
  background: white;
  border-radius: 8px;
  border: 2px solid #e2e8f0;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  font-weight: 600;
  color: #1e293b;
}

.checkbox-label input[type="checkbox"] {
  width: 18px;
  height: 18px;
  cursor: pointer;
}

.session-checkbox {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 12px;
  background: white;
  border: 2px solid #e2e8f0;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.session-checkbox:hover {
  border-color: #667eea;
  background: #f8f9ff;
}

.session-checkbox input[type="checkbox"] {
  margin-top: 4px;
  width: 18px;
  height: 18px;
  cursor: pointer;
}

.session-info {
  flex: 1;
}

.session-main {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}

.session-id {
  font-weight: 700;
  color: #7c3aed;
}

.session-screen {
  color: #0891b2;
  font-weight: 600;
  font-size: 14px;
}

.session-time {
  color: #64748b;
  font-size: 13px;
}

.ticket-types-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 12px;
  margin-top: 12px;
}

.ticket-type-checkbox {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: white;
  border: 2px solid #e2e8f0;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.ticket-type-checkbox:hover {
  border-color: #667eea;
  background: #f8f9ff;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
}

.ticket-type-checkbox input[type="checkbox"] {
  width: 18px;
  height: 18px;
  cursor: pointer;
}

.ticket-type-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.ticket-type-name {
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 6px;
  font-size: 15px;
}

.ticket-type-code {
  color: #6b7280;
  font-size: 12px;
  font-weight: 500;
  margin-top: 2px;
  padding: 2px 8px;
  background: #f3f4f6;
  border-radius: 4px;
  display: inline-block;
  width: fit-content;
}

.ticket-type-price {
  color: #059669;
  font-weight: 700;
  font-size: 13px;
  margin-top: 6px;
}

.ticket-type-checkbox:has(input:checked) {
  border-color: #667eea;
  background: linear-gradient(135deg, #667eea08 0%, #764ba208 100%);
}

.ticket-type-checkbox:has(input:checked) .ticket-type-name {
  color: #667eea;
}

.preview-section {
  margin-top: 32px;
  padding: 24px;
  background: linear-gradient(135deg, #667eea15 0%, #764ba215 100%);
  border-radius: 12px;
  border: 2px solid #667eea;
}

.preview-header h3 {
  font-size: 18px;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 16px 0;
}

.preview-stats {
  display: flex;
  justify-content: space-around;
  gap: 24px;
  margin-bottom: 16px;
}

.preview-stat {
  text-align: center;
}

.preview-stat.highlight {
  padding: 16px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.preview-label {
  display: block;
  color: #64748b;
  font-size: 14px;
  margin-bottom: 8px;
}

.preview-value {
  display: block;
  font-size: 24px;
  font-weight: 700;
  color: #2563eb;
}

.preview-value-large {
  font-size: 36px;
  font-weight: 800;
  color: #667eea;
}

.preview-unit {
  font-size: 18px;
  color: #64748b;
  margin-left: 4px;
}

.preview-detail {
  text-align: center;
}

.preview-formula {
  color: #64748b;
  font-size: 14px;
  margin: 0;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  font-weight: 600;
  color: #374151;
}

.required {
  color: #ef4444;
}

.form-input, .form-textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  font-size: 14px;
  font-family: inherit;
  transition: border-color 0.2s;
}

.form-input:focus, .form-textarea:focus {
  outline: none;
  border-color: #2563eb;
  box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1);
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 20px 24px;
  border-top: 1px solid #e2e8f0;
}

@media (max-width: 768px) {
  .header-right {
    flex-direction: column;
  }
  
  .tickets-table {
    font-size: 12px;
  }
  
  .tickets-table th,
  .tickets-table td {
    padding: 12px 8px;
  }
  
  .summary {
    flex-direction: column;
    gap: 12px;
  }
  
  .sessions-grid,
  .ticket-types-grid {
    grid-template-columns: 1fr;
  }
  
  .preview-stats {
    flex-direction: column;
  }
}
</style>