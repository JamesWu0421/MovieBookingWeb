// src/services/packageItemsService.js
import api from './movieApi'

export default {
  // 取得所有套票內容物
  list() {
    return api.get('/package-items')
  },

  // 依 ID 取得單一套餐內容物
  getById(id) {
    return api.get(`/package-items/${id}`)
  },

  // 依 package_id 取得內容物
  getByPackageId(packageId) {
    return api.get(`/package-items/package/${packageId}`)
  },

  // 新增套餐內容物
  create(payload) {
    return api.post('/package-items', payload)
  },

  // 更新套餐內容物
  update(id, payload) {
    return api.put(`/package-items/${id}`, payload)
  },

  // 刪除套餐內容物
  delete(id) {
    return api.delete(`/package-items/${id}`)
  }
}


























