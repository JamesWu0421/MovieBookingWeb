<template>
    <h3>Comp2</h3>
    <div><input type="text" v-model="msg">：{{ msg }}</div>
</template>
    
<script setup>
    import { ref } from 'vue'
    const msg = ref("this is a demo")
</script>
    
<style>
    h3 {
        color: red
    }
</style>