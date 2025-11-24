<template>
  <div class="ticket-package-system">
    <!-- 頁面標題 -->
    <div class="page-header">
      <h1>票種套餐管理系統</h1>
    </div>

    <!-- 票種設定區塊 -->
    <el-card shadow="hover" style="margin-bottom: 20px;">
      <template #header>
        <div class="card-header">
          <span class="card-title">票種設定</span>
          <el-button 
            size="small" 
            @click="toggleSection('ticket')"
          >
            {{ collapsedSections.ticket ? '展開' : '收合' }}
          </el-button>
        </div>
      </template>
      
      <el-collapse-transition>
        <div v-show="!collapsedSections.ticket">
          <!-- 編輯模式提示 -->
          <el-alert
            v-if="ticketForm.id"
            title="編輯模式"
            type="warning"
            :closable="false"
            style="margin-bottom: 20px;"
          >
            <template #default>
              正在編輯票種：{{ ticketForm.packageName }}（{{ ticketForm.packageCode }}）
            </template>
          </el-alert>

          <el-form :model="ticketForm" label-width="120px" label-position="right">
            <!-- 基本資訊 -->
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="套餐類型" required>
                  <el-select v-model="ticketForm.packageType" placeholder="請選擇套餐類型" style="width: 100%;">
                    <el-option label="套票組合" value="bundle ticket"></el-option>
                    <el-option label="單一票種" value="single ticket"></el-option>
                    <el-option label="特殊票種" value="special ticket"></el-option>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="套餐代碼" required>
                  <el-input v-model="ticketForm.packageCode" placeholder="輸入套餐代碼"></el-input>
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="套餐名稱" required>
                  <el-input v-model="ticketForm.packageName" placeholder="輸入套餐名稱"></el-input>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="顯示順序">
                  <el-input-number v-model="ticketForm.displayOrder" :min="1" style="width: 100%;"></el-input-number>
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="20">
              <el-col :span="24">
                <el-form-item label="圖片網址">
                  <el-input v-model="ticketForm.imageUrl" placeholder="輸入圖片URL"></el-input>
                </el-form-item>
              </el-col>
            </el-row>

            <!-- 有效時間 -->
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="有效開始時間">
                  <el-date-picker
                    v-model="ticketForm.validFrom"
                    type="datetime"
                    placeholder="選擇開始時間"
                    style="width: 100%;"
                    value-format="YYYY-MM-DD HH:mm:ss"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="有效結束時間">
                  <el-date-picker
                    v-model="ticketForm.validUntil"
                    type="datetime"
                    placeholder="選擇結束時間"
                    style="width: 100%;"
                    value-format="YYYY-MM-DD HH:mm:ss"
                  />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="價格調整" required>
                  <el-input-number v-model="ticketForm.priceAdjustment" :step="10" style="width: 100%;"></el-input-number>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="早場調整">
                  <el-input-number 
                    v-model="ticketForm.earlyBirdAdjustment" 
                    :step="10" 
                    :disabled="!canEnableEarlyBird"
                    style="width: 100%;"
                  ></el-input-number>
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="啟用早場">
                  <el-switch 
                    v-model="ticketForm.enableEarlyBird"
                    :disabled="ticketForm.packageType !== 'special ticket'"
                  ></el-switch>
                  <el-text
                    v-if="ticketForm.packageType !== 'special ticket'"
                    type="info"
                    size="small"
                    style="margin-left: 10px;"
                  >
                    僅特殊票種可啟用早場優惠
                  </el-text>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="啟用套餐">
                  <el-switch v-model="ticketForm.isActive"></el-switch>
                </el-form-item>
              </el-col>
            </el-row>

            <!-- 套票內容設定 -->
            <el-divider content-position="left">
              <span style="font-weight: 600; color: #409eff;">套票內容設定</span>
            </el-divider>

            <div
              v-for="(item, index) in ticketForm.packageItems"
              :key="index"
              style="margin-bottom: 16px;"
            >
              <el-row :gutter="10" align="middle">
                <el-col :span="5">
                  <el-select v-model="item.itemType" placeholder="選擇類型" size="default">
                    <el-option label="票券" value="ticket"></el-option>
                    <el-option label="爆米花" value="popcorn"></el-option>
                    <el-option label="飲料" value="drink"></el-option>
                    <el-option label="咖啡" value="coffee"></el-option>
                    <el-option label="點心" value="snack"></el-option>
                  </el-select>
                </el-col>
                <el-col :span="7">
                  <el-input v-model="item.itemName" placeholder="商品名稱" size="default"></el-input>
                </el-col>
                <el-col :span="5">
                  <el-input v-model="item.itemSpec" placeholder="規格（選填）" size="default"></el-input>
                </el-col>
                <el-col :span="4">
                  <el-input-number 
                    v-model="item.quantity" 
                    :min="1" 
                    size="default"
                    controls-position="right"
                    style="width: 100%;"
                  ></el-input-number>
                </el-col>
                <el-col :span="3">
                  <el-button 
                    type="danger" 
                    circle 
                    size="default"
                    @click="removePackageItem(index)"
                    v-if="ticketForm.packageItems.length > 1"
                  >
                    ×
                  </el-button>
                </el-col>
              </el-row>
            </div>

            <el-button type="primary" plain @click="addPackageItem">
              + 新增項目
            </el-button>

            <el-divider></el-divider>

            <el-form-item>
              <el-button type="primary" size="large" @click="saveTicket">
                {{ ticketForm.id ? '更新票種' : '新增票種' }}
              </el-button>
              <el-button size="large" @click="resetTicketForm">
                {{ ticketForm.id ? '取消編輯' : '重置' }}
              </el-button>
            </el-form-item>
          </el-form>
        </div>
      </el-collapse-transition>
    </el-card>

    <!-- 票種列表顯示 -->
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span class="card-title">票種列表</span>
          <el-button type="primary" size="small" @click="fetchTicketList">
            🔄 重新整理
          </el-button>
        </div>
      </template>

      <div v-if="ticketList.length === 0" class="empty-state">
        <el-empty description="尚未建立任何票種，請在上方「票種設定」區塊新增"></el-empty>
      </div>

      <el-row v-else :gutter="20">
        <el-col 
          v-for="ticket in ticketList" 
          :key="ticket.id" 
          :xs="24" 
          :sm="12" 
          :md="8" 
          :lg="8"
          style="margin-bottom: 20px;"
        >
          <el-card class="ticket-card" shadow="hover">
            <!-- 票種標題 -->
            <div class="ticket-header">
              <div class="ticket-title-area">
                <h3>{{ ticket.packageName }}</h3>
                <el-tag size="small" type="info">{{ ticket.packageCode }}</el-tag>
              </div>
              <div class="ticket-badges">
                <el-tag :type="getPackageTypeTag(ticket.packageType)" size="small">
                  {{ getPackageTypeText(ticket.packageType) }}
                </el-tag>
                <el-tag :type="ticket.isActive ? 'success' : 'danger'" size="small">
                  {{ ticket.isActive ? '啟用中' : '已停用' }}
                </el-tag>
                <el-tag v-if="ticket.enableEarlyBird" type="warning" size="small">早場</el-tag>
              </div>
            </div>

            <!-- 價格資訊 -->
            <el-divider></el-divider>
            <div class="price-section">
              <div class="price-item">
                <span class="label">價格調整：</span>
                <span class="value">{{ ticket.priceAdjustment >= 0 ? '+' : '' }}{{ ticket.priceAdjustment }} 元</span>
              </div>
              <div v-if="ticket.enableEarlyBird" class="price-item">
                <span class="label">早場調整：</span>
                <span class="value">{{ ticket.earlyBirdAdjustment >= 0 ? '+' : '' }}{{ ticket.earlyBirdAdjustment }} 元</span>
              </div>
            </div>

            <!-- 套票內容 -->
            <el-divider></el-divider>
            <div class="package-items">
              <div class="section-label">套票內容：</div>
              <div class="items-container">
                <el-tag 
                  v-for="(item, index) in (ticket.packageItems || [])" 
                  :key="index"
                  class="item-tag"
                  type="info"
                  effect="plain"
                >
                  {{ item.itemName }}
                  <span v-if="item.itemSpec">({{ item.itemSpec }})</span>
                  × {{ item.quantity }}
                </el-tag>
              </div>
            </div>

            <!-- 有效期間 -->
            <div v-if="ticket.validFrom || ticket.validUntil">
              <el-divider></el-divider>
              <div class="validity-info">
                <div class="section-label">有效期間：</div>
                <div style="font-size: 13px; color: #606266; margin-top: 4px;">
                  <div v-if="ticket.validFrom">開始：{{ formatDateTime(ticket.validFrom) }}</div>
                  <div v-if="ticket.validUntil">結束：{{ formatDateTime(ticket.validUntil) }}</div>
                </div>
              </div>
            </div>

            <!-- 操作按鈕 -->
            <el-divider></el-divider>
            <div class="card-actions">
              <el-button type="primary" size="small" @click="editTicket(ticket)">編輯</el-button>
              <el-button type="danger" size="small" @click="removeTicket(ticket)">刪除</el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import ticketPackageService from '../services/ticketPackageService'

