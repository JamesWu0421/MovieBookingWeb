import axios from 'axios'
import { ElMessage } from 'element-plus'

// 從環境變數讀取 API 基礎路徑，如果沒有則使用預設值
const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 請求攔截器 - 添加認證 token
api.interceptors.request.use(
  config => {
    // 從 localStorage 讀取 token（如果有登入功能）
    const token = localStorage.getItem('admin_token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    console.error('Request Error:', error)
    return Promise.reject(error)
  }
)

// 回應攔截器 - 統一錯誤處理
api.interceptors.response.use(
  response => {
    // 直接返回 data，簡化組件中的使用
    return response.data
  },
  error => {
    console.error('API Error:', error)
    
    if (error.response) {
      // 伺服器回應錯誤
      const status = error.response.status
      const message = error.response.data?.message || '伺服器錯誤'
      
      switch (status) {
        case 401:
          ElMessage.error('未授權，請重新登入')
          // 可以加入登出邏輯
          localStorage.removeItem('admin_token')
          // window.location.href = '/login'
          break
        case 403:
          ElMessage.error('沒有權限執行此操作')
          break
        case 404:
          ElMessage.error('請求的資源不存在')
          break
        case 500:
          ElMessage.error('伺服器內部錯誤')
          break
        default:
          ElMessage.error(message)
      }
    } else if (error.request) {
      // 請求發送但無回應
      ElMessage.error('網路錯誤，請檢查連線')
    } else {
      // 其他錯誤
      ElMessage.error(`發生錯誤: ${error.message}`)
    }
    
    return Promise.reject(error)
  }
)

export default api
