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
      { path: "/orders", component: () => import("../pages/Orders.vue") },
      // 票種與場次管理
      {
        path: "/ticket-package",
        component: () => import("../pages/TicketPackage.vue"),
      },
      {
        path: "/showtimes-price",
        component: () => import("../pages/ShowtimesPrice.vue"),
      },
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
];

export default createRouter({
  history: createWebHistory(),
  routes,
});
