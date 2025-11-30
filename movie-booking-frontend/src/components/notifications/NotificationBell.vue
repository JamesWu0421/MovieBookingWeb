<template>
  <div class="notification-bell">
    <!-- 通知鈴鐺按鈕 -->
    <button class="bell-button" @click="toggleDropdown">
      <span class="bell-icon">🔔</span>
      <span v-if="notificationStore.hasUnread" class="badge">
        {{ notificationStore.unreadCount > 99 ? '99+' : notificationStore.unreadCount }}
      </span>
    </button>

    <!-- 下拉通知面板 -->
    <transition name="dropdown">
      <div v-if="showDropdown" class="dropdown" v-click-outside="closeDropdown">
        
        <!-- 標題列 -->
        <div class="dropdown-header">
          <div class="header-left">
            <h3>通知</h3>
            <span v-if="notificationStore.unreadCount > 0" class="unread-badge">
              {{ notificationStore.unreadCount }} 則未讀
            </span>
          </div>
          <div class="header-actions">
            <!-- 🆕 聲音開關 -->
            <button 
              class="icon-button" 
              @click.stop="toggleSound"
              :title="notificationStore.soundEnabled ? '關閉聲音' : '開啟聲音'"
            >
              <span v-if="notificationStore.soundEnabled">🔊</span>
              <span v-else>🔇</span>
            </button>
            
            <!-- 🆕 瀏覽器通知開關 -->
            <button 
              v-if="!notificationStore.browserNotificationEnabled"
              class="icon-button" 
              @click.stop="enableBrowserNotification"
              title="啟用瀏覽器通知"
            >
              🔔
            </button>
            
            <!-- 全部標記已讀 -->
            <button 
              v-if="notificationStore.hasUnread" 
              class="mark-all-btn"
              @click.stop="markAllAsRead"
              title="全部標記為已讀"
            >
              ✓
            </button>
          </div>
        </div>

        <!-- 載入中 -->
        <div v-if="loading" class="dropdown-loading">
          <div class="spinner"></div>
          <p>載入中...</p>
        </div>

        <!-- 通知列表 -->
        <div v-else-if="notifications.length" class="notification-list">
          <div
            v-for="n in notifications"
            :key="n.id"
            class="notification-item"
            :class="{ unread: !n.isRead }"
            @click="handleNotificationClick(n)"
          >
            <!-- 通知圖標 -->
            <div class="notification-icon" :class="getIconClass(n.type)">
              <span>{{ getIcon(n.type) }}</span>
            </div>

            <!-- 通知內容 -->
            <div class="notification-content">
              <div class="notification-header-row">
                <p class="notification-title">{{ n.title }}</p>
                <span v-if="!n.isRead" class="unread-dot"></span>
              </div>
              <p class="notification-message">{{ n.content || n.message }}</p>
              <div class="notification-footer">
                <span class="notification-type-tag">{{ getTypeLabel(n.type) }}</span>
                <span class="notification-time">{{ formatTime(n.createdAt) }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 空狀態 -->
        <div v-else class="dropdown-empty">
          <div class="empty-icon">📭</div>
          <p>目前沒有通知</p>
        </div>

        <!-- 底部操作列 -->
        <div class="dropdown-footer">
          <router-link to="/notifications" class="view-all-link" @click="closeDropdown">
            查看全部通知 →
          </router-link>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { useNotificationStore } from "../../stores/notification";
import { useAuthStore } from "../../stores/login";
import { ElMessage } from 'element-plus';

const router = useRouter();
const notificationStore = useNotificationStore();
const authStore = useAuthStore();

const showDropdown = ref(false);
const notifications = ref([]);
const loading = ref(false);

// 切換下拉選單
async function toggleDropdown() {
  showDropdown.value = !showDropdown.value;
  if (showDropdown.value) {
    await loadRecent();
  }
}

// 關閉下拉選單
function closeDropdown() {
  showDropdown.value = false;
}

// 載入最近通知
async function loadRecent() {
  loading.value = true;
  await notificationStore.fetchNotifications(false);
  notifications.value = notificationStore.notifications.slice(0, 6); // 🆕 顯示6則
  loading.value = false;
}

// 處理通知點擊
async function handleNotificationClick(n) {
  closeDropdown();
  await notificationStore.markAsRead(n.id);
  
  // 根據類型跳轉
  if (n.relatedType === 'EVENT' && n.relatedId) {
    router.push(`/events/${n.relatedId}`);
  } else if (n.relatedType === 'MOVIE' && n.relatedId) {
    router.push(`/movies/${n.relatedId}`);
  }
}

// 全部標記已讀
async function markAllAsRead() {
  await notificationStore.markAllAsRead();
  await loadRecent();
  ElMessage.success('已全部標記為已讀');
}

// 🆕 切換聲音
function toggleSound() {
  notificationStore.toggleSound();
  ElMessage.success(
    notificationStore.soundEnabled ? '已開啟通知音效' : '已關閉通知音效'
  );
}

// 🆕 啟用瀏覽器通知
async function enableBrowserNotification() {
  const granted = await notificationStore.requestNotificationPermission();
  if (granted) {
    ElMessage.success('已啟用瀏覽器通知');
  } else {
    ElMessage.warning('瀏覽器通知已被拒絕');
  }
}

// 🆕 獲取通知圖標
function getIcon(type) {
  const icons = {
    SYSTEM: '⚙️',
    PROMOTION: '🎉',
    MOVIE: '🎬',
    ORDER: '🎫',
  };
  return icons[type] || '📢';
}

// 🆕 獲取圖標樣式類別
function getIconClass(type) {
  return `icon-${type?.toLowerCase() || 'default'}`;
}

// 🆕 獲取類型標籤
function getTypeLabel(type) {
  const labels = {
    SYSTEM: '系統',
    PROMOTION: '優惠',
    MOVIE: '電影',
    ORDER: '訂單',
  };
  return labels[type] || '通知';
}

// 格式化時間
function formatTime(dateString) {
  if (!dateString) return '';
  
  const date = new Date(dateString);
  const now = new Date();
  const diff = now - date;
  
  // 少於1分鐘
  if (diff < 60000) return '剛剛';
  
  // 少於1小時
  if (diff < 3600000) {
    const minutes = Math.floor(diff / 60000);
    return `${minutes}分鐘前`;
  }
  
  // 少於24小時
  if (diff < 86400000) {
    const hours = Math.floor(diff / 3600000);
    return `${hours}小時前`;
  }
  
  // 少於7天
  if (diff < 604800000) {
    const days = Math.floor(diff / 86400000);
    return `${days}天前`;
  }
  
  // 超過7天,顯示日期
  return date.toLocaleDateString('zh-TW', {
    month: '2-digit',
    day: '2-digit',
  });
}

// 初始化
onMounted(() => {
  if (authStore.isAuthenticated) {
    notificationStore.initSettings();
    notificationStore.startAutoRefresh();
    notificationStore.fetchUnreadCount();
  }
});
</script>

<style scoped>
/* 整體容器 */
.notification-bell {
  position: relative;
}

/* 鈴鐺按鈕 */
.bell-button {
  background: none;
  border: none;
  cursor: pointer;
  position: relative;
  padding: 8px;
  border-radius: 8px;
  transition: background-color 0.2s;
}

.bell-button:hover {
  background-color: rgba(0, 0, 0, 0.05);
}

.bell-icon {
  font-size: 22px;
}

/* 未讀徽章 */
.badge {
  position: absolute;
  top: 2px;
  right: 2px;
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
  color: white;
  font-size: 10px;
  min-width: 18px;
  height: 18px;
  padding: 0 5px;
  border-radius: 999px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 4px rgba(239, 68, 68, 0.3);
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.1);
  }
}

