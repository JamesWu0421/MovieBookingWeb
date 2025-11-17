<template>
  <div class="profile-page">
    <div class="profile-wrapper">
      <!-- 左側：個人資料 -->
      <div class="left-column">
        <div class="profile-card">
          <div class="profile-header">
            <h2>個人資料</h2>
          </div>

          <!-- 成功/錯誤訊息 -->
          <div v-if="successMessage" class="alert-success">
            ✓ {{ successMessage }}
          </div>
          <div v-if="errorMessage" class="alert-error">
            {{ errorMessage }}
          </div>

          <!-- 頭像區域 -->
          <div class="avatar-section">
            <div class="avatar-container">
              <img :src="currentAvatar" alt="用戶頭像" class="avatar-preview" />
              <div v-if="isEditing" class="avatar-upload-overlay">
                <label for="avatarUpload" class="avatar-upload-btn">📷</label>
                <input
                  type="file"
                  id="avatarUpload"
                  accept="image/*"
                  @change="handleAvatarUpload"
                />
              </div>
            </div>
            <div v-if="uploading" class="uploading-state">上傳中...</div>
          </div>

          <!-- 顯示模式 -->
          <div v-if="!isEditing" class="profile-view">
            <div class="profile-item">
              <span class="label">帳號</span>
              <span class="value">{{ profileData.username }}</span>
            </div>
            <div class="profile-item">
              <span class="label">暱稱</span>
              <span class="value">{{ profileData.nickname || "未設定" }}</span>
            </div>
            <div class="profile-item">
              <span class="label">Email</span>
              <span class="value">{{ profileData.email }}</span>
            </div>
            <div class="profile-item">
              <span class="label">手機號碼</span>
              <span class="value">{{ profileData.phoneNumber }}</span>
            </div>
            <div class="profile-item">
              <span class="label">性別</span>
              <span class="value">{{ formatGender(profileData.gender) }}</span>
            </div>
            <div class="profile-item">
              <span class="label">生日</span>
              <span class="value">{{
                formatBirthday(profileData.birthday)
              }}</span>
            </div>
            <div class="profile-item">
              <span class="label">註冊時間</span>
              <span class="value">{{ formatDate(profileData.createdAt) }}</span>
            </div>

            <div class="button-group">
              <button class="btn-primary" @click="startEdit">編輯資料</button>
              <router-link to="/change-password" class="btn-secondary"
                >修改密碼</router-link
              >
            </div>
          </div>

          <!-- 編輯模式 -->
          <form v-else @submit.prevent="handleUpdate" class="profile-edit">
            <div class="form-group">
              <label for="username">帳號</label>
              <input
                type="text"
                id="username"
                v-model="editForm.username"
                disabled
                class="readonly-input"
              />
              <small>帳號無法修改</small>
            </div>

            <div class="form-group">
              <label for="email">Email</label>
              <input
                type="email"
                id="email"
                v-model="editForm.email"
                disabled
                class="readonly-input"
              />
              <small>Email 無法修改</small>
            </div>

            <div class="form-group">
              <label for="nickname">暱稱</label>
              <input
                type="text"
                id="nickname"
                v-model="editForm.nickname"
                maxlength="20"
                placeholder="請輸入暱稱"
              />
            </div>

            <div class="form-group">
              <label for="phone">手機號碼</label>
              <input
                type="tel"
                id="phone"
                v-model="editForm.phoneNumber"
                pattern="[0-9]{10}"
                placeholder="0912345678"
                required
              />
            </div>

            <div class="form-group">
              <label>性別</label>
              <div class="radio-group">
                <label class="radio-label">
                  <input type="radio" value="male" v-model="editForm.gender" />
                  男
                </label>
                <label class="radio-label">
                  <input
                    type="radio"
                    value="female"
                    v-model="editForm.gender"
                  />
                  女
                </label>
                <label class="radio-label">
                  <input type="radio" value="other" v-model="editForm.gender" />
                  其他
                </label>
              </div>
            </div>

            <div class="form-group">
              <label for="birthday">生日</label>
              <input
                type="date"
                id="birthday"
                v-model="editForm.birthday"
                :max="maxBirthday"
              />
              <small>選擇您的出生日期</small>
            </div>

            <div class="button-group">
              <button type="submit" class="btn-primary" :disabled="loading">
                {{ loading ? "更新中..." : "儲存變更" }}
              </button>
              <button
                type="button"
                class="btn-secondary"
                @click="cancelEdit"
                :disabled="loading"
              >
                取消
              </button>
            </div>
          </form>
        </div>
      </div>

      <!-- 右側：Tab 頁籤內容（票券 + 訂單） -->
      <div class="right-column">
        <!-- Tab 頁籤按鈕 -->
        <div class="tabs-header">
          <button
            :class="['tab-btn', { active: activeTab === 'tickets' }]"
            @click="activeTab = 'tickets'"
          >
            🎫 我的票券
            <span class="count">{{ tickets.length }}</span>
          </button>
          <button
            :class="['tab-btn', { active: activeTab === 'orders' }]"
            @click="activeTab = 'orders'"
          >
            📋 訂單紀錄
            <span class="count">{{ orders.length }}</span>
          </button>
        </div>

        <!-- Tab 內容：我的票券 -->
        <div v-if="activeTab === 'tickets'" class="tab-content">
          <div class="content-card">
            <div v-if="ticketsLoading" class="loading">載入中...</div>
            <div v-else-if="tickets.length === 0" class="empty-state">
              <div class="empty-icon">🎫</div>
              <p>暫無票券</p>
            </div>
            <div v-else class="items-list">
              <div v-for="ticket in tickets" :key="ticket.id" class="item-card">
                <div class="item-info">
                  <div class="item-title">{{ ticket.movieName }}</div>
                  <div class="item-details">
                    <span>{{ formatShowtime(ticket.showtime) }}</span>
                    <span>座位：{{ ticket.seatNumber }}</span>
                  </div>
                </div>
                <div class="item-status">
                  <span :class="['status', `status-${ticket.status}`]">
                    {{ formatStatus(ticket.status) }}
                  </span>
                </div>
                <img :src="ticket.qrCode" class="qr-code" />
              </div>
            </div>
          </div>
        </div>

        <!-- Tab 內容：訂單紀錄 -->
        <div v-if="activeTab === 'orders'" class="tab-content">
          <div class="content-card">
            <div v-if="ordersLoading" class="loading">載入中...</div>
            <div v-else-if="orders.length === 0" class="empty-state">
              <div class="empty-icon">📋</div>
              <p>暫無訂單</p>
            </div>
            <div v-else class="items-list">
              <div v-for="order in orders" :key="order.id" class="order-card">
                <div class="order-header">
                  <div class="order-title">訂單 #{{ order.orderNumber }}</div>
                  <span :class="['status', `status-${order.status}`]">
                    {{ formatOrderStatus(order.status) }}
                  </span>
                </div>
                <div class="order-info">
                  <div class="info-item">
                    <span class="label">日期</span>
                    <span class="value">{{ formatDate(order.createdAt) }}</span>
                  </div>
                  <div class="info-item">
                    <span class="label">票數</span>
                    <span class="value">{{ order.ticketCount }} 張</span>
                  </div>
                  <div class="info-item">
                    <span class="label">金額</span>
                    <span :class="['value', `price-${order.status}`]">
                      NT${{ order.totalAmount }}
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useAuthStore } from "../../stores/login";
import * as authApi from "../../services/api";

