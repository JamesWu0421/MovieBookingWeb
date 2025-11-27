import request from "../utils/request";
import { useAuthStore } from "../stores/login";

// 取得 userId（可靠版本）
function getUserId() {
  try {
    const authStore = useAuthStore();
    if (authStore?.user?.id) return authStore.user.id;

    const saved = localStorage.getItem("user");
    if (saved) {
      const obj = JSON.parse(saved);
      if (obj.id) return obj.id;
    }

    return null;
  } catch (e) {
    return null;
  }
}

/** 取得使用者通知（支援 unreadOnly） */
export const getUserNotifications = ({ unreadOnly = false } = {}) => {
  const userId = getUserId();
  if (!userId) return Promise.resolve({ notifications: [] });

  return request({
    url: `/notifications/user/${userId}`,
    method: "get",
    params: { unreadOnly }
  });
};

/** 取得未讀數量 */
export const getUnreadCount = () => {
  const userId = getUserId();
  if (!userId) return Promise.resolve({ unreadCount: 0 });

  return request({
    url: `/notifications/user/${userId}/unread-count`,
    method: "get",
  });
};

/** 單一已讀 */
export const markOneRead = (notificationId) => {
  const userId = getUserId();
  if (!userId) return Promise.resolve();

  return request({
    url: `/notifications/user/${userId}/notification/${notificationId}/read`,
    method: "put",
  });
};

/** 全部已讀 */
export const markAllRead = () => {
  const userId = getUserId();
  if (!userId) return Promise.resolve();

  // ⚠️ 注意：後端目前沒有提供「全部已讀」的 API
  // 臨時方案：在前端循環標記已讀
  return getUserNotifications({ unreadOnly: true })
    .then(response => {
      const notifications = response.content || response.notifications || [];
      const promises = notifications.map(n => markOneRead(n.notificationId || n.id));
      return Promise.all(promises);
    });
};

export default {
  getUserNotifications,
  getUnreadCount,
  markOneRead,
  markAllRead,
};
