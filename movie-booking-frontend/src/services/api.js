import request from "../utils/request";

// =============================
// 🔥 前台活動 API（新增，不覆蓋舊的）
// =============================

// 活動列表
export const fetchHomeEvents = () => {
  return request({
    url: "/public/events/home",  // ✅ 加上 /public/
    method: "get",
  });
};

// 活動詳
export const fetchEventDetail = (id) => {
  return request({
    url: `/public/events/${id}`,  // ✅ 加上 /public/
    method: "get",
  });
};

// =============================
// 🧑‍💼 你原本的所有 API（完整保留）
// =============================

// 使用者註冊
export const register = (data) => {
  return request({
    url: "/auth/register",
    method: "post",
    data,
  });
};

// 使用者登入
export const login = (data) => {
  return request({
    url: "/auth/login",
    method: "post",
    data,
  });
};

// 變更密碼
export const changePassword = async (oldPassword, newPassword) => {
  const response = await request({
    url: "/user/change_password",
    method: "put",
    data: {
      oldPassword,
      newPassword,
    },
  });
  return response.data;
};

// 登出
export const logout = () => {
  return request({
    url: "/user/logout",
    method: "post",
  });
};

// Email 驗證
export const verifyEmail = (code) => {
  return request({
    url: `/auth/verify?token=${code}`,
    method: "get",
  });
};

// 取得個人資料
export const getProfile = () => {
  return request({
    url: "/user/profile",
    method: "get",
  });
};

// 上傳頭像
export const uploadAvatar = async (file) => {
  const formData = new FormData();
  formData.append("file", file);

  const response = await request({
    url: "/upload/avatar",
    method: "post",
    data: formData,
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });

  return response.data;
};

// 重設密碼
export const resetPassword = (token, newPassword) => {
  return request({
    url: "/user/reset-password",
    method: "post",
    data: {
      token,
      newPassword,
    },
  });
};

// 更新個人資料
export const updateProfile = (data) => {
  return request({
    url: "/user/profile",
    method: "put",
    data,
  });
};

// 你原本就有的活動查詢（保留，不動）
export const fetchAllEvents = (params) => {
  return request({
    url: "/events",
    method: "get",
    params // 分頁、分類、搜尋
  });
};

export const fetchEventById = (id) => {
  return request({
    url: `/events/${id}`,
    method: "get",
  });
};

// =============================
// default export（保留）
// =============================
export default {
  register,
  login,
  changePassword,
  logout,
  resetPassword,
  verifyEmail,
  getProfile,
  uploadAvatar,
  updateProfile,
};
