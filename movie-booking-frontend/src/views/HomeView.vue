<template>
  <section class="home-hero">
    <div class="hero-text"></div>

    <p v-if="loading" class="status-text">載入中...</p>
    <p v-else-if="error" class="status-text error">{{ error }}</p>

    <div v-else class="carousel-wrap">
      <Swiper
        class="poster-swiper"
        :modules="[EffectCoverflow, Navigation, Autoplay]"
        effect="coverflow"
        :grabCursor="true"
        :centeredSlides="true"
        :slidesPerView="'auto'"
        :loop="true"
        :speed="1200"
        :coverflowEffect="{
          rotate: 0,
          stretch: 0,
          depth: 200,
          modifier: 1,
          slideShadows: false
        }"
        navigation
        :autoplay="{ delay: 3000, disableOnInteraction: false }"
        @swiper="onSwiper"
        @mouseleave="onSwiperLeave"
      >
        <SwiperSlide
          v-for="(m, index) in movies"
          :key="m.id"
          class="poster-slide"
          @click="goMovieDetail(m.id)"
          @mouseenter="focusSlide(index)"
        >
          <div class="poster-card">
            <img :src="m.posterUrl" :alt="m.title" />
            <div class="poster-overlay"></div>
          </div>
        </SwiperSlide>
      </Swiper>
    </div>
  </section>

  <section class="home-block">
    <h2 class="home-block-title"></h2>
    <MoviesView />
  </section>

  <section class="home-block">
    <h2 class="home-block-title"></h2>
    <UpcomingMoviesView />
  </section>
</template>


<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { Swiper, SwiperSlide } from 'swiper/vue';
import { EffectCoverflow, Navigation, Autoplay } from 'swiper/modules';
import { movieApi } from '../services/api';
import MoviesView from './MoviesView.vue';
import UpcomingMoviesView from './UpcomingMoviesView.vue';

const router = useRouter();

const movies = ref([]);
const loading = ref(false);
const error = ref('');

// 存 Swiper 實例
const swiperRef = ref(null);
const focusLocked = ref(false); // 動畫鎖，避免瘋狂連環切

const onSwiper = (swiper) => {
  swiperRef.value = swiper;
};

// 滑鼠移到某張海報 -> 那張慢慢移到中間
const focusSlide = (index) => {
  const swiper = swiperRef.value;
  if (!swiper) return;

  // 已經在動畫中，就先不要再切了
  if (focusLocked.value) return;

  // 如果本來就在中間，不用動
  if (swiper.realIndex === index) return;

  focusLocked.value = true;

  // 使用 loop 模式的 slideToLoop，會選最接近的那個
  swiper.slideToLoop(index);

  // 有 autoplay 的話，滑鼠有操作就先暫停
  if (swiper.autoplay) {
    swiper.autoplay.stop();
  }

  // 動畫跑完再解鎖（用 speed 當基準）
  const speed = swiper.params.speed || 1200;
  setTimeout(() => {
    focusLocked.value = false;
  }, speed + 100);
};

// 滑鼠離開整個 Swiper 區域 -> 再讓 autoplay 繼續
const onSwiperLeave = () => {
  const swiper = swiperRef.value;
  if (swiper && swiper.autoplay) {
    swiper.autoplay.start();
  }
};

const goMoviesList = () => {
  router.push('/movies');
};

const goMovieDetail = (id) => {
  router.push(`/movies/${id}`);
};

onMounted(async () => {
  loading.value = true;
  error.value = '';
  try {
    const res = await movieApi.getAll();
    movies.value = res.data || [];
  } catch (e) {
    console.error(e);
    error.value = '載入電影資料失敗，請稍後再試。';
  } finally {
    loading.value = false;
  }
});
</script>


<style scoped>
.home-hero {
  height: 420px;
  padding: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-start;
  background: radial-gradient(circle at top, #ffffff, #e1dede);
  color: #fff;
}

.home-block {
  background: white;
  padding: 40px 0;
}

.hero-text {
  text-align: center;
  margin-bottom: 10px;
}

.hero-text h1 {
  font-size: 32px;
  margin-bottom: 8px;
}

.hero-text p {
  opacity: 0.85;
  margin-bottom: 16px;
}

.status-text {
  margin-top: 24px;
  font-size: 14px;
  opacity: 0.9;
}

.status-text.error {
  color: #ff9fa3;
}

.carousel-wrap {
  width: 100%;
  max-width: 1200%;
  margin-top: 16px;
}

.poster-swiper {
  width: 100%;
  /* padding: 40px 0; */
}

.poster-slide {
  width: 260px;
}

.poster-card {
  position: relative;
  width: 100%;
  height: 100%;
  /* border-radius: 16px; */
  cursor: pointer;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.4);
}

.poster-card img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.poster-overlay {
  position: absolute;
  inset: 0;
  color: #fff;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  padding: 16px;
}

.poster-overlay h2 {
  font-size: 20px;
  letter-spacing: 2px;
  margin-bottom: 4px;
}

.poster-overlay p {
  font-size: 13px;
  opacity: 0.8;
}

:deep(.poster-swiper) {
  --swiper-navigation-color: #7b7a79;
  --swiper-navigation-size: 40px;
}

:deep(.swiper-button-next),
:deep(.swiper-button-prev) {
  background: rgba(255, 255, 255, 0.8);
  width: 40px;
  height: 40px;
  border-radius: 50%;
}

:deep(.swiper-button-next:hover),
:deep(.swiper-button-prev:hover) {
  background: #ffffff;
}

:deep(.poster-swiper .swiper-wrapper) {
  transition-timing-function: cubic-bezier(0.25, 0.8, 0.25, 1) !important;
}
</style>
