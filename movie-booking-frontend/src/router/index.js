import { createRouter, createWebHistory } from "vue-router";
import { useAuthStore } from "../stores/login";

import HomeView from "../views/HomeView.vue";
import MoviesView from "../views/MoviesView.vue";
import MovieDetailView from "../views/MovieDetailView.vue";
import SeatSelectionView from "../views/SeatSelectionView.vue";
import CheckoutView from "../views/CheckoutView.vue";
import BookingResultView from "../views/BookingResultView.vue";
import QuickBookingView from "../views/QuickBookingView.vue";
import TicketBookingView from "../views/TicketBookingView .vue";
import TicketSelectionView from "../views/TicketSelectionView.vue";

import Login from "../views/Login/Login.vue";
import Register from "../views/Login/Register.vue";
import ForgotPasswordView from "../views/Login/ForgotPasswordView.vue";
import ProfileView from "../views/Profile/Profile.vue";
import ChangePassword from "../views/Profile/ChangePassword.vue";
import VerifySuccess from "../views/Login/VerifySuccess.vue";
import VerifyFailed from "../views/Login/VerifyFailed.vue";

import TicketView from "../components/qrcode/TicketView.vue";
import VerifyView from "../components/qrcode/VerifyView.vue";

const routes = [

  // ===== 公開頁面 =====
  {
    path: "/",
    name: "HomeView",
    component: HomeView,
  },
  {
    path: "/movies",
    name: "MoviesView",
    component: MoviesView,
  },
  {
    path: "/movies/:id",
    name: "movie-detail",
    component: MovieDetailView,
    props: true,
  },

  // ===== 登入/註冊相關 (已登入不能訪問) =====
  {
    path: "/login",
    name: "Login",
    component: Login,
    meta: { requiresGuest: true },
  },
  {
    path: "/register",
    name: "Register",
    component: Register,
    meta: { requiresGuest: true },
  },
  {
    path: "/forgot-password",
    name: "ForgotPasswordView",
    component: ForgotPasswordView,
    meta: { requiresGuest: true },
  },
  {
    path: "/verify-success",
    name: "VerifySuccess",
    component: VerifySuccess,
  },
  {
    path: "/verify-failed",
    name: "VerifyFailed",
    component: VerifyFailed,
  },

  // ===== 需要登入的頁面 =====
  {
    path: "/profile",
    name: "ProfileView",
    component: ProfileView,
    meta: { requiresAuth: true },
  },
  {
    path: "/change-password",
    name: "ChangePassword",
    component: ChangePassword,
    meta: { requiresAuth: true },
  },

  // ===== 訂票流程 (需要登入) =====
  {
    path: "/booking/:showtimeId/seats",
    name: "seat-selection",
    component: SeatSelectionView,
    props: true,
    meta: { requiresAuth: true },
  },
  {
    path: "/booking/checkout",
    name: "checkout",
    component: CheckoutView,
    meta: { requiresAuth: true },
  },
  {
    path: "/booking/result",
    name: "booking-result",
    component: BookingResultView,
    meta: { requiresAuth: true },
  },
  {
    path: "/booking/QuickBooking",
    name: "QuickBooking",
    component: QuickBookingView,
    meta: { requiresAuth: true },
  },
  {
    path: "/booking/TicketBooking/:id",
    name: "TicketBooking",
    component: TicketBookingView,
    meta: { requiresAuth: true },
  },
  {
    path: "/booking/ticket-selection",
    name: "TicketSelection",
    component: TicketSelectionView,
    meta: { requiresAuth: true },
  },

  // ⭐ 前台建立 + 查看票券（直接在 TicketView.vue 建立）
  { path: "/ticket", name: "Ticket", component: TicketView },

  // ⭐ 驗票端（需登入）
  { path: "/verify", name: "Verify", component: VerifyView, meta: { requiresAuth: true } },

  // ⭐ 掃 QR 後（帶 code）→ 驗票頁
  { path: "/verify/:code", name: "VerifyCode", component: VerifyView, meta: { requiresAuth: true } },

  // ⭐（保留你的原始 verify 註解，不執行）
  // {
  //   path: "/auth/verify",
  //   name: "Verify",
  //   beforeEnter: async (to, from, next) => {
  //     const code = to.query.code; // 改為 code (對應後端的 @RequestParam("code"))
  //     if (!code) {
  //       next("/verify-failed");
  //       return;
  //     }
  //     try {
  //       const authApi = await import("../services/api");
  //       await authApi.verifyEmail(code);
  //       next("/verify-success");
  //     } catch (error) {
  //       next("/verify-failed");
  //     }
  //   },
  // },

  // ⭐ 統一處理 redirect —— 避免覆蓋 "/"
  { path: "/ticket-home", redirect: "/ticket" },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

// ===== 路由守衛 =====
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore();
  const isAuthenticated = authStore.isAuthenticated;

  // 需要登入的頁面
  if (to.meta.requiresAuth && !isAuthenticated) {
    next("/login");
  }
  // 已登入不能訪問的頁面
  else if (to.meta.requiresGuest && isAuthenticated) {
    next("/");
  } else {
    next();
  }
});

export default router;
