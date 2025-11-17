<template>
  <div class="user-notifications">
    <!-- 通知圖標和徽章 -->
    <el-badge :value="unreadCount" :hidden="unreadCount === 0" class="notification-badge">
      <el-button circle @click="drawerVisible = true">
        <el-icon><Bell /></el-icon>
      </el-button>
    </el-badge>

    <!-- 通知抽屜 -->
    <el-drawer
      v-model="drawerVisible"
      title="我的通知"
      direction="rtl"
      size="400px"
    >
      <div class="notifications-list">
        <!-- 未讀/全部切換 -->
        <el-radio-group v-model="filterType" @change="fetchNotifications" class="filter-tabs">
          <el-radio-button label="unread">未讀 ({{ unreadCount }})</el-radio-button>
          <el-radio-button label="all">全部</el-radio-button>
        </el-radio-group>

        <!-- 通知列表 -->
        <div class="notification-items">
          <div
            v-for="item in notifications"
            :key="item.id"
            class="notification-item"
            :class="{ 'is-read': item.isRead }"
            @click="handleNotificationClick(item)"
          >
            <!-- 通知類型圖標 -->
            <div class="notification-icon">
              <el-icon v-if="item.type === 'SYSTEM'" color="#409EFF"><InfoFilled /></el-icon>
              <el-icon v-else-if="item.type === 'ORDER'" color="#67C23A"><ShoppingCart /></el-icon>
              <el-icon v-else-if="item.type === 'PROMOTION'" color="#F56C6C"><Promotion /></el-icon>
              <el-icon v-else color="#909399"><Message /></el-icon>
            </div>

            <!-- 通知內容 -->
            <div class="notification-content">
              <div class="notification-title">{{ item.title }}</div>
              <div class="notification-text">{{ item.content }}</div>
              <div class="notification-time">{{ formatTime(item.createdAt) }}</div>
            </div>

            <!-- 未讀標記 -->
            <div v-if="!item.isRead" class="unread-dot"></div>
          </div>

          <!-- 空狀態 -->
          <el-empty v-if="notifications.length === 0" description="暫無通知" />
        </div>

        <!-- 加載更多 -->
        <div class="load-more" v-if="hasMore">
          <el-button @click="loadMore" :loading="loading">載入更多</el-button>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { Bell, InfoFilled, ShoppingCart, Promotion, Message } from '@element-plus/icons-vue'
import notificationService from '../services/notificationService'
import { ElMessage } from 'element-plus'

const drawerVisible = ref(false)
const notifications = ref([])
const filterType = ref('unread') // 'unread' 或 'all'
const loading = ref(false)
const page = ref(1)
const pageSize = ref(20)
const hasMore = ref(true)

// 計算未讀數量
const unreadCount = computed(() => {
  return notifications.value.filter(n => !n.isRead).length
})

// 獲取當前用戶ID (實際應該從登入狀態獲取)
const currentUserId = ref(1) // TODO: 從 store 或 localStorage 獲取

// 獲取通知列表
async function fetchNotifications(reset = false) {
  if (reset) {
    page.value = 1
    notifications.value = []
  }

  loading.value = true
  try {
    const params = {
      page: page.value,
      size: pageSize.value,
      unreadOnly: filterType.value === 'unread'
    }
    
    const response = await notificationService.getUserNotifications(currentUserId.value, params)
    const newNotifications = response.data.data || []
    
    if (reset) {
      notifications.value = newNotifications
    } else {
      notifications.value.push(...newNotifications)
    }
    
    hasMore.value = newNotifications.length === pageSize.value
  } catch (error) {
    console.error('獲取通知失敗:', error)
    ElMessage.error('載入通知失敗')
  } finally {
    loading.value = false
  }
}

// 載入更多
function loadMore() {
  page.value++
  fetchNotifications()
}

// 點擊通知
async function handleNotificationClick(notification) {
  // 如果未讀,標記為已讀
  if (!notification.isRead) {
    try {
      await notificationService.markAsRead(currentUserId.value, notification.id)
      notification.isRead = true
    } catch (error) {
      console.error('標記已讀失敗:', error)
    }
  }

  // 根據通知類型跳轉到相關頁面
  if (notification.relatedType === 'ORDER' && notification.relatedId) {
    // 跳轉到訂單詳情
    // router.push(`/orders/${notification.relatedId}`)
  } else if (notification.relatedType === 'MOVIE' && notification.relatedId) {
    // 跳轉到電影詳情
    // router.push(`/movies/${notification.relatedId}`)
  }
}

// 格式化時間
function formatTime(dateString) {
  const date = new Date(dateString)
  const now = new Date()
  const diff = now - date
  
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)
  
  if (minutes < 1) return '剛剛'
  if (minutes < 60) return `${minutes}分鐘前`
  if (hours < 24) return `${hours}小時前`
  if (days < 7) return `${days}天前`
  
  return date.toLocaleDateString('zh-TW')
}

onMounted(() => {
  fetchNotifications(true)
})
</script>

<style scoped>
.user-notifications {
  position: relative;
}

.notification-badge {
  cursor: pointer;
}

.notifications-list {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.filter-tabs {
  margin-bottom: 16px;
  width: 100%;
}

.notification-items {
  flex: 1;
  overflow-y: auto;
}

.notification-item {
  display: flex;
  align-items: flex-start;
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: background-color 0.2s;
  position: relative;
}

.notification-item:hover {
  background-color: #f5f7fa;
}

.notification-item.is-read {
  opacity: 0.6;
}

.notification-icon {
  flex-shrink: 0;
  margin-right: 12px;
  font-size: 24px;
}

.notification-content {
  flex: 1;
  min-width: 0;
}

.notification-title {
  font-weight: 600;
  margin-bottom: 4px;
  color: #303133;
}

.notification-text {
  font-size: 14px;
  color: #606266;
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.notification-time {
  font-size: 12px;
  color: #909399;
}

.unread-dot {
  position: absolute;
  top: 20px;
  right: 16px;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: #f56c6c;
}

.load-more {
  padding: 16px;
  text-align: center;
  border-top: 1px solid #f0f0f0;
}
</style>
