<template>
  <div>
    <el-row style="margin-bottom:12px;">
      <el-col :span="12">
        <el-input placeholder="搜尋通知標題" v-model="query" @keyup.enter="fetchList" clearable/>
      </el-col>
      <el-col :span="12" style="text-align:right;">
        <el-button type="primary" @click="openCreate">新增通知</el-button>
      </el-col>
    </el-row>

    <el-table :data="list" style="width:100%">
      <el-table-column prop="id" label="ID" width="80"/>
      <el-table-column prop="type" label="類型" width="120">
        <template #default="{ row }">
          <el-tag v-if="row.type === 'SYSTEM'" type="warning">系統</el-tag>
          <el-tag v-else-if="row.type === 'ORDER'" type="success">訂單</el-tag>
          <el-tag v-else-if="row.type === 'PROMOTION'" type="danger">促銷</el-tag>
          <el-tag v-else>{{ row.type }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="title" label="標題"/>
      <el-table-column prop="content" label="內容" :show-overflow-tooltip="true"/>
      <el-table-column prop="relatedType" label="相關類型" width="100"/>
      <el-table-column prop="relatedId" label="相關ID" width="100"/>
      <el-table-column label="狀態" width="100">
        <template #default="{ row }">
          <el-tag v-if="row.isActive" type="success">啟用</el-tag>
          <el-tag v-else type="info">停用</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="建立時間" width="160"/>
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button size="small" @click="openEdit(row)">編輯</el-button>
          <el-button size="small" type="danger" @click="remove(row)">刪除</el-button>
          <el-button size="small" type="info" @click="sendToUsers(row)">推送</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div style="margin-top:12px; text-align:right;">
      <el-pagination background layout="prev, pager, next" :total="total" :page-size="pageSize" @current-change="onPageChange"/>
    </div>

    <!-- 編輯對話框 -->
    <el-dialog v-model="dialogVisible" title="系統通知" width="600px">
      <el-form :model="form" ref="formRef" label-width="100px">
        <el-form-item label="類型">
          <el-select v-model="form.type" placeholder="請選擇通知類型" style="width:100%">
            <el-option label="系統通知" value="SYSTEM"/>
            <el-option label="訂單通知" value="ORDER"/>
            <el-option label="促銷通知" value="PROMOTION"/>
            <el-option label="電影通知" value="MOVIE"/>
            <el-option label="支付通知" value="PAYMENT"/>
          </el-select>
        </el-form-item>
        <el-form-item label="標題">
          <el-input v-model="form.title" placeholder="請輸入通知標題"></el-input>
        </el-form-item>
        <el-form-item label="內容">
          <el-input type="textarea" v-model="form.content" :rows="6" placeholder="請輸入通知內容"></el-input>
        </el-form-item>
        <el-form-item label="相關類型">
          <el-input v-model="form.relatedType" placeholder="例如: ORDER, MOVIE (可選)"></el-input>
        </el-form-item>
        <el-form-item label="相關ID">
          <el-input v-model="form.relatedId" placeholder="相關項目的ID (可選)"></el-input>
        </el-form-item>
        <el-form-item label="狀態">
          <el-switch v-model="form.isActive" active-text="啟用" inactive-text="停用"/>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible=false">取消</el-button>
        <el-button type="primary" @click="save">儲存</el-button>
      </template>
    </el-dialog>

    <!-- 推送對話框 -->
    <el-dialog v-model="sendDialogVisible" title="推送通知給用戶" width="500px">
      <el-form label-width="100px">
        <el-form-item label="推送方式">
          <el-radio-group v-model="sendType">
            <el-radio label="all">全部用戶</el-radio>
            <el-radio label="specific">指定用戶</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="sendType === 'specific'" label="用戶ID">
          <el-input 
            v-model="userIds" 
            type="textarea" 
            :rows="3" 
            placeholder="請輸入用戶ID,多個ID用逗號分隔,例如: 1,2,3"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="sendDialogVisible=false">取消</el-button>
        <el-button type="primary" @click="confirmSend">確認推送</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
// TODO: 需要創建 notificationService
// import notificationService from '../services/notificationService'

const list = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const query = ref('')
const dialogVisible = ref(false)
const sendDialogVisible = ref(false)
const sendType = ref('all')
const userIds = ref('')
const currentNotification = ref(null)
const form = ref({ 
  id: null, 
  type: 'SYSTEM', 
  title: '', 
  content: '', 
  relatedType: '', 
  relatedId: '', 
  isActive: true 
})
const formRef = ref(null)

async function fetchList() {
  // TODO: 實際調用後端 API
  // const params = { 
  //   page: page.value, 
  //   size: pageSize.value
  // }
  // if (query.value && query.value.trim()) {
  //   params.q = query.value
  // }
  
  // try {
  //   const res = await notificationService.list(params)
  //   if (res && res.data) {
  //     list.value = res.data.data || res.data || []
  //     total.value = res.data.total || (list.value.length)
  //   }
  // } catch (error) {
  //   console.error('Failed to fetch notifications:', error)
  //   ElMessage.error('載入通知失敗')
  // }

  // 模擬數據
  list.value = [
    {
      id: 1,
      type: 'SYSTEM',
      title: '系統維護通知',
      content: '系統將於本週日凌晨2:00-4:00進行維護',
      relatedType: null,
      relatedId: null,
      isActive: true,
      createdAt: '2024-11-15 10:00:00'
    },
    {
      id: 2,
      type: 'ORDER',
      title: '訂單確認',
      content: '您的訂單已確認,請準時入場觀影',
      relatedType: 'ORDER',
      relatedId: '1',
      isActive: true,
      createdAt: '2024-11-15 14:30:00'
    }
  ]
  total.value = list.value.length
}

function onPageChange(p) {
  page.value = p
  fetchList()
}

function openCreate() {
  form.value = { 
    id: null, 
    type: 'SYSTEM', 
    title: '', 
    content: '', 
    relatedType: '', 
    relatedId: '', 
    isActive: true 
  }
  dialogVisible.value = true
}

function openEdit(row) {
  form.value = Object.assign({}, row)
  dialogVisible.value = true
}

async function save() {
  // TODO: 實際調用後端 API
  // try {
  //   if (form.value.id) {
  //     await notificationService.update(form.value.id, form.value)
  //     ElMessage.success('更新成功')
  //   } else {
  //     await notificationService.create(form.value)
  //     ElMessage.success('新增成功')
  //   }
  //   dialogVisible.value = false
  //   fetchList()
  // } catch (error) {
  //   console.error('Failed to save notification:', error)
  //   ElMessage.error('儲存失敗')
  // }

  console.log('儲存通知:', form.value)
  ElMessage.success(form.value.id ? '更新成功' : '新增成功')
  dialogVisible.value = false
  fetchList()
}

async function remove(row) {
  // TODO: 實際調用後端 API
  // try {
  //   await notificationService.delete(row.id)
  //   ElMessage.success('刪除成功')
  //   fetchList()
  // } catch (error) {
  //   console.error('Failed to delete notification:', error)
  //   ElMessage.error('刪除失敗')
  // }

  console.log('刪除通知:', row.id)
  ElMessage.success('刪除成功')
  fetchList()
}

function sendToUsers(row) {
  currentNotification.value = row
  sendType.value = 'all'
  userIds.value = ''
  sendDialogVisible.value = true
}

async function confirmSend() {
  // TODO: 實際調用後端 API 推送通知
  // try {
  //   const data = {
  //     notificationId: currentNotification.value.id,
  //     type: sendType.value,
  //     userIds: sendType.value === 'specific' ? userIds.value.split(',').map(id => id.trim()) : null
  //   }
  //   await notificationService.send(data)
  //   ElMessage.success('推送成功')
  //   sendDialogVisible.value = false
  // } catch (error) {
  //   console.error('Failed to send notification:', error)
  //   ElMessage.error('推送失敗')
  // }

  console.log('推送通知:', {
    notificationId: currentNotification.value.id,
    type: sendType.value,
    userIds: sendType.value === 'specific' ? userIds.value.split(',') : 'all'
  })
  ElMessage.success('推送成功')
  sendDialogVisible.value = false
}

onMounted(fetchList)
</script>
