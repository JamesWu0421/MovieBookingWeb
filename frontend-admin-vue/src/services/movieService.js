import axios from 'axios'

const API_URL = '/api/movies'

export default {
  // 獲取所有電影
  list() {
    return axios.get(API_URL)
  },

  // 根據ID獲取電影
  getById(id) {
    return axios.get(`${API_URL}/${id}`)
  },

  // 搜索電影
  search(params) {
    return axios.get(`${API_URL}/search`, { params })
  },

  // 新增電影
  create(movie) {
    return axios.post(API_URL, movie)
  },

  // 更新電影
  update(id, movie) {
    return axios.put(`${API_URL}/${id}`, movie)
  },

  // 刪除電影
  delete(id) {
    return axios.delete(`${API_URL}/${id}`)
  }
}
