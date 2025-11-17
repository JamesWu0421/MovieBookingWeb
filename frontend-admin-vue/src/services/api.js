import axios from 'axios'

const api = axios.create({
  baseURL: 'http://localhost:8080/api', // 後端 Spring Boot 預設 API
  timeout: 5000
})

export default api
