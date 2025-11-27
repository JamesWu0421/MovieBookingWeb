import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import notificationService from '../services/notificationService'

export const useNotificationStore = defineStore('notification', () => {
  const notifications = ref([])
  const unreadCount = ref(0)
  const loading = ref(false)

  const hasUnread = computed(() => unreadCount.value > 0)

  /** 載入通知 */
  async function fetchNotifications(unreadOnly = false) {
    loading.value = true
    try {
      const res = await notificationService.getUserNotifications({ unreadOnly })
      notifications.value = res.notifications || []
    } catch (e) {
      console.error("獲取通知失敗:", e)
    } finally {
      loading.value = false
    }
  }

  /** 未讀數 */
  async function fetchUnreadCount() {
    try {
      const res = await notificationService.getUnreadCount()
      unreadCount.value = res.unreadCount || 0
    } catch (e) {
      unreadCount.value = 0
    }
  }

  /** 單筆已讀 */
  async function markAsRead(id) {
    try {
      await notificationService.markOneRead(id)
      const n = notifications.value.find(n => n.id === id)
      if (n && !n.isRead) {
        n.isRead = true
        unreadCount.value = Math.max(0, unreadCount.value - 1)
      }
    } catch (e) {
      console.error("標記已讀失敗:", e)
    }
  }

  /** 全部已讀 */
  async function markAllAsRead() {
    try {
      await notificationService.markAllRead()
      notifications.value.forEach(n => (n.isRead = true))
      unreadCount.value = 0
    } catch (e) {
      console.error("全部已讀失敗:", e)
    }
  }

  /** 30 秒自動刷新 */
  let timer = null
  function startAutoRefresh() {
    if (timer) return
    fetchUnreadCount()
    timer = setInterval(fetchUnreadCount, 30000)
  }

  function stopAutoRefresh() {
    if (timer) {
      clearInterval(timer)
      timer = null
    }
  }

  return {
    notifications,
    unreadCount,
    hasUnread,
    fetchNotifications,
    fetchUnreadCount,
    markAsRead,
    markAllAsRead,
    startAutoRefresh,
    stopAutoRefresh,
  }
})
