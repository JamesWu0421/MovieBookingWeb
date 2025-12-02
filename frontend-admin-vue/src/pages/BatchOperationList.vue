<template>
  <div class="batch-operations-page">
    <div class="page-header">
      <h1>📦 批次管理</h1>
      <button @click="showCreateDialog = true" class="btn-primary">
        + 建立新批次
      </button>
    </div>

    <div class="filter-section">
      <select v-model="filterStatus" @change="loadBatchList" class="filter-select">
        <option value="">全部狀態</option>
        <option value="PENDING">待處理</option>
        <option value="EXECUTING">執行中</option>
        <option value="COMPLETED">已完成</option>
        <option value="FAILED">失敗</option>
      </select>
    </div>

    <div v-if="loading" class="loading">⏳ 載入中...</div>
    <div v-else-if="error" class="error-box">❌ {{ error }}</div>
    <div v-else-if="batchList.length === 0" class="empty-state">
      <p>📭 目前沒有批次記錄</p>
      <button @click="showCreateDialog = true" class="btn-secondary">
        建立第一個批次
      </button>
    </div>

    <div v-else class="batch-list">
      <div
        v-for="batch in batchList"
        :key="batch.batchId"
        class="batch-card"
        :class="`status-${batch.status.toLowerCase()}`"
      >
        <!-- 卡片標頭 -->
        <div class="card-header">
          <div class="header-left">
            <h3>批次 #{{ batch.batchId }}</h3>
            <span class="status-badge" :class="`badge-${batch.status.toLowerCase()}`">
              {{ getStatusText(batch.status) }}
            </span>
          </div>
          <button
            @click="deleteBatch(batch)"
            class="btn-icon btn-delete"
            :disabled="batch.status === 'EXECUTING'"
            title="刪除批次"
          >
            🗑️
          </button>
        </div>

        <!-- 卡片內容 -->
        <div class="card-body">
          <div class="info-row">
            <span class="label">👤 操作者ID</span>
            <span class="value">{{ batch.operatorId ?? '-' }}</span>
          </div>
          <div class="info-row">
            <span class="label">⚙ 類型</span>
            <span class="value">{{ batch.operationType || '-' }}</span>
          </div>
          <div class="info-row">
            <span class="label">📝 說明</span>
            <span class="value">{{ batch.description || '-' }}</span>
          </div>
          <div class="info-row">
            <span class="label">🕐 建立時間</span>
            <span class="value">{{ formatDate(batch.createdAt) }}</span>
          </div>

          <div v-if="batch.status !== 'PENDING'" class="progress-info">
            <div class="stats">
              <span class="stat success">✓ {{ batch.successCount || 0 }}</span>
              <span class="stat fail">✗ {{ batch.failCount || 0 }}</span>
              <span class="stat total">總 {{ getTotalCount(batch) }}</span>
            </div>
            <div v-if="getTotalCount(batch) > 0" class="progress-bar">
              <div
                class="progress-fill"
                :style="{ width: getProgress(batch) + '%' }"
              ></div>
            </div>
          </div>
        </div>

        <!-- 卡片 footer：導頁 + 狀態操作 -->
        <div class="card-footer">
          <button
            @click="goToSessions(batch.batchId)"
            class="btn-nav btn-sessions"
          >
            📋 管理場次
          </button>
          <button
            @click="goToTickets(batch.batchId)"
            class="btn-nav btn-tickets"
          >
            🎫 管理票券
          </button>

          <div class="status-actions">
            <button
              v-if="batch.status === 'PENDING'"
              @click="startBatch(batch)"
              class="btn-action btn-start"
            >
              ▶️ 開始
            </button>
            <button
              v-if="batch.status === 'EXECUTING'"
              @click="completeBatch(batch)"
              class="btn-action btn-complete"
            >
              ✓ 完成
            </button>
            <button
              v-if="batch.status === 'EXECUTING'"
              @click="failBatch(batch)"
              class="btn-action btn-fail"
            >
              ✗ 失敗
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 建立新批次 Dialog -->
    <div
      v-if="showCreateDialog"
      class="batch-modal-overlay"
      @click.self="closeCreateDialog"
    >
      <div class="batch-modal">
        <div class="modal-header">
          <h2>建立新批次</h2>
          <button @click="closeCreateDialog" class="btn-close">×</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>說明 <span class="required">*</span></label>
            <textarea
              v-model="newBatch.description"
              placeholder="請輸入批次說明"
              class="form-textarea"
              rows="3"
              @keyup.enter="createBatch"
            ></textarea>
          </div>
        </div>
        <div class="modal-footer">
          <button @click="closeCreateDialog" class="btn-secondary">
            取消
          </button>
          <button
            @click="createBatch"
            class="btn-primary"
            :disabled="!newBatch.description || !newBatch.description.trim()"
          >
            建立並前往場次管理
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import Swal from 'sweetalert2'
import { batchOperationService } from '../services/batchOperationService'

