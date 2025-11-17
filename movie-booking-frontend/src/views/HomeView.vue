<!-- src/views/HomeView.vue -->
<template>
  <section>
    <h1>歡迎使用電影訂票系統</h1>
    <p>先去看看現在有哪些電影吧。</p>
    <button @click="$router.push('/movies')">前往現正熱映</button>
  </section>
</template>

<script setup></script>
