<template>
  <div class="batch-sessions-page">
    <!-- 頁面標題 + 返回按鈕 -->
    <div class="page-header">
      <div class="header-left">
        <button @click="goBack" class="btn-back">
          ← 返回批次列表
        </button>
        <h1>📋 場次管理</h1>
        <span class="batch-info">批次 #{{ batchId }}</span>
      </div>
      <button @click="showAddDialog = true" class="btn-primary">
        + 批次新增場次
      </button>
    </div>

    <!-- 操作工具列 -->
    <div class="toolbar">
      <div class="toolbar-left">
        <button @click="goToTickets" class="btn-navigate">
          🎫 前往票券管理
        </button>
      </div>
      <div class="toolbar-right">
        <button @click="loadSessions" class="btn-refresh">
          🔄 重新整理
        </button>
      </div>
    </div>

    <!-- 載入中 / 錯誤 / 空狀態 -->
    <div v-if="loading" class="loading">⏳ 載入場次中...</div>
    <div v-else-if="error" class="error-box">❌ {{ error }}</div>
    <div v-else-if="sessions.length === 0" class="empty-state">
      <p>🔭 此批次尚未新增場次</p>
      <button @click="showAddDialog = true" class="btn-secondary">
        新增第一個場次
      </button>
    </div>

    <!-- 場次列表 -->
    <div v-else class="sessions-table-container">
      <table class="sessions-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>電影</th>
            <th>影廳</th>
            <th>放映日期</th>
            <th>開始時間</th>
            <th>結束時間</th>
            <th>狀態</th>
            <th>錯誤訊息</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="session in sessions" :key="session.id">
            <td class="id-cell">{{ session.id }}</td>
            <td>
              <div class="movie-info">
                <strong>{{ getMovieName(session.movieId) }}</strong>
                <span class="id-badge">ID: {{ session.movieId }}</span>
              </div>
            </td>
            <td>
              <div class="screen-info">
                <strong>{{ getScreenName(session.screenId) }}</strong>
                <span class="id-badge">ID: {{ session.screenId }}</span>
              </div>
            </td>
            <td class="date-cell">
              {{ formatDate(session.showDate) }}
            </td>
            <td class="time-cell">
              {{ session.showTime }}
            </td>
            <td class="time-cell">
              {{ session.endTime }}
            </td>
            <td>
              <span :class="getStatusClass(session.status)">
                {{ getStatusText(session.status) }}
              </span>
            </td>
            <td class="error-cell">
              {{ session.errorMessage || '-' }}
            </td>
            <td class="action-cell">
              <button
                @click="editSession(session)"
                class="btn-icon btn-edit"
                title="編輯"
              >
                ✏️
              </button>
              <button
                @click="deleteSession(session)"
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
        <p>共 <strong>{{ sessions.length }}</strong> 個場次</p>
      </div>
    </div>

    <!-- 批次新增場次對話框 -->
    <div
      v-if="showAddDialog"
      class="modal-overlay"
      @click.self="closeDialogs"
    >
      <div class="modal modal-large">
        <div class="modal-header">
          <h2>📦 批次新增場次</h2>
          <button @click="closeDialogs" class="btn-close">×</button>
        </div>
        <div class="modal-body">
          <!-- 電影選擇 -->
          <div class="form-group">
            <label>電影 <span class="required">*</span></label>
            <select
              v-model.number="formData.movieId"
              class="form-input"
              required
              @change="calculateTimeslots"
            >
              <option value="" disabled>請選擇電影</option>
              <option v-for="m in movies" :key="m.id" :value="m.id">
                {{ m.id }} - {{ m.title }}
              </option>
            </select>
          </div>

          <!-- 多選影廳 -->
          <div class="form-group">
            <label>
              影廳 <span class="required">*</span>
              <span class="hint">（可複選多個影廳）</span>
            </label>
            <div class="checkbox-group">
              <label 
                v-for="s in screens" 
                :key="s.id" 
                class="checkbox-item"
              >
                <input
                  type="checkbox"
                  :value="s.id"
                  v-model="formData.selectedScreenIds"
                />
                <span class="checkbox-label">
                  {{ s.id }} - {{ s.name }}
                </span>
              </label>
            </div>
            <div v-if="formData.selectedScreenIds.length > 0" class="selected-info">
              已選擇 {{ formData.selectedScreenIds.length }} 個影廳
            </div>
          </div>

          <!-- 多選日期 -->
          <div class="form-group">
            <label>
              放映日期 <span class="required">*</span>
              <span class="hint">（可新增多個日期）</span>
            </label>
            <div class="date-input-group">
              <input
                v-model="tempDate"
                type="date"
                class="form-input date-input"
              />
              <button 
                @click="addDate" 
                class="btn-add-date"
                type="button"
                :disabled="!tempDate"
              >
                + 新增日期
              </button>
            </div>
            
            <!-- 已選日期列表 -->
            <div v-if="formData.selectedDates.length > 0" class="selected-dates">
              <div 
                v-for="(date, index) in formData.selectedDates" 
                :key="index"
                class="date-tag"
              >
                <span>{{ formatDate(date) }}</span>
                <button 
                  @click="removeDate(index)" 
                  class="btn-remove-date"
                  type="button"
                >
                  ×
                </button>
              </div>
            </div>
            <div v-if="formData.selectedDates.length > 0" class="selected-info">
              已選擇 {{ formData.selectedDates.length }} 個日期
            </div>
          </div>

          <div class="divider"></div>

          <!-- 時間自動計算設定 -->
          <div class="time-calc-section">
            <h3 class="section-title">⏰ 場次時間自動計算</h3>
            
            <div class="form-row">
              <div class="form-group half">
                <label>首場開始時間 <span class="required">*</span></label>
                <input
                  v-model="formData.firstShowTime"
                  type="time"
                  class="form-input"
                  @change="calculateTimeslots"
                />
              </div>
              <div class="form-group half">
                <label>最晚放映時間 <span class="required">*</span></label>
                <input
                  v-model="formData.lastShowTime"
                  type="time"
                  class="form-input"
                  @change="calculateTimeslots"
                />
                <div class="hint-text">最後一場開始時間不得超過此時間</div>
              </div>
            </div>

            <div class="form-row">
              <div class="form-group half">
                <label>片長（分鐘）<span class="required">*</span></label>
                <input
                  v-model.number="formData.movieDuration"
                  type="number"
                  class="form-input"
                  placeholder="例：120"
                  min="1"
                  @input="calculateTimeslots"
                />
              </div>
              <div class="form-group half">
                <label>打掃時間（分鐘）<span class="required">*</span></label>
                <input
                  v-model.number="formData.cleaningTime"
                  type="number"
                  class="form-input"
                  placeholder="例：10"
                  min="0"
                  @input="calculateTimeslots"
                />
              </div>
            </div>

            <!-- 時間規則說明 -->
            <div class="info-box">
              <div class="info-title">📌 自動計算規則</div>
              <ul class="info-list">
                <li>下一場開始時間 = 上一場結束時間 + 打掃時間</li>
                <li>開始時間會自動調整到 :00、:15、:30、:45</li>
                <li>例：上一場 10:20 結束，打掃 10 分鐘 → 下一場 10:45 開始</li>
                <li>系統會自動計算到最晚放映時間為止</li>
              </ul>
            </div>
          </div>

          <!-- 自動計算的時間段預覽 -->
          <div v-if="calculatedTimeslots.length > 0" class="timeslots-preview">
            <h3 class="section-title">
              🎬 自動計算的場次時間
              <span class="timeslot-count">(共 {{ calculatedTimeslots.length }} 場)</span>
            </h3>
            <div class="timeslots-grid">
              <div 
                v-for="(slot, index) in calculatedTimeslots" 
                :key="index"
                class="timeslot-card"
              >
                <div class="timeslot-number">第 {{ index + 1 }} 場</div>
                <div class="timeslot-time">
                  <span class="time-start">{{ slot.startTime }}</span>
                  <span class="time-arrow">→</span>
                  <span class="time-end">{{ slot.endTime }}</span>
                </div>
                <div class="timeslot-duration">
                  片長 {{ formData.movieDuration }} 分鐘
                </div>
              </div>
            </div>
          </div>

          <!-- 狀態 -->
          <div class="form-group">
            <label>狀態</label>
            <select v-model="formData.status" class="form-input">
              <option value="pending">待處理</option>
              <option value="processing">處理中</option>
              <option value="completed">已完成</option>
              <option value="failed">失敗</option>
            </select>
          </div>

          <!-- 預覽將要建立的場次 -->
          <div v-if="previewSessions.length > 0" class="preview-section">
            <h3>📋 預覽將建立的場次</h3>
            <div class="preview-summary">
              <div class="summary-item">
                <span class="summary-label">影廳數：</span>
                <span class="summary-value">{{ formData.selectedScreenIds.length }}</span>
              </div>
              <div class="summary-item">
                <span class="summary-label">日期數：</span>
                <span class="summary-value">{{ formData.selectedDates.length }}</span>
              </div>
              <div class="summary-item">
                <span class="summary-label">每日場次：</span>
                <span class="summary-value">{{ calculatedTimeslots.length }}</span>
              </div>
              <div class="summary-item total">
                <span class="summary-label">總場次數：</span>
                <span class="summary-value highlight">{{ previewSessions.length }}</span>
              </div>
            </div>
            <div class="preview-list">
              <div 
                v-for="(preview, index) in previewSessions.slice(0, 15)" 
                :key="index"
                class="preview-item"
              >
                <span class="preview-number">{{ index + 1 }}.</span>
                <span class="preview-movie">{{ getMovieName(preview.movieId) }}</span>
                <span class="preview-separator">→</span>
                <span class="preview-screen">{{ getScreenName(preview.screenId) }}</span>
                <span class="preview-separator">|</span>
                <span class="preview-date">{{ formatDate(preview.showDate) }}</span>
                <span class="preview-time">{{ preview.showTime }} - {{ preview.endTime }}</span>
              </div>
              <div v-if="previewSessions.length > 15" class="preview-more">
                ...還有 {{ previewSessions.length - 15 }} 個場次
              </div>
            </div>
          </div>
        </div>
        
        <div class="modal-footer">
          <button @click="closeDialogs" class="btn-secondary">取消</button>
          <button
            @click="batchAddSessions"
            class="btn-primary"
            :disabled="!isBatchFormValid || submitting"
          >
            <span v-if="submitting">處理中...</span>
            <span v-else>批次新增 ({{ previewSessions.length }} 個場次)</span>
          </button>
        </div>
      </div>
    </div>

    <!-- 編輯單個場次對話框 -->
    <div
      v-if="showEditDialog"
      class="modal-overlay"
      @click.self="closeDialogs"
    >
      <div class="modal">
        <div class="modal-header">
          <h2>編輯場次</h2>
          <button @click="closeDialogs" class="btn-close">×</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>電影 <span class="required">*</span></label>
            <select
              v-model.number="editFormData.movieId"
              class="form-input"
              required
            >
              <option value="" disabled>請選擇電影</option>
              <option v-for="m in movies" :key="m.id" :value="m.id">
                {{ m.id }} - {{ m.title }}
              </option>
            </select>
          </div>

          <div class="form-group">
            <label>影廳 <span class="required">*</span></label>
            <select
              v-model.number="editFormData.screenId"
              class="form-input"
              required
            >
              <option value="" disabled>請選擇影廳</option>
              <option v-for="s in screens" :key="s.id" :value="s.id">
                {{ s.id }} - {{ s.name }}
              </option>
            </select>
          </div>

          <div class="form-group">
            <label>放映日期 <span class="required">*</span></label>
            <input
              v-model="editFormData.showDate"
              type="date"
              class="form-input"
            />
          </div>

          <div class="form-row">
            <div class="form-group half">
              <label>開始時間 <span class="required">*</span></label>
              <input
                v-model="editFormData.showTime"
                type="time"
                class="form-input"
              />
            </div>
            <div class="form-group half">
              <label>結束時間 <span class="required">*</span></label>
              <input
                v-model="editFormData.endTime"
                type="time"
                class="form-input"
              />
            </div>
          </div>

          <div class="form-group">
            <label>狀態</label>
            <select v-model="editFormData.status" class="form-input">
              <option value="pending">待處理</option>
              <option value="processing">處理中</option>
              <option value="completed">已完成</option>
              <option value="failed">失敗</option>
            </select>
          </div>

          <div class="form-group">
            <label>錯誤訊息</label>
            <textarea
              v-model="editFormData.errorMessage"
              class="form-input"
              rows="3"
              placeholder="若有錯誤可填寫說明"
            ></textarea>
          </div>
        </div>
        <div class="modal-footer">
          <button @click="closeDialogs" class="btn-secondary">取消</button>
          <button
            @click="updateSession"
            class="btn-primary"
            :disabled="!isEditFormValid"
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
import { batchSessionTempService } from '../services/batchSessionTempService'
import { movieApi, screenApi } from '../services/movieApi'

