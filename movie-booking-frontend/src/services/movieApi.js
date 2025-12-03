import axios from 'axios'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api',
  timeout: 5000
})

export const movieApi = {
  getAll() {
    return api.get('/movies');
  },
  getById(id) {
    return api.get(`/movies/${id}`);
  },
  create(data) {
    return api.post('/movies', data);
  },
  update(id, data) {
    return api.put(`/movies/${id}`, data);
  },
  remove(id) {
    return api.delete(`/movies/${id}`);
  },
};

export const screenApi = {
  getAll() {
    return api.get('/screens');
  },
  getById(id) {
    return api.get(`/screens/${id}`);
  },
  create(data) {
    return api.post('/screens', data);
  },
  update(id, data) {
    return api.put(`/screens/${id}`, data);
  },
  remove(id) {
    return api.delete(`/screens/${id}`);
  },
};

export const showApi = {
  getAll() {
    return api.get('/shows');
  },
  getById(id) {
    return api.get(`/shows/${id}`);
  },
  create(data) {
    return api.post('/shows', data);
  },
  update(id, data) {
    return api.put(`/shows/${id}`, data);
  },
  remove(id) {
    return api.delete(`/shows/${id}`);
  },
};

export const seatApi = {
  getByScreen(screenId) {
    // 對應 GET /api/seats?screenId=xxx
    return api.get('/seats', { params: { screenId } });
  },
  updateBlocked(id, blocked) {
    // 對應 PATCH /api/seats/{id}/block
    return api.patch(`/seats/${id}/block`, { blocked });
  },
};

export const seatLockApi = {
  // 鎖定座位
  lockSeats(payload) {
    return api.post('/seat-locks/lock', payload);
  },

  // 釋放座位（之後如果要用）
  releaseSeats(payload) {
    return api.post('/seat-locks/release', payload);
  },

  // 撈某場次目前 active 的 lock（之後如果 SeatMap 要用）
  getActive(showId) {
    return api.get('/seat-locks/active', { params: { showId } });
  },
};

export const ticketApi = {
  getSold(showId) {
    return api.get('/tickets/sold', { params: { showId } });
  },
};

export default api