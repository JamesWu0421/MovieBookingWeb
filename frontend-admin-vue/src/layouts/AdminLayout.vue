<template>
  <el-container style="height: 100vh">
    <el-aside width="240px" class="sidebar">
      <div class="logo">🎬 電影訂票管理</div>
      <el-menu
        router
        :default-active="$route.path"
        background-color="#2b3a4b"
        text-color="#fff"
        class="menu"
      >
        <el-menu-item index="/dashboard">儀表板</el-menu-item>
        <el-menu-item index="/employees">員工管理</el-menu-item>
        <el-menu-item index="/members">會員管理</el-menu-item>
        <el-menu-item index="/movies">電影/影廳管理</el-menu-item>
         <el-sub-menu index="/activities">
        <template #title>票種管理</template>
        <el-menu-item index="/ticket-package">票種管理</el-menu-item>
        <el-menu-item index="/showtimes-price">場次票種管理</el-menu-item>
        </el-sub-menu>
        <el-menu-item index="/orders">訂單管理</el-menu-item>
        <el-sub-menu index="/activities">
          <template #title>活動管理</template>
          <el-menu-item index="/promotions">優惠活動</el-menu-item>
          <el-menu-item index="/announcements">公告活動</el-menu-item>
        </el-sub-menu>
        <el-menu-item index="/notifications">通知管理</el-menu-item>
        <el-menu-item index="/reports">營收報表</el-menu-item>
        <el-menu-item index="/security">安全日誌</el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header">
        <div class="header-left">電影訂票系統管理後台</div>
        <div class="header-right">
          <el-button type="text" @click="logout">登出</el-button>
        </div>
      </el-header>
      <el-main style="padding: 20px">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useRouter } from "vue-router";
const router = useRouter();
function logout() {
  localStorage.removeItem("admin_token");
  router.push("/login");
}
</script>

<style scoped>
.sidebar {
  background-color: #2b3a4b;
  color: white;
  padding-top: 12px;
}
.logo {
  text-align: center;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  padding: 16px 0;
}
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #f6f7fb;
  font-weight: bold;
  padding: 12px 20px;
  border-bottom: 1px solid #e6e9ee;
}
.menu >>> .el-menu-item,
.menu >>> .el-sub-menu__title {
  color: #fff;
}
</style>