const loginStore = useAuthStore();
const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

// 基本資料
const profileData = ref({
  username: "",
  nickname: "",
  email: "",
  phoneNumber: "",
  gender: "",
  birthday: "",
  avatarUrl: "",
  createdAt: "",
});

const editForm = ref({
  username: "",
  nickname: "",
  email: "",
  phoneNumber: "",
  gender: "",
  birthday: "",
  avatarUrl: "",
});

// 狀態
const isEditing = ref(false);
const loading = ref(false);
const uploading = ref(false);
const activeTab = ref("tickets");

const ticketsLoading = ref(false);
const ordersLoading = ref(false);

const errorMessage = ref("");
const successMessage = ref("");

// 資料
const tickets = ref([]);
const orders = ref([]);

// 計算頭像
const currentAvatar = computed(() => {
  const url = isEditing.value
    ? editForm.value.avatarUrl
    : profileData.value.avatarUrl;
  if (url) {
    return url.startsWith("/") ? `${API_BASE_URL}${url}` : url;
  }
  const displayName =
    profileData.value.nickname || profileData.value.username || "User";
  return `https://ui-avatars.com/api/?name=${encodeURIComponent(
    displayName
  )}&background=random&size=300&bold=true`;
});

const maxBirthday = computed(() => {
  return new Date().toISOString().split("T")[0];
});

// 載入資料
const loadProfile = async () => {
  try {
    const data = await authApi.getProfile();
    profileData.value = data;
    loginStore.user = data;
  } catch (error) {
    errorMessage.value = "載入個人資料失敗";
  }
};

const loadTickets = async () => {
  ticketsLoading.value = true;
  try {
    const data = await authApi.getTickets?.();
    if (data) tickets.value = data;
  } catch (error) {
    console.error("載入票券失敗:", error);
  } finally {
    ticketsLoading.value = false;
  }
};

const loadOrders = async () => {
  ordersLoading.value = true;
  try {
    const data = await authApi.getOrders?.();
    if (data) orders.value = data;
  } catch (error) {
    console.error("載入訂單失敗:", error);
  } finally {
    ordersLoading.value = false;
  }
};

