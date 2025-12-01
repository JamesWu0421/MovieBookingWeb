<template>
  <el-container style="height: 100vh">
    <el-aside width="200px" class="sidebar">
      <div class="logo" @click="$router.push('/')">
        <img class="icon" src="../assets/images/theater6.png" alt="" />
      </div>

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
        <el-sub-menu index="/activities">
        <template #title>電影/影廳管理</template>
          <el-menu-item index="/movies">電影管理</el-menu-item>
          <el-menu-item index="/screens">影廳管理</el-menu-item>
          <el-menu-item index="/shows">場次管理</el-menu-item>
          </el-sub-menu>
        <el-sub-menu index="/activities">
        <template #title>票種管理</template>
        <el-menu-item index="/ticket-package">票種管理</el-menu-item>
        <el-menu-item index="/showtimes-price">場次票種管理</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="/batch">
          <template #title>批次管理</template>
          <el-menu-item index="/batch-operations">批次列表</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="/orders">
          <template #title>訂單管理</template>
          <el-menu-item index="/orders">查看訂單</el-menu-item>
          <el-menu-item index="/issues">查看問題回報</el-menu-item>
        </el-sub-menu>
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
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useRouter } from "vue-router";
import { ElMessageBox, ElMessage } from "element-plus";

const router = useRouter();

async function logout() {
  try {
    await ElMessageBox.confirm("確認要登出嗎？", "提示", {
      type: "warning",
    });

    // ✅ 改成 admin_token (與其他地方統一)
    localStorage.removeItem("admin_token");
    localStorage.removeItem("admin_name");    // 可選
    localStorage.removeItem("admin_email");   // 可選

    ElMessage.success("已登出");
    router.push("/login");
  } catch {
    // 取消登出
  }
}
</script>

<style scoped>
/* Sidebar 主體 */
.sidebar {
  background-color: #2b3a4b;
  color: white;
  padding-top: 0;
}

/* Logo 區塊：靠左對齊並與 menu 一致 */
.logo {
  display: flex;
  align-items: center;
  height: 80px;
  padding-left: 35px; /* 👈 與 menu 的 padding 一樣 */
  cursor: pointer;
}

/* Logo 圖片大小 */
.icon {
  width: 80px;
  height: 45px;
}

/* Menu 文字：向右縮，使其與 Logo 左側對齊 */
.menu >>> .el-menu-item,
.menu >>> .el-sub-menu__title {
  padding-left: 35px !important; /* 👈 與 Logo 相同 */
  color: #fff;
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
