<template>
  <div v-if="loading" class="movie-detail-page">
    載入中…
  </div>

  <div v-else-if="error" class="movie-detail-page">
    {{ error }}
  </div>

  <div v-else-if="!movie" class="movie-detail-page">
    找不到這部電影
  </div>

  <!-- 正常內容 -->
  <div v-else class="movie-detail-page">
    <!-- 上半部：海報 + 資訊 -->
    <section class="movie-hero">
      <div class="poster-wrap">
        <img
          class="poster"
          :src="movie.posterUrl"
          :alt="movie.title"
        />
      </div>

      <div class="meta-wrap">
        <h1 class="title">{{ movie.title }}</h1>
        <p class="eng-title">{{ movie.engTitle }}</p>

        <ul class="info-list">
          <li>
            <span class="label">級別</span>
            <span class="value">
              <img
                v-if="movie.ratingLevel"
                :src="movie.ratingLevel"
                alt="分級"
              />
            </span>
          </li>
          <li>
            <span class="label">片長</span>
            <span class="value">{{ movie.runtimeMinutes }} 分</span>
          </li>
          <li>
            <span class="label">上映日</span>
            <span class="value">{{ movie.releaseDate }}</span>
          </li>
          <li>
            <span class="label">類型</span>
            <span class="value">{{ movie.genres }}</span>
          </li>
          <li>
            <span class="label">演員</span>
            <span class="value">{{ movie.cast }}</span>
          </li>
          <li>
            <span class="label">導演</span>
            <span class="value">{{ movie.director }}</span>
          </li>
        </ul>

        <div class="synopsis">
          <h3>簡介</h3>
          <p v-if="showFullIntro">
            {{ movie.description }}
          </p>
          <p v-else>
            {{ shortIntro }}
          </p>

          <button
            v-if="movie.description && movie.description.length > 120"
            type="button"
            class="more-btn"
            @click="toggleIntro"
          >
            {{ showFullIntro ? '收起' : '更多...' }}
          </button>
        </div>
      </div>
    </section>

    <!-- 下半部：預告 -->
      <div class="div1">
        <div class="div2">電影預告</div>
      </div>
      <div class="div3">
      <div class="trailer-frame">
        <iframe
          :src="trailerUrlEmbed"
          title="YouTube video player"
          frameborder="0"
          allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share"
          referrerpolicy="strict-origin-when-cross-origin"
          allowfullscreen
        ></iframe>
        </div>
      </div>
      <div class="div4">
        <div class="div2">線上訂票</div>
      </div>
  </div>
</template>

<script setup>
import { computed, ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useMoviesStore } from '../stores/movies';

const route = useRoute()
const moviesStore = useMoviesStore()

// 先從 store 拿 loading / error
const loading = computed(() => moviesStore.loading)
const error = computed(() => moviesStore.error)

// 進來頁面時，如果還沒載電影列表就去 fetch 一次
onMounted(async () => {
  if (!moviesStore.movies.length) {
    await moviesStore.fetchMovies()
  }
})

// 依照網址上的 :id 找對應電影
const movie = computed(() =>
  moviesStore.getMovieById(route.params.id)
)

// 顯示簡介用
const showFullIntro = ref(false)

const shortIntro = computed(() => {
  if (!movie.value || !movie.value.description) return ''
  if (movie.value.description.length <= 120) return movie.value.description
  return movie.value.description.slice(0, 120) + '…'
})

const toggleIntro = () => {
  showFullIntro.value = !showFullIntro.value
}

const trailerUrlEmbed = computed(() => {
  if (!movie.value || !movie.value.trailerUrl) return ''
  return movie.value.trailerUrl.replace('watch?v=', 'embed/')
})
</script>

<style scoped>
.movie-detail-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px 64px 48px;
}

.div1 {
  width: 100%;
  height: 60px;
  margin-top: 60px;
  border-top: 1px solid rgb(230, 230, 230);
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

.div3 {
  background-color: #000000;
}

.div4 {
  width: 100%;
  height: 60px;
  border-top: 1px solid rgb(230, 230, 230);
  border-bottom: 1px solid rgb(230, 230, 230);
}

/* 上半部：兩欄 */
.movie-hero {
  display: grid;
  grid-template-columns: 280px minmax(0, 1fr);
  gap: 24px;
  align-items: flex-start;
}

/* 海報 */
.poster-wrap {
  max-width: 100%;
}

.poster {
  width: 100%;
  aspect-ratio: 2 / 3;
  object-fit: cover;
  display: block;
}

/* 文字區 */
.meta-wrap {
  border-left: 1px solid #eee;
  padding-left: 24px;
}

.title {
  font-weight: 400;
  letter-spacing: 0.1em;
  font-size: 28px;
  margin: 0;
}

.eng-title {
  font-weight: 300;
  font-size: 16px;
  letter-spacing: 0.2em;
  color: #666;
  margin: 4px 0 16px;
}

/* 基本資訊列表 */
.info-list {
  list-style: none;
  padding: 0;
  margin: 0 0 24px;
}

.info-list li {
  display: grid;
  grid-template-columns: 80px minmax(0, 1fr);
  padding: 2px 0;
  font-weight: 300;
  letter-spacing: 0.1em;
  font-size: 14px;
}

.label {
  font-weight: 400;
  color: #020202;
}

.value {
  color: #333;
}

.value img {
  height: 20px;
}

/* 簡介 */
.synopsis h3 {
  margin: 0 0 8px;
  font-weight: 400;
  font-size: 14px;
}

.synopsis p {
  margin: 0 0 12px;
  line-height: 1.7;
  font-weight: 300;
  letter-spacing: 0.1em;
  font-size: 14px;
}

.more-btn {
  padding: 4px 14px;
  font-size: 13px;
  border-radius: 4px;
  border: 1px solid #ad9278;
  background: #fff;
  color: #ad9278;
  cursor: pointer;
}

.more-btn:hover {
  background: #ad9278;
  color: #fff;
}



.trailer-frame {
  position: relative;
  width: 803px;
  height: 452px;
  margin: 0 auto;
}

.trailer-frame iframe {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
}

/* 響應式：手機就上下排 */
@media (max-width: 768px) {
  .movie-hero {
    grid-template-columns: 1fr;
  }

  .meta-wrap {
    border-left: none;
    padding-left: 0;
  }

  .poster-wrap {
    max-width: 260px;
  }

  .poster-wrap,
  .meta-wrap {
    margin: 0 auto;
  }
}
</style>
