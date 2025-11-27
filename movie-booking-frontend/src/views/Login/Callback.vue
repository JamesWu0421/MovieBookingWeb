<template>
  <div class="oauth-page">
    <div class="oauth-container">
      <div class="oauth-card">
        <!-- 載入中 -->
        <div v-if="loading" class="loading-state">
          <div class="spinner"></div>
          <p>登入中，請稍候...</p>
        </div>

        <!-- 錯誤狀態 -->
        <div v-if="error && !loading" class="error-state">
          <div class="error-icon">⚠️</div>
          <p class="error-message">{{ error }}</p>
          <button class="btn-back" @click="goToLogin">返回登入頁面</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "../../stores/login";
import * as authApi from "../../services/api";

const loading = ref(true);
const error = ref("");
const router = useRouter();
const loginStore = useAuthStore();

const goToLogin = () => {
  router.push("/login");
};

onMounted(async () => {
  try {
    // 從 URL 參數取得 token 和用戶資訊
    const urlParams = new URLSearchParams(window.location.search);
    const token = urlParams.get("token");
    const nickname = urlParams.get("nickname");

    console.log("收到的 token:", token);
    console.log("收到的 nickname:", nickname);

    if (!token) {
      error.value = "登入失敗，找不到授權憑證";
      loading.value = false;
      setTimeout(() => {
        router.push("/login");
      }, 2000);
      return;
    }

    // 1. 設定 token 到 store（類似傳統登入）
    loginStore.setAuth(token, { nickname: nickname || "User" });
    console.log("Token 已設定到 loginStore");

    // 2. 取得完整用戶資料
    try {
      const profile = await authApi.getProfile();
      console.log("取得的用戶資料:", profile);

      loginStore.user = profile;
      localStorage.setItem("user", JSON.stringify(profile));
    } catch (profileError) {
      console.error("取得個人資料失敗:", profileError);
      // 即使取得 profile 失敗，token 已經設定，還是算登入成功
    }

    console.log("Google 登入成功");

    // 跳轉到首頁
    setTimeout(() => {
      router.replace("/");
    }, 500);
  } catch (err) {
    console.error("登入處理失敗:", err);
    error.value = err.message || "登入失敗，請重新嘗試";
    loginStore.clearAuth();

    setTimeout(() => {
      router.push("/login");
    }, 2000);
  } finally {
    loading.value = false;
  }
});
</script>

<style scoped>
.oauth-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #1a3a52 0%, #2d5a7b 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.oauth-container {
  width: 100%;
  max-width: 420px;
}

.oauth-card {
  background: white;
  border-radius: 12px;
  padding: 60px 35px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
  text-align: center;
}

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
}

.spinner {
  width: 50px;
  height: 50px;
  border: 4px solid #e0e7ff;
  border-top-color: #2d5a7b;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.loading-state p {
  color: #243c52;
  font-size: 16px;
  font-weight: 500;
  margin: 0;
}

.error-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
}

.error-icon {
  font-size: 48px;
}

.error-message {
  color: #c33;
  font-size: 15px;
  margin: 0;
  line-height: 1.6;
}

.btn-back {
  padding: 12px 30px;
  background-color: #243c52;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.3s;
  margin-top: 10px;
}

.btn-back:hover {
  background-color: #2d5a7b;
}
</style>