const route = useRoute()
const router = useRouter()

// 取得路由上的 batchId
const batchId = computed(() => route.params.batchId)

const sessions = ref([])
const loading = ref(false)
const error = ref('')
const submitting = ref(false)

// 電影和影廳資料
const movies = ref([])
const screens = ref([])

const showAddDialog = ref(false)
const showEditDialog = ref(false)
const editingSession = ref(null)

// 批次新增表單
const formData = ref({
  movieId: '',
  selectedScreenIds: [],
  selectedDates: [],
  firstShowTime: '09:00',      // 首場時間
  lastShowTime: '00:30',       // 最晚放映時間（凌晨00:30）
  movieDuration: 120,          // 片長（分鐘）
  cleaningTime: 10,            // 打掃時間（分鐘）
  status: 'pending',
})

// 自動計算的時間段
const calculatedTimeslots = ref([])

// 臨時日期輸入
const tempDate = ref('')

// 編輯表單
const editFormData = ref({
  movieId: '',
  screenId: '',
  showDate: '',
  showTime: '',
  endTime: '',
  status: 'pending',
  errorMessage: '',
})

// 批次表單驗證
const isBatchFormValid = computed(() => {
  return (
    !!formData.value.movieId &&
    formData.value.selectedScreenIds.length > 0 &&
    formData.value.selectedDates.length > 0 &&
    !!formData.value.firstShowTime &&
    !!formData.value.lastShowTime &&
    !!formData.value.movieDuration &&
    formData.value.cleaningTime >= 0 &&
    calculatedTimeslots.value.length > 0
  )
})

