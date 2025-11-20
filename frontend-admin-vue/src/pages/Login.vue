<template>
  <div class="admin-login-page">
    <div class="login-card">
      <h1 class="title">後台管理登入</h1>

      <form @submit.prevent="handleSubmit">
        <div class="form-group">
          <label for="email">員工 Email</label>
          <input
            id="email"
            v-model="email"
            type="email"
            placeholder="請輸入員工 Email"
            required
          />
        </div>

        <div class="form-group">
          <label for="password">密碼</label>
          <input
            id="password"
            v-model="password"
            type="password"
            placeholder="請輸入密碼"
            required
          />
        </div>

        <div v-if="errorMessage" class="error">
          {{ errorMessage }}
        </div>

        <button class="login-btn" type="submit" :disabled="loading">
          {{ loading ? "登入中..." : "登入" }}
        </button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import { adminLogin } from "../api/auth"; // 路徑依你的實際放置調整

const router = useRouter();
const email = ref("");
const password = ref("");
const errorMessage = ref("");
const loading = ref(false);

const handleSubmit = async () => {
  if (!email.value || !password.value) {
    errorMessage.value = "請輸入帳號與密碼";
    return;
  }

  loading.value = true;
  errorMessage.value = "";

  try {
    const token = await adminLogin(email.value, password.value);
    localStorage.setItem("adminToken", token);
    router.push("/dashboard");
  } catch (err) {
    if (err.response && err.response.status === 401) {
      errorMessage.value = "帳號或密碼錯誤";
    } else {
      errorMessage.value = "登入失敗，請稍後再試";
    }
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.admin-login-page {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #0b1120; /* 深藍底 */
}

.login-card {
  width: 360px;
  padding: 32px 28px;
  border-radius: 12px;
  background: #020617;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.45);
  color: #e5e7eb;
}

.title {
  margin-bottom: 24px;
  text-align: center;
  font-size: 22px;
  letter-spacing: 0.08em;
}

.form-group {
  margin-bottom: 16px;
  display: flex;
  flex-direction: column;
}

label {
  font-size: 14px;
  margin-bottom: 4px;
  color: #9ca3af;
}

input {
  padding: 8px 10px;
  border-radius: 6px;
  border: 1px solid #4b5563;
  background: #020617;
  color: #e5e7eb;
  outline: none;
}

input:focus {
  border-color: #3b82f6;
}

.login-btn {
  width: 100%;
  margin-top: 8px;
  padding: 8px 0;
  border-radius: 6px;
  border: none;
  background: #2563eb;
  color: #f9fafb;
  font-size: 15px;
  cursor: pointer;
}

.login-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.error {
  margin-top: 4px;
  margin-bottom: 4px;
  font-size: 13px;
  color: #f97373;
}
</style>