/* 下拉框主體 */
.dropdown {
  position: absolute;
  right: 0;
  top: 45px;
  width: 420px; /* 🆕 加寬 */
  background: white;
  border-radius: 16px; /* 🆕 更圓潤 */
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
  overflow: hidden;
  z-index: 9999;
  border: 1px solid rgba(0, 0, 0, 0.05);
}

/* 標題列 */
.dropdown-header {
  padding: 18px 20px; /* 🆕 增加間距 */
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #f1f5f9;
  background: linear-gradient(to bottom, #fafafa, #ffffff);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.dropdown-header h3 {
  font-size: 18px;
  font-weight: 700;
  margin: 0;
  color: #1f2937;
}

.unread-badge {
  font-size: 12px;
  color: #6b7280;
  background: #f3f4f6;
  padding: 3px 8px;
  border-radius: 999px;
  font-weight: 500;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

/* 🆕 圖標按鈕 */
.icon-button {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 18px;
  padding: 4px 8px;
  border-radius: 6px;
  transition: background-color 0.2s;
}

.icon-button:hover {
  background-color: #f3f4f6;
}

.mark-all-btn {
  background: #3b82f6;
  color: white;
  border: none;
  font-size: 14px;
  font-weight: 600;
  padding: 5px 12px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
}

.mark-all-btn:hover {
  background: #2563eb;
  transform: translateY(-1px);
}

/* 載入中 */
.dropdown-loading {
  padding: 50px 20px;
  text-align: center;
  color: #9ca3af;
}

.spinner {
  width: 36px;
  height: 36px;
  border: 3px solid #f3f4f6;
  border-top-color: #3b82f6;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin: 0 auto 12px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* 通知列表 */
.notification-list {
  max-height: 450px; /* 🆕 增加高度 */
  overflow-y: auto;
  padding: 8px 0; /* 🆕 增加上下間距 */
}

/* 自訂滾動條 */
.notification-list::-webkit-scrollbar {
  width: 6px;
}

.notification-list::-webkit-scrollbar-track {
  background: #f9fafb;
}

.notification-list::-webkit-scrollbar-thumb {
  background: #d1d5db;
  border-radius: 3px;
}

.notification-list::-webkit-scrollbar-thumb:hover {
  background: #9ca3af;
}

/* 單一通知卡片 */
.notification-item {
  display: flex;
  gap: 14px; /* 🆕 增加間距 */
  padding: 16px 20px; /* 🆕 增加內距 */
  cursor: pointer;
  transition: all 0.2s;
  border-bottom: 1px solid #f9fafb;
  position: relative;
}

.notification-item:last-child {
  border-bottom: none;
}

.notification-item:hover {
  background: #f9fafb;
}

.notification-item.unread {
  background: linear-gradient(to right, #eff6ff, #ffffff);
  border-left: 3px solid #3b82f6;
}

.notification-item.unread:hover {
  background: linear-gradient(to right, #dbeafe, #f9fafb);
}

/* 通知圖標 */
.notification-icon {
  width: 44px; /* 🆕 加大 */
  height: 44px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  flex-shrink: 0;
  transition: transform 0.2s;
}

.notification-item:hover .notification-icon {
  transform: scale(1.05);
}

/* 🆕 不同類型的圖標顏色 */
.icon-system {
  background: linear-gradient(135deg, #f3f4f6 0%, #e5e7eb 100%);
}

.icon-promotion {
  background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
}

.icon-movie {
  background: linear-gradient(135deg, #ddd6fe 0%, #c4b5fd 100%);
}

.icon-order {
  background: linear-gradient(135deg, #fecaca 0%, #fca5a5 100%);
}

.icon-default {
  background: linear-gradient(135deg, #e0f2fe 0%, #bae6fd 100%);
}

/* 通知內容 */
.notification-content {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 6px; /* 🆕 增加間距 */
}

.notification-header-row {
  display: flex;
  align-items: flex-start;
  gap: 8px;
}

.notification-title {
  font-size: 15px;
  font-weight: 600;
  color: #1f2937;
  margin: 0;
  flex: 1;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.unread-dot {
  width: 8px;
  height: 8px;
  background: #3b82f6;
  border-radius: 50%;
  flex-shrink: 0;
  margin-top: 4px;
  animation: pulse-dot 2s ease-in-out infinite;
}

@keyframes pulse-dot {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}

.notification-message {
  font-size: 13px;
  color: #6b7280;
  margin: 0;
  line-height: 1.5;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

/* 🆕 通知底部 */
.notification-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
}

.notification-type-tag {
  font-size: 11px;
  color: #6b7280;
  background: #f3f4f6;
  padding: 2px 8px;
  border-radius: 4px;
  font-weight: 500;
  flex-shrink: 0;
}

.notification-time {
  font-size: 12px;
  color: #9ca3af;
  flex-shrink: 0;
}

/* 空狀態 */
.dropdown-empty {
  text-align: center;
  padding: 50px 20px;
  color: #9ca3af;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 12px;
  opacity: 0.6;
}

.dropdown-empty p {
  font-size: 14px;
  margin: 0;
}

/* 🆕 底部操作列 */
.dropdown-footer {
  padding: 12px 20px;
  border-top: 1px solid #f1f5f9;
  background: #fafafa;
  text-align: center;
}

.view-all-link {
  color: #3b82f6;
  text-decoration: none;
  font-size: 14px;
  font-weight: 600;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  transition: color 0.2s;
}

.view-all-link:hover {
  color: #2563eb;
  text-decoration: underline;
}

/* 下拉動畫 */
.dropdown-enter-active {
  animation: dropdown-in 0.2s ease-out;
}

.dropdown-leave-active {
  animation: dropdown-out 0.15s ease-in;
}

@keyframes dropdown-in {
  from {
    opacity: 0;
    transform: translateY(-10px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

@keyframes dropdown-out {
  from {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
  to {
    opacity: 0;
    transform: translateY(-10px) scale(0.95);
  }
}

/* 響應式 */
@media (max-width: 480px) {
  .dropdown {
    width: calc(100vw - 40px);
    right: -10px;
  }
}
</style>