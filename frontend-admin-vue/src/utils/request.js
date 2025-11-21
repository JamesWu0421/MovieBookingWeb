// src/utils/request.js
import axios from "axios";

const request = axios.create({
  baseURL: "http://localhost:8080", // 改成你後端實際 baseURL
  timeout: 10000,
});

// 如果有 JWT，這裡自動帶上 Authorization header
request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("token"); // 你存 token 的地方
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

export default request;