// 編輯表單驗證
const isEditFormValid = computed(() => {
  return (
    !!editFormData.value.movieId &&
    !!editFormData.value.screenId &&
    !!editFormData.value.showDate &&
    !!editFormData.value.showTime &&
    !!editFormData.value.endTime
  )
})

// 將時間調整到最近的 :00, :15, :30, :45
const adjustToQuarterHour = (timeInMinutes) => {
  const quarters = [0, 15, 30, 45]
  const hour = Math.floor(timeInMinutes / 60)
  const minute = timeInMinutes % 60
  
  // 找到最近的15分鐘刻度（向上取整）
  let adjustedMinute = 0
  for (let q of quarters) {
    if (minute <= q) {
      adjustedMinute = q
      break
    }
  }
  
  // 如果分鐘數超過45，進位到下一小時
  if (minute > 45) {
    return (hour + 1) * 60
  }
  
  return hour * 60 + adjustedMinute
}

// 將時間字串轉換為分鐘數
const timeToMinutes = (timeStr) => {
  if (!timeStr) return 0
  const [hours, minutes] = timeStr.split(':').map(Number)
  return hours * 60 + minutes
}

// 將分鐘數轉換為時間字串
const minutesToTime = (minutes) => {
  const hours = Math.floor(minutes / 60) % 24
  const mins = minutes % 60
  return `${String(hours).padStart(2, '0')}:${String(mins).padStart(2, '0')}`
}

