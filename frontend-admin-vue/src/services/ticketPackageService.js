// src/services/ticketPackageService.js
import axios from 'axios'

const api = axios.create({
  baseURL: 'http://localhost:8080/api', // 後端 Spring Boot 預設 API
  timeout: 5000
})


// 這邊假設後端路由：
// GET    /api/ticket-packages
// POST   /api/ticket-packages
// PUT    /api/ticket-packages/:id
// DELETE /api/ticket-packages/:id

export default {
  list() {
    return api.get('/ticket-packages')
  },

  create(payload) {
    return api.post('/ticket-packages', payload)
  },

  update(id, payload) {
    return api.put(`/ticket-packages/${id}`, payload)
  },

  delete(id) {
    return api.delete(`/ticket-packages/${id}`)
  },
}




