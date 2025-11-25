<template>
  <section>
    <div class="div1">
      <div class="div2">現正熱映</div>
    </div>

    <p v-if="loading">載入中...</p>
    <p v-else-if="error">{{ error }}</p>

    <MovieList
      v-else
      :movies="nowPlayingMovies"
      @select-movie="goToMovieDetail"
    />
  </section>
</template>

<script setup>
import { computed, onMounted } from 'vue';
import { storeToRefs } from 'pinia';
import { useMoviesStore } from '../stores/movies';
import MovieList from '../components/movies/MovieList.vue';

const moviesStore = useMoviesStore();
const { movies, loading, error } = storeToRefs(moviesStore);

// 今日日期 00:00
const today = new Date();
today.setHours(0, 0, 0, 0);

// 只顯示「已上映、已上架」的電影
const nowPlayingMovies = computed(() =>
  movies.value.filter(m => {
    // 後端 return 的欄位名字兼容
    const release = m.releaseDate || m.release_date;
    const published = m.isPublished === true || m.is_published === true;

    if (!release || !published) return false;

    const d = new Date(release);
    if (Number.isNaN(d.getTime())) return false;

    d.setHours(0, 0, 0, 0);

    // 只留上映日期 <= 今天的
    return d <= today;
  })
);

onMounted(() => {
  if (!movies.value.length) {
    moviesStore.fetchMovies();
  }
});

const goToMovieDetail = (movieId) => {
  window.location.href = `/movies/${movieId}`;
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
  background: repeating-linear-gradient(
    -45deg,
    rgba(99, 99, 99, 0.067),
    rgba(103, 103, 103, 0.067) 2px,
    rgba(0, 0, 0, 0) 2px,
    rgba(0, 0, 0, 0) 4px
  );
}
</style>