// 收合狀態
const collapsedSections = ref({
  ticket: false
})

// 票種表單
const ticketForm = ref({
  id: null,
  packageType: '',
  packageName: '',
  packageCode: '',
  priceAdjustment: 0,
  earlyBirdAdjustment: 0,
  imageUrl: '',
  enableEarlyBird: false,
  isActive: true,
  displayOrder: 1,
  validFrom: null,
  validUntil: null,
  packageItems: [
    {
      itemType: '',
      itemName: '',
      itemSpec: '',
      quantity: 1,
      displayOrder: 1
    }
  ]
})

// 票種列表
const ticketList = ref([])

// 判斷是否可以啟用早場優惠（僅特殊票種可以）
const canEnableEarlyBird = computed(() => {
  return ticketForm.value.packageType === 'special ticket' && ticketForm.value.enableEarlyBird
})

// 監聽套餐類型變化
watch(() => ticketForm.value.packageType, (newType) => {
  // 只有特殊票種可以啟用早場，其他類型強制關閉
  if (newType !== 'special ticket') {
    ticketForm.value.enableEarlyBird = false
    ticketForm.value.earlyBirdAdjustment = 0
  }
})

// 小工具：處理日期（空值 → null；有空白就換成 T）
function normalizeDate(val) {
  if (!val) return null
  return val.includes('T') ? val : val.replace(' ', 'T')
}

