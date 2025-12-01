// src/services/ticketPackageService.js
import api from './movieApi'

export default {
  list() {
    return api.get('/ticket-packages')
  },

  get(id) {
    return api.get(`/ticket-packages/${id}`)
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