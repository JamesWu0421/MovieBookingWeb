<template>
  <div class="auth-page">
    <div class="auth-container">
      <div class="auth-card">
        <div class="card-header">
          <h2>重設密碼</h2>
          <p class="subtitle">請輸入新密碼以重設您的帳號</p>
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
          <!-- 新密碼 -->
          <div class="form-group">
            <label for="password">新密碼 <span class="required">*</span></label>
            <input
              type="password"
              id="password"
              v-model="form.password"
              placeholder="至少 6 個字元"
              minlength="6"
              required
            />
            <small>密碼必須至少 6 個字元</small>
          </div>

          <!-- 確認新密碼 -->
          <div class="form-group">
            <label for="confirmPassword"
              >確認新密碼 <span class="required">*</span></label
            >
            <input
              type="password"
              id="confirmPassword"
              v-model="form.confirmPassword"
              :class="{
                'input-error':
                  form.confirmPassword &&
                  form.password !== form.confirmPassword,
              }"
              placeholder="請再次輸入新密碼"
              required
            />
            <span
              v-if="
                form.confirmPassword && form.password !== form.confirmPassword
              "
              class="error-text"
            >
              密碼不一致
            </span>
          </div>

          <!-- 重設按鈕 -->
          <button
            type="submit"
            class="btn-primary"
            :disabled="loading || form.password !== form.confirmPassword"
          >
            {{ loading ? "重設中..." : "重設密碼" }}
          </button>
        </form>

        <!-- 成功後提示 -->
        <div v-else class="success-box">
          <div class="success-icon">✓</div>
          <p class="success-text">密碼重設成功！</p>
          <p class="redirect-text">即將跳轉到登入頁面...</p>
          <router-link to="/login" class="btn-primary"> 立即登入 </router-link>
        </div>

        <!-- 返回連結 -->
        <div class="footer-link">
          <router-link to="/login">← 返回登入</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import * as authApi from "@/services/api";

const route = useRoute();
const router = useRouter();

const form = ref({
  password: "",
  confirmPassword: "",
});

const loading = ref(false);
const errorMessage = ref("");
const successMessage = ref("");
const isSuccess = ref(false);
const resetToken = ref("");

onMounted(() => {
  resetToken.value = route.query.token;

  if (!resetToken.value) {
    errorMessage.value = "無效的重設連結，請重新申請";
  }
});

const handleSubmit = async () => {
  errorMessage.value = "";
  successMessage.value = "";

  if (form.value.password !== form.value.confirmPassword) {
    errorMessage.value = "新密碼與確認密碼不一致";
    return;
  }

  if (form.value.password.length < 6) {
    errorMessage.value = "密碼至少需要 6 個字元";
    return;
  }

  loading.value = true;

  try {
    await authApi.resetPassword?.(resetToken.value, form.value.password);

    successMessage.value = "密碼重設成功！";
    isSuccess.value = true;

    setTimeout(() => {
      router.push("/login");
    }, 2000);
  } catch (error) {
    if (error.response?.status === 400) {
      errorMessage.value = "重設連結已過期，請重新申請";
    } else {
      errorMessage.value = error.message || "重設失敗，請稍後再試";
    }
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

.card-header {
  text-align: center;
  margin-bottom: 30px;
}

.card-header h2 {
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

/* 表單 */
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

input[type="password"] {
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

input.input-error {
  border-color: #dc3545;
}

small {
  display: block;
  color: #8b95a1;
  font-size: 12px;
  margin-top: 4px;
}

.error-text {
  color: #dc3545;
  font-size: 12px;
  display: block;
  margin-top: 4px;
}

/* 按鈕 */
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
  text-decoration: none;
  text-align: center;
  display: inline-block;
}

.btn-primary:hover:not(:disabled) {
  background-color: #2d5a7b;
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* 成功狀態 */
.success-box {
  text-align: center;
  padding: 20px 0;
}

.success-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 50px;
  height: 50px;
  background-color: #dcfce7;
  border-radius: 50%;
  color: #15803d;
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 15px;
}

.success-text {
  color: #243c52;
  font-size: 18px;
  font-weight: 600;
  margin: 0 0 8px 0;
}

.redirect-text {
  color: #8b95a1;
  font-size: 14px;
  margin: 0 0 20px 0;
}

/* 底部連結 */
.footer-link {
  text-align: center;
  padding-top: 20px;
  border-top: 1px solid #e0e7ff;
}

.footer-link a {
  color: #2d5a7b;
  text-decoration: none;
  font-weight: 500;
  font-size: 14px;
}

.footer-link a:hover {
  text-decoration: underline;
}

@media (max-width: 480px) {
  .auth-card {
    padding: 30px 25px;
  }

  .card-header h2 {
    font-size: 20px;
  }
}
</style>
