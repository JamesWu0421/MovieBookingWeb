<template>
  <el-card>
    <h4>{{ title }}</h4>
    <p class="value"><slot>{{ value }}</slot></p>
    <small>{{ desc }}</small>
  </el-card>
</template>

<script setup>
defineProps({
  title: String,
  value: [String, Number],
  desc: String
})
</script>

<style scoped>
.value { font-size:18px; font-weight:700 }
</style>
