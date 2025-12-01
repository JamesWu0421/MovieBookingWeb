import { createApp } from 'vue';
import { createPinia } from 'pinia';
import App from './App.vue';
import router from './router';
import './assets/main.css';
import 'swiper/css';
import 'swiper/css/effect-coverflow';
import 'swiper/css/navigation';

const app = createApp(App);

app.use(createPinia());
app.use(router);

app.mount('#app');
createApp(App).use(router).mount('#app');
