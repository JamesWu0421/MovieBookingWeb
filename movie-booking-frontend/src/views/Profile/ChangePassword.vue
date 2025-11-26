<template>
  <div class="auth-page">
    <div class="auth-container">
      <div class="auth-card">
        <div class="card-header">
          <h2>修改密碼</h2>
          <p class="subtitle">請輸入目前密碼和新密碼</p>
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
          <!-- 目前密碼 -->
          <div class="form-group">
            <label for="currentPassword"
              >目前密碼 <span class="required">*</span></label
            >
            <input
              type="password"
              id="currentPassword"
              v-model="form.oldPassword"
              placeholder="請輸入目前密碼"
              required
            />
          </div>

          <!-- 新密碼 -->
          <div class="form-group">
            <label for="newPassword"
              >新密碼 <span class="required">*</span></label
            >
            <input
              type="password"
              id="newPassword"
              v-model="form.newPassword"
              placeholder="至少 6 個字元"
              minlength="6"
              required
            />
            <small>新密碼必須至少 6 個字元</small>
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
                  form.newPassword !== form.confirmPassword,
              }"
              placeholder="請再次輸入新密碼"
              required
            />
            <span
              v-if="
                form.confirmPassword &&
                form.newPassword !== form.confirmPassword
              "
              class="error-text"
            >
              密碼不一致
            </span>
          </div>

          <!-- 提交按鈕 -->
          <button
            type="submit"
            class="btn-primary"
            :disabled="loading || form.newPassword !== form.confirmPassword"
          >
            {{ loading ? "修改中..." : "確認修改" }}
          </button>
        </form>

        <!-- 成功後的提示 -->
        <div v-else class="success-box">
          <div class="success-icon">✓</div>
          <p class="success-text">密碼修改成功！</p>
          <p class="redirect-text">3 秒後返回個人資料...</p>
          <router-link to="/profile" class="btn-primary">
            立即返回
          </router-link>
        </div>

        <!-- 返回連結 -->
        <div class="footer-links">
          <router-link to="/profile">← 返回個人資料</router-link>
          <span class="separator">|</span>
          <router-link to="/forgot-password">忘記密碼？</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import * as authApi from "../../services/api";

const router = useRouter();

const form = ref({
  oldPassword: "",
  newPassword: "",
  confirmPassword: "",
});

const loading = ref(false);
const errorMessage = ref("");
const successMessage = ref("");
const isSuccess = ref(false);

const handleSubmit = async () => {
  errorMessage.value = "";
  successMessage.value = "";

  if (form.value.newPassword !== form.value.confirmPassword) {
    errorMessage.value = "新密碼與確認密碼不一致";
    return;
  }

  if (form.value.newPassword.length < 6) {
    errorMessage.value = "新密碼至少需要 6 個字元";
    return;
  }

  loading.value = true;

  try {
    await authApi.changePassword(
      form.value.oldPassword,
      form.value.newPassword
    );

    successMessage.value = "密碼修改成功！";
    isSuccess.value = true;

    setTimeout(() => {
      router.push("/profile");
    }, 3000);
  } catch (error) {
    errorMessage.value = error.message || "修改失敗，請檢查目前密碼是否正確";
  } finally {
    loading.value = false;
  }
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

/* 成功提示 */
.success-box {
  text-align: center;
  padding: 30px 20px;
}

.success-icon {
  font-size: 48px;
  margin-bottom: 15px;
  color: #15803d;
}

.success-text {
  color: #243c52;
  font-size: 18px;
  font-weight: 600;
  margin: 0 0 10px 0;
}

.redirect-text {
  color: #8b95a1;
  font-size: 14px;
  margin: 0 0 20px 0;
}

/* 底部連結 */
.footer-links {
  text-align: center;
  font-size: 14px;
  color: #5a6c7d;
  margin-top: 25px;
  padding-top: 20px;
  border-top: 1px solid #e0e7ff;
}

.footer-links a {
  color: #2d5a7b;
  text-decoration: none;
  font-weight: 500;
}

.footer-links a:hover {
  text-decoration: underline;
}

.separator {
  color: #d0d7de;
  margin: 0 10px;
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
