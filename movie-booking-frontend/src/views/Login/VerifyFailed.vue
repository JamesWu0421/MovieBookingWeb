<template>
  <div class="error-page">
    <div class="error-container">
      <div class="error-card">
        <div class="error-header">
          <div class="error-icon">✕</div>
          <h2>驗證失敗</h2>
        </div>

        <div class="error-content">
          <p class="main-text">{{ errorMessage }}</p>
        </div>

        <div class="action-section">
          <button
            class="btn-primary"
            @click="resendEmail"
            :disabled="resendLoading"
          >
            {{ resendLoading ? "發送中..." : "重新發送驗證信" }}
          </button>
          <router-link to="/login" class="btn-secondary">
            返回登入
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRouter, useRoute } from "vue-router";
import * as authApi from "../../services/api";

const router = useRouter();
const route = useRoute();

const errorMessage = ref(route.query.message || "驗證連結已過期，請重新申請");
const resendLoading = ref(false);

const resendEmail = async () => {
  resendLoading.value = true;
  try {
    const email = localStorage.getItem("registerEmail");
    if (!email) {
      errorMessage.value = "無法取得信箱，請返回登入頁重新操作";
      return;
    }
    await authApi.resendVerificationEmail?.(email);
    errorMessage.value = "驗證信已重新發送至您的信箱，請檢查";
  } catch (error) {
    errorMessage.value = "重新發送失敗，請稍後再試";
  } finally {
    resendLoading.value = false;
  }
};
</script>

<style scoped>
.error-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #1a3a52 0%, #2d5a7b 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.error-container {
  width: 100%;
  max-width: 420px;
}

.error-card {
  background: white;
  border-radius: 12px;
  padding: 50px 40px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
  text-align: center;
}

.error-header {
  margin-bottom: 30px;
}

.error-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 60px;
  height: 60px;
  background-color: #fee2e2;
  border-radius: 50%;
  color: #dc2626;
  font-size: 32px;
  font-weight: bold;
  margin-bottom: 15px;
}

h2 {
  color: #243c52;
  font-size: 24px;
  font-weight: 600;
  margin: 0;
}

.error-content {
  margin-bottom: 35px;
}

.main-text {
  color: #8b95a1;
  font-size: 14px;
  margin: 0;
  line-height: 1.6;
}

.action-section {
  margin-top: 30px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.btn-primary,
.btn-secondary {
  padding: 13px;
  border: none;
  border-radius: 6px;
  font-size: 16px;
  font-weight: 600;
  text-decoration: none;
  cursor: pointer;
  transition: all 0.3s;
  display: block;
  width: 100%;
}

.btn-primary {
  background-color: #243c52;
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background-color: #2d5a7b;
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-secondary {
  background-color: white;
  color: #243c52;
  border: 1.5px solid #d0d7de;
}

.btn-secondary:hover {
  background-color: #f8f9fa;
  border-color: #2d5a7b;
}

@media (max-width: 480px) {
  .error-card {
    padding: 40px 30px;
  }

  h2 {
    font-size: 20px;
  }
}
</style>
