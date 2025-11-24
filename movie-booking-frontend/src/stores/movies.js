// src/stores/movies.js
import { defineStore } from 'pinia'
import { movieApi } from '../services/api'  // 修正：改為 services

export const useMoviesStore = defineStore('movies', {
  state: () => ({
    movies: [],
    loading: false,
    error: null,
  }),
  
  getters: {
    // 根據 ID 取得電影
    getMovieById: (state) => (id) => {
      return state.movies.find((m) => String(m.id) === String(id)) || null
    },
  },
  
  actions: {
    /**
     * 從後端 API 取得所有電影
     */
    async fetchMovies() {
      this.loading = true
      this.error = null
      
      try {
        const response = await movieApi.getAll()
        this.movies = response.data
      } catch (err) {
        this.error = '載入電影列表失敗'
        console.error('Failed to fetch movies:', err)
      } finally {
        this.loading = false
      }
    },

    /**
     * 從後端 API 取得單一電影詳細資訊
     */
    async fetchMovieById(id) {
      this.loading = true
      this.error = null
      
      try {
        const response = await movieApi.getById(id)
        const movie = response.data
        
        // 更新或新增到 store 中
        const index = this.movies.findIndex(m => m.id === movie.id)
        if (index !== -1) {
          this.movies[index] = movie
        } else {
          this.movies.push(movie)
        }
        
        return movie
      } catch (err) {
        this.error = '載入電影詳細資訊失敗'
        console.error('Failed to fetch movie by id:', err)
        throw err
      } finally {
        this.loading = false
      }
    },

    /**
     * 清除錯誤訊息
     */
    clearError() {
      this.error = null
    },
  },
})