// 自動計算所有場次時間
const calculateTimeslots = () => {
  // 檢查必要欄位
  if (!formData.value.firstShowTime || 
      !formData.value.lastShowTime || 
      !formData.value.movieDuration ||
      formData.value.cleaningTime === null) {
    calculatedTimeslots.value = []
    return
  }

  const slots = []
  const firstShowMinutes = timeToMinutes(formData.value.firstShowTime)
  let lastShowMinutes = timeToMinutes(formData.value.lastShowTime)
  
  // 如果最晚時間小於首場時間，表示跨日（如凌晨00:30），加上24小時
  if (lastShowMinutes < firstShowMinutes) {
    lastShowMinutes += 24 * 60
  }

  const duration = formData.value.movieDuration
  const cleaning = formData.value.cleaningTime

  let currentStartTime = firstShowMinutes

  // 持續計算直到超過最晚放映時間
  while (currentStartTime <= lastShowMinutes) {
    const startTime = minutesToTime(currentStartTime)
    const endTime = minutesToTime(currentStartTime + duration)
    
    slots.push({
      startTime,
      endTime
    })

    // 計算下一場開始時間：當前結束時間 + 打掃時間，然後調整到整刻
    const nextStartRaw = currentStartTime + duration + cleaning
    const nextStartAdjusted = adjustToQuarterHour(nextStartRaw)
    
    // 如果調整後的時間等於原時間，說明已經在整刻上，跳到下一個整刻
    if (nextStartAdjusted === nextStartRaw) {
      currentStartTime = nextStartAdjusted
    } else {
      currentStartTime = nextStartAdjusted
    }

    // 防止無限迴圈
    if (slots.length > 50) {
      console.warn('場次數量超過50場，停止計算')
      break
    }
  }

  calculatedTimeslots.value = slots
  console.log('✅ 計算完成，共', slots.length, '場')
}

