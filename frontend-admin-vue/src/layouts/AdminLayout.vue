<template>
  <el-container style="height: 100vh">
    <el-aside width="200px" class="sidebar">
    <div class="logo" @click="$router.push('/')">
      <img class="icon" src="@/assets/images/theater6.png" alt="" />
    </div>
    
      <el-menu router :default-active="$route.path" background-color="#2b3a4b" text-color="#fff" class="menu">
        <el-menu-item index="/dashboard">儀表板</el-menu-item>
        <el-menu-item index="/members">會員管理</el-menu-item>
        <el-menu-item index="/movies">電影/影廳管理</el-menu-item>
        <el-menu-item index="/showtimes">場次/票價管理</el-menu-item>
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
      <el-main style="padding:20px;">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
<<<<<<< HEAD
import { useRouter } from "vue-router";
import { ElMessageBox, ElMessage } from "element-plus";

const router = useRouter();

async function logout() {
  try {
    await ElMessageBox.confirm("確認要登出嗎？", "提示", {
      type: "warning",
    });

    // 清除 token (改成 adminToken)
    localStorage.removeItem("adminToken");

    ElMessage.success("已登出");
    router.push("/login");
  } catch {
    // 取消登出
  }
=======
import { useRouter } from 'vue-router'
const router = useRouter()
function logout() {
  localStorage.removeItem('admin_token')
  router.push('/login')
>>>>>>> 2eeb41fca6023512bb0be263c6f19e7e5ff7905e
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
  padding-left: 35px;   /* 👈 與 menu 的 padding 一樣 */
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
  padding-left: 35px !important;   /* 👈 與 Logo 相同 */
  color: #fff;
}


.header {
  display:flex;
  justify-content:space-between;
  align-items:center;
  background:#f6f7fb;
  font-weight: bold;
  padding: 12px 20px;
  border-bottom:1px solid #e6e9ee;
}
<<<<<<< HEAD

.main-content {
  padding: 20px;
  width: 100%;
  box-sizing: border-box;
  background: #f6f7fb;
}

.menu :deep(.el-menu-item),
.menu :deep(.el-sub-menu__title) {
=======
.menu >>> .el-menu-item, .menu >>> .el-sub-menu__title {
>>>>>>> 2eeb41fca6023512bb0be263c6f19e7e5ff7905e
  color: #fff;
}




</style>
