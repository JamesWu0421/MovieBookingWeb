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
      // 活動管理
      {
        path: "/promotions",
        component: () => import("../pages/Promotions.vue"),
      },
      {
        path: "/announcements",
        component: () => import("../pages/Announcements.vue"),
      },
      // 通知管理
      {
        path: "/notifications",
        component: () => import("../pages/Notifications.vue"),
      },
      // 報表和安全
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

export default createRouter({
  history: createWebHistory(),
  routes,
});
