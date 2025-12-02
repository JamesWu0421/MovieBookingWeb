import axios from "axios";
import { ElMessage } from "element-plus";

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || "http://localhost:8080/api/admin",
  timeout: 100000,
  headers: {
    "Content-Type": "application/json",
  },
});

// ✅ 請求攔截器 - 自動添加 Token
api.interceptors.request.use(
  (config) => {
    // 從 localStorage 讀取 token
    const token = localStorage.getItem("admin_token");

    if (token) {
      // 添加 Authorization header
      config.headers["Authorization"] = `Bearer ${token}`;
    }

    return config;
  },
  (error) => {
    console.error("請求錯誤:", error);
    return Promise.reject(error);
  }
);

// ✅ 回應攔截器 - 統一錯誤處理
api.interceptors.response.use(
  (response) => {
    // 直接返回 data，簡化組件中的使用
    return response.data;
  },
  (error) => {
    console.error("API 錯誤:", error);

    if (error.response) {
      const status = error.response.status;
      const message = error.response.data?.message || "伺服器錯誤";

      switch (status) {
        case 401:
          ElMessage.error("未授權，請重新登入");
          localStorage.removeItem("admin_token");
          localStorage.removeItem("admin_name");
          localStorage.removeItem("admin_email");
          // 跳轉到登入頁
          window.location.href = "/login";
          break;
        case 403:
          ElMessage.error("沒有權限執行此操作");
          break;
        case 404:
          ElMessage.error("請求的資源不存在");
          break;
        case 500:
          ElMessage.error("伺服器內部錯誤");
          break;
        default:
          ElMessage.error(message);
      }
    } else if (error.request) {
      ElMessage.error("網路錯誤，請檢查連線");
    } else {
      ElMessage.error(`發生錯誤: ${error.message}`);
    }

    return Promise.reject(error);
  }
);

export default api;
