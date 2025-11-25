import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080/api", // 後端 Spring Boot 預設 API
  timeout: 100000, // 請求超時時間
});

export default api;
