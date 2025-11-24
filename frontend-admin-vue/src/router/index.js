import { createRouter, createWebHistory } from "vue-router";
import AdminLayout from "../layouts/AdminLayout.vue";
import Dashboard from "../pages/Dashboard.vue";

const routes = [
  {
    path: "/",
    component: AdminLayout,
    redirect: "/dashboard",
    children: [
      { path: "/dashboard", component: Dashboard },
      { path: "/members", component: () => import("../pages/Members.vue") },
      { path: "/employees", component: () => import("../pages/Employees.vue") },
      { path: "/movies", component: () => import("../pages/Movies.vue") },
      { path: "/showtimes", component: () => import("../pages/Showtimes.vue") },
      { path: "/orders", component: () => import("../pages/Orders.vue") },
      {
        path: "/promotions",
        component: () => import("../pages/Promotions.vue"),
      },
      {
        path: "/announcements",
        component: () => import("../pages/Announcements.vue"),
      },
      {
        path: "/notifications",
        component: () => import("../pages/Notifications.vue"),
      },
      { path: "/reports", component: () => import("../pages/Reports.vue") },
      {
        path: "/security",
        component: () => import("../pages/SecurityLogs.vue"),
      },
    ],
  },
  {
    path: "/login",
    component: () => import("../pages/Login.vue"),
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

// 全域前置守衛 - 所有路由都檢查 token
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem("adminToken");

  // 如果沒有 token 且不是要去登入頁
  if (!token && to.path !== "/login") {
    // 導向登入頁
    next("/login");
  }
  // 如果已有 token 且要去登入頁,導向首頁
  else if (token && to.path === "/login") {
    next("/dashboard");
  }
  // 其他情況正常放行
  else {
    next();
  }
});

export default router;
