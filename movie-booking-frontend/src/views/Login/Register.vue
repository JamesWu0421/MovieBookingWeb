<template>
  <div class="auth-page">
    <div class="auth-container">
      <div class="auth-card">
        <div class="logo-section">
          <img src="/images/theater6.png" alt="Logo" class="logo-img" />
          <h2>會員註冊</h2>
        </div>

        <!-- 成功訊息 -->
        <div v-if="successMessage" class="alert-success">
          ✓ {{ successMessage }}
        </div>

        <!-- 錯誤訊息 -->
        <div v-if="errorMessage" class="alert-error">
          {{ errorMessage }}
        </div>

        <!-- 頭像預覽與上傳 -->
        <div class="avatar-section">
          <img :src="currentAvatar" alt="頭像預覽" class="avatar-preview" />
          <div class="avatar-controls">
            <label for="avatarUpload" class="btn-upload"> 📤 上傳頭像 </label>
            <input
              type="file"
              id="avatarUpload"
              accept="image/*"
              @change="handleAvatarUpload"
            />

            <button
              v-if="uploadedAvatarUrl"
              type="button"
              class="btn-remove"
              @click="removeUploadedAvatar"
            >
              ✕ 移除
            </button>
          </div>
          <p class="avatar-hint">{{ avatarDescription }}</p>
          <div v-if="uploading" class="uploading">上傳中...</div>
        </div>

        <form @submit.prevent="handleRegister">
          <!-- 帳號 -->
          <div class="form-group">
            <label>帳號 <span class="required">*</span></label>
            <input
              type="text"
              v-model="form.username"
              placeholder="請輸入帳號（4-20字元）"
              required
              minlength="4"
              maxlength="20"
            />
          </div>

          <!-- 暱稱 -->
          <div class="form-group">
            <label>暱稱</label>
            <input
              type="text"
              v-model="form.nickname"
              placeholder="選填，用於顯示"
              maxlength="20"
            />
          </div>

          <!-- Email -->
          <div class="form-group">
            <label>Email <span class="required">*</span></label>
            <input
              type="email"
              v-model="form.email"
              placeholder="example@example.com"
              required
            />
          </div>

          <!-- 手機號碼 -->
          <div class="form-group">
            <label>手機號碼 <span class="required">*</span></label>
            <input
              type="tel"
              v-model="form.phoneNumber"
              placeholder="0912345678"
              pattern="[0-9]{10}"
              maxlength="10"
              required
            />
          </div>

          <!-- 性別 -->
          <div class="form-group">
            <label>性別 <span class="required">*</span></label>
            <div class="radio-group">
              <label class="radio-label">
                <input
                  type="radio"
                  value="male"
                  v-model="form.gender"
                  required
                />
                男
              </label>
              <label class="radio-label">
                <input
                  type="radio"
                  value="female"
                  v-model="form.gender"
                  required
                />
                女
              </label>
              <label class="radio-label">
                <input
                  type="radio"
                  value="other"
                  v-model="form.gender"
                  required
                />
                其他
              </label>
            </div>
          </div>

          <!-- 生日 -->
          <div class="form-group">
            <label>生日 <span class="required">*</span></label>
            <input type="date" v-model="form.birthday" required />
          </div>

          <!-- 密碼 -->
          <div class="form-group">
            <label>密碼 <span class="required">*</span></label>
            <input
              type="password"
              v-model="form.password"
              placeholder="至少6個字元"
              minlength="6"
              required
            />
          </div>

          <!-- 確認密碼 -->
          <div class="form-group">
            <label>確認密碼 <span class="required">*</span></label>
            <input
              type="password"
              v-model="form.confirmPassword"
              :class="{
                'input-error':
                  form.confirmPassword &&
                  form.password !== form.confirmPassword,
              }"
              placeholder="請再次輸入密碼"
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

          <!-- 註冊按鈕 -->
          <button
            type="submit"
            class="btn-primary"
            :disabled="
              loading || uploading || form.password !== form.confirmPassword
            "
          >
            {{ loading ? "註冊中..." : "註冊" }}
          </button>
        </form>

        <!-- 已有帳號 -->
        <div class="login-link">
          已經有帳號？
          <router-link to="/login">立即登入</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from "vue";
import { useRouter } from "vue-router";
import { register, uploadAvatar } from "../../services/api";

const router = useRouter();

const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

const form = ref({
  username: "",
  email: "",
  phoneNumber: "",
  nickname: "",
  gender: "",
  birthday: "",
  password: "",
  confirmPassword: "",
});

const loading = ref(false);
const uploading = ref(false);
const errorMessage = ref("");
const successMessage = ref("");
const uploadedAvatarUrl = ref("");