// 預覽將要建立的場次
const previewSessions = computed(() => {
  if (!isBatchFormValid.value) return []
  
  const sessions = []
  
  // 遍歷所有選擇的日期
  for (const date of formData.value.selectedDates) {
    // 遍歷所有選擇的影廳
    for (const screenId of formData.value.selectedScreenIds) {
      // 遍歷所有計算出的時間段
      for (const slot of calculatedTimeslots.value) {
        sessions.push({
          movieId: formData.value.movieId,
          screenId: screenId,
          showDate: date,
          showTime: slot.startTime,
          endTime: slot.endTime,
          status: formData.value.status,
        })
      }
    }
  }
  
  return sessions
})

// 新增日期
const addDate = () => {
  if (!tempDate.value) return
  
  // 檢查是否已存在
  if (formData.value.selectedDates.includes(tempDate.value)) {
    Swal.fire({
      icon: 'warning',
      title: '重複日期',
      text: '此日期已經在列表中',
      timer: 2000,
    })
    return
  }
  
  formData.value.selectedDates.push(tempDate.value)
  // 排序日期
  formData.value.selectedDates.sort()
  tempDate.value = ''
}

// 移除日期
const removeDate = (index) => {
  formData.value.selectedDates.splice(index, 1)
}

// 取得電影名稱
const getMovieName = (movieId) => {
  const movie = movies.value.find(m => m.id === movieId)
  return movie ? movie.title : `未知電影(${movieId})`
}

// 取得影廳名稱
const getScreenName = (screenId) => {
  const screen = screens.value.find(s => s.id === screenId)
  return screen ? screen.name : `未知影廳(${screenId})`
}

// 載入電影和影廳資料
const loadMoviesAndScreens = async () => {
  try {
    const [moviesRes, screensRes] = await Promise.all([
      movieApi.getAll(),
      screenApi.getAll(),
    ])
    
    movies.value = moviesRes.data || []
    screens.value = screensRes.data || []
    
    console.log('✅ 載入成功:', {
      電影數量: movies.value.length,
      影廳數量: screens.value.length
    })
  } catch (err) {
    console.error('❌ 載入電影或影廳資料失敗:', err)
    Swal.fire({
      icon: 'error',
      title: '載入失敗',
      text: '無法載入電影或影廳資料',
    })
  }
}

// 載入場次列表
const loadSessions = async () => {
  if (!batchId.value) return

  loading.value = true
  error.value = ''

  try {
    const response = await batchSessionTempService.getByBatchId(batchId.value)
    
    if (response.data.success) {
      sessions.value = response.data.data || []
    } else {
      error.value = response.data.message || '載入失敗'
    }
  } catch (err) {
    console.error(err)
    error.value = err.response?.data?.message || err.message
  } finally {
    loading.value = false
  }
}