// 編輯
const startEdit = () => {
  editForm.value = { ...profileData.value };
  isEditing.value = true;
  errorMessage.value = "";
  successMessage.value = "";
};

const cancelEdit = () => {
  isEditing.value = false;
};

// 上傳頭像
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
    const result = await authApi.uploadAvatar(file);
    editForm.value.avatarUrl = result.url || result;
    event.target.value = "";
  } catch (error) {
    errorMessage.value = error.message || "頭像上傳失敗";
  } finally {
    uploading.value = false;
  }
};

// 更新資料
const handleUpdate = async () => {
  errorMessage.value = "";
  successMessage.value = "";
  loading.value = true;

  try {
    const updateData = {
      nickname: editForm.value.nickname,
      phoneNumber: editForm.value.phoneNumber,
      gender: editForm.value.gender,
      birthday: editForm.value.birthday,
      avatarUrl: editForm.value.avatarUrl,
    };

    const updatedData = await authApi.updateProfile(updateData);
    profileData.value = { ...profileData.value, ...updatedData };
    loginStore.user = { ...loginStore.user, ...updatedData };

    successMessage.value = "個人資料更新成功！";
    isEditing.value = false;
  } catch (error) {
    errorMessage.value = error.message || "更新失敗，請稍後再試";
  } finally {
    loading.value = false;
  }
};

// 格式化
const formatGender = (gender) => {
  const map = { male: "男", female: "女", other: "其他" };
  return map[gender] || "未設定";
};

const formatBirthday = (birthday) => {
  if (!birthday) return "未設定";
  return new Date(birthday).toLocaleDateString("zh-TW", {
    year: "numeric",
    month: "long",
    day: "numeric",
  });
};

const formatDate = (dateString) => {
  if (!dateString) return "-";
  return new Date(dateString).toLocaleDateString("zh-TW", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
  });
};

const formatShowtime = (showtimeString) => {
  if (!showtimeString) return "-";
  return new Date(showtimeString).toLocaleDateString("zh-TW", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
  });
};

const formatStatus = (status) => {
  const map = {
    valid: "有效",
    used: "已使用",
    expired: "已過期",
    cancelled: "已取消",
  };
  return map[status] || status;
};

const formatOrderStatus = (status) => {
  const map = { pending: "待支付", completed: "已完成", cancelled: "已取消" };
  return map[status] || status;
};

onMounted(() => {
  loadProfile();
  loadTickets();
  loadOrders();
});
</script>

<style scoped>
.profile-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #1a3a52 0%, #2d5a7b 100%);
  padding: 40px 20px;
}

.profile-wrapper {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 25px;
}

.left-column,
.right-column {
  min-width: 0;
}

.profile-card,
.content-card {
  background: white;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
}

.profile-header h2 {
  color: #243c52;
  font-size: 20px;
  font-weight: 600;
  margin: 0 0 25px 0;
}

.alert-success,
.alert-error {
  padding: 12px 15px;
  border-radius: 6px;
  margin-bottom: 20px;
  font-size: 13px;
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

/* 頭像 */
.avatar-section {
  text-align: center;
  margin-bottom: 25px;
}

.avatar-container {
  position: relative;
  display: inline-block;
}

.avatar-preview {
  width: 110px;
  height: 110px;
  border-radius: 10px;
  border: 3px solid #e0e7ff;
  object-fit: cover;
  transition: all 0.3s;
}

.avatar-preview:hover {
  border-color: #2d5a7b;
}

.avatar-upload-overlay {
  position: absolute;
  bottom: 0;
  right: 0;
}

.avatar-upload-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  background: #243c52;
  border-radius: 50%;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

.avatar-upload-btn:hover {
  background: #2d5a7b;
  transform: scale(1.1);
}

#avatarUpload {
  display: none;
}

.uploading-state {
  color: #2d5a7b;
  font-size: 12px;
  margin-top: 6px;
}

/* 個人資料顯示 */
.profile-view {
  margin-bottom: 20px;
}

.profile-item {
  display: flex;
  justify-content: space-between;
  padding: 10px 0;
  border-bottom: 1px solid #e0e7ff;
  font-size: 14px;
}

.profile-item:last-child {
  border-bottom: none;
}

.profile-item .label {
  color: #243c52;
  font-weight: 600;
  min-width: 70px;
}

.profile-item .value {
  color: #5a6c7d;
  text-align: right;
  flex: 1;
}

/* 表單 */
.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  color: #243c52;
  font-weight: 600;
  margin-bottom: 6px;
  font-size: 13px;
}

input[type="text"],
input[type="email"],
input[type="tel"],
input[type="date"] {
  width: 100%;
  padding: 10px 12px;
  border: 1.5px solid #d0d7de;
  border-radius: 6px;
  font-size: 13px;
  box-sizing: border-box;
}

