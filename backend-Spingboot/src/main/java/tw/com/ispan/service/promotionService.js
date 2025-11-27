import api from './api'

const promotionService = {
  /**
   * 獲取促銷活動列表（包含公告）
   * @param {Object} params - 查詢參數
   * @param {string} params.category - 類型篩選: 'promotion' | 'announcement' | 'all'
   * @param {number} params.page - 頁碼
   * @param {number} params.size - 每頁數量
   * @param {string} params.q - 搜尋關鍵字
   */
  list(params) {
    return api.get('/events', { params })
  },

  /**
   * 獲取單一活動詳情
   */
  get(id) {
    return api.get(`/events/${id}`)
  },

  /**
   * 創建促銷活動
   */
  create(data) {
    return api.post('/events', data)
  },

  /**
   * 更新促銷活動
   */
  update(id, data) {
    return api.put(`/events/${id}`, data)
  },

  /**
   * 刪除促銷活動
   */
  delete(id) {
    return api.delete(`/events/${id}`)
  },

  /**
   * 獲取進行中的促銷活動（給用戶前台使用）
   */
  getActivePromotions() {
    return api.get('/events/active', {
      params: { category: 'promotion' }
    })
  },

  /**
   * 驗證優惠碼
   * @param {string} couponCode - 優惠碼
   * @param {number} orderAmount - 訂單金額（用於檢查是否達到最低消費）
   */
  validateCoupon(couponCode, orderAmount = 0) {
    return api.post('/events/validate-coupon', {
      couponCode,
      orderAmount
    })
  },

  /**
   * 增加活動瀏覽次數
   */
  incrementViewCount(id) {
    return api.post(`/events/${id}/view`)
  },

  /**
   * 記錄優惠碼使用
   */
  useCoupon(eventId, userId) {
    return api.post(`/events/${eventId}/use-coupon`, { userId })
  }
}

export default promotionService
