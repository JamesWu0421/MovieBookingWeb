<template>
  <div class="notification-center">
    <div class="header">
      <h1>通知中心</h1>
      <div class="actions">
        <button 
          class="filter-btn" 
          :class="{ active: notificationStore.unreadOnly }"
          @click="notificationStore.toggleUnreadFilter()"
        >
          <span class="icon">📬</span>
          {{ notificationStore.unreadOnly ? '顯示全部' : '只顯示未讀' }}
        </button>
        <button 
          v-if="notificationStore.hasUnread" 
          class="mark-all-btn"
          @click="handleMarkAllAsRead"
        >
          <span class="icon">✓</span>
          全部標記為已讀
        </button>
      </div>
    </div>

    <!-- 載入中狀態 -->
    <div v-if="notificationStore.loading && notificationStore.notifications.length === 0" class="loading">
      <div class="spinner"></div>
      <p>載入中...</p>
    </div>

    <!-- 空狀態 -->
    <div v-else-if="notificationStore.notifications.length === 0" class="empty-state">
      <div class="empty-icon">🔔</div>
      <p v-if="notificationStore.unreadOnly">目前沒有未讀通知</p>
      <p v-else>目前沒有任何通知</p>
    </div>

    <!-- 通知列表 -->
    <div v-else class="notification-list">
      <div
        v-for="notification in notificationStore.notifications"
        :key="notification.id"
        class="notification-item"
        :class="{ unread: !notification.isRead }"
        @click="handleNotificationClick(notification)"
      >
        <div class="notification-icon">
          <span v-if="notification.type === 'SYSTEM'">⚙️</span>
          <span v-else-if="notification.type === 'PROMOTION'">🎉</span>
          <span v-else-if="notification.type === 'MOVIE'">🎬</span>
          <span v-else-if="notification.type === 'ORDER'">🎫</span>
          <span v-else>📢</span>
        </div>

        <div class="notification-content">
          <div class="notification-header">
            <h3 class="notification-title">{{ notification.title }}</h3>
            <span v-if="!notification.isRead" class="unread-badge">未讀</span>
          </div>
          <p class="notification-message">{{ notification.content }}</p>
          <div class="notification-footer">
            <span class="notification-type">{{ getTypeLabel(notification.type) }}</span>
            <span class="notification-time">{{ formatTime(notification.createdAt) }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 分頁 -->
    <div v-if="notificationStore.total > notificationStore.pageSize" class="pagination">
      <button
        class="page-btn"
        :disabled="notificationStore.currentPage === 1"
        @click="notificationStore.changePage(notificationStore.currentPage - 1)"
      >
        上一頁
      </button>
      <span class="page-info">
        第 {{ notificationStore.currentPage }} 頁 / 共 {{ totalPages }} 頁
      </span>
      <button
        class="page-btn"
        :disabled="notificationStore.currentPage >= totalPages"
        @click="notificationStore.changePage(notificationStore.currentPage + 1)"
      >
        下一頁
      </button>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useNotificationStore } from '../stores/notification'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const notificationStore = useNotificationStore()

// 計算總頁數
const totalPages = computed(() => {
  return Math.ceil(notificationStore.total / notificationStore.pageSize)
})

// 格式化時間
function formatTime(dateString) {
  if (!dateString) return ''
  
  const date = new Date(dateString)
  const now = new Date()
  const diff = now - date
  
  // 少於1分鐘
  if (diff < 60000) {
    return '剛剛'
  }
  
  // 少於1小時
  if (diff < 3600000) {
    const minutes = Math.floor(diff / 60000)
    return `${minutes}分鐘前`
  }
  
  // 少於24小時
  if (diff < 86400000) {
    const hours = Math.floor(diff / 3600000)
    return `${hours}小時前`
  }
  
  // 少於7天
  if (diff < 604800000) {
    const days = Math.floor(diff / 86400000)
    return `${days}天前`
  }
  
  // 超過7天，顯示完整日期
  return date.toLocaleString('zh-TW', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  })
}

// 獲取類型標籤
function getTypeLabel(type) {
  const typeMap = {
    SYSTEM: '系統通知',
    PROMOTION: '優惠活動',
    MOVIE: '電影通知',
    ORDER: '訂單通知',
  }
  return typeMap[type] || '其他'
}

