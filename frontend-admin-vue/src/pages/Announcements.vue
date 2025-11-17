<template>
  <div>
    <el-row style="margin-bottom:12px;">
      <el-col :span="12">
        <el-input placeholder="搜尋公告標題" v-model="query" @keyup.enter="fetchList" clearable/>
      </el-col>
      <el-col :span="12" style="text-align:right;">
        <el-button type="primary" @click="openCreate">新增公告</el-button>
      </el-col>
    </el-row>

    <el-table :data="list" style="width:100%">
      <el-table-column prop="id" label="ID" width="80"/>
      <el-table-column prop="title" label="標題"/>
      <el-table-column prop="content" label="內容" :show-overflow-tooltip="true"/>
      <el-table-column prop="startAt" label="開始時間" width="160"/>
      <el-table-column prop="endAt" label="結束時間" width="160"/>
      <el-table-column label="狀態" width="100">
        <template #default="{ row }">
          <el-tag v-if="isActive(row)" type="success">進行中</el-tag>
          <el-tag v-else type="info">已結束</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button size="small" @click="openEdit(row)">編輯</el-button>
          <el-button size="small" type="danger" @click="remove(row)">刪除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div style="margin-top:12px; text-align:right;">
      <el-pagination background layout="prev, pager, next" :total="total" :page-size="pageSize" @current-change="onPageChange"/>
    </div>

    <el-dialog v-model="dialogVisible" title="公告活動" width="600px">
      <el-form :model="form" ref="formRef" label-width="100px">
        <el-form-item label="標題">
          <el-input v-model="form.title" placeholder="請輸入公告標題"></el-input>
        </el-form-item>
        <el-form-item label="內容">
          <el-input type="textarea" v-model="form.content" :rows="6" placeholder="請輸入公告內容"></el-input>
        </el-form-item>
        <el-form-item label="開始時間">
          <el-date-picker 
            v-model="form.startAt" 
            type="datetime" 
            placeholder="選擇開始時間"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width:100%"
          />
        </el-form-item>
        <el-form-item label="結束時間">
          <el-date-picker 
            v-model="form.endAt" 
            type="datetime" 
            placeholder="選擇結束時間"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width:100%"
          />
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
import announcementService from '../services/announcementService'

const list = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const query = ref('')
const dialogVisible = ref(false)
const form = ref({ id:null, title:'', content:'', startAt:'', endAt:'' })
const formRef = ref(null)

// 判斷公告是否進行中
function isActive(row) {
  if (!row.startAt || !row.endAt) return false
  const now = new Date()
  const start = new Date(row.startAt)
  const end = new Date(row.endAt)
  return now >= start && now <= end
}

async function fetchList() {
  const params = { 
    page: page.value, 
    size: pageSize.value
  }
  if (query.value && query.value.trim()) {
    params.q = query.value
  }
  
  try {
    const res = await announcementService.list(params)
    if (res && res.data) {
      list.value = res.data.data || res.data || []
      total.value = res.data.total || (list.value.length)
    } else {
      list.value = []
      total.value = 0
    }
  } catch (error) {
    console.error('Failed to fetch announcements:', error)
    list.value = []
    total.value = 0
  }
}

function onPageChange(p) {
  page.value = p
  fetchList()
}

function openCreate() {
  form.value = { id:null, title:'', content:'', startAt:'', endAt:'' }
  dialogVisible.value = true
}

function openEdit(row) {
  form.value = Object.assign({}, row)
  dialogVisible.value = true
}

async function save() {
  if (form.value.id) {
    await announcementService.update(form.value.id, form.value)
  } else {
    await announcementService.create(form.value)
  }
  dialogVisible.value = false
  fetchList()
}

async function remove(row) {
  await announcementService.delete(row.id)
  fetchList()
}

onMounted(fetchList)
</script>