// 批次新增場次
const batchAddSessions = async () => {
  if (!isBatchFormValid.value) {
    Swal.fire({
      icon: 'warning',
      title: '資料不完整',
      text: '請填寫所有必填欄位',
    })
    return
  }

  // 確認對話框
  const result = await Swal.fire({
    icon: 'question',
    title: '確認批次新增',
    html: `
      <div style="text-align: left; padding: 10px;">
        <p><strong>電影：</strong>${getMovieName(formData.value.movieId)}</p>
        <p><strong>影廳數量：</strong>${formData.value.selectedScreenIds.length} 個</p>
        <p><strong>日期數量：</strong>${formData.value.selectedDates.length} 個</p>
        <p><strong>每日場次：</strong>${calculatedTimeslots.value.length} 場</p>
        <p><strong>首場時間：</strong>${formData.value.firstShowTime}</p>
        <p><strong>末場時間：</strong>${calculatedTimeslots.value[calculatedTimeslots.value.length - 1]?.startTime || '-'}</p>
        <hr>
        <p style="color: #2563eb; font-size: 18px;"><strong>總共將建立：${previewSessions.value.length} 個場次</strong></p>
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

    // 逐一建立場次
    for (const session of previewSessions.value) {
      try {
        const response = await batchSessionTempService.create({
          batchId: parseInt(batchId.value, 10),
          movieId: session.movieId,
          screenId: session.screenId,
          showDate: session.showDate,
          showTime: session.showTime,
          endTime: session.endTime,
          status: session.status,
          errorMessage: null,
        })

        if (response.data.success) {
          successCount++
        } else {
          failCount++
          errors.push(`${formatDate(session.showDate)} ${session.showTime} - 影廳${session.screenId}: ${response.data.message}`)
        }
      } catch (err) {
        failCount++
        errors.push(`${formatDate(session.showDate)} ${session.showTime} - 影廳${session.screenId}: ${err.message}`)
      }
    }

    // 顯示結果
    if (failCount === 0) {
      await Swal.fire({
        icon: 'success',
        title: '批次新增成功',
        html: `成功建立 <strong>${successCount}</strong> 個場次`,
      })
    } else {
      await Swal.fire({
        icon: 'warning',
        title: '部分新增失敗',
        html: `
          <p>成功：${successCount} 個</p>
          <p>失敗：${failCount} 個</p>
          ${errors.length > 0 ? `<div style="text-align: left; max-height: 200px; overflow-y: auto; margin-top: 10px; padding: 10px; background: #fee; border-radius: 4px;">
            <strong>錯誤詳情：</strong><br>
            ${errors.slice(0, 5).map(e => `• ${e}`).join('<br>')}
            ${errors.length > 5 ? `<br>...還有 ${errors.length - 5} 個錯誤` : ''}
          </div>` : ''}
        `,
      })
    }

    closeDialogs()
    await loadSessions()

  } catch (err) {
    console.error(err)
    Swal.fire({
      icon: 'error',
      title: '批次新增失敗',
      text: err.message,
    })
  } finally {
    submitting.value = false
  }
}

// 點「編輯」時填入 form
const editSession = (session) => {
  editingSession.value = session
  editFormData.value = {
    movieId: session.movieId,
    screenId: session.screenId,
    showDate: session.showDate,
    showTime: session.showTime,
    endTime: session.endTime,
    status: session.status || 'pending',
    errorMessage: session.errorMessage || '',
  }
  showEditDialog.value = true
}

// 更新場次
const updateSession = async () => {
  try {
    const response = await batchSessionTempService.update(editingSession.value.id, {
      batchId: parseInt(batchId.value, 10),
      movieId: editFormData.value.movieId,
      screenId: editFormData.value.screenId,
      showDate: editFormData.value.showDate,
      showTime: editFormData.value.showTime,
      endTime: editFormData.value.endTime,
      status: editFormData.value.status || 'pending',
      errorMessage: editFormData.value.errorMessage || null,
    })

    if (response.data.success) {
      closeDialogs()
      await loadSessions()

      Swal.fire({
        icon: 'success',
        title: '更新成功',
        text: response.data.message,
      })
    } else {
      throw new Error(response.data.message)
    }
  } catch (err) {
    console.error(err)
    Swal.fire({
      icon: 'error',
      title: '更新失敗',
      text: err.response?.data?.message || err.message,
    })
  }
}

// 刪除場次
const deleteSession = async (session) => {
  const result = await Swal.fire({
    icon: 'warning',
    title: `確定刪除場次 #${session.id}？`,
    text: `${getMovieName(session.movieId)} - ${getScreenName(session.screenId)} - ${formatDate(session.showDate)}`,
    showCancelButton: true,
    confirmButtonText: '刪除',
    cancelButtonText: '取消',
    confirmButtonColor: '#dc2626',
  })

  if (!result.isConfirmed) return

  try {
    const response = await batchSessionTempService.remove(session.id)
    
    if (response.data.success) {
      await loadSessions()

      Swal.fire({
        icon: 'success',
        title: '刪除成功',
        text: response.data.message,
      })
    } else {
      throw new Error(response.data.message)
    }
  } catch (err) {
    console.error(err)
    Swal.fire({
      icon: 'error',
      title: '刪除失敗',
      text: err.response?.data?.message || err.message,
    })
  }
}

// 導向票券管理頁
const goToTickets = () => {
  router.push({
    name: 'BatchTicketTemp',
    params: { batchId: batchId.value },
  })
}

// 返回批次列表
const goBack = () => {
  router.push({ name: 'BatchOperationList' })
}

// 關閉對話框 & reset 表單
const closeDialogs = () => {
  showAddDialog.value = false
  showEditDialog.value = false
  editingSession.value = null
  
  // 重置批次新增表單
  formData.value = {
    movieId: '',
    selectedScreenIds: [],
    selectedDates: [],
    firstShowTime: '09:00',
    lastShowTime: '00:30',
    movieDuration: 120,
    cleaningTime: 10,
    status: 'pending',
  }
  calculatedTimeslots.value = []
  tempDate.value = ''
  
  // 重置編輯表單
  editFormData.value = {
    movieId: '',
    screenId: '',
    showDate: '',
    showTime: '',
    endTime: '',
    status: 'pending',
    errorMessage: '',
  }
}

// 工具：顯示日期
const formatDate = (date) => {
  if (!date) return '-'
  return date
}

// 工具：狀態樣式
const getStatusClass = (status) => {
  const statusMap = {
    'pending': 'status-pending',
    'processing': 'status-processing',
    'completed': 'status-completed',
    'failed': 'status-failed'
  }
  return statusMap[status] || 'status-default'
}

