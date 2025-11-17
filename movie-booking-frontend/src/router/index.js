import { createRouter, createWebHistory } from 'vue-router';

import HomeView from '../views/HomeView.vue';
import MoviesView from '../views/MoviesView.vue';
import MovieDetailView from '../views/MovieDetailView.vue';
import SeatSelectionView from '../views/SeatSelectionView.vue';
import CheckoutView from '../views/CheckoutView.vue';
import BookingResultView from '../views/BookingResultView.vue';
import QuickBookingView from '../views/QuickBookingView.vue';
import TicketBookingView from '../views/TicketBookingView .vue';
import TicketSelectionView from '../views/TicketSelectionView.vue';

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
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;