const router = useRouter()

const batchList = ref([])
const loading = ref(false)
const error = ref('')
const filterStatus = ref('')

const showCreateDialog = ref(false)
const newBatch = ref({
  description: '',
})

// 測試模式設定（改成順序測 1–50）
const testMode = ref({
  autoRetry: true,  // 自動重試
  maxRetries: 50,   // 最多嘗試 50 次（對應 ID 1-50）
  currentRetry: 0,
})

// 載入批次列表
const loadBatchList = async () => {
  loading.value = true
  error.value = ''

  try {
    const response = await batchOperationService.getAll()
    let list = response.data?.data || []

    if (filterStatus.value) {
      list = list.filter((b) => b.status === filterStatus.value)
    }

    batchList.value = list.sort(
      (a, b) => new Date(b.createdAt) - new Date(a.createdAt),
    )
  } catch (err) {
    console.error(err)
    error.value = err.response?.data?.message || err.message
  } finally {
    loading.value = false
  }
}

onMounted(loadBatchList)

// ✅ 嘗試建立批次（依序測試 ID 1-50）
const createBatchWithRetry = async (operatorId, description, isRetry = false) => {
  try {
    const response = await batchOperationService.create({
      operatorId: operatorId,
      operationType: 'IMPORT',
      status: 'PENDING',
      description: description,
    })

    const newBatchId = response.data?.data?.batchId

    if (!newBatchId) {
      throw new Error('回傳未包含 batchId')
    }

    // ✅ 成功！
    console.log(`✅ 成功！使用 operatorId: ${operatorId}`)
    
    if (isRetry) {
      await Swal.fire({
        icon: 'success',
        title: '找到有效的員工！',
        html: `
          <p style="font-size: 18px; margin: 12px 0;">
            員工 ID: <strong style="color: #10b981;">${operatorId}</strong>
          </p>
          <p style="color: #64748b; font-size: 14px;">
            已在第 ${testMode.value.currentRetry + 1} 次嘗試找到有效 ID
          </p>
        `,
        timer: 2000,
      })
    } else {
      await Swal.fire({
        icon: 'success',
        title: '建立成功！',
        text: `使用的員工 ID: ${operatorId}`,
        timer: 2000,
      })
    }

    closeCreateDialog()
    testMode.value.currentRetry = 0

    router.push({
      name: 'BatchSessionTemp',
      params: { batchId: newBatchId },
    })

    return true

  } catch (err) {
    console.log(`❌ operatorId ${operatorId} 無效`)
    
    const errorMessage = err.response?.data?.message || err.message
    
    // ✅ 如果是找不到操作員且還沒超過重試次數，自動「往下一個 ID」重試
    if (
      (errorMessage.includes('找不到操作員') || 
       errorMessage.includes('operatorId') ||
       errorMessage.includes('不存在')) &&
      testMode.value.autoRetry &&
      testMode.value.currentRetry < testMode.value.maxRetries &&
      operatorId < 50   // 只測到 50
    ) {
      testMode.value.currentRetry++
      const nextId = operatorId + 1
      console.log(`🔄 重試 (${testMode.value.currentRetry}/${testMode.value.maxRetries})，嘗試 operatorId: ${nextId}`)
      
      // 遞迴重試（順序 1,2,3,...,50）
      return await createBatchWithRetry(nextId, description, true)
    }

    // 達到最大重試次數或其他錯誤
    throw err
  }
}