// 工具：狀態文字
const getStatusText = (status) => {
  const statusMap = {
    'pending': '待處理',
    'processing': '處理中',
    'completed': '已完成',
    'failed': '失敗'
  }
  return statusMap[status] || status || '-'
}

// 初始化
onMounted(async () => {
  if (!batchId.value) {
    router.push({ name: 'BatchOperationList' })
    return
  }
  
  // 同時載入電影、影廳和場次資料
  await Promise.all([
    loadMoviesAndScreens(),
    loadSessions()
  ])
})
</script>

<style scoped>
.batch-sessions-page {
  max-width: 1600px;
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
  background: #7c3aed;
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
  background: #6d28d9;
  transform: translateY(-1px);
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

.sessions-table-container {
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  overflow: hidden;
}

.sessions-table {
  width: 100%;
  border-collapse: collapse;
}

.sessions-table thead {
  background: #f8fafc;
}

.sessions-table th {
  padding: 16px;
  text-align: left;
  font-size: 13px;
  font-weight: 700;
  color: #475569;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  border-bottom: 2px solid #e2e8f0;
}

.sessions-table td {
  padding: 16px;
  border-bottom: 1px solid #f1f5f9;
  font-size: 14px;
  color: #1e293b;
}

.sessions-table tbody tr:hover {
  background: #f8fafc;
}

.sessions-table tbody tr:last-child td {
  border-bottom: none;
}

.id-cell {
  font-weight: 700;
  color: #2563eb;
}

.movie-info, .screen-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.id-badge {
  font-size: 11px;
  color: #64748b;
  background: #f1f5f9;
  padding: 2px 8px;
  border-radius: 4px;
  display: inline-block;
  width: fit-content;
}

.date-cell {
  color: #64748b;
  font-size: 13px;
  font-weight: 600;
}

.time-cell {
  color: #64748b;
  font-size: 13px;
  font-family: 'Courier New', monospace;
}

.error-cell {
  color: #dc2626;
  font-size: 12px;
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.status-pending {
  background: #fef3c7;
  color: #92400e;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
}

.status-processing {
  background: #dbeafe;
  color: #1e40af;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
}

.status-completed {
  background: #d1fae5;
  color: #065f46;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
}

.status-failed {
  background: #fee2e2;
  color: #991b1b;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
}

.status-default {
  background: #f1f5f9;
  color: #64748b;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
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
  padding: 16px;
  background: #f8fafc;
  border-top: 1px solid #e2e8f0;
  text-align: center;
  color: #64748b;
  font-size: 14px;
}

.summary strong {
  color: #2563eb;
  font-size: 18px;
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
  overflow: hidden;
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

.hint {
  font-size: 12px;
  font-weight: 400;
  color: #64748b;
  margin-left: 4px;
}

.hint-text {
  margin-top: 4px;
  font-size: 12px;
  color: #64748b;
}

.form-input {
  width: 100%;
  padding: 12px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  font-size: 14px;
  font-family: inherit;
  transition: border-color 0.2s;
  box-sizing: border-box;
}

.form-input:focus {
  outline: none;
  border-color: #2563eb;
  box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1);
}

textarea.form-input {
  resize: vertical;
  min-height: 80px;
}

select.form-input {
  cursor: pointer;
}

.form-row {
  display: flex;
  gap: 16px;
}

.form-row .half {
  flex: 1;
}

.checkbox-group {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 12px;
  padding: 12px;
  background: #f8fafc;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  max-height: 200px;
  overflow-y: auto;
}

.checkbox-item {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 6px;
  transition: background 0.2s;
}

.checkbox-item:hover {
  background: white;
}

.checkbox-item input[type="checkbox"] {
  width: 18px;
  height: 18px;
  cursor: pointer;
}

.checkbox-label {
  font-size: 14px;
  color: #1e293b;
}

.date-input-group {
  display: flex;
  gap: 12px;
}

.date-input {
  flex: 1;
}

.btn-add-date {
  background: #10b981;
  color: white;
  border: none;
  padding: 12px 20px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  white-space: nowrap;
}

.btn-add-date:hover:not(:disabled) {
  background: #059669;
}

.btn-add-date:disabled {
  background: #94a3b8;
  cursor: not-allowed;
}

.selected-dates {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 12px;
  padding: 12px;
  background: #f8fafc;
  border-radius: 8px;
  min-height: 50px;
}

.date-tag {
  display: flex;
  align-items: center;
  gap: 6px;
  background: #2563eb;
  color: white;
  padding: 6px 12px;
  border-radius: 6px;
  font-size: 13px;
  font-weight: 500;
}

.btn-remove-date {
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border: none;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  cursor: pointer;
  font-size: 16px;
  line-height: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.2s;
}

.btn-remove-date:hover {
  background: rgba(255, 255, 255, 0.3);
}

.selected-info {
  margin-top: 8px;
  font-size: 13px;
  color: #2563eb;
  font-weight: 600;
}

.divider {
  height: 1px;
  background: #e2e8f0;
  margin: 32px 0;
}

.time-calc-section {
  background: #fefce8;
  padding: 20px;
  border-radius: 12px;
  border: 2px solid #fde047;
}

.section-title {
  margin: 0 0 16px 0;
  font-size: 18px;
  font-weight: 700;
  color: #1e293b;
}

.timeslot-count {
  color: #2563eb;
  font-size: 16px;
}

.info-box {
  background: white;
  padding: 16px;
  border-radius: 8px;
  border-left: 4px solid #3b82f6;
  margin-top: 16px;
}

.info-title {
  font-weight: 600;
  color: #1e40af;
  margin-bottom: 8px;
}

.info-list {
  margin: 0;
  padding-left: 20px;
  color: #475569;
  font-size: 13px;
  line-height: 1.8;
}

.timeslots-preview {
  margin-top: 24px;
  padding: 20px;
  background: #f0fdf4;
  border: 2px solid #86efac;
  border-radius: 12px;
}

.timeslots-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 12px;
  margin-top: 16px;
  max-height: 400px;
  overflow-y: auto;
  padding: 8px;
}

.timeslot-card {
  background: white;
  padding: 16px;
  border-radius: 8px;
  border: 2px solid #10b981;
  text-align: center;
  transition: all 0.2s;
}

.timeslot-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.2);
}

