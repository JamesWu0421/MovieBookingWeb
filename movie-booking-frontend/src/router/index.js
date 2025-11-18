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

const routes = [
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
    path: "/login",
    name: "Login",
    component: Login,
  },
  {
    path: "/forgot-password",
    name: "ForgotPasswordView",
    component: ForgotPasswordView,
  },
  {
    path: "/profile",
    name: "ProfileView",
    component: ProfileView,
  },
  {
    path: "/register",
    name: "Register",
    component: Register,
  },
  {
    path: "/change-password",
    name: "ChangePassword",
    component: ChangePassword,
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
  {
    path: "/movies/:id",
    name: "movie-detail",
    component: MovieDetailView,
    props: true,
  },
  {
    path: "/booking/:showtimeId/seats",
    name: "seat-selection",
    component: SeatSelectionView,
    props: true,
  },
  {
    path: "/booking/checkout",
    name: "checkout",
    component: CheckoutView,
  },
  {
    path: "/booking/result",
    name: "booking-result",
    component: BookingResultView,
  },
  {
    path: "/booking/QuickBooking",
    name: "QuickBooking",
    component: QuickBookingView,
  },
  {
    path: "/booking/TicketBooking/:id",
    name: "TicketBooking",
    component: TicketBookingView,
  },
  {
    path: "/booking/ticket-selection",
    name: "TicketSelection",
    component: TicketSelectionView,
  },
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
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

//其他功能開發完後再補充
// 路由守衛
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore();
  const isAuthenticated = authStore.isAuthenticated;

  // 需要登入的頁面
  if (to.meta.requiresAuth && !isAuthenticated) {
    next("/login");
  }
  // 已登入不能訪問的頁面(如登入、註冊)
  else if (to.meta.requiresGuest && isAuthenticated) {
    next("/profile");
  } else {
    next();
  }
});

export default router;
