<template>
  <div class="event-list-page">
    <h1 class="page-title">最新活動</h1>

    <!-- 分類 Tabs -->
    <div class="tabs">
      <button :class="{ active: activeTab === 'all' }" @click="changeTab('all')">
        全部
      </button>
      <button :class="{ active: activeTab === 'announcement' }" @click="changeTab('announcement')">
        公告
      </button>
      <button :class="{ active: activeTab === 'promotion' }" @click="changeTab('promotion')">
        優惠活動
      </button>
    </div>

    <div v-if="events.length === 0" class="empty-text">
      目前沒有相關活動
    </div>

    <!-- 活動卡片 -->
    <div class="event-grid" v-else>
      <div
        v-for="item in events"
        :key="item.id"
        class="event-card"
        @click="goDetail(item.id)"
      >
        <div class="img-box">
          <img :src="item.imageUrl" alt="活動圖片" />
          <span v-if="isExpired(item.endDate)" class="expired-tag">已結束</span>
          <span v-else class="countdown-tag">
            剩 {{ getRemainingDays(item.endDate) }} 天
          </span>
        </div>

        <div class="event-info">
          <span class="tag" :class="item.category">
            {{ item.category === "announcement" ? "公告" : "優惠活動" }}
          </span>
          <h3 class="event-name">{{ item.name }}</h3>
          <p class="date">
            {{ formatDate(item.startDate) }} ~ {{ formatDate(item.endDate) }}
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { fetchAllEvents } from "../../services/api";
import { useRouter } from "vue-router";

const router = useRouter();

const events = ref([]);
const activeTab = ref("all");

// 切換分類
const changeTab = async (tab) => {
  activeTab.value = tab;
  await loadEvents();
};

// 判斷是否過期
const isExpired = (endDate) => {
  if (!endDate) return false;
  return new Date(endDate) < new Date();
};

// 剩餘天數
const getRemainingDays = (endDate) => {
  if (!endDate) return 0;
  const end = new Date(endDate);
  const now = new Date();
  const diff = end - now;
  if (diff <= 0) return 0;
  return Math.ceil(diff / (1000 * 60 * 60 * 24));
};

const formatDate = (dt) => {
  if (!dt) return "";
  return dt.replace("T", " ").slice(0, 16);
};

// 跳詳細頁
const goDetail = (id) => {
  router.push(`/events/${id}`);
};

// 載入活動列表（含排序：進行中在前，已結束在後）
const loadEvents = async () => {
  const params = {};
  if (activeTab.value !== "all") params.category = activeTab.value;

  const res = await fetchAllEvents(params);

  const now = new Date();
  events.value = [...res].sort((a, b) => {
    const aEnd = a.endDate ? new Date(a.endDate) : null;
    const bEnd = b.endDate ? new Date(b.endDate) : null;

    const aExpired = aEnd && aEnd < now;
    const bExpired = bEnd && bEnd < now;

    // 一個過期一個未過期 → 未過期排前面
    if (aExpired !== bExpired) return aExpired ? 1 : -1;

    // 兩個都未過期或都過期 → 依結束時間由近到遠
    if (aEnd && bEnd) return aEnd - bEnd;

    return 0;
  });
};

onMounted(() => {
  loadEvents();
});
</script>

<style scoped>
.event-list-page {
  padding: 24px 60px;
  max-width: 1100px;
  margin: 0 auto;
}

.page-title {
  font-size: 24px;
  font-weight: 700;
  margin-bottom: 16px;
}

.tabs {
  display: flex;
  gap: 10px;
  margin-bottom: 18px;
}

.tabs button {
  padding: 6px 16px;
  border-radius: 999px;
  background: #f3f4f6;
  border: 1px solid transparent;
  cursor: pointer;
  font-size: 14px;
}

.tabs .active {
  background: #203a53;
  color: white;
}

.empty-text {
  margin-top: 40px;
  color: #777;
  font-size: 14px;
}

.event-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 22px;
}

.event-card {
  cursor: pointer;
  border-radius: 10px;
  overflow: hidden;
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: transform 0.18s ease, box-shadow 0.18s ease;
  font-size: 14px;
}

.event-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 14px rgba(0, 0, 0, 0.12);
}

.img-box {
  position: relative;
  height: 200px;
  background: #eee;
}

.img-box img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.expired-tag,
.countdown-tag {
  position: absolute;
  top: 8px;
  left: 8px;
  padding: 3px 8px;
  border-radius: 999px;
  font-size: 11px;
  color: #fff;
}

.expired-tag {
  background: rgba(55, 65, 81, 0.9);
}

.countdown-tag {
  background: rgba(220, 38, 38, 0.9);
}

.event-info {
  padding: 10px 12px 12px;
}

.tag {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 999px;
  font-size: 11px;
}

.tag.announcement {
  background: #e5f0ff;
  color: #1d4ed8;
}

.tag.promotion {
  background: #dcfce7;
  color: #15803d;
}

.event-name {
  margin: 6px 0 4px;
  font-size: 15px;
  font-weight: 600;
}

.date {
  color: #6b7280;
  font-size: 12px;
}
</style>
