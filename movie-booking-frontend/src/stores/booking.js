import { defineStore } from 'pinia';

export const useBookingStore = defineStore('booking', {
  state: () => ({
    selectedMovie: null,
    selectedShowtime: null,
    selectedSeats: [], // [{ rowLabel: 'A', seatNumber: 10, price: 300 }]
    totalPrice: 0,
    contactInfo: {
      name: '',
      phone: '',
      email: '',
    },
    orderId: null,
  }),
  getters: {
    seatCount(state) {
      return state.selectedSeats.length;
    },
  },
  actions: {
    setMovie(movie) {
      this.selectedMovie = movie;
    },
    setShowtime(showtime) {
      this.selectedShowtime = showtime;
    },
    toggleSeat(seat) {
      const key = `${seat.rowLabel}-${seat.seatNumber}`;
      const index = this.selectedSeats.findIndex(
        (s) => `${s.rowLabel}-${s.seatNumber}` === key,
      );
      if (index >= 0) {
        this.selectedSeats.splice(index, 1);
      } else {
        this.selectedSeats.push(seat);
      }
      this.computeTotalPrice();
    },
    clearSeats() {
      this.selectedSeats = [];
      this.totalPrice = 0;
    },
    computeTotalPrice() {
      this.totalPrice = this.selectedSeats.reduce(
        (sum, seat) => sum + (seat.price || 0),
        0,
      );
    },
    setContactInfo(info) {
      this.contactInfo = { ...this.contactInfo, ...info };
    },
    setOrderId(orderId) {
      this.orderId = orderId;
    },
    resetAll() {
      this.selectedMovie = null;
      this.selectedShowtime = null;
      this.selectedSeats = [];
      this.totalPrice = 0;
      this.contactInfo = {
        name: '',
        phone: '',
        email: '',
      };
      this.orderId = null;
    },
  },
});
