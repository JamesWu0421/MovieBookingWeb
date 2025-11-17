<template>
  <div>
    <el-row style="margin-bottom:12px;">
      <el-col :span="12">
        <el-input placeholder="搜尋優惠標題" v-model="query" @keyup.enter="fetchList" clearable/>
      </el-col>
      <el-col :span="12" style="text-align:right;">
        <el-button type="primary" @click="openCreate">新增優惠</el-button>
      </el-col>
    </el-row>

    <el-table :data="list" style="width:100%">
      <el-table-column prop="id" label="ID" width="80"/>
      <el-table-column prop="name" label="標題"/>  <!-- 改這裡 -->
      <el-table-column prop="discountType" label="類型" width="120"/>  <!-- 改這裡 -->
      <el-table-column prop="discountValue" label="折扣" width="120"/>  <!-- 改這裡 -->
      <el-table-column prop="startDate" label="開始" width="160"/>  <!-- 改這裡 -->
      <el-table-column prop="endDate" label="結束" width="160"/>  <!-- 改這裡 -->
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button size="small" @click="openEdit(row)">編輯</el-button>
          <el-button size="small" type="danger" @click="remove(row)">刪除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div style="margin-top:12px; text-align:right;">
      <el-pagination background layout="prev, pager, next" :total="total" :page-size="pageSize" @current-change="onPageChange"/>
    </div>

    <el-dialog :visible.sync="dialogVisible" title="優惠活動">
      <el-form :model="form" ref="formRef">
        <el-form-item label="標題">
          <el-input v-model="form.name"></el-input>  <!-- 改這裡 -->
        </el-form-item>
        <el-form-item label="說明">
          <el-input v-model="form.description" type="textarea"></el-input>  <!-- 新增 -->
        </el-form-item>
        <el-form-item label="折扣類型">  <!-- 新增 -->
          <el-select v-model="form.discountType">
            <el-option label="百分比" value="percentage"></el-option>
            <el-option label="固定金額" value="fixed"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="折扣值">
          <el-input-number v-model="form.discountValue" :min="0"></el-input-number>  <!-- 改這裡 -->
        </el-form-item>
        <el-form-item label="開始日期">
          <el-date-picker v-model="form.startDate" type="date" value-format="yyyy-MM-dd"></el-date-picker>  <!-- 改這裡 -->
        </el-form-item>
        <el-form-item label="結束日期">
          <el-date-picker v-model="form.endDate" type="date" value-format="yyyy-MM-dd"></el-date-picker>  <!-- 改這裡 -->
        </el-form-item>
        <el-form-item label="最低消費">  <!-- 新增 -->
          <el-input-number v-model="form.minAmount" :min="0"></el-input-number>
        </el-form-item>
        <el-form-item label="是否需要優惠碼">  <!-- 新增 -->
          <el-switch v-model="form.requiresCoupon"></el-switch>
        </el-form-item>
        <el-form-item label="優惠碼" v-if="form.requiresCoupon">  <!-- 新增 -->
          <el-input v-model="form.couponCode"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible=false">取消</el-button>
        <el-button type="primary" @click="save">儲存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import promotionService from '../services/promotionService'

const list = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const query = ref('')
const dialogVisible = ref(false)

// 改成對應後端的欄位
const form = ref({ 
  id: null, 
  name: '',  // 改這裡
  description: '',
  discountType: 'percentage',  // 改這裡
  discountValue: 0,  // 改這裡
  startDate: '',  // 改這裡
  endDate: '',  // 改這裡
  minAmount: null,
  requiresCoupon: false,
  couponCode: null,
  category: 'promotion',  // 固定值
  isActive: true  // 固定值
})

const formRef = ref(null)

async function fetchList() {
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
      list.value = res.data
      total.value = res.data.total || (list.value.length)
    } else {
      list.value = []
      total.value = 0
    }
  } catch (error) {
    console.error('Failed to fetch promotions:', error)
    list.value = []
    total.value = 0
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
    startDate: '', 
    endDate: '',
    minAmount: null,
    requiresCoupon: false,
    couponCode: null,
    category: 'promotion',
    isActive: true
  }
  dialogVisible.value = true
}

function openEdit(row) {
  form.value = Object.assign({}, row)
  dialogVisible.value = true
}

async function save() {
  if (form.value.id) {
    await promotionService.update(form.value.id, form.value)
  } else {
    await promotionService.create(form.value)
  }
  dialogVisible.value = false
  fetchList()
}

async function remove(row) {
  await promotionService.delete(row.id)
  fetchList()
}

onMounted(fetchList)
</script>