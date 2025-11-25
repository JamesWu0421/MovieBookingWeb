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
      { path: "/movies",
        name: 'movies',
        component: () => import("../pages/Movies.vue") },
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
    ],
  },
];

export default createRouter({
  history: createWebHistory(),
  routes,
});
