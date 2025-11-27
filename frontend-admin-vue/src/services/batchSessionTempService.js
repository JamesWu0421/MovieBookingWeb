import api from './api'

export const batchSessionTempService = {
  /**
   * 取得所有暫存場次
   * GET /batch-sessions-temp
   */
  getAll() {
    return api.get('/batch-sessions-temp');
  },

  /**
   * 依 ID 取得單一暫存場次
   * GET /batch-sessions-temp/{id}
   */
  getById(id) {
    return api.get(`/batch-sessions-temp/${id}`);
  },

  /**
   * 依 batchId 取得該批次的所有場次
   * GET /batch-sessions-temp/batch/{batchId}
   */
  getByBatchId(batchId) {
    return api.get(`/batch-sessions-temp/batch/${batchId}`);
  },

  /**
   * 依狀態查詢
   * GET /batch-sessions-temp/status/{status}
   */
  getByStatus(status) {
    return api.get(`/batch-sessions-temp/status/${status}`);
  },

  /**
   * 新增暫存場次
   * POST /batch-sessions-temp
   * 請求格式: {
   *   batchId: number,
   *   movieId: number,
   *   screenId: number,
   *   showDate: string (YYYY-MM-DD),
   *   showTime: string (HH:mm:ss or HH:mm),
   *   endTime: string (HH:mm:ss or HH:mm),
   *   status?: string,
   *   errorMessage?: string
   * }
   */
  create(data) {
    return api.post('/batch-sessions-temp', data);
  },

  /**
   * 更新暫存場次
   * PUT /batch-sessions-temp/{id}
   */
  update(id, data) {
    return api.put(`/batch-sessions-temp/${id}`, data);
  },

  /**
   * 刪除暫存場次
   * DELETE /batch-sessions-temp/{id}
   */
  remove(id) {
    return api.delete(`/batch-sessions-temp/${id}`);
  }
}











