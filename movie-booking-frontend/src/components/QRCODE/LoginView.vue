<template>
    <div class="login-box">
    <h2>驗票員登入</h2>

    <input v-model="password" type="password" placeholder="輸入密碼（1234）" />
    <button @click="login" class="btn">登入</button>

    <p v-if="error" class="error">{{ error }}</p>
    </div>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import { loginStaff } from "../router";

const password = ref("");
const error = ref("");
const router = useRouter();

function login() {
    if (password.value === "1234") {
    loginStaff();
    router.push("/verify");
    } else {
    error.value = "密碼錯誤";
    }
}
</script>

<style scoped>
.login-box {
    width: 280px;
    margin: 100px auto;
    text-align: center;
}
input {
    padding: 10px;
    width: 100%;
    margin-top: 10px;
}
.btn {
    margin-top: 12px;
    padding: 10px;
    background: #ffcc00;
    border: none;
    border-radius: 999px;
    cursor: pointer;
}
.error {
    color: #ff4444;
    margin-top: 10px;
}
</style>