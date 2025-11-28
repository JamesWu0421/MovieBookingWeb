// notificationService.js
import request from "./api";

const notificationService = {
  /**
   * 獲取通知列表
   */
  async getNotifications(page = 1, size = 10, query = "", type = "") {
    try {
      const data = await request.get("/notifications", {
        params: { page, size, query, type },
      });
      return data;
    } catch (error) {
      console.error("獲取通知列表失敗:", error);
      throw error;
    }
  },

  /**
   * 根據 ID 獲取通知
   */
  async getNotificationById(id) {
    try {
      const data = await request.get(`/notifications/${id}`);
      return data;
    } catch (error) {
      console.error("獲取通知詳情失敗:", error);
      throw error;
    }
  },

  /**
   * 創建通知
   */
  async createNotification(data) {
    try {
      const res = await request.post("/notifications", data);
      return res;
    } catch (error) {
      console.error("創建通知失敗:", error);
      throw error;
    }
  },

  /**
   * 更新通知
   */
  async updateNotification(id, data) {
    try {
      const res = await request.put(`/notifications/${id}`, data);
      return res;
    } catch (error) {
      console.error("更新通知失敗:", error);
      throw error;
    }
  },

  /**
   * 刪除通知
   */
  async deleteNotification(id) {
    try {
      const res = await request.delete(`/notifications/${id}`);
      return res;
    } catch (error) {
      console.error("刪除通知失敗:", error);
      throw error;
    }
  },

  /**
   * 推送通知
   */
  async sendNotification(data) {
    try {
      const res = await request.post(
        `/notifications/${data.notificationId}/send`,
        data
      );
      return res;
    } catch (error) {
      console.error("推送通知失敗:", error);
      throw error;
    }
  },

  /**
   * 【新功能】快速創建並推送通知
   */
  async quickCreate(data) {
    try {
      const endpoint =
        data.sourceType === "EVENT"
          ? "/notifications/quick/event"
          : "/notifications/quick/movie";

      const res = await request.post(endpoint, data);
      return res;
    } catch (error) {
      console.error("快速創建失敗:", error);
      throw error;
    }
  },

  /**
   * 【新功能】獲取活動列表
   */
  async getEventSources(category = "") {
    try {
      const data = await request.get("/notifications/sources/events", {
        params: { category },
      });
      return data;
    } catch (error) {
      console.error("獲取活動列表失敗:", error);
      throw error;
    }
  },

  /**
   * 【新功能】獲取電影列表
   */
  async getMovieSources(published = true) {
    try {
      const data = await request.get("/notifications/sources/movies", {
        params: { published },
      });
      return data;
    } catch (error) {
      console.error("獲取電影列表失敗:", error);
      throw error;
    }
  },

  /**
   * 獲取用戶通知列表
   */
  async getUserNotifications(userId, page = 1, size = 10, unreadOnly = false) {
    try {
      const data = await request.get(`/notifications/user/${userId}`, {
        params: { page, size, unreadOnly },
      });
      return data;
    } catch (error) {
      console.error("獲取用戶通知失敗:", error);
      throw error;
    }
  },

  /**
   * 標記通知為已讀
   */
  async markAsRead(userId, notificationId) {
    try {
      const data = await request.put(
        `/notifications/user/${userId}/notification/${notificationId}/read`
      );
      return data;
    } catch (error) {
      console.error("標記已讀失敗:", error);
      throw error;
    }
  },

  /**
   * 獲取未讀通知數量
   */
  async getUnreadCount(userId) {
    try {
      const data = await request.get(
        `/notifications/user/${userId}/unread-count`
      );
      return data;
    } catch (error) {
      console.error("獲取未讀數量失敗:", error);
      throw error;
    }
  },
};

export default notificationService;