// 建立批次（主要入口）──改成從 1 開始依序測到 50
const createBatch = async () => {
  if (!newBatch.value.description.trim()) {
    Swal.fire({
      icon: 'warning',
      title: '請輸入批次說明',
    })
    return
  }

  const startOperatorId = 1
  testMode.value.currentRetry = 0
  
  console.log(`🎯 開始依序測試 operatorId 1-50，從 ${startOperatorId} 開始`)

  try {
    await createBatchWithRetry(startOperatorId, newBatch.value.description)
  } catch (err) {
    console.error('❌ 1-50 全部嘗試都失敗了:', err)
    
    const errorMessage = err.response?.data?.message || err.message
    
    if (errorMessage.includes('找不到操作員') || 
        errorMessage.includes('operatorId') ||
        errorMessage.includes('不存在')) {
      Swal.fire({
        icon: 'error',
        title: '找不到有效的操作員',
        html: `
          <p>已依序嘗試 <strong>${Math.min(testMode.value.currentRetry + 1, 50)}</strong> 次（ID 1-50）</p>
          <p style="color: #ef4444; margin-top: 12px;">
            資料庫中沒有可用的員工資料（ID 1-50）
          </p>
          <hr style="margin: 16px 0;">
          <p style="color: #64748b; font-size: 14px;">
            請執行以下 SQL 新增測試資料：
          </p>
          <pre style="background: #f1f5f9; padding: 12px; border-radius: 6px; text-align: left; font-size: 12px; max-height: 200px; overflow-y: auto;">
SET IDENTITY_INSERT employees ON;

INSERT INTO employees (id, emp_name, emp_password_hash, emp_email, created_at, status)
VALUES 
  (1, '測試員工01', 'hash', 'test01@example.com', GETDATE(), 1),
  (2, '測試員工02', 'hash', 'test02@example.com', GETDATE(), 1),
  (3, '測試員工03', 'hash', 'test03@example.com', GETDATE(), 1);

SET IDENTITY_INSERT employees OFF;
          </pre>
        `,
        width: 600,
      })
    } else {
      Swal.fire({
        icon: 'error',
        title: '建立失敗',
        text: errorMessage,
      })
    }
    
    testMode.value.currentRetry = 0
  }
}

// 開始批次
const startBatch = async (batch) => {
  const result = await Swal.fire({
    icon: 'question',
    title: `確定開始執行批次 #${batch.batchId}？`,
    showCancelButton: true,
    confirmButtonText: '開始',
    cancelButtonText: '取消',
  })
  if (!result.isConfirmed) return

  try {
    const actualTotal = getTotalCount(batch)
    await batchOperationService.start(batch.batchId, actualTotal)
    await loadBatchList()
    Swal.fire({ icon: 'success', title: '批次已開始執行' })
  } catch (err) {
    console.error(err)
    Swal.fire({
      icon: 'error',
      title: '開始失敗',
      text: err.response?.data?.message || err.message,
    })
  }
}

// 完成批次
const completeBatch = async (batch) => {
  const result = await Swal.fire({
    icon: 'question',
    title: `確定完成批次 #${batch.batchId}？`,
    showCancelButton: true,
    confirmButtonText: '標記完成',
    cancelButtonText: '取消',
  })
  if (!result.isConfirmed) return

  try {
    await batchOperationService.complete(
      batch.batchId,
      batch.successCount || 0,
      batch.failCount || 0,
    )
    await loadBatchList()
    Swal.fire({ icon: 'success', title: '批次已標記為完成' })
  } catch (err) {
    console.error(err)
    Swal.fire({
      icon: 'error',
      title: '完成失敗',
      text: err.response?.data?.message || err.message,
    })
  }
}

// 標記失敗
const failBatch = async (batch) => {
  const result = await Swal.fire({
    icon: 'warning',
    title: `確定將批次 #${batch.batchId} 標記為失敗？`,
    showCancelButton: true,
    confirmButtonText: '標記失敗',
    cancelButtonText: '取消',
  })
  if (!result.isConfirmed) return

  try {
    await batchOperationService.updateStatus(batch.batchId, 'FAILED')
    await loadBatchList()
    Swal.fire({ icon: 'success', title: '批次已標記為失敗' })
  } catch (err) {
    console.error(err)
    Swal.fire({
      icon: 'error',
      title: '更新失敗',
      text: err.response?.data?.message || err.message,
    })
  }
}

// 刪除批次
const deleteBatch = async (batch) => {
  if (batch.status === 'EXECUTING') {
    Swal.fire({
      icon: 'info',
      title: '執行中的批次無法刪除',
    })
    return
  }

  const result = await Swal.fire({
    icon: 'warning',
    title: `確定刪除批次 #${batch.batchId}？`,
    text: '此操作無法復原！',
    showCancelButton: true,
    confirmButtonText: '刪除',
    cancelButtonText: '取消',
  })
  if (!result.isConfirmed) return

  try {
    await batchOperationService.remove(batch.batchId)
    await loadBatchList()
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

// 導頁
const goToSessions = (batchId) => {
  router.push({
    name: 'BatchSessionTemp',
    params: { batchId },
  })
}

const goToTickets = (batchId) => {
  router.push({
    name: 'BatchTicketTemp',
    params: { batchId },
  })
}

// UI 小工具
const closeCreateDialog = () => {
  showCreateDialog.value = false
  newBatch.value = { description: '' }
}

const getStatusText = (status) => {
  const map = {
    PENDING: '待處理',
    EXECUTING: '執行中',
    COMPLETED: '已完成',
    FAILED: '失敗',
  }
  return map[status] || status
}

const getTotalCount = (batch) => {
  const actualCount = (batch.successCount || 0) + (batch.failCount || 0)
  return actualCount > 0 ? actualCount : (batch.totalItems || 0)
}

const getProgress = (batch) => {
  const total = getTotalCount(batch)
  if (!total) return 0
  const done = (batch.successCount || 0) + (batch.failCount || 0)
  return Math.round((done / total) * 100)
}

const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleString('zh-TW')
}
</script>

<style scoped>
.batch-operations-page {
  max-width: 1400px;
  margin: 0 auto;
  padding: 24px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-header h1 {
  font-size: 28px;
  font-weight: 700;
  color: #1a1a1a;
  margin: 0;
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
  border-color: #cbd5e1;
}

.filter-section {
  margin-bottom: 24px;
}

.filter-select {
  padding: 10px 16px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  font-size: 14px;
  background: white;
  cursor: pointer;
  min-width: 150px;
}

.batch-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(500px, 1fr));
  gap: 20px;
}

