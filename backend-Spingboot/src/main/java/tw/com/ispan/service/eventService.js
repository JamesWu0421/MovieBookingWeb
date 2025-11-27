import api from './api'

export default {
  // 查詢活動列表（可依 category 篩選）
  list(params) {
    return api.get('/events', { params })
  },
  
  // 查詢單一活動
  get(id) {
    return api.get(`/events/${id}`)
  },
  
  // 新增活動
  create(data) {
    return api.post('/events', data)
  },
  
  // 更新活動
  update(id, data) {
    return api.put(`/events/${id}`, data)
  },
  
  // 刪除活動
  delete(id) {
    return api.delete(`/events/${id}`)
  },

  // === 便捷方法（給前端用戶端使用）===

  // 查詢進行中的優惠活動
  getActivePromotions() {
    return api.get('/events/active-promotions')
  },

  // 查詢進行中的公告
  getActiveAnnouncements() {
    return api.get('/events/active-announcements')
  },

  // 驗證優惠券
  validateCoupon(couponCode, orderAmount) {
    return api.post('/events/validate-coupon', null, {
      params: { couponCode, orderAmount }
    })
  }
}