// ⭐ 小工具：解析 packageItems（可能是字串或陣列）
function parsePackageItems(packageItems) {
  if (!packageItems) return []
  
  if (typeof packageItems === 'string') {
    try {
      return JSON.parse(packageItems)
    } catch (e) {
      console.error('Failed to parse packageItems:', e)
      return []
    }
  }
  
  if (Array.isArray(packageItems)) {
    return packageItems
  }
  
  return []
}

// 載入票種列表（CRUD: Read）
async function fetchTicketList() {
  console.log('===== 開始載入票種列表 =====')
  
  try {
    console.log('📡 呼叫 API: GET /api/ticket-packages')
    const res = await ticketPackageService.list()
    
    console.log('✅ API 回應成功:', res)
    
    if (res && res.data) {
      console.log('資料筆數:', res.data.length)
      
      // ⭐ 處理每個票種的 packageItems（可能是 JSON 字串）
      ticketList.value = res.data.map(ticket => ({
        ...ticket,
        packageItems: parsePackageItems(ticket.packageItems)
      }))
      
      console.log('✅ 處理後的票種列表:', ticketList.value)
      ElMessage.success('票種列表載入成功')
    } else {
      console.warn('⚠️ API 沒有回傳資料')
      ticketList.value = []
    }
  } catch (error) {
    console.error('===== ❌ 載入票種列表失敗 =====')
    console.error('錯誤:', error)
    console.error('錯誤回應:', error.response?.data)
    ElMessage.error('載入票種列表失敗')
    ticketList.value = []
  }
}

// UI 方法
function toggleSection(section) {
  collapsedSections.value[section] = !collapsedSections.value[section]
}

function addPackageItem() {
  ticketForm.value.packageItems.push({
    itemType: '',
    itemName: '',
    itemSpec: '',
    quantity: 1,
    displayOrder: ticketForm.value.packageItems.length + 1
  })
}

function removePackageItem(index) {
  ticketForm.value.packageItems.splice(index, 1)
}

