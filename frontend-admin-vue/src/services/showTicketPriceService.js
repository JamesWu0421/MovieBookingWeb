import api from './movieApi'

export default {
  list(params) {
    return api.get('/show-ticket-prices/all', { params })
  },
  
  get(id) {
    return api.get(`/show-ticket-prices/${id}`)
  },
  
  create(data) {
    return api.post('/show-ticket-prices', data)
  },
  
  delete(id) {
    return api.delete(`/show-ticket-prices/${id}`)
  },
  
  getByShowId(showId) {
    return api.get(`/show-ticket-prices/by-show/${showId}`)
  },
  
  getAvailableByShowId(showId) {
    return api.get(`/show-ticket-prices/by-show/${showId}/available`)
  },
  
  getByPriceRange(minPrice, maxPrice) {
    return api.get('/show-ticket-prices/by-price-range', {
      params: { minPrice, maxPrice }
    })
  },
  
  getByTimeRange(start, end) {
    return api.get('/show-ticket-prices/by-time-range', {
      params: { start, end }
    })
  }
}



