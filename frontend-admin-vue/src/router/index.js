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
      {
        path: "/movies",
        name: 'movies',
        component: () => import("../pages/Movies.vue")
      },
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
      {
        path: '/movies/new',
        name: 'movie-new',
        component: () => import("../pages/MovieFormView.vue"),
      },
      {
        path: '/movies/:id/edit',
        name: 'movie-edit',
        component: () => import("../pages/MovieFormView.vue"),
        props: true,
      },
      {
        path: '/screens',
        name: 'admin-screens',
        component: () => import("../pages/Screens.vue"),
      },
      {
        path: '/screens/new',
        component: () => import("../pages/ScreenFormView.vue"),
      },
      {
        path: '/screens/:id/edit',
        component: () => import("../pages/ScreenFormView.vue"),
      },
      {
        path: '/shows',
        name: 'admin-shows',
        component: () => import("../pages/Shows.vue"),
      },
      {
        path: '/shows/new',
        component: () => import("../pages/ShowFormView.vue"),
      },
      {
        path: '/shows/:id/edit',
        component: () => import("../pages/ShowFormView.vue"),
      },
      {
        path: '/seats/batch',
        component: () => import("../pages/SeatBatchView.vue"),
      },
      {
        path: '/screens/:screenId/seats',
        component: () => import("../pages/Seats.vue"),
      },
      {
        path: '/screens/:screenId/seat-map',
        component: () => import("../pages/SeatMapView.vue"),
      },

      {
        path: "/batch-operations",
        name: "BatchOperationList",
        component: () => import("../pages/BatchOperationList.vue"),
      },
      {
        path: "/batch-operations/:batchId/sessions",
        name: "BatchSessionTemp",
        component: () => import("../pages/BatchSessionTemp.vue"),
      },
      {
        path: "/batch-operations/:batchId/tickets",
        name: "BatchTicketTemp",
        component: () => import("../pages/BatchTicketTemp.vue"),
      },
      {
        path: "/issues",
        component: () => import("../pages/AdminTickets.vue"),
      },
      {
        path: "/tickets/:id",
        component: () => import("../pages/FormDetail.vue")
      },
      //訂單id
      {
        path: "/orders/:id",
        component: () => import("../pages/OrderDetail.vue")
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
  const token = localStorage.getItem("admin_token");

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
