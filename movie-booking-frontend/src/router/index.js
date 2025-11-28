import { createRouter, createWebHistory } from 'vue-router';

import HomeView from '../views/HomeView.vue';
import MoviesView from '../views/MoviesView.vue';
import UpcomingMoviesView from '../views/UpcomingMoviesView.vue';
import MovieDetailView from '../views/MovieDetailView.vue';
import SeatSelectionView from '../views/SeatSelectionView.vue';
import CheckoutView from '../views/CheckoutView.vue';
import BookingResultView from '../views/BookingResultView.vue';
import QuickBookingView from '../views/QuickBookingView.vue';
import TicketBookingView from '../views/TicketBookingView .vue';
import TicketSelectionView from '../views/TicketSelectionView.vue';
import CustomerServiceTicket from "../views/CustomerServiceTicket.vue";
import ConfirmOrderView from "../views/ConfirmOrderView.vue";
import PaymentView from "../views/PaymentView.vue";
import PaymentSuccess from "../views/PaymentSuccess.vue";
import RefundView from "../views/RefundView.vue";
import RefundSuccess from "../views/RefundSuccess.vue";
import Payment from "../views/Payment.vue";

const routes = [
  {
    path: '/',
    name: 'HomeView',
    component: HomeView,
  },
  {
    path: '/movies',
    name: 'MoviesView',
    component: MoviesView,
  },
  {
    path: '/upcomingmovies',
    name: 'UpcomingMovies',
    component: UpcomingMoviesView,
  },
  {
    path: '/movies/:id',
    name: 'movie-detail',
    component: MovieDetailView,
    props: true,
  },
  {
    path: '/booking/:showtimeId/seats',
    name: 'seat-selection',
    component: SeatSelectionView,
    props: true,
  },
  {
    path: '/booking/checkout',
    name: 'checkout',
    component: CheckoutView,
  },
  {
    path: '/booking/result',
    name: 'booking-result',
    component: BookingResultView,
  },
  {
    path: '/booking/QuickBooking',
    name: 'QuickBooking',
    component: QuickBookingView,
  },
  {
    path: '/booking/TicketBooking/:id',
    name: 'TicketBooking',
    component: TicketBookingView,
  },
  {
    path: '/booking/ticket-selection',
    name: 'TicketSelection',
    component: TicketSelectionView,
  },
  {
    path: "/issues",
    name: "CustomerServiceTicket",
    component: CustomerServiceTicket,
  },
  {
    path: "/booking/confirm-order",
    name: "ConfirmOrderView",
    component: ConfirmOrderView,
    //  meta: { requiresAuth: true }, // 若需要登入才可下單
  },
  {
    path: "/payment/:id",
    name: "PaymentView",
    component: PaymentView,
  },

  {
    path: "/payment/success/:id",
    name: "PaymentSuccess",
    component: PaymentSuccess,
  },
  {
    path: "/refund/:orderId",
    name: "Refund",
    component: RefundView
  },
  {
    path: "/refund-success",
    name: "RefundSuccess",
    component: RefundSuccess
  },
  {
    path: "/checkout/:orderId",
    name: "ConfirmOrder",
    component: () => import("../views/ConfirmOrderView.vue")
  }


];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;