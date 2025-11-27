import api from './api'

export const batchTicketTempService = {
  getAll() {
    return api.get('/batch-tickets-temp');
  },
  getById(id) {
    return api.get(`/batch-tickets-temp/${id}`);
  },
  getByBatchId(batchId) {
    return api.get(`/batch-tickets-temp/batch/${batchId}`);
  },
  getBySession(sessionId) {
    return api.get(`/batch-tickets-temp/session/${sessionId}`);
  },
  getByStatus(status) {
    return api.get(`/batch-tickets-temp/status/${status}`);
  },
  create(data) {
    return api.post('/batch-tickets-temp', data);
  },
  update(id, data) {
    return api.put(`/batch-tickets-temp/${id}`, data);
  },
  remove(id) {
    return api.delete(`/batch-tickets-temp/${id}`);
  }
}






