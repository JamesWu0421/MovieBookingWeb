import api from './movieApi'

export const batchOperationService = {
  getAll() {
    return api.get('/batch-operations');
  },
  getById(id) {
    return api.get(`/batch-operations/${id}`);
  },
  create(data) {
    return api.post('/batch-operations', data);
  },
  update(id, data) {
    return api.put(`/batch-operations/${id}`, data);
  },
  remove(id) {
    return api.delete(`/batch-operations/${id}`);
  },
  start(id, totalItems) {
    return api.post(`/batch-operations/${id}/start`, null, {
      params: { totalItems }
    });
  },
  complete(id, successCount, failCount) {
    return api.post(`/batch-operations/${id}/complete`, null, {
      params: { successCount, failCount }
    });
  },
  updateStatus(id, status) {
    return api.patch(`/batch-operations/${id}/status`, null, {
      params: { status }
    });
  }
}