const autoGeneratedAvatar = computed(() => {
  const displayName = form.value.nickname || form.value.username || "User";
  try {
    const encodedName = encodeURIComponent(displayName);
    return `https://ui-avatars.com/api/?name=${encodedName}&background=random&size=300&bold=true`;
  } catch {
    return "https://ui-avatars.com/api/?name=User&background=random&size=300";
  }
});

const currentAvatar = computed(() => {
  if (uploadedAvatarUrl.value) {
    if (uploadedAvatarUrl.value.startsWith("/")) {
      return `${API_BASE_URL}${uploadedAvatarUrl.value}`;
    }
    return uploadedAvatarUrl.value;
  }
  return autoGeneratedAvatar.value;
});

const avatarDescription = computed(() => {
  return uploadedAvatarUrl.value ? "已上傳自訂頭像" : "自動生成預設頭像";
});

const handleAvatarUpload = async (event) => {
  const file = event.target.files[0];
  if (!file) return;

  if (!file.type.startsWith("image/")) {
    errorMessage.value = "請選擇圖片檔案";
    return;
  }

  if (file.size > 10 * 1024 * 1024) {
    errorMessage.value = "圖片大小不能超過 10MB";
    return;
  }

  uploading.value = true;
  errorMessage.value = "";

  try {
    const result = await uploadAvatar(file);
    uploadedAvatarUrl.value = result.url || result;
    event.target.value = "";
  } catch (error) {
    errorMessage.value = error.message || "頭像上傳失敗";
  } finally {
    uploading.value = false;
  }
};

const removeUploadedAvatar = () => {
  uploadedAvatarUrl.value = "";
};

const handleRegister = async () => {
  errorMessage.value = "";
  successMessage.value = "";

  if (form.value.password !== form.value.confirmPassword) {
    errorMessage.value = "密碼與確認密碼不一致";
    return;
  }

  loading.value = true;

  try {
    const { confirmPassword, ...registerData } = form.value;
    registerData.avatarUrl = uploadedAvatarUrl.value || "";
    // 確保生日不是空字串
    if (!registerData.birthday) {
      errorMessage.value = "請選擇生日";
      loading.value = false;
      return;
    }

    console.log("送出資料:", registerData); // ✅ 檢查 JSON
    await register(registerData);

    successMessage.value = "註冊成功！請檢查您的信箱進行驗證。";

    setTimeout(() => {
      router.push("/login");
    }, 3000);
  } catch (error) {
    if (error.response && error.response.data) {
      errorMessage.value = error.response.data;
    } else {
      errorMessage.value = error.message || "註冊失敗，請稍後再試";
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
  max-width: 500px;
}

.auth-card {
  background: white;
  border-radius: 12px;
  padding: 40px 35px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
}

.logo-section {
  text-align: center;
  margin-bottom: 30px;
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

.avatar-section {
  text-align: center;
  margin-bottom: 30px;
  padding: 20px;
  background: linear-gradient(135deg, #243c52 0%, #2d5a7b 100%);
  border-radius: 10px;
}

.avatar-preview {
  width: 100px;
  height: 100px;
  border-radius: 10px;
  border: 3px solid white;
  object-fit: cover;
  margin-bottom: 12px;
}

.avatar-controls {
  display: flex;
  justify-content: center;
  gap: 8px;
  margin-bottom: 10px;
}

.btn-upload,
.btn-remove {
  padding: 8px 12px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 13px;
  border: none;
  transition: all 0.3s;
}

.btn-upload {
  background: white;
  color: #243c52;
  font-weight: 600;
}

.btn-upload:hover {
  background: #f8f9fa;
}

#avatarUpload {
  display: none;
}

.btn-remove {
  background: rgba(255, 255, 255, 0.2);
  color: white;
  font-weight: 500;
}

.btn-remove:hover {
  background: rgba(255, 255, 255, 0.3);
}

.avatar-hint {
  color: #d0d7de;
  font-size: 12px;
  margin: 8px 0 0 0;
}

.uploading {
  color: white;
  font-size: 13px;
  margin-top: 8px;
}

.form-group {
  margin-bottom: 18px;
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

input[type="text"],
input[type="email"],
input[type="tel"],
input[type="date"],
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

.error-text {
  color: #dc3545;
  font-size: 12px;
  display: block;
  margin-top: 4px;
}

.radio-group {
  display: flex;
  gap: 20px;
}

.radio-label {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  font-weight: 400;
  color: #5a6c7d;
}

input[type="radio"] {
  cursor: pointer;
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

.login-link {
  text-align: center;
  font-size: 14px;
  color: #5a6c7d;
}

.login-link a {
  color: #2d5a7b;
  text-decoration: none;
  font-weight: 600;
}

.login-link a:hover {
  text-decoration: underline;
}
</style>
