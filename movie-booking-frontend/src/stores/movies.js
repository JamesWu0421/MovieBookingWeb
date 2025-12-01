import { defineStore } from 'pinia';
import { movieApi } from '../services/movieApi';

export const useMoviesStore = defineStore('movies', {
  state: () => ({
    movies: [],
    loading: false,
    error: null,
  }),

  actions: {
    async fetchMovies() {
      this.loading = true;
      this.error = null;

      try {
        const res = await movieApi.getAll(); 
        this.movies = res.data;
      } catch (err) {
        console.error(err);
        this.error = '載入電影列表失敗';
      } finally {
        this.loading = false;
      }
    },

    getMovieById(id) {
      return this.movies.find((m) => String(m.id) === String(id)) || null;
    },
  },
});
