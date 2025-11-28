<template>
  <div class="detail-page" v-if="event">
    
    <!-- 返回活動列表 -->
    <button class="back-btn" @click="goBack">← 返回活動列表</button>

    <h1 class="title">{{ event.name }}</h1>

    <div class="status-bar">
      <span v-if="isExpired(event.endDate)" class="expired-badge">活動已結束</span>
      <span v-else class="countdown">剩 {{ remainingDays }} 天</span>
    </div>

    <div class="content">
      <!-- 左側圖片 -->
      <div class="image-box">
        <img :src="event.imageUrl" alt="活動圖片" />
      </div>

      <!-- 右側內容 -->
      <div class="info-box">
        <div class="row"><strong>分類：</strong>{{ categoryText }}</div>

        <div class="row">
          <strong>活動時間：</strong>
          {{ formatDate(event.startDate) }} ~ {{ formatDate(event.endDate) }}
        </div>

        <div class="section" v-if="event.description">
          <strong>活動內容：</strong>
          <p>{{ event.description }}</p>
        </div>

        <div class="section" v-if="event.notes">
          <strong>注意事項：</strong>
          <p>{{ event.notes }}</p>
        </div>

        <!-- 優惠類活動 -->
        <div v-if="event.category === 'promotion'">
          <div class="row" v-if="event.minAmount">
            <strong>最低消費金額：</strong>{{ event.minAmount }} 元
          </div>

          <div class="row" v-if="event.maxUsagePerUser">
            <strong>每人最多使用：</strong>{{ event.maxUsagePerUser }} 次
          </div>

          <div class="row" v-if="event.requiresCoupon">
            <strong>優惠碼：</strong>
            <span class="coupon">{{ event.couponCode }}</span>
          </div>
        </div>

        <!-- 抽獎活動 -->
        <div v-if="event.discountType === 'lottery'" class="section">
          <div class="row">
            <strong>抽獎資格：</strong> {{ event.lotteryRequirement }}
          </div>

          <div class="row" v-if="event.prizeDescription">
            <strong>獎品內容：</strong>
            <p>{{ event.prizeDescription }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 右下角瀏覽次數 -->
    <div class="view-count">
      👁️ {{ event.viewCount || 0 }} 次瀏覽
    </div>
  </div>

  <div v-else class="loading">載入中…</div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import { fetchEventDetail } from "../../services/api";

const route = useRoute();
const router = useRouter();
const event = ref(null);

const loadDetail = async () => {
  const id = route.params.id;
  const res = await fetchEventDetail(id);
  event.value = res;
};

// 返回列表
const goBack = () => {
  router.push("/events");
};

// 格式化日期
const formatDate = (dt) => {
  if (!dt) return "";
  return dt.replace("T", " ").slice(0, 16);
};

// 活動分類文字
const categoryText = computed(() => {
  if (!event.value) return "";
  return event.value.category === "announcement" ? "公告" : "優惠活動";
});

// 判斷是否已結束
const isExpired = (end) => {
  return new Date(end) < new Date();
};

// 倒數天數
const remainingDays = computed(() => {
  if (!event.value || !event.value.endDate) return 0;
  const end = new Date(event.value.endDate);
  const now = new Date();
  const diff = end - now;
  if (diff <= 0) return 0;
  return Math.ceil(diff / (1000 * 60 * 60 * 24));
});

onMounted(loadDetail);
</script>

<style scoped>
.detail-page {
  padding: 40px 80px;
  max-width: 1100px;
  margin: auto;
}

.back-btn {
  background: none;
  border: none;
  font-size: 17px;
  color: #244060;
  cursor: pointer;
  margin-bottom: 10px;
}

.title {
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 10px;
}

.status-bar {
  margin-bottom: 20px;
}

.countdown {
  font-size: 16px;
  color: #d9534f;
  font-weight: bold;
}

.expired-badge {
  background: #666;
  color: white;
  padding: 5px 10px;
  border-radius: 6px;
  font-size: 14px;
}

.content {
  display: flex;
  gap: 30px;
}

.image-box {
  width: 320px;
  height: 220px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 3px 10px rgba(0,0,0,0.1);
}

.image-box img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.info-box {
  flex: 1;
  font-size: 17px;
  line-height: 1.7;
}

.section {
  margin-top: 15px;
}

.row {
  margin-bottom: 12px;
}

.coupon {
  background: #244060;
  color: white;
  padding: 4px 10px;
  border-radius: 5px;
}

.view-count {
  position: fixed;
  right: 25px;
  bottom: 25px;
  background: rgba(0,0,0,0.75);
  color: white;
  padding: 8px 16px;
  border-radius: 8px;
  font-size: 14px;
}
</style>
