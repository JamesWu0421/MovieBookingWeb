import api from './api'

const notificationService = {
  /**
   * 獲取通知列表
   */
  getNotifications(page = 1, size = 10, query = '', type = '') {
    return api.get('/notifications', {
      params: { page, size, query, type }
    })
  },

  /**
   * 根據 ID 獲取通知
   */
  getNotificationById(id) {
    return api.get(`/notifications/${id}`)
  },

  /**
   * 創建通知
   */
  createNotification(data) {
    return api.post('/notifications', data)
  },

  /**
   * 更新通知
   */
  updateNotification(id, data) {
    return api.put(`/notifications/${id}`, data)
  },

  /**
   * 刪除通知
   */
  deleteNotification(id) {
    return api.delete(`/notifications/${id}`)
  },

  /**
   * 推送通知
   */
  sendNotification(data) {
    return api.post(`/notifications/${data.notificationId}/send`, data)
  },

  /**
   * 快速創建並推送通知
   */
  quickCreate(data) {
    const endpoint = data.sourceType === 'EVENT' 
      ? '/notifications/quick/event'
      : '/notifications/quick/movie'
    
    return api.post(endpoint, data)
  },

  /**
   * 獲取活動列表
   */
  getEventSources(category = '') {
    return api.get('/notifications/sources/events', {
      params: { category }
    })
  },

  /**
   * 獲取電影列表
   */
  getMovieSources(published = true) {
    return api.get('/notifications/sources/movies', {
      params: { published }
    })
  },

  /**
   * 獲取用戶通知列表
   */
  getUserNotifications(userId, page = 1, size = 10, unreadOnly = false) {
    return api.get(`/notifications/user/${userId}`, {
      params: { page, size, unreadOnly }
    })
  },

  /**
   * 標記通知為已讀
   */
  markAsRead(userId, notificationId) {
    return api.put(`/notifications/user/${userId}/notification/${notificationId}/read`)
  },

  /**
   * 獲取未讀通知數量
   */
  getUnreadCount(userId) {
    return api.get(`/notifications/user/${userId}/unread-count`)
  }
}

export default notificationService
