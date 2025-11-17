<!-- src/components/movies/MovieList.vue -->
<template>
  <div class="movie-list">
    <MovieCard
      v-for="movie in movies"
      :key="movie.id"
      :movie="movie"
      @click="() => $emit('select-movie', movie.id)"
    />
  </div>
</template>

<script setup>
import MovieCard from './MovieCard.vue';

defineProps({
  movies: {
    type: Array,
    default: () => [],
  },
});
</script>

<style scoped>
.movie-list {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 16px;
}

/* 平板橫向或中型螢幕 */
@media (max-width: 1200px) {
  .movie-list {
    grid-template-columns: repeat(4, 1fr);
  }
}

/* 平板直向 */
@media (max-width: 900px) {
  .movie-list {
    grid-template-columns: repeat(3, 1fr);
  }
}

/* 手機橫向 */
@media (max-width: 600px) {
  .movie-list {
    grid-template-columns: repeat(2, 1fr);
  }
}

/* 手機直向 */
@media (max-width: 400px) {
  .movie-list {
    grid-template-columns: repeat(1, 1fr);
  }
}

</style>
