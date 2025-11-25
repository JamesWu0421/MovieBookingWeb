<template>
  <header class="app-header">
      <div class="logo" @click="$router.push('/')">
        <img class="icon" src="/images/theater6.png" alt="">
        </div>
    <nav class="nav-links">
      <router-link to="/movies">現正熱映</router-link>
      <router-link to="/upcomingmovies">即將上映</router-link>
      <router-link to="/booking/QuickBooking">快速訂票</router-link>
      <router-link to="#">會員登入</router-link>
    </nav>
  </header>
</template>

<script setup></script>

<style scoped>
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

.nav-links a {
  font-weight: 300;
  letter-spacing: 0.1em;
  margin-left: 18px;
  text-decoration: none;
  color: #ffffff;
}

.icon {
  width: 80px;
  height: 45px;
  padding-left: 20px;
}
</style>
