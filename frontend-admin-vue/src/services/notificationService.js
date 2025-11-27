import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

const notificationService = {
  /**
   * 獲取通知列表
   */
  async getNotifications(page = 1, size = 10, query = '', type = '') {
    try {
      const response = await axios.get(`${API_BASE_URL}/notifications`, {
        params: { page, size, query, type }
      });
      return response.data;
    } catch (error) {
      console.error('獲取通知列表失敗:', error);
      throw error;
    }
  },

  /**
   * 根據 ID 獲取通知
   */
  async getNotificationById(id) {
    try {
      const response = await axios.get(`${API_BASE_URL}/notifications/${id}`);
      return response.data;
    } catch (error) {
      console.error('獲取通知詳情失敗:', error);
      throw error;
    }
  },

  /**
   * 創建通知
   */
  async createNotification(data) {
    try {
      const response = await axios.post(`${API_BASE_URL}/notifications`, data);
      return response.data;
    } catch (error) {
      console.error('創建通知失敗:', error);
      throw error;
    }
  },

  /**
   * 更新通知
   */
  async updateNotification(id, data) {
    try {
      const response = await axios.put(`${API_BASE_URL}/notifications/${id}`, data);
      return response.data;
    } catch (error) {
      console.error('更新通知失敗:', error);
      throw error;
    }
  },

  /**
   * 刪除通知
   */
  async deleteNotification(id) {
    try {
      const response = await axios.delete(`${API_BASE_URL}/notifications/${id}`);
      return response.data;
    } catch (error) {
      console.error('刪除通知失敗:', error);
      throw error;
    }
  },

  /**
   * 推送通知
   */
  async sendNotification(data) {
    try {
      const response = await axios.post(`${API_BASE_URL}/notifications/${data.notificationId}/send`, data);
      return response.data;
    } catch (error) {
      console.error('推送通知失敗:', error);
      throw error;
    }
  },

  /**
   * 【新功能】快速創建並推送通知
   */
  async quickCreate(data) {
    try {
      const endpoint = data.sourceType === 'EVENT'
        ? `${API_BASE_URL}/notifications/quick/event`
        : `${API_BASE_URL}/notifications/quick/movie`;

      const response = await axios.post(endpoint, data);
      return response.data;
    } catch (error) {
      console.error('快速創建失敗:', error);
      throw error;
    }
  },

  /**
   * 【新功能】獲取活動列表
   */
  async getEventSources(category = '') {
    try {
      const response = await axios.get(`${API_BASE_URL}/notifications/sources/events`, {
        params: { category }
      });
      return response.data;
    } catch (error) {
      console.error('獲取活動列表失敗:', error);
      throw error;
    }
  },

  /**
   * 【新功能】獲取電影列表
   */
  async getMovieSources(published = true) {
    try {
      const response = await axios.get(`${API_BASE_URL}/notifications/sources/movies`, {
        params: { published }
      });
      return response.data;
    } catch (error) {
      console.error('獲取電影列表失敗:', error);
      throw error;
    }
  },

  /**
   * 獲取用戶通知列表
   */
  async getUserNotifications(userId, page = 1, size = 10, unreadOnly = false) {
    try {
      const response = await axios.get(`${API_BASE_URL}/notifications/user/${userId}`, {
        params: { page, size, unreadOnly }
      });
      return response.data;
    } catch (error) {
      console.error('獲取用戶通知失敗:', error);
      throw error;
    }
  },

  /**
   * 標記通知為已讀
   */
  async markAsRead(userId, notificationId) {
    try {
      const response = await axios.put(
        `${API_BASE_URL}/notifications/user/${userId}/notification/${notificationId}/read`
      );
      return response.data;
    } catch (error) {
      console.error('標記已讀失敗:', error);
      throw error;
    }
  },

  /**
   * 獲取未讀通知數量
   */
  async getUnreadCount(userId) {
    try {
      const response = await axios.get(`${API_BASE_URL}/notifications/user/${userId}/unread-count`);
      return response.data;
    } catch (error) {
      console.error('獲取未讀數量失敗:', error);
      throw error;
    }
  }
};

export default notificationService;