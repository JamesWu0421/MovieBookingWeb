import { defineStore } from "pinia";
import { ref } from "vue";
import * as authApi from "../services/movieApi";

export const useAuthStore = defineStore("login", () => {
  const user = ref(null);
  const token = ref(localStorage.getItem("token") || null);
  const isAuthenticated = ref(!!token.value);

  const setAuth = (authToken, userData) => {
    token.value = authToken;
    user.value = userData;
    isAuthenticated.value = true;
    localStorage.setItem("token", authToken);
    if (userData) {
      localStorage.setItem("user", JSON.stringify(userData));
    }
  };

  const clearAuth = () => {
    token.value = null;
    user.value = null;
    isAuthenticated.value = false;
    localStorage.removeItem("token");
    localStorage.removeItem("user");
  };

  const login = async (credentials) => {
    const response = await authApi.login(credentials);
    setAuth(response.token, { username: credentials.username });

    try {
      const profile = await authApi.getProfile();
      user.value = profile;
      localStorage.setItem("user", JSON.stringify(profile));
    } catch (error) {
      console.error("取得個人資料失敗:", error);
    }

    return response;
  };

  const logout = async () => {
    try {
      await authApi.logout();
    } finally {
      clearAuth();
    }
  };

  return {
    user,
    token,
    isAuthenticated,
    setAuth,
    clearAuth,
    login,
    logout,
  };
});
export default { useAuthStore };
