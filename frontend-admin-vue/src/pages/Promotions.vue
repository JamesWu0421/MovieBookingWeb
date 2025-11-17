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
      <el-table-column prop="title" label="標題"/>
      <el-table-column prop="type" label="類型" width="120"/>
      <el-table-column prop="discount" label="折扣" width="120"/>
      <el-table-column prop="startAt" label="開始" width="160"/>
      <el-table-column prop="endAt" label="結束" width="160"/>
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
          <el-input v-model="form.title"></el-input>
        </el-form-item>
        <el-form-item label="折扣">
          <el-input v-model="form.discount"></el-input>
        </el-form-item>
        <el-form-item label="開始">
          <el-date-picker v-model="form.startAt" type="datetime" value-format="yyyy-MM-dd HH:mm:ss"></el-date-picker>
        </el-form-item>
        <el-form-item label="結束">
          <el-date-picker v-model="form.endAt" type="datetime" value-format="yyyy-MM-dd HH:mm:ss"></el-date-picker>
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
const form = ref({ id:null, title:'', discount:'', startAt:'', endAt:'' })
const formRef = ref(null)

async function fetchList() {
  const params = { 
    category: 'promotion',
    page: page.value, 
    size: pageSize.value
  }
  // 只有當 query 有值時才加入參數
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
  form.value = { id:null, title:'', discount:'', startAt:'', endAt:'' }
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
