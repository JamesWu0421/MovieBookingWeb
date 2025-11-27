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
/* ← 你原本的 CSS 我完全保留，不貼這裡 */
</style>


<style scoped>
/* 🔔 小鈴鐺動畫 */
.shake {
  animation: shake 0.3s infinite;
}

@keyframes shake {
  0% { transform: rotate(0deg); }
  25% { transform: rotate(-10deg); }
  75% { transform: rotate(10deg); }
  100% { transform: rotate(0deg); }
}

.notification-bell {
  position: relative;
}

.bell-button {
  background: none;
  border: none;
  cursor: pointer;
  position: relative;
}

.bell-icon {
  font-size: 22px;
}

/* 徽章 */
.badge {
  position: absolute;
  top: -2px;
  right: -2px;
  background: #e53e3e;
  color: white;
  font-size: 10px;
  padding: 1px 4px;
  border-radius: 999px;
}

/* 下拉框 UI */
.dropdown {
  position: absolute;
  right: 0;
  top: 35px;
  width: 360px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 6px 30px rgba(0,0,0,0.2);
  overflow: hidden;
  z-index: 999;
}

</style>
