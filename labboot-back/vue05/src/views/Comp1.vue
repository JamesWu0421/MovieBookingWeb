<template>
    <h3>Comp1</h3>
    <button @click="count--">Count: {{ count }}</button>
</template>
    
<script setup>
    import { ref } from 'vue'
    const count = ref(0)
    // function doclick() {
    //     count.value++
    // }
</script>
    
<style scoped>
    h3 {
        color: blue
    }
</style>