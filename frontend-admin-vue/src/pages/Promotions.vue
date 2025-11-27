<template>
  <div>
    <el-row :gutter="12" style="margin-bottom: 16px;">
      <el-col :span="12">
        <el-input 
          placeholder="搜尋優惠標題" 
          v-model="query" 
          @keyup.enter="fetchList" 
          clearable
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </el-col>
      <el-col :span="12" style="text-align: right;">
        <el-button type="primary" @click="openCreate">
          <el-icon><Plus /></el-icon>
          新增優惠
        </el-button>
      </el-col>
    </el-row>

    <el-table 
      :data="list" 
      style="width: 100%" 
      v-loading="loading"
      border
      stripe
    >
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column label="縮圖" width="100" align="center">
        <template #default="{ row }">
          <div v-if="row.imageUrl" style="display: flex; justify-content: center;">
            <el-image
              :src="row.imageUrl"
              fit="cover"
              style="width: 60px; height: 60px; border-radius: 4px;"
              :preview-src-list="[row.imageUrl]"
              preview-teleported
            />
          </div>
          <span v-else style="color: #909399; font-size: 12px;">無</span>
        </template>
      </el-table-column>
      <el-table-column prop="name" label="標題" min-width="150" />
      <el-table-column prop="description" label="說明" min-width="200" show-overflow-tooltip />
      <el-table-column label="類型" width="110" align="center">
        <template #default="{ row }">
          <el-tag v-if="row.discountType === 'percentage'" type="success">百分比折扣</el-tag>
          <el-tag v-else-if="row.discountType === 'fixed'" type="warning">固定金額</el-tag>
          <el-tag v-else-if="row.discountType === 'lottery'" type="danger">抽獎活動</el-tag>
          <el-tag v-else type="info">其他</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="優惠內容" width="120" align="center">
        <template #default="{ row }">
          <span v-if="row.discountType === 'percentage'">{{ row.discountValue }}%</span>
          <span v-else-if="row.discountType === 'fixed'">${{ row.discountValue }}</span>
          <span v-else-if="row.discountType === 'lottery'" style="color: #f56c6c;">抽獎</span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column prop="startDate" label="開始時間" width="180" align="center" />
      <el-table-column prop="endDate" label="結束時間" width="180" align="center" />
      <el-table-column label="狀態" width="100" align="center">
        <template #default="{ row }">
          <el-tag v-if="isActivePromotion(row)" type="success">進行中</el-tag>
          <el-tag v-else-if="isUpcomingPromotion(row)" type="info">未開始</el-tag>
          <el-tag v-else type="info">已結束</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="啟用狀態" width="100" align="center">
        <template #default="{ row }">
          <el-tag :type="row.isActive ? 'success' : 'info'">
            {{ row.isActive ? '啟用' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" align="center" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="openEdit(row)">編輯</el-button>
          <el-button size="small" type="danger" @click="confirmDelete(row)">刪除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div style="margin-top: 16px; text-align: right;">
      <el-pagination
        v-model:current-page="page"
        background
        layout="total, prev, pager, next"
        :total="total"
        :page-size="pageSize"
        @current-change="onPageChange"
      />
    </div>

    <el-dialog 
      v-model="dialogVisible" 
      :title="form.id ? '編輯優惠' : '新增優惠'"
      width="600px"
    >
      <el-form 
        :model="form" 
        :rules="rules"
        ref="formRef"
        label-width="110px"
      >
        <el-form-item label="標題" prop="name">
          <el-input 
            v-model="form.name" 
            placeholder="請輸入優惠標題"
            maxlength="100"
            show-word-limit
          />
        </el-form-item>
        
        <el-form-item label="說明" prop="description">
          <el-input 
            v-model="form.description" 
            type="textarea"
            :rows="3"
            placeholder="請輸入優惠說明"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="優惠類型" prop="discountType">
          <el-radio-group v-model="form.discountType">
            <el-radio value="percentage">百分比折扣</el-radio>
            <el-radio value="fixed">固定金額折扣</el-radio>
            <el-radio value="lottery">抽獎活動</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <!-- 只有在選擇折扣類型時才顯示折扣值 -->
        <el-form-item 
          v-if="form.discountType !== 'lottery'" 
          label="折扣值" 
          prop="discountValue"
        >
          <el-input-number 
            v-model="form.discountValue" 
            :min="0"
            :max="form.discountType === 'percentage' ? 100 : 9999"
            :precision="form.discountType === 'percentage' ? 0 : 2"
            style="width: 200px"
          />
          <span style="margin-left: 8px; color: #909399;">
            {{ form.discountType === 'percentage' ? '(0-100%)' : '(元)' }}
          </span>
        </el-form-item>

        <!-- 抽獎活動專用欄位 -->
        <el-form-item 
          v-if="form.discountType === 'lottery'" 
          label="獎品說明" 
          prop="prizeDescription"
        >
          <el-input 
            v-model="form.prizeDescription" 
            type="textarea"
            :rows="3"
            placeholder="請輸入獎品說明，例如：iPhone 15 Pro x1、AirPods Pro x3、$500折價券 x10"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>

        <el-form-item 
          v-if="form.discountType === 'lottery'" 
          label="抽獎資格"
        >
          <el-input 
            v-model="form.lotteryRequirement" 
            placeholder="例如：消費滿$1000、會員專屬、無限制"
            maxlength="100"
          />
        </el-form-item>

        <el-form-item label="開始時間" prop="startDate">
          <el-date-picker 
            v-model="form.startDate" 
            type="datetime" 
            placeholder="選擇開始時間"
            value-format="YYYY-MM-DD HH:mm:ss"
            format="YYYY-MM-DD HH:mm"
            style="width: 100%"
          />
        </el-form-item>
        
        <el-form-item label="結束時間" prop="endDate">
          <el-date-picker 
            v-model="form.endDate" 
            type="datetime" 
            placeholder="選擇結束時間"
            value-format="YYYY-MM-DD HH:mm:ss"
            format="YYYY-MM-DD HH:mm"
            :disabled-date="disabledEndDate"
            style="width: 100%"
          />
        </el-form-item>

        <!-- 折扣類型才需要最低消費 -->
        <el-form-item 
          v-if="form.discountType !== 'lottery'" 
          label="最低消費金額"
        >
          <el-input-number 
            v-model="form.minAmount" 
            :min="0"
            :precision="0"
            placeholder="選填，0 表示無限制"
            style="width: 200px"
          />
          <span style="margin-left: 8px; color: #909399;">(元，0 表示無限制)</span>
        </el-form-item>

        <el-form-item label="每人使用次數">
          <el-input-number 
            v-model="form.maxUsagePerUser" 
            :min="0"
            :precision="0"
            :placeholder="form.discountType === 'lottery' ? '每人可抽次數' : '每人使用次數'"
            style="width: 200px"
          />
          <span style="margin-left: 8px; color: #909399;">
            ({{ form.discountType === 'lottery' ? '每人可抽次數' : '次' }}，0 表示無限制)
          </span>
        </el-form-item>
        
        <!-- 折扣類型才需要優惠碼 -->
        <el-form-item v-if="form.discountType !== 'lottery'" label="需要優惠碼">
          <el-switch v-model="form.requiresCoupon" />
        </el-form-item>
        
        <el-form-item 
          v-if="form.requiresCoupon && form.discountType !== 'lottery'" 
          label="優惠碼" 
          prop="couponCode"
        >
          <el-input 
            v-model="form.couponCode" 
            placeholder="請輸入優惠碼"
            maxlength="20"
          />
        </el-form-item>

        <el-form-item label="備註">
          <el-input 
            v-model="form.notes" 
            type="textarea"
            :rows="2"
            placeholder="選填備註資訊"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="活動圖片">
          <el-upload
            class="image-uploader"
            :action="uploadUrl"
            :show-file-list="false"
            :on-success="handleImageSuccess"
            :on-error="handleImageError"
            :before-upload="beforeImageUpload"
            :headers="uploadHeaders"
          >
            <img v-if="form.imageUrl" :src="form.imageUrl" class="uploaded-image" />
            <el-icon v-else class="image-uploader-icon"><Plus /></el-icon>
          </el-upload>
          <div style="color: #909399; font-size: 12px; margin-top: 4px;">
            建議尺寸：800x400，檔案大小不超過 2MB
          </div>
          <el-button 
            v-if="form.imageUrl" 
            size="small" 
            type="danger" 
            style="margin-top: 8px;"
            @click="form.imageUrl = ''"
          >
            移除圖片
          </el-button>
        </el-form-item>

        <el-form-item label="是否啟用">
          <el-switch v-model="form.isActive" />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="save" :loading="saving">
          {{ saving ? '儲存中...' : '儲存' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus } from '@element-plus/icons-vue'
import promotionService from '../services/promotionService'
import { formatDateFields, toISOFormat } from '../utils/dateFormatter'

const list = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const query = ref('')
const dialogVisible = ref(false)
const loading = ref(false)
const saving = ref(false)

// 圖片上傳相關
const uploadUrl = computed(() => `${import.meta.env.VITE_UPLOAD_URL}/image`)
const uploadHeaders = computed(() => ({
  // 如果需要認證，可以加上 token
  'Authorization': `Bearer ${localStorage.getItem('admin_token') || ''}`
}))

const form = ref({
  id: null,
  name: '',
  description: '',
  discountType: 'percentage',
  discountValue: 0,
  prizeDescription: '',
  lotteryRequirement: '',
  startDate: '',
  endDate: '',
  minAmount: null,
  maxUsagePerUser: null,
  requiresCoupon: false,
  couponCode: null,
  notes: '',
  imageUrl: '',
  category: 'promotion',
  isActive: true
})

const formRef = ref(null)

// 表單驗證規則
const rules = computed(() => {
  const baseRules = {
    name: [
      { required: true, message: '請輸入優惠標題', trigger: 'blur' },
      { min: 2, max: 100, message: '標題長度在 2 到 100 個字元', trigger: 'blur' }
    ],
    description: [
      { required: true, message: '請輸入優惠說明', trigger: 'blur' }
    ],
    discountType: [
      { required: true, message: '請選擇優惠類型', trigger: 'change' }
    ],
    startDate: [
      { required: true, message: '請選擇開始日期', trigger: 'change' }
    ],
    endDate: [
      { required: true, message: '請選擇結束日期', trigger: 'change' }
    ]
  }

  // 如果是抽獎活動
  if (form.value.discountType === 'lottery') {
    return {
      ...baseRules,
      prizeDescription: [
        { required: true, message: '請輸入獎品說明', trigger: 'blur' }
      ]
    }
  }

  // 如果是折扣類型
  return {
    ...baseRules,
    discountValue: [
      { required: true, message: '請輸入折扣值', trigger: 'blur' },
      { 
        validator: (rule, value, callback) => {
          if (form.value.discountType === 'percentage' && (value <= 0 || value > 100)) {
            callback(new Error('百分比折扣範圍為 1-100'))
          } else if (form.value.discountType === 'fixed' && value <= 0) {
            callback(new Error('固定金額必須大於 0'))
          } else {
            callback()
          }
        },
        trigger: 'blur'
      }
    ],
    couponCode: [
      { 
        validator: (rule, value, callback) => {
          if (form.value.requiresCoupon && !value) {
            callback(new Error('需要優惠碼時必須輸入優惠碼'))
          } else {
            callback()
          }
        },
        trigger: 'blur'
      }
    ]
  }
})

// 禁用結束日期（不能早於開始日期）
function disabledEndDate(time) {
  if (!form.value.startDate) return false
  return time.getTime() < new Date(form.value.startDate).getTime()
}

// 判斷優惠是否進行中
function isActivePromotion(row) {
  if (!row.startDate || !row.endDate) return false
  const now = new Date()
  const start = new Date(row.startDate)
  const end = new Date(row.endDate)
  end.setHours(23, 59, 59, 999) // 結束日期包含當天整天
  return now >= start && now <= end && row.isActive
}

// 判斷優惠是否未開始
function isUpcomingPromotion(row) {
  if (!row.startDate) return false
  const now = new Date()
  const start = new Date(row.startDate)
  return now < start && row.isActive
}

// 圖片上傳前的檢查
function beforeImageUpload(file) {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上傳圖片檔案！')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('圖片大小不能超過 2MB！')
    return false
  }
  return true
}

// 圖片上傳成功
function handleImageSuccess(response, file) {
  if (response.success || response.url) {
    form.value.imageUrl = response.url || response.data
    ElMessage.success('圖片上傳成功')
  } else {
    ElMessage.error('圖片上傳失敗：' + (response.message || '未知錯誤'))
  }
}

// 圖片上傳失敗
function handleImageError(error) {
  console.error('Image upload error:', error)
  ElMessage.error('圖片上傳失敗，請稍後再試')
}

async function fetchList() {
  loading.value = true
  const params = {
    category: 'promotion',
    page: page.value,
    size: pageSize.value
  }

  if (query.value && query.value.trim()) {
    params.q = query.value
  }

  try {
    const res = await promotionService.list(params)
    if (res && res.data) {
      // ✨ 格式化日期欄位
      const rawData = Array.isArray(res.data) ? res.data : []
      list.value = formatDateFields(rawData)
      total.value = res.data.total || list.value.length
    } else {
      list.value = []
      total.value = 0
    }
  } catch (error) {
    console.error('Failed to fetch promotions:', error)
    ElMessage.error('載入優惠列表失敗：' + (error.message || '未知錯誤'))
    list.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

function onPageChange(p) {
  page.value = p
  fetchList()
}

function openCreate() {
  form.value = {
    id: null,
    name: '',
    description: '',
    discountType: 'percentage',
    discountValue: 0,
    prizeDescription: '',
    lotteryRequirement: '',
    startDate: '',
    endDate: '',
    minAmount: null,
    maxUsagePerUser: null,
    requiresCoupon: false,
    couponCode: null,
    notes: '',
    imageUrl: '',
    category: 'promotion',
    isActive: true
  }
  // 重置表單驗證
  if (formRef.value) {
    formRef.value.clearValidate()
  }
  dialogVisible.value = true
}

function openEdit(row) {
  form.value = Object.assign({}, row)
  // 重置表單驗證
  if (formRef.value) {
    formRef.value.clearValidate()
  }
  dialogVisible.value = true
}

async function save() {
  // 表單驗證
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
  } catch (error) {
    ElMessage.warning('請檢查表單填寫是否正確')
    return
  }

  saving.value = true
  try {
    // ✨ 準備資料，將日期格式轉回 ISO 格式給後端
    const dataToSave = {
      ...form.value,
      startDate: toISOFormat(form.value.startDate),
      endDate: toISOFormat(form.value.endDate)
    }

    // 如果是抽獎活動，清空折扣相關欄位
    if (form.value.discountType === 'lottery') {
      dataToSave.discountValue = null
      dataToSave.minAmount = null
      dataToSave.requiresCoupon = false
      dataToSave.couponCode = null
    } else {
      // 如果是折扣類型，清空抽獎相關欄位
      dataToSave.prizeDescription = null
      dataToSave.lotteryRequirement = null
    }

    if (form.value.id) {
      await promotionService.update(form.value.id, dataToSave)
      ElMessage.success('更新優惠成功')
    } else {
      await promotionService.create(dataToSave)
      ElMessage.success('新增優惠成功')
    }
    dialogVisible.value = false
    fetchList()
  } catch (error) {
    console.error('Failed to save promotion:', error)
    ElMessage.error('儲存失敗：' + (error.response?.data?.message || error.message || '未知錯誤'))
  } finally {
    saving.value = false
  }
}

function confirmDelete(row) {
  ElMessageBox.confirm(
    `確定要刪除優惠「${row.name}」嗎？此操作無法復原。`,
    '刪除確認',
    {
      confirmButtonText: '確定刪除',
      cancelButtonText: '取消',
      type: 'warning',
    }
  )
    .then(async () => {
      try {
        await promotionService.delete(row.id)
        ElMessage.success('刪除成功')
        fetchList()
      } catch (error) {
        console.error('Failed to delete promotion:', error)
        ElMessage.error('刪除失敗：' + (error.response?.data?.message || error.message || '未知錯誤'))
      }
    })
    .catch(() => {
      // 用戶取消刪除
    })
}

// 監聽開始時間的變化
watch(() => form.value.startDate, (newStartDate) => {
  if (newStartDate && !form.value.endDate) {
    // 如果有開始時間但沒有結束時間，自動設置為當天 23:59:59
    const startDate = new Date(newStartDate)
    const endDate = new Date(startDate)
    endDate.setHours(23, 59, 59, 0)
    
    // 格式化為 YYYY-MM-DD HH:mm:ss
    const year = endDate.getFullYear()
    const month = String(endDate.getMonth() + 1).padStart(2, '0')
    const day = String(endDate.getDate()).padStart(2, '0')
    form.value.endDate = `${year}-${month}-${day} 23:59:59`
  }
})

// 監聽優惠類型的變化，自動清理不相關的欄位
watch(() => form.value.discountType, (newType) => {
  if (newType === 'lottery') {
    // 切換到抽獎活動，清空折扣相關欄位
    form.value.discountValue = 0
    form.value.minAmount = null
    form.value.requiresCoupon = false
    form.value.couponCode = null
  } else {
    // 切換到折扣類型，清空抽獎相關欄位
    form.value.prizeDescription = ''
    form.value.lotteryRequirement = ''
  }
})

onMounted(fetchList)
</script>

<style scoped>
:deep(.el-table) {
  font-size: 14px;
}

:deep(.el-form-item__label) {
  font-weight: 500;
}

.image-uploader :deep(.el-upload) {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: border-color 0.3s;
}

.image-uploader :deep(.el-upload:hover) {
  border-color: #409eff;
}

.image-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.uploaded-image {
  width: 178px;
  height: 178px;
  object-fit: cover;
  display: block;
}
</style>