// src/utils/request.js
import axios from "axios";
import { ElMessage } from "element-plus";
import router from "../router/index";

const request = axios.create({
  baseURL: import.meta.env.VITE_BASE_URL || "http://localhost:8080",
  timeout: 10000,
});

// 請求攔截器
request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("admin_token");
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// 響應攔截器
request.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    if (error.response) {
      const status = error.response.status;

      // 401 未授權 - token 過期
      if (status === 401) {
        ElMessage.error("登入已過期，請重新登入");
        localStorage.removeItem("admin_token");
        router.push("/login"); // 改成 /login
      }
      // 403 權限不足
      else if (status === 403) {
        ElMessage.error("權限不足");
      }
      // 500 伺服器錯誤
      else if (status === 500) {
        ElMessage.error("伺服器錯誤");
      }
    } else {
      ElMessage.error("網路連線失敗");
    }

    return Promise.reject(error);
  }
);

export default request;