input:disabled {
  background-color: #f8f9fa;
  color: #8b95a1;
}

input:focus {
  outline: none;
  border-color: #2d5a7b;
}

small {
  display: block;
  color: #8b95a1;
  font-size: 11px;
  margin-top: 3px;
}

.radio-group {
  display: flex;
  gap: 16px;
}

.radio-label {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  font-size: 13px;
  color: #5a6c7d;
}

/* 按鈕 */
.button-group {
  display: flex;
  gap: 10px;
  margin-top: 20px;
}

.btn-primary,
.btn-secondary {
  flex: 1;
  padding: 11px;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  text-decoration: none;
  text-align: center;
}

.btn-primary {
  background-color: #243c52;
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background-color: #2d5a7b;
}

.btn-secondary {
  background-color: white;
  color: #243c52;
  border: 1.5px solid #d0d7de;
}

.btn-secondary:hover:not(:disabled) {
  background-color: #f8f9fa;
}

/* Tab 頁籤 */
.tabs-header {
  display: flex;
  gap: 8px;
  margin-bottom: 20px;
  border-bottom: 2px solid #e0e7ff;
}

.tab-btn {
  padding: 12px 16px;
  border: none;
  background: transparent;
  color: #8b95a1;
  font-weight: 600;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
  position: relative;
  display: flex;
  align-items: center;
  gap: 8px;
}

.tab-btn:hover {
  color: #243c52;
}

/* .tab-btn.active {
  color: #243c52;
} */

.tab-btn.active::after {
  content: "";
  position: absolute;
  bottom: -2px;
  left: 0;
  right: 0;
  height: 2px;
  background: #243c52;
}

.count {
  background: #e0e7ff;
  color: #243c52;
  padding: 2px 6px;
  border-radius: 10px;
  font-size: 12px;
  font-weight: 700;
}

.tab-content {
  animation: fadeIn 0.3s ease-in;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

/* 內容 */
.loading,
.empty-state {
  text-align: center;
  padding: 40px 20px;
  color: #8b95a1;
}

.empty-icon {
  font-size: 40px;
  margin-bottom: 12px;
}

.empty-state p {
  margin: 0;
}

.items-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  max-height: 500px;
  overflow-y: auto;
}

.item-card {
  display: flex;
  gap: 15px;
  padding: 14px;
  border: 1.5px solid #e0e7ff;
  border-radius: 8px;
  background: #f8f9fa;
  align-items: center;
  transition: all 0.3s;
}

.item-card:hover {
  border-color: #2d5a7b;
  background: white;
  box-shadow: 0 4px 12px rgba(45, 90, 123, 0.1);
}

.item-info {
  flex: 1;
}

.item-title {
  color: #243c52;
  font-weight: 600;
  font-size: 13px;
  margin-bottom: 6px;
}

.item-details {
  display: flex;
  gap: 12px;
  font-size: 12px;
  color: #8b95a1;
}

.item-status {
  flex-shrink: 0;
}

.qr-code {
  width: 60px;
  height: 60px;
  border: 1px solid #d0d7de;
  border-radius: 6px;
  padding: 2px;
  background: white;
}

/* 訂單卡片 */
.order-card {
  padding: 14px;
  border: 1.5px solid #e0e7ff;
  border-radius: 8px;
  background: #f8f9fa;
  transition: all 0.3s;
}

.order-card:hover {
  border-color: #2d5a7b;
  background: white;
  box-shadow: 0 4px 12px rgba(45, 90, 123, 0.1);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.order-title {
  color: #243c52;
  font-weight: 600;
  font-size: 13px;
}

.status {
  display: inline-block;
  padding: 4px 10px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
}

.status-valid {
  background: #dcfce7;
  color: #15803d;
}

.status-used {
  background: #fed7aa;
  color: #ea580c;
}

.status-expired,
.status-cancelled {
  background: #fee2e2;
  color: #dc2626;
}

.status-completed {
  background: #dcfce7;
  color: #15803d;
}

.status-pending {
  background: #fef08a;
  color: #ca8a04;
}

.order-info {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  font-size: 12px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-item .label {
  color: #8b95a1;
  font-weight: 600;
}

.info-item .value {
  color: #243c52;
  font-weight: 600;
}

.price-completed {
  color: #15803d;
}

.price-pending {
  color: #ca8a04;
}

.price-cancelled {
  color: #dc2626;
}

/* 響應式 */
@media (max-width: 768px) {
  .profile-wrapper {
    grid-template-columns: 1fr;
    gap: 20px;
  }

  .order-info {
    grid-template-columns: 1fr;
  }

  .tabs-header {
    flex-wrap: wrap;
  }
}
</style>
