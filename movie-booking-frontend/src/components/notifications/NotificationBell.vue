<template>
  <div class="notification-bell">
    <button class="bell-button" @click="toggleDropdown">
      <span class="bell-icon">🔔</span>
      <span v-if="notificationStore.hasUnread" class="badge">
        {{ notificationStore.unreadCount > 99 ? '99+' : notificationStore.unreadCount }}
      </span>
    </button>

    <transition name="dropdown">
      <div v-if="showDropdown" class="dropdown" v-click-outside="closeDropdown">
        <div class="dropdown-header">
          <h3>通知</h3>
          <button v-if="notificationStore.hasUnread" class="mark-all" @click="markAllAsRead">
            全部標記已讀
          </button>
        </div>

        <div v-if="loading" class="dropdown-loading">
          <div class="spinner"></div>
        </div>

        <div v-else-if="notifications.length" class="notification-list">
          <div
            v-for="n in notifications"
            :key="n.id"
            class="notification-item"
            :class="{ unread: !n.isRead }"
            @click="handleNotificationClick(n)"
          >
            <div class="notification-icon">📢</div>
            <div class="notification-content">
              <p class="notification-title">{{ n.title }}</p>
              <p class="notification-message">{{ n.message }}</p>
              <span class="notification-time">{{ formatTime(n.createdAt) }}</span>
            </div>
          </div>
        </div>

        <div v-else class="dropdown-empty">目前沒有通知</div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useNotificationStore } from "../../stores/notification";
import { useAuthStore } from "../../stores/login";

const notificationStore = useNotificationStore()
const authStore = useAuthStore()

const showDropdown = ref(false)
const notifications = ref([])
const loading = ref(false)

async function toggleDropdown() {
  showDropdown.value = !showDropdown.value
  if (showDropdown.value) loadRecent()
}

function closeDropdown() {
  showDropdown.value = false
}

async function loadRecent() {
  loading.value = true
  await notificationStore.fetchNotifications(false)
  notifications.value = notificationStore.notifications.slice(0, 5)
  loading.value = false
}

async function handleNotificationClick(n) {
  closeDropdown()
  await notificationStore.markAsRead(n.id)
}

async function markAllAsRead() {
  await notificationStore.markAllAsRead()
  await loadRecent()
}

function formatTime(t) {
  const d = new Date(t)
  return d.toLocaleString("zh-TW")
}

onMounted(() => {
  if (authStore.isAuthenticated) {
    notificationStore.startAutoRefresh()
    notificationStore.fetchUnreadCount()
  }
})
</script>

<style scoped>
/* 整體位置 */
.notification-bell {
  position: relative;
}

/* 鈴鐺按鈕 */
.bell-button {
  background: none;
  border: none;
  cursor: pointer;
  position: relative;
}

.bell-icon {
  font-size: 22px;
}

/* 未讀徽章 */
.badge {
  position: absolute;
  top: -4px;
  right: -6px;
  background: #ef4444;
  color: white;
  font-size: 11px;
  padding: 2px 5px;
  border-radius: 999px;
  font-weight: bold;
}

/* 下拉框主體 */
.dropdown {
  position: absolute;
  right: 0;
  top: 38px;
  width: 380px;
  background: white;
  border-radius: 14px;
  box-shadow: 0 6px 28px rgba(0,0,0,0.18);
  padding: 16px 0;
  overflow: hidden;
  z-index: 999;
}

/* 標題列 */
.dropdown-header {
  padding: 0 20px 12px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #e5e7eb;
}

.dropdown-header h3 {
  font-size: 18px;
  font-weight: 700;
  margin: 0;
}

.mark-all {
  background: none;
  border: none;
  color: #2563eb;
  font-size: 14px;
  cursor: pointer;
}

.mark-all:hover {
  text-decoration: underline;
}

/* loading */
.dropdown-loading {
  padding: 40px;
  text-align: center;
}

.spinner {
  width: 32px;
  height: 32px;
  border: 4px solid #e5e7eb;
  border-top-color: #3b82f6;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* 通知列表 */
.notification-list {
  max-height: 380px;
  overflow-y: auto;
  padding: 10px 0;
}

/* 單一通知卡片 */
.notification-item {
  display: flex;
  gap: 14px;
  padding: 16px 20px;
  cursor: pointer;
  transition: 0.18s;
  border-bottom: 1px solid #f1f5f9;
}

.notification-item:hover {
  background: #f8fafc;
}

.notification-item.unread {
  background: #eff6ff;
}

.notification-item.unread:hover {
  background: #e0edff;
}

/* 左側 icon */
.notification-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  background: #f1f5f9;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  flex-shrink: 0;
}

/* 內容文字 */
.notification-content {
  flex: 1;
}

.notification-title {
  font-size: 15px;
  font-weight: 600;
  color: #1f2937;
  margin: 0;
  margin-bottom: 4px;
}

.notification-message {
  font-size: 13px;
  color: #6b7280;
  margin: 0;
  line-height: 1.4;
  margin-bottom: 6px;
  display: -webkit-box;
  -webkit-line-clamp: 2; /* 兩行後省略 */
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.notification-time {
  font-size: 12px;
  color: #94a3b8;
}

/* 空狀態 */
.dropdown-empty {
  text-align: center;
  padding: 30px;
  color: #9ca3af;
}

/* 下拉動畫 */
.dropdown-enter-active,
.dropdown-leave-active {
  transition: opacity 0.15s ease, transform 0.15s ease;
}

.dropdown-enter-from {
  opacity: 0;
  transform: translateY(-10px);
}

.dropdown-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

</style>
