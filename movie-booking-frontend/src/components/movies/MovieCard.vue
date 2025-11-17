<template>
  <article class="movie-card" @click="$emit('click')">
    <img
      class="poster"
      :src="movie.posterUrl"
      :alt="movie.title"
    />
    <div class="info">
      <h2 class="title">{{ movie.title }}</h2>
      <p class="eng-title">{{ movie.engTitle }}</p>
      <img class="rating"
      :src="movie.ratingLevel"
      />

      <p class="meta">上映日期：{{ movie.releaseDate }} </p>
    </div>
  </article>
</template>

<script setup>
defineProps({
  movie: {
    type: Object,
    required: true,
  },
});
</script>

<style scoped>
.movie-card {
  border: 1px solid #ddd;
  overflow: hidden;
  cursor: pointer;
  display: flex;
  flex-direction: column;
}

.poster {
  width: 100%;
  aspect-ratio: 3.5 / 5;
  object-fit: cover;
  display: block;
}

.rating{
  width: 30px;
  height: 30px;
}

.info {
  padding: 8px 10px;
  
}

.title{
  font-size: 18px;
  font-weight: 500;
}

.eng-title {
  font-size: 12px;
  color:  #797979;
}

.meta {
  font-size: 12px;
  color: #797979;
}
</style>
