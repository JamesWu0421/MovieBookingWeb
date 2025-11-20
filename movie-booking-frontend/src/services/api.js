import request from "../utils/request";

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

// Google OAuth 登入（備用）
// export const oauth2Login = (token) => {
//   return request({
//     url: "/api/oauth2/login",
//     method: "post",
//     data: { token },
//   });
// };

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
