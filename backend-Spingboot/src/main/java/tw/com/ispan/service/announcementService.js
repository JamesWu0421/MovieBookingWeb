import api from './api'

export default {
  list(params) {
    return api.get('/announcements', { params })
  },
  get(id) {
    return api.get(`/announcements/${id}`)
  },
  create(data) {
    return api.post('/announcements', data)
  },
  update(id, data) {
    return api.put(`/announcements/${id}`, data)
  },
  delete(id) {
    return api.delete(`/announcements/${id}`)
  }
}
