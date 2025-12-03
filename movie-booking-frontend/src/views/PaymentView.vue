<template>
  <div class="pay-box">

    <h2>付款資訊</h2>

    <p>訂單編號：{{ order?.id }}</p>
    <p>金額：<b>{{ order?.totalAmount }} 元</b></p>

    <button @click="goPay" class="pay-btn">前往綠界付款 →</button>

  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRoute } from "vue-router";
import axios from "axios";

const route = useRoute();
const id = route.params.id;
const order = ref(null);

onMounted(async()=>{
  const res = await axios.get(import.meta.env.VITE_API_BASE_URL+`orders/by-id/${id}`||`http://localhost:8080/api/orders/by-id/${id}`); // ←已修正
  order.value = res.data;
});

function goPay(){
  window.location.href = import.meta.env.VITE_API_BASE_URL+`ecpay/pay?amount=${order.value.totalAmount}&orderId=${order.value.id}`||`http://localhost:8080/ecpay/pay?amount=${order.value.totalAmount}&orderId=${order.value.id}`
}
</script>

<style scoped>
.pay-box { padding:30px; text-align:center; font-size:20px }
.pay-btn{
  padding:15px 30px;
  font-size:22px;
  background:#28a745;
  color:#fff;
  border-radius:6px;
  border:none;
  cursor:pointer;
}
</style>
