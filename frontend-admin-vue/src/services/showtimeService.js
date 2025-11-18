import api from './api'

export default {
  list(params) {
    return api.get('/showtimes', { params })
  },
  
  get(id) {
    return api.get(`/showtimes/${id}`)
  },
  
  create(data) {
    return api.post('/showtimes', data)
  },
  
  update(id, data) {
    return api.put(`/showtimes/${id}`, data)
  },
  
  delete(id) {
    return api.delete(`/showtimes/${id}`)
  },
  
  getByDate(date) {
    return api.get('/showtimes/by-date', { params: { date } })
  },
  
  getByMovie(movieId) {
    return api.get(`/showtimes/movie/${movieId}`)
  }
}