// 🔧 修復：儲存票種（CRUD: Create / Update）
async function saveTicket() {
  console.log('===== 開始儲存票種 =====')
  
  // 驗證必填欄位
  if (!ticketForm.value.packageType || !ticketForm.value.packageName || !ticketForm.value.packageCode) {
    ElMessage.warning('請填寫所有必填欄位')
    return
  }

  // 驗證套票內容
  const hasInvalidItem = ticketForm.value.packageItems.some(item => 
    !item.itemType || !item.itemName
  )
  if (hasInvalidItem) {
    ElMessage.warning('請完整填寫套票內容')
    return
  }

  // 🔧 整理 packageItems，移除可能的 id 欄位（避免後端誤判）
  const cleanedPackageItems = ticketForm.value.packageItems.map((item, idx) => ({
    itemType: item.itemType,
    itemName: item.itemName,
    itemSpec: item.itemSpec || '',
    quantity: item.quantity || 1,
    displayOrder: item.displayOrder || idx + 1
  }))

  // 🔧 整理要送給後端的資料（不包含 id，因為 id 在 URL 裡）
  const payload = {
    packageType: ticketForm.value.packageType,
    packageName: ticketForm.value.packageName,
    packageCode: ticketForm.value.packageCode,
    priceAdjustment: ticketForm.value.priceAdjustment || 0,
    earlyBirdAdjustment: ticketForm.value.earlyBirdAdjustment || 0,
    enableEarlyBird: ticketForm.value.enableEarlyBird || false,
    isActive: ticketForm.value.isActive !== undefined ? ticketForm.value.isActive : true,
    displayOrder: ticketForm.value.displayOrder || 1,
    validFrom: normalizeDate(ticketForm.value.validFrom),
    validUntil: normalizeDate(ticketForm.value.validUntil),
    imageUrl: ticketForm.value.imageUrl || '',
    packageItems: cleanedPackageItems
  }

  // 清除空值
  if (!payload.validFrom) delete payload.validFrom
  if (!payload.validUntil) delete payload.validUntil
  if (!payload.imageUrl) delete payload.imageUrl

  console.log('📦 送出的資料:', JSON.stringify(payload, null, 2))

  try {
    if (ticketForm.value.id) {
      // 🔧 Update - 不要把 id 放在 payload 裡
      console.log('📡 執行更新: PUT /api/ticket-packages/' + ticketForm.value.id)
      const response = await ticketPackageService.update(ticketForm.value.id, payload)
      console.log('✅ 更新回應:', response)
      ElMessage.success('票種更新成功！')
    } else {
      // Create
      console.log('📡 執行新增: POST /api/ticket-packages')
      const response = await ticketPackageService.create(payload)
      console.log('✅ 新增回應:', response)
      ElMessage.success('票種新增成功！')
    }
    
    resetTicketForm()
    await fetchTicketList()
  } catch (error) {
    console.error('===== ❌ 儲存失敗 =====')
    console.error('錯誤:', error)
    console.error('錯誤訊息:', error.message)
    console.error('錯誤回應:', error.response?.data)
    console.error('錯誤狀態:', error.response?.status)
    
    // 🔧 顯示更詳細的錯誤訊息
    const errorMsg = error.response?.data?.message || error.response?.data || error.message || '未知錯誤'
    ElMessage.error(`儲存失敗：${errorMsg}`)
  }
}

// Reset 表單
function resetTicketForm() {
  ticketForm.value = {
    id: null,
    packageType: '',
    packageName: '',
    packageCode: '',
    priceAdjustment: 0,
    earlyBirdAdjustment: 0,
    imageUrl: '',
    enableEarlyBird: false,
    isActive: true,
    displayOrder: 1,
    validFrom: null,
    validUntil: null,
    packageItems: [
      {
        itemType: '',
        itemName: '',
        itemSpec: '',
        quantity: 1,
        displayOrder: 1
      }
    ]
  }
}

function getPackageTypeText(type) {
  const typeMap = {
    'bundle ticket': '套票組合',
    'single ticket': '單一票種',
    'special ticket': '特殊票種'
  }
  return typeMap[type] || type
}

function getPackageTypeTag(type) {
  const tagMap = {
    'bundle ticket': 'info',      // ✅ 改為 'info' 而不是空字串
    'single ticket': 'success',
    'special ticket': 'warning'
  }
  return tagMap[type] || 'info'   // ✅ 預設也改為 'info'
}

