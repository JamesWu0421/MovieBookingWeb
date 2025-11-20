<template>
  <div class="auth-page">
    <div class="auth-container">
      <div class="auth-card">
        <div class="logo-section">
          <img src="/images/theater6.png" alt="Logo" class="logo-img" />
          <h2>忘記密碼</h2>
          <p class="subtitle">
            請輸入您的 Email，我們將發送重設密碼的連結給您。
          </p>
        </div>

        <!-- 成功訊息 -->
        <div v-if="successMessage" class="alert-success">
          ✓ {{ successMessage }}
        </div>

        <!-- 錯誤訊息 -->
        <div v-if="errorMessage" class="alert-error">
          {{ errorMessage }}
        </div>

        <!-- 表單 -->
        <form @submit.prevent="handleSubmit" v-if="!isSuccess">
          <div class="form-group">
            <label for="email">Email <span class="required">*</span></label>
            <input
              type="email"
              id="email"
              v-model="email"
              placeholder="請輸入您的註冊 Email"
              required
            />
          </div>

          <button type="submit" class="btn-primary" :disabled="loading">
            {{ loading ? "發送中..." : "發送重設連結" }}
          </button>
        </form>

        <!-- 返回登入 -->
        <div class="back-link">
          <router-link to="/login">← 返回登入</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import request from "../../utils/request";

const email = ref("");
const loading = ref(false);
const errorMessage = ref("");
const successMessage = ref("");
const isSuccess = ref(false);

const handleSubmit = async () => {
  errorMessage.value = "";
  successMessage.value = "";
  loading.value = true;

  try {
    await request({
      url: `/auth/forgotpassword`,
      method: "post",
      data: { email: email.value },
    });

    successMessage.value = "重設密碼連結已發送到您的信箱，請查收。";
    isSuccess.value = true;
  } catch (error) {
    errorMessage.value = error.message || "發送失敗，請確認 Email 是否正確";
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.auth-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #1a3a52 0%, #2d5a7b 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.auth-container {
  width: 100%;
  max-width: 420px;
}

.auth-card {
  background: white;
  border-radius: 12px;
  padding: 40px 35px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
}

.logo-section {
  text-align: center;
  margin-bottom: 35px;
}

.logo-img {
  width: 100px;
  height: auto;
  margin-bottom: 15px;
}

h2 {
  color: #243c52;
  font-size: 24px;
  font-weight: 600;
  margin: 0 0 10px 0;
}

.subtitle {
  color: #5a6c7d;
  font-size: 14px;
  margin: 0;
  line-height: 1.5;
}

.alert-success,
.alert-error {
  padding: 12px 15px;
  border-radius: 6px;
  margin-bottom: 20px;
  font-size: 14px;
}

.alert-success {
  background-color: #f0fdf4;
  color: #15803d;
  border: 1px solid #86efac;
}

.alert-error {
  background-color: #fee;
  color: #c33;
  border: 1px solid #fcc;
}

.form-group {
  margin-bottom: 20px;
}

label {
  display: block;
  color: #243c52;
  font-weight: 500;
  margin-bottom: 8px;
  font-size: 14px;
}

.required {
  color: #dc3545;
}

input[type="email"] {
  width: 100%;
  padding: 12px 15px;
  border: 1.5px solid #d0d7de;
  border-radius: 6px;
  font-size: 14px;
  transition: border-color 0.3s;
  box-sizing: border-box;
}

input:focus {
  outline: none;
  border-color: #2d5a7b;
}

.btn-primary {
  width: 100%;
  padding: 13px;
  background-color: #243c52;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.3s;
  margin-bottom: 20px;
}

.btn-primary:hover:not(:disabled) {
  background-color: #2d5a7b;
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.back-link {
  text-align: center;
}

.back-link a {
  color: #2d5a7b;
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
}

.back-link a:hover {
  text-decoration: underline;
}
</style>
