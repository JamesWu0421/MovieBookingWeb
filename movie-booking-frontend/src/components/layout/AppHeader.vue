<template>
  <header class="app-header">
    <div class="logo" @click="$router.push('/')">
      <img class="icon" src="/images/theater6.png" alt="" />
    </div>
    <nav class="nav-links">
      <router-link to="/movies">現正熱映</router-link>
      <router-link to="#">即將上映</router-link>
      <router-link to="/events">最新活動</router-link>
      <!-- 快速訂票 - 始終顯示,未登入點擊會被路由守衛導向 login -->
      <router-link to="/booking/QuickBooking"> 快速訂票 </router-link>

      <!-- 個人資料 - 只有登入時才顯示 -->
      <router-link v-if="authStore.isAuthenticated" to="/profile">
        個人資料
      </router-link>
      <!-- 🔔 登入後才顯示 -->
      <NotificationBell v-if="authStore.isAuthenticated" />
      <!-- 登入/登出按鈕 -->
      <router-link v-if="!authStore.isAuthenticated" to="/login">
        會員登入
      </router-link>
      <button v-else @click="handleLogout" class="logout-btn">登出</button>
    </nav>
  </header>
</template>

<script setup>
import { useAuthStore } from "../../stores/login";
import { useRouter } from "vue-router";
import NotificationBell from "../notifications/NotificationBell.vue";

const authStore = useAuthStore();
const router = useRouter();

const handleLogout = async () => {
  await authStore.logout();
  router.push("/");
};
</script>

<style scoped>
/* 原本的 CSS 完整保留 */
.app-header {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  z-index: 1000;
  background-color: rgb(36, 60, 82);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 35px 8px 12px;
  box-sizing: border-box;
}

.logo {
  font-weight: bold;
  font-size: 20px;
  cursor: pointer;
}

.nav-links {
  display: flex;
  align-items: center;
  gap: 10px;
}

.nav-links a,
.logout-btn {
  font-weight: 300;
  letter-spacing: 0.1em;
  margin-left: 18px;
  text-decoration: none;
  color: #ffffff;
  background: none;
  border: none;
  cursor: pointer;
  font-size: 14px;
}

.logout-btn:hover,
.nav-links a:hover {
  text-decoration: underline;
}

.icon {
  width: 80px;
  height: 45px;
  padding-left: 20px;
}
</style>