function formatDateTime(dateTime) {
  if (!dateTime) return ''
  if (typeof dateTime === 'string') {
    // 2025-01-01T00:00:00 → 2025-01-01 00:00
    if (dateTime.includes('T')) {
      const [date, time] = dateTime.split('T')
      return `${date} ${time.slice(0, 5)}`
    }
    // 2025-01-01 00:00:00 → 2025-01-01 00:00
    if (dateTime.includes(' ')) {
      const parts = dateTime.split(' ')
      if (parts.length >= 2) {
        return `${parts[0]} ${parts[1].slice(0, 5)}`
      }
    }
    return dateTime
  }
  return dateTime.toString()
}

// 🔧 修復：編輯票種（CRUD: Update → 先把資料塞回表單）
function editTicket(ticket) {
  console.log('===== 開始編輯票種 =====')
  console.log('原始票種資料:', ticket)
  
  // ⭐ 解析 packageItems
  const packageItems = parsePackageItems(ticket.packageItems)
  console.log('解析後的 packageItems:', packageItems)
  
  // 🔧 確保 packageItems 都有正確的結構
  const cleanedItems = packageItems.length > 0 
    ? packageItems.map((item, idx) => ({
        itemType: item.itemType || '',
        itemName: item.itemName || '',
        itemSpec: item.itemSpec || '',
        quantity: item.quantity || 1,
        displayOrder: item.displayOrder || idx + 1
      }))
    : [{
        itemType: '',
        itemName: '',
        itemSpec: '',
        quantity: 1,
        displayOrder: 1
      }]
  
  ticketForm.value = {
    id: ticket.id,
    packageType: ticket.packageType,
    packageName: ticket.packageName,
    packageCode: ticket.packageCode,
    priceAdjustment: ticket.priceAdjustment || 0,
    earlyBirdAdjustment: ticket.earlyBirdAdjustment || 0,
    imageUrl: ticket.imageUrl || '',
    enableEarlyBird: ticket.enableEarlyBird || false,
    isActive: ticket.isActive !== undefined ? ticket.isActive : true,
    displayOrder: ticket.displayOrder || 1,
    validFrom: ticket.validFrom ? ticket.validFrom.replace('T', ' ') : null,
    validUntil: ticket.validUntil ? ticket.validUntil.replace('T', ' ') : null,
    packageItems: cleanedItems
  }
  
  console.log('載入到表單的資料:', ticketForm.value)
  
  collapsedSections.value.ticket = false
  window.scrollTo({ top: 0, behavior: 'smooth' })
  
  ElMessage.info('已載入票種資料，請修改後點擊「更新票種」')
}

// 刪除票種（CRUD: Delete）
async function removeTicket(ticket) {
  try {
    await ElMessageBox.confirm(
      '確定要刪除此票種嗎？',
      '警告',
      {
        confirmButtonText: '確定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )
    
    console.log('📡 執行刪除: DELETE /api/ticket-packages/' + ticket.id)
    await ticketPackageService.delete(ticket.id)
    
    ElMessage.success('票種已刪除')
    fetchTicketList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('===== ❌ 刪除失敗 =====')
      console.error('錯誤:', error)
      ElMessage.error('刪除失敗，請檢查網路連線或聯絡系統管理員')
    }
  }
}

onMounted(() => {
  console.log('===== 組件已掛載 =====')
  console.log('ticketPackageService:', ticketPackageService)
  console.log('list 方法:', ticketPackageService.list)
  console.log('list 是函數嗎?', typeof ticketPackageService.list === 'function')
  
  fetchTicketList()
})
</script>

<style scoped>
.ticket-package-system {
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

.ticket-card {
  height: 100%;
  transition: all 0.3s ease;
}

.ticket-card:hover {
  transform: translateY(-4px);
}

.ticket-header {
  margin-bottom: 12px;
}

.ticket-title-area {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.ticket-title-area h3 {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

.ticket-badges {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

.price-section {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.price-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.price-item .label {
  font-size: 14px;
  color: #606266;
}

.price-item .value {
  font-size: 16px;
  font-weight: 600;
  color: #409eff;
}

.package-items,
.validity-info {
  margin-top: 12px;
}

.section-label {
  font-size: 14px;
  font-weight: 600;
  color: #606266;
  margin-bottom: 8px;
}

.items-container {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.item-tag {
  font-size: 13px;
}

.card-actions {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}

@media (max-width: 768px) {
  .ticket-package-system {
    padding: 10px;
  }

  .page-header h1 {
    font-size: 20px;
  }
}
</style>