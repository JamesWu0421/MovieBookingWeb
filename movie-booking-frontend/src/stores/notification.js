// 增強版通知 Store - 加入瀏覽器通知和聲音提示
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import notificationService from '../services/notificationService'

export const useNotificationStore = defineStore('notification', () => {
  const notifications = ref([])
  const unreadCount = ref(0)
  const loading = ref(false)
  
  // 🆕 增強功能
  const browserNotificationEnabled = ref(false)
  const soundEnabled = ref(true)
  const lastUnreadCount = ref(0)

  const hasUnread = computed(() => unreadCount.value > 0)

  // 🆕 請求瀏覽器通知權限
  async function requestNotificationPermission() {
    if (!('Notification' in window)) {
      console.log('此瀏覽器不支援桌面通知')
      return false
    }

    if (Notification.permission === 'granted') {
      browserNotificationEnabled.value = true
      return true
    }

    if (Notification.permission !== 'denied') {
      const permission = await Notification.requestPermission()
      browserNotificationEnabled.value = (permission === 'granted')
      return browserNotificationEnabled.value
    }

    return false
  }

  // 🆕 顯示瀏覽器通知
  function showBrowserNotification(title, body, icon = '🔔') {
    if (!browserNotificationEnabled.value) return

    const notification = new Notification(title, {
      body: body,
      icon: icon,
      badge: icon,
      tag: 'cinema-notification', // 避免重複通知
      requireInteraction: false,
      silent: !soundEnabled.value,
    })

    // 點擊通知時聚焦視窗
    notification.onclick = () => {
      window.focus()
      notification.close()
    }

    // 3秒後自動關閉
    setTimeout(() => notification.close(), 3000)
  }

  // 🆕 播放通知音效
  function playNotificationSound() {
    if (!soundEnabled.value) return

    try {
      // 使用 Web Audio API 生成簡單的提示音
      const audioContext = new (window.AudioContext || window.webkitAudioContext)()
      const oscillator = audioContext.createOscillator()
      const gainNode = audioContext.createGain()

      oscillator.connect(gainNode)
      gainNode.connect(audioContext.destination)

      oscillator.frequency.value = 800 // 頻率
      oscillator.type = 'sine'
      
      gainNode.gain.setValueAtTime(0.3, audioContext.currentTime)
      gainNode.gain.exponentialRampToValueAtTime(0.01, audioContext.currentTime + 0.1)

      oscillator.start(audioContext.currentTime)
      oscillator.stop(audioContext.currentTime + 0.1)
    } catch (error) {
      console.error('播放通知音效失敗:', error)
    }
  }

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
      const newCount = res.unreadCount || 0
      
      // 🆕 檢測到新通知
      if (newCount > lastUnreadCount.value) {
        const newNotificationsCount = newCount - lastUnreadCount.value
        
        // 播放音效
        playNotificationSound()
        
        // 顯示瀏覽器通知
        if (newNotificationsCount === 1) {
          showBrowserNotification(
            '📬 收到新通知',
            '您有一則新通知,點擊查看'
          )
        } else {
          showBrowserNotification(
            '📬 收到新通知',
            `您有 ${newNotificationsCount} 則新通知`
          )
        }
      }
      
      lastUnreadCount.value = newCount
      unreadCount.value = newCount
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
        lastUnreadCount.value = unreadCount.value
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
      lastUnreadCount.value = 0
    } catch (e) {
      console.error("全部已讀失敗:", e)
    }
  }

  // 🆕 切換聲音
  function toggleSound() {
    soundEnabled.value = !soundEnabled.value
    localStorage.setItem('notification_sound', soundEnabled.value)
  }

  // 🆕 初始化設定
  function initSettings() {
    // 讀取聲音設定
    const savedSound = localStorage.getItem('notification_sound')
    if (savedSound !== null) {
      soundEnabled.value = savedSound === 'true'
    }

    // 檢查瀏覽器通知權限
    if ('Notification' in window && Notification.permission === 'granted') {
      browserNotificationEnabled.value = true
    }
  }

  /** 30 秒自動刷新 */
  let timer = null
  function startAutoRefresh() {
    if (timer) return
    
    // 初始化設定
    initSettings()
    
    // 初始化未讀數
    fetchUnreadCount()
    
    // 每30秒檢查一次
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
    loading,
    // 🆕 新增屬性
    browserNotificationEnabled,
    soundEnabled,
    // 原有方法
    fetchNotifications,
    fetchUnreadCount,
    markAsRead,
    markAllAsRead,
    startAutoRefresh,
    stopAutoRefresh,
    // 🆕 新增方法
    requestNotificationPermission,
    toggleSound,
    initSettings,
  }
})