.timeslot-number {
  font-size: 12px;
  color: #059669;
  font-weight: 600;
  margin-bottom: 8px;
}

.timeslot-time {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  margin-bottom: 8px;
}

.time-start {
  font-size: 16px;
  font-weight: 700;
  color: #1e293b;
  font-family: 'Courier New', monospace;
}

.time-arrow {
  color: #64748b;
  font-size: 14px;
}

.time-end {
  font-size: 14px;
  font-weight: 600;
  color: #64748b;
  font-family: 'Courier New', monospace;
}

.timeslot-duration {
  font-size: 11px;
  color: #64748b;
}

.preview-section {
  margin-top: 24px;
  padding: 20px;
  background: #f0f9ff;
  border: 2px solid #bfdbfe;
  border-radius: 12px;
}

.preview-section h3 {
  margin: 0 0 16px 0;
  color: #1e40af;
  font-size: 16px;
}

.preview-summary {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 12px;
  margin-bottom: 16px;
  padding: 16px;
  background: white;
  border-radius: 8px;
}

.summary-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.summary-label {
  font-size: 12px;
  color: #64748b;
}

.summary-value {
  font-size: 20px;
  font-weight: 700;
  color: #1e293b;
}

.summary-item.total {
  grid-column: span 2;
}

.highlight {
  color: #2563eb;
  font-size: 28px;
}

.preview-list {
  max-height: 300px;
  overflow-y: auto;
  background: white;
  border-radius: 8px;
  padding: 12px;
}

.preview-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px;
  border-bottom: 1px solid #f1f5f9;
  font-size: 13px;
}

.preview-item:last-child {
  border-bottom: none;
}

.preview-number {
  color: #64748b;
  font-weight: 600;
  min-width: 30px;
}

.preview-movie {
  color: #2563eb;
  font-weight: 600;
}

.preview-screen {
  color: #7c3aed;
  font-weight: 600;
}

.preview-date {
  color: #64748b;
}

.preview-time {
  color: #64748b;
  font-family: 'Courier New', monospace;
  font-size: 12px;
}

.preview-separator {
  color: #cbd5e1;
}

.preview-more {
  text-align: center;
  padding: 12px;
  color: #64748b;
  font-style: italic;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 20px 24px;
  border-top: 1px solid #e2e8f0;
}

@media (max-width: 768px) {
  .sessions-table {
    font-size: 12px;
  }
  
  .sessions-table th,
  .sessions-table td {
    padding: 12px 8px;
  }

  .error-cell {
    max-width: 100px;
  }
  
  .checkbox-group {
    grid-template-columns: 1fr;
  }
  
  .form-row {
    flex-direction: column;
  }
  
  .timeslots-grid {
    grid-template-columns: 1fr;
  }
  
  .preview-summary {
    grid-template-columns: 1fr;
  }
  
  .summary-item.total {
    grid-column: span 1;
  }
}
</style>