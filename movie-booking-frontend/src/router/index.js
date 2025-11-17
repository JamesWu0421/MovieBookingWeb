import { createRouter, createWebHistory } from "vue-router";

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
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
