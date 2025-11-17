import api from './api'

const notificationService = {
  /**
   * 獲取通知列表
   * @param {Object} params - 查詢參數
   * @param {number} params.page - 頁碼
   * @param {number} params.size - 每頁數量
   * @param {string} params.q - 搜尋關鍵字(可選)
   * @param {string} params.type - 通知類型(可選): SYSTEM, ORDER, PROMOTION等
   */
  async list(params) {
    try {
      const response = await api.get('/notifications', { params })
      return response
    } catch (error) {
      console.error('獲取通知列表失敗:', error)
      throw error
    }
  },

  /**
   * 獲取單個通知詳情
   * @param {number} id - 通知ID
   */
  async get(id) {
    try {
      const response = await api.get(`/notifications/${id}`)
      return response
    } catch (error) {
      console.error('獲取通知詳情失敗:', error)
      throw error
    }
  },

  /**
   * 創建新通知
   * @param {Object} data - 通知數據
   * @param {string} data.type - 通知類型: SYSTEM, ORDER, PROMOTION, MOVIE, PAYMENT
   * @param {string} data.title - 通知標題
   * @param {string} data.content - 通知內容
   * @param {string} data.relatedType - 相關類型(可選)
   * @param {string} data.relatedId - 相關ID(可選)
   * @param {boolean} data.isActive - 是否啟用
   */
  async create(data) {
    try {
      const response = await api.post('/notifications', data)
      return response
    } catch (error) {
      console.error('創建通知失敗:', error)
      throw error
    }
  },

  /**
   * 更新通知
   * @param {number} id - 通知ID
   * @param {Object} data - 更新的數據
   */
  async update(id, data) {
    try {
      const response = await api.put(`/notifications/${id}`, data)
      return response
    } catch (error) {
      console.error('更新通知失敗:', error)
      throw error
    }
  },

  /**
   * 刪除通知
   * @param {number} id - 通知ID
   */
  async delete(id) {
    try {
      const response = await api.delete(`/notifications/${id}`)
      return response
    } catch (error) {
      console.error('刪除通知失敗:', error)
      throw error
    }
  },

  /**
   * 推送通知給用戶
   * @param {Object} data - 推送數據
   * @param {number} data.notificationId - 通知ID
   * @param {string} data.type - 推送類型: 'all' 或 'specific'
   * @param {Array<number>} data.userIds - 用戶ID列表(當type為'specific'時必填)
   */
  async send(data) {
    try {
      const response = await api.post('/notifications/send', data)
      return response
    } catch (error) {
      console.error('推送通知失敗:', error)
      throw error
    }
  },

  /**
   * 獲取用戶的通知列表
   * @param {number} userId - 用戶ID
   * @param {Object} params - 查詢參數
   * @param {boolean} params.unreadOnly - 只顯示未讀(可選)
   */
  async getUserNotifications(userId, params = {}) {
    try {
      const response = await api.get(`/users/${userId}/notifications`, { params })
      return response
    } catch (error) {
      console.error('獲取用戶通知列表失敗:', error)
      throw error
    }
  },

  /**
   * 標記通知為已讀
   * @param {number} userId - 用戶ID
   * @param {number} notificationId - 通知ID
   */
  async markAsRead(userId, notificationId) {
    try {
      const response = await api.put(`/users/${userId}/notifications/${notificationId}/read`)
      return response
    } catch (error) {
      console.error('標記通知為已讀失敗:', error)
      throw error
    }
  }
}

export default notificationService
