// stores/show.js
import { defineStore } from "pinia";
import axios from "axios";

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api",
  timeout: 5000,
});

export const useShowStore = defineStore("show", {
  state: () => ({
    shows: [], // 所有場次資料
    screens: {}, // screen_id -> screen 物件的對應表
    loading: false,
    error: null,
  }),

  actions: {
    /**
     * 取得場次資料（會自動載入 screens 資料）
     */
    async fetchShowtimes({ movieId, date }) {
      this.loading = true;
      this.error = null;

      try {
        // 同時取得 shows 和 screens
        const [showsResponse, screensResponse] = await Promise.all([
          api.get("/shows"),
          api.get("/screens"),
        ]);

        this.shows = showsResponse.data;

        // 建立 screenId -> screen 的對應表
        this.screens = {};
        screensResponse.data.forEach((screen) => {
          this.screens[screen.id] = screen;
        });

        console.log("場次資料已載入:", this.shows.length);
        console.log("影廳資料已載入:", Object.keys(this.screens).length);
      } catch (err) {
        this.error = err.message || "載入場次失敗";
        console.error("Error fetching showtimes:", err);
        throw err;
      } finally {
        this.loading = false;
      }
    },

    /**
     * 清空資料
     */
    clearShowtimes() {
      this.shows = [];
      this.screens = {};
      this.error = null;
    },
  },

  getters: {
    /**
     * 取得符合條件的場次列表
     * 注意：已移除 cinemaId 參數，因為只有固定台北館
     */
    getShowtimes:
      (state) =>
      ({ movieId, date, hallType = "所有" }) => {
        console.log("篩選條件:", { movieId, date, hallType });

        // 篩選符合條件的場次
        let filtered = state.shows.filter((show) => {
          // 篩選電影（支援兩種命名方式）
          const showMovieId = show.movieId || show.movie_id;
          const matchMovie = !movieId || showMovieId == movieId;

          // 篩選日期（支援兩種命名方式）
          const showDate = show.showDate || show.show_date;
          const matchDate = !date || showDate === date;

          // 取得影廳資訊
          const screenId = show.screenId || show.screen_id;
          const screen = state.screens[screenId];

          if (!screen) {
            console.warn(`找不到 screenId=${screenId} 的影廳資訊`);
            return false;
          }

          // 取得影廳類型（支援兩種命名方式）
          const screenType = screen.screenType || screen.screen_type;

          // 篩選影廳類型
          const matchHallType = hallType === "所有" || screenType === hallType;

          const matched = matchMovie && matchDate && matchHallType;

          if (!matched) {
            console.log("不符合條件:", {
              showMovieId,
              matchMovie,
              showDate,
              matchDate,
              screenType,
              matchHallType,
            });
          }

          return matched;
        });

        console.log(`共篩選出 ${filtered.length} 個場次`);

        // 格式化資料
        return filtered.map((show) => {
          const screenId = show.screenId || show.screen_id;
          const screen = state.screens[screenId];
          const screenType =
            screen?.screenType || screen?.screen_type || "NORMAL_2D";
          const screenName = screen?.name || `${screenId}廳`;
          const showDate = show.showDate || show.show_date;
          const showTime = show.showTime || show.show_time;
          const endTime = show.endTime || show.end_time;
          const showMovieId = show.movieId || show.movie_id;

          return {
            id: show.id,
            movieId: showMovieId,
            screenId: screenId,
            date: showDate,
            startTime: showTime,
            endTime: endTime,
            hall: screenName,
            hallType: screenType,
            ticketStatus: null,
            price: screen?.price || 0,
          };
        });
      },

    /**
     * 取得可用的影廳類型
     * 注意：已移除 cinemaId 參數
     */
    getHallTypes: (state) => (movieId, date) => {
      // 篩選符合條件的場次
      const showtimes = state.shows.filter((show) => {
        const showMovieId = show.movieId || show.movie_id;
        const matchMovie = !movieId || showMovieId == movieId;

        const showDate = show.showDate || show.show_date;
        const matchDate = !date || showDate === date;

        return matchMovie && matchDate;
      });

      console.log(`該電影在該日期有 ${showtimes.length} 個場次`);

      // 收集所有不重複的 screen_type
      const types = new Set(["所有"]);

      showtimes.forEach((show) => {
        const screenId = show.screenId || show.screen_id;
        const screen = state.screens[screenId];
        const screenType = screen?.screenType || screen?.screen_type;

        if (screenType) {
          types.add(screenType);
          console.log(`找到影廳類型: ${screenType}`);
        }
      });

      const result = Array.from(types);
      console.log("可用的影廳類型:", result);

      return result;
    },

    /**
     * 根據場次 ID 取得場次詳細資訊
     */
    getShowById: (state) => (showId) => {
      const show = state.shows.find((s) => s.id == showId);
      if (!show) return null;

      const screenId = show.screenId || show.screen_id;
      const screen = state.screens[screenId];

      return {
        ...show,
        screen: screen,
        hallType: screen?.screenType || screen?.screen_type || "NORMAL_2D",
      };
    },
  },
});
