import axios from "axios";
import { useAuthStore } from "../stores/login"; // ✅ 您的路徑
import router from "../router/index"; // ✅ 您的路徑

const request = axios.create({
  baseURL: "http://localhost:8080/api",
  timeout: 10000,
  headers: {
    "Content-Type": "application/json",
  },
});

let isLoggingOut = false;

// 請求攔截器
request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("token");
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// 回應攔截器
request.interceptors.response.use(
  (response) => response.data,
  (error) => {
    if (error.response?.status === 401) {
      console.warn("收到 401 錯誤:", error.config.url);

      // 如果是 logout 請求本身失敗
      if (
        error.config.url.includes("/logout") ||
        error.config.url.includes("/user/logout")
      ) {
        console.log("Logout 請求失敗，直接清理本地狀態");
        if (!isLoggingOut) {
          isLoggingOut = true;
          const authStore = useAuthStore();
          authStore.clearAuth();
          router.push("/login");
          setTimeout(() => {
            isLoggingOut = false;
          }, 1000);
        }
        return Promise.reject(error);
      }

      // 避免重複執行 logout
      if (!isLoggingOut && !error.config._retry) {
        error.config._retry = true;
        isLoggingOut = true;

        console.log("執行 logout...");
        const authStore = useAuthStore();
        authStore.clearAuth();
        router.push("/login");

        setTimeout(() => {
          isLoggingOut = false;
        }, 1000);
      }

      return Promise.reject(new Error("請先登入"));
    }

    // 其他錯誤處理
    const message = error.response?.data?.message; //|| "請求失敗";
    return Promise.reject(new Error(message));
  }
);

export default request;
