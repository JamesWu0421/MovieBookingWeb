<template>
  <section>
    <div class="div1">
      <div class="div2">現正熱映</div>
    </div>

    <p v-if="loading">載入中...</p>
    <p v-else-if="error">{{ error }}</p>

    <MovieList class="movielist"
      v-else
      :movies="movies"
      @select-movie="goToMovieDetail"
    />
  </section>
</template>

<script setup>
import { onMounted } from 'vue';
import { storeToRefs } from 'pinia';
import { useMoviesStore } from '../stores/movies';
import MovieList from '../components/movies/MovieList.vue';

const moviesStore = useMoviesStore();
const { movies, loading, error } = storeToRefs(moviesStore);

onMounted(() => {
  if (!movies.value.length) {
    moviesStore.fetchMovies();
  }
});

const goToMovieDetail = (movieId) => {
  // 可以先在這邊把 selectedMovie 存進 booking store（可選）
  // const bookingStore = useBookingStore();
  // bookingStore.setMovie(moviesStore.getMovieById(movieId));

  // 跳轉到 TicketBooking 頁面（不是 movies 詳細頁）
  window.location.href = `/booking/TicketBooking/${movieId}`;
};
</script>

<style scoped>
.movielist {
  padding: 32px;
}

.div1 {
  width: 100%;
  height: 60px;
  margin-top: 20px;
  margin-bottom: 10px;
  border-bottom: 1px solid rgb(230, 230, 230);
}

.div2 {
  font-weight: 300;
  letter-spacing: 0.5em;
  color: rgb(51, 51, 51);
  height: 100%;
  width: 145px;
  font-size: 1.5em;
  padding: 0px 2em;
  display: flex;
  -webkit-box-align: center;
  align-items: center;
  border-left: 1px solid rgb(230, 230, 230);
  border-right: 1px solid rgb(230, 230, 230);
  background: repeating-linear-gradient(-45deg, rgba(99, 99, 99, 0.067), rgba(103, 103, 103, 0.067) 2px, rgba(0, 0, 0, 0) 2px, rgba(0, 0, 0, 0) 4px);
}
</style>