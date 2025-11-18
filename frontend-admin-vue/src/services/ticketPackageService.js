import api from './api'

export default {
  list(params) {
    return api.get('/ticket-packages', { params })
  },
  
  get(id) {
    return api.get(`/ticket-packages/${id}`)
  },
  
  create(data) {
    return api.post('/ticket-packages', data)
  },
  
  update(id, data) {
    return api.put(`/ticket-packages/${id}`, data)
  },
  
  delete(id) {
    return api.delete(`/ticket-packages/${id}`)
  }
}
