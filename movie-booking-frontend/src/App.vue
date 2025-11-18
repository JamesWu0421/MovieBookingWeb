<template>
  <div class="app-root">
    <AppHeader />

    <main class="app-main">
      <router-view />
    </main>

    <AppFooter />
  </div>
</template>

<script setup>
import AppHeader from "./components/layout/AppHeader.vue";
import AppFooter from "./components/layout/AppFooter.vue";
</script>

<style scoped>
.app-root {
  min-height: 100vh;
  /* display: flex;
  flex-direction: column; */
}

.app-main {
  flex: 1;
  max-width: 1200px;
  margin: 0 auto;
  padding: 16px;
  padding-top: 80px;
}
</style>
