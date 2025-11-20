<template>
  <div class="success-page">
    <div class="success-container">
      <div class="success-card">
        <div class="success-header">
          <div class="success-icon">✓</div>
          <h2>Email 驗證成功</h2>
        </div>

        <div class="success-content">
          <p class="main-text">恭喜！您的帳號已成功驗證</p>
          <p class="sub-text">現在您可以使用完整功能了</p>
        </div>

        <div class="action-section">
          <router-link to="/login" class="btn-primary"> 立即登入 </router-link>
          <p class="countdown-text">
            <small v-if="countdown > 0">
              將在 {{ countdown }} 秒後自動跳轉...
            </small>
            <small v-else>正在跳轉...</small>
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from "vue";
import { useRouter } from "vue-router";

const router = useRouter();
const countdown = ref(5);
let timer = null;

onMounted(() => {
  timer = setInterval(() => {
    countdown.value--;
    if (countdown.value <= 0) {
      clearInterval(timer);
      router.push("/login");
    }
  }, 1000);
});

onUnmounted(() => {
  if (timer) {
    clearInterval(timer);
  }
});
</script>

<style scoped>
.success-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #1a3a52 0%, #2d5a7b 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.success-container {
  width: 100%;
  max-width: 420px;
}

.success-card {
  background: white;
  border-radius: 12px;
  padding: 50px 40px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
  text-align: center;
}

.success-header {
  margin-bottom: 30px;
}

.success-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 60px;
  height: 60px;
  background-color: #dcfce7;
  border-radius: 50%;
  color: #15803d;
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

.success-content {
  margin-bottom: 35px;
}

.main-text {
  color: #243c52;
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 10px 0;
}

.sub-text {
  color: #8b95a1;
  font-size: 14px;
  margin: 0;
}

.action-section {
  margin-top: 30px;
}

.btn-primary {
  display: inline-block;
  width: 100%;
  padding: 13px;
  background-color: #243c52;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 16px;
  font-weight: 600;
  text-decoration: none;
  cursor: pointer;
  transition: background-color 0.3s;
  margin-bottom: 15px;
}

.btn-primary:hover {
  background-color: #2d5a7b;
}

.countdown-text {
  font-size: 12px;
  color: #8b95a1;
  margin: 0;
}

.countdown-text small {
  display: block;
}

@media (max-width: 480px) {
  .success-card {
    padding: 40px 30px;
  }

  h2 {
    font-size: 20px;
  }

  .main-text {
    font-size: 15px;
  }

  .sub-text {
    font-size: 13px;
  }
}
</style>
