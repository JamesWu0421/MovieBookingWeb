import request from "../utils/request";

const promotionService = {
  /**
   * 取得活動列表（優惠/公告）
   */
  list(params) {
    return request.get("/events", { params });
  },

  /**
   * 取得單一活動
   */
  get(id) {
    return request.get(`/events/${id}`);
  },

  /**
   * 新增活動
   */
  create(data) {
    return request.post("/events", data);
  },

  /**
   * 更新活動
   */
  update(id, data) {
    return request.put(`/events/${id}`, data);
  },

  /**
   * 刪除活動
   */
  delete(id) {
    return request.delete(`/events/${id}`);
  },

  /**
   * 後台不會用到（但保留也沒關係）
   */
  getActivePromotions() {
    return request.get("/events/active", {
      params: { category: "promotion" },
    });
  },

  validateCoupon(couponCode, orderAmount = 0) {
    return request.post("/events/validate-coupon", {
      couponCode,
      orderAmount,
    });
  },

  incrementViewCount(id) {
    return request.post(`/events/${id}/view`);
  },

  useCoupon(eventId, userId) {
    return request.post(`/events/${eventId}/use-coupon`, { userId });
  },
};

export default promotionService;
