<template>
  <div class="auth-page">
    <div class="auth-container">
      <div class="auth-card">
        <div class="logo-section">
          <img src="/images/theater6.png" alt="Logo" class="logo-img" />
          <h2>會員登入</h2>
        </div>

        <div v-if="errorMessage" class="alert-error">
          {{ errorMessage }}
        </div>

        <form @submit.prevent="handleLogin">
          <div class="form-group">
            <label for="username">帳號</label>
            <input
              type="text"
              id="username"
              v-model="form.username"
              placeholder="請輸入帳號"
              required
            />
          </div>

          <div class="form-group">
            <label for="password">密碼</label>
            <input
              type="password"
              id="password"
              v-model="form.password"
              placeholder="請輸入密碼"
              required
            />
          </div>

          <button type="submit" class="btn-primary" :disabled="loading">
            <span v-if="loading" class="spinner"></span>
            {{ loading ? "登入中..." : "登入" }}
          </button>
        </form>

        <div class="divider">
          <span>或</span>
        </div>

        <!-- Google 登入按鈕 -->
        <GoogleLoginButton @error="handleGoogleError" />

        <div class="forgot-link-section">
          <router-link to="/forgot-password" class="forgot-link">
            忘記密碼？
          </router-link>
        </div>

        <div class="register-link">
          還沒有帳號？
          <router-link to="/register">立即註冊</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "../../stores/login";
import GoogleLoginButton from "../../components/Login/GoogleLoginButton.vue";

const router = useRouter();
const authStore = useAuthStore();

const form = ref({
  username: "",
  password: "",
});
const loading = ref(false);
const errorMessage = ref("");

const handleLogin = async () => {
  errorMessage.value = "";
  loading.value = true;

  try {
    await authStore.login(form.value);
    router.push("/");
  } catch (error) {
    errorMessage.value = error.message || "登入失敗，請檢查帳號密碼";
  } finally {
    loading.value = false;
  }
};

const handleGoogleError = (msg) => {
  errorMessage.value = msg;
};
</script>

<style scoped>
.auth-page {
  /* min-height: 100vh;
  background: linear-gradient(135deg, #1a3a52 0%, #2d5a7b 100%); */
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
  margin: 0;
}

.alert-error {
  background-color: #fee;
  color: #c33;
  padding: 12px 15px;
  border-radius: 6px;
  margin-bottom: 20px;
  font-size: 14px;
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

input[type="text"],
input[type="password"],
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
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.btn-primary:hover:not(:disabled) {
  background-color: #2d5a7b;
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.spinner {
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 0.6s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.divider {
  text-align: center;
  margin: 25px 0;
  position: relative;
}

.divider::before,
.divider::after {
  content: "";
  position: absolute;
  top: 50%;
  width: 42%;
  height: 1px;
  background-color: #d0d7de;
}

.divider::before {
  left: 0;
}

.divider::after {
  right: 0;
}

.divider span {
  background: white;
  padding: 0 15px;
  color: #8b95a1;
  font-size: 14px;
}

.forgot-link-section {
  text-align: center;
  margin-top: 20px;
}

.forgot-link {
  font-size: 14px;
  color: #2d5a7b;
  text-decoration: none;
}

.forgot-link:hover {
  text-decoration: underline;
}

.register-link {
  text-align: center;
  margin-top: 25px;
  font-size: 14px;
  color: #5a6c7d;
}

.register-link a {
  color: #2d5a7b;
  text-decoration: none;
  font-weight: 600;
}

.register-link a:hover {
  text-decoration: underline;
}
</style>