// 處理通知點擊
async function handleNotificationClick(notification) {
  try {
    // 如果未讀，標記為已讀
    if (!notification.isRead) {
      await notificationStore.markAsRead(notification.id)
    }

    // 根據通知類型跳轉到相關頁面
    if (notification.relatedType === 'EVENT' && notification.relatedId) {
      router.push(`/events/${notification.relatedId}`)
    } else if (notification.relatedType === 'MOVIE' && notification.relatedId) {
      router.push(`/movies/${notification.relatedId}`)
    } else if (notification.relatedType === 'ORDER' && notification.relatedId) {
      // 如果有訂單詳情頁面，可以跳轉到那裡
      // router.push(`/orders/${notification.relatedId}`)
      ElMessage.info('訂單相關功能開發中')
    }
  } catch (error) {
    console.error('處理通知失敗:', error)
    ElMessage.error('操作失敗，請稍後再試')
  }
}

// 標記全部為已讀
async function handleMarkAllAsRead() {
  try {
    await ElMessageBox.confirm(
      '確定要將所有通知標記為已讀嗎？',
      '確認',
      {
        confirmButtonText: '確定',
        cancelButtonText: '取消',
        type: 'info',
      }
    )

    await notificationStore.markAllAsRead()
    ElMessage.success('已將所有通知標記為已讀')
    
    // 重新載入列表
    await notificationStore.fetchNotifications(true)
  } catch (error) {
    if (error !== 'cancel') {
      console.error('標記全部已讀失敗:', error)
      ElMessage.error('操作失敗，請稍後再試')
    }
  }
}

// 組件掛載時載入通知
onMounted(async () => {
  await notificationStore.fetchNotifications(true)
  await notificationStore.fetchUnreadCount()
})

// 組件卸載時停止自動刷新
onUnmounted(() => {
  notificationStore.stopAutoRefresh()
})
</script>
<style scoped>
.notification-center {
  max-width: 900px;
  margin: 0 auto;
  padding: 24px;
  min-height: calc(100vh - 200px);
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 2px solid #e5e7eb;
}

.header h1 {
  font-size: 28px;
  font-weight: 700;
  color: #1f2937;
  margin: 0;
}

.actions {
  display: flex;
  gap: 12px;
}

.filter-btn,
.mark-all-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  background: white;
  color: #374151;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
}

.filter-btn:hover,
.mark-all-btn:hover {
  background: #f9fafb;
  border-color: #9ca3af;
}

.filter-btn.active {
  background: #244060;
  color: white;
  border-color: #244060;
}

.mark-all-btn {
  background: #10b981;
  color: white;
  border-color: #10b981;
}

.mark-all-btn:hover {
  background: #059669;
  border-color: #059669;
}

.icon {
  font-size: 16px;
}

/* 載入狀態 */
.loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  color: #6b7280;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 3px solid #e5e7eb;
  border-top-color: #244060;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 16px;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* 空狀態 */
.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #6b7280;
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 16px;
  opacity: 0.5;
}

.empty-state p {
  font-size: 16px;
  margin: 0;
}

/* 通知列表 */
.notification-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.notification-item {
  display: flex;
  gap: 16px;
  padding: 16px;
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
}

.notification-item:hover {
  background: #f9fafb;
  border-color: #d1d5db;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.notification-item.unread {
  background: #eff6ff;
  border-color: #3b82f6;
}

.notification-item.unread:hover {
  background: #dbeafe;
}

.notification-icon {
  flex-shrink: 0;
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f3f4f6;
  border-radius: 12px;
  font-size: 24px;
}

.notification-item.unread .notification-icon {
  background: #dbeafe;
}

.notification-content {
  flex: 1;
  min-width: 0;
}

.notification-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.notification-title {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  margin: 0;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.unread-badge {
  flex-shrink: 0;
  padding: 2px 8px;
  background: #3b82f6;
  color: white;
  font-size: 11px;
  border-radius: 999px;
  font-weight: 500;
}

.notification-message {
  font-size: 14px;
  color: #6b7280;
  margin: 0 0 12px 0;
  line-height: 1.5;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.notification-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: #9ca3af;
}

.notification-type {
  padding: 2px 8px;
  background: #f3f4f6;
  border-radius: 4px;
  font-weight: 500;
}

.notification-time {
  font-weight: 400;
}

/* 分頁 */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16px;
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid #e5e7eb;
}

.page-btn {
  padding: 8px 16px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  background: white;
  color: #374151;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
}

.page-btn:hover:not(:disabled) {
  background: #f9fafb;
  border-color: #9ca3af;
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  font-size: 14px;
  color: #6b7280;
}

/* 響應式設計 */
@media (max-width: 768px) {
  .notification-center {
    padding: 16px;
  }

  .header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .actions {
    width: 100%;
    flex-direction: column;
  }

  .filter-btn,
  .mark-all-btn {
    width: 100%;
    justify-content: center;
  }

  .notification-icon {
    width: 40px;
    height: 40px;
    font-size: 20px;
  }

  .notification-title {
    font-size: 14px;
  }

  .notification-message {
    font-size: 13px;
  }
}
</style>