.batch-card {
  background: white;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s;
}

.batch-card:hover {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
  transform: translateY(-4px);
}

.batch-card.status-pending {
  border-left: 5px solid #f59e0b;
}

.batch-card.status-executing {
  border-left: 5px solid #3b82f6;
}

.batch-card.status-completed {
  border-left: 5px solid #10b981;
}

.batch-card.status-failed {
  border-left: 5px solid #ef4444;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background: #f8fafc;
  border-bottom: 1px solid #e2e8f0;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-left h3 {
  font-size: 18px;
  font-weight: 700;
  margin: 0;
  color: #1e293b;
}

.status-badge {
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 700;
  text-transform: uppercase;
}

.badge-pending {
  background: #fef3c7;
  color: #92400e;
}

.badge-executing {
  background: #dbeafe;
  color: #1e40af;
}

.badge-completed {
  background: #d1fae5;
  color: #065f46;
}

.badge-failed {
  background: #fee2e2;
  color: #991b1b;
}

.btn-icon {
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
  padding: 8px;
  border-radius: 6px;
  transition: all 0.2s;
}

.btn-delete:hover:not(:disabled) {
  background: #fee2e2;
}

.btn-delete:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.card-body {
  padding: 20px;
}

.info-row {
  display: flex;
  align-items: flex-start;
  margin-bottom: 12px;
  font-size: 14px;
}

.info-row .label {
  min-width: 110px;
  color: #64748b;
  font-weight: 600;
}

.info-row .value {
  color: #1e293b;
  flex: 1;
}

.progress-info {
  margin-top: 20px;
  padding-top: 16px;
  border-top: 1px solid #e2e8f0;
}

.stats {
  display: flex;
  gap: 20px;
  margin-bottom: 12px;
}

.stat {
  font-size: 14px;
  font-weight: 700;
}

.stat.success {
  color: #10b981;
}

.stat.fail {
  color: #ef4444;
}

.stat.total {
  color: #64748b;
}

.progress-bar {
  height: 10px;
  background: #e2e8f0;
  border-radius: 5px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #3b82f6, #2563eb);
  transition: width 0.5s ease;
}

.card-footer {
  padding: 16px 20px;
  background: #f8fafc;
  border-top: 1px solid #e2e8f0;
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.btn-nav {
  flex: 1;
  min-width: 140px;
  padding: 12px;
  border: 2px solid #e2e8f0;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  background: white;
}

.btn-sessions {
  color: #2563eb;
  border-color: #2563eb;
}

.btn-sessions:hover {
  background: #2563eb;
  color: white;
}

.btn-tickets {
  color: #7c3aed;
  border-color: #7c3aed;
}

.btn-tickets:hover {
  background: #7c3aed;
  color: white;
}

.status-actions {
  display: flex;
  gap: 8px;
  width: 100%;
}

.btn-action {
  flex: 1;
  padding: 10px;
  border: none;
  border-radius: 6px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-start {
  background: #10b981;
  color: white;
}

.btn-start:hover {
  background: #059669;
}

.btn-complete {
  background: #3b82f6;
  color: white;
}

.btn-complete:hover {
  background: #2563eb;
}

.btn-fail {
  background: #ef4444;
  color: white;
}

.btn-fail:hover {
  background: #dc2626;
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

.batch-modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 900;            /* 再抬高一點 */
  backdrop-filter: blur(4px);
}

.batch-modal {
  display: block;            /* ★ 強制不是 display:none */
  background: #fff;
  border-radius: 16px;
  width: 90%;
  max-width: 500px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
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
  box-sizing: border-box;
  padding: 12px;
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
  .batch-list {
    grid-template-columns: 1fr;
  }
  
  .card-footer {
    flex-direction: column;
  }
  
  .btn-nav {
    min-width: 100%;
  }
}
</style>