import axios from "axios";
// import request from "../utils/request";

const BASE_URL = "http://localhost:8080";

// 認證/登入
const authClient = axios.create({ baseURL: BASE_URL });

// 後台受保護 API
const adminApi = axios.create({ baseURL: BASE_URL });

adminApi.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("adminToken");
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// 後台登入
export async function adminLogin(email, password) {
  const res = await authClient.post("/api/admin/auth/login", {
    email,
    password,
  });
  return res.data.token;
}

// 取得全部員工
export async function fetchEmployees(params) {
  const res = await adminApi.get("/api/admin/employees", { params });
  return res.data;
}

// 用關鍵字搜尋員工
export async function searchEmployees({ keyword, page, size }) {
  return adminApi
    .get("/api/admin/employees/search", {
      params: { keyword, page, size },
    })
    .then((res) => res.data);
}

// 新增員工
export async function createEmployee(emp, roleId) {
  const res = await adminApi.post("/api/admin/employees", emp, {
    params: { roleId },
  });
  return res.data;
}

// 更新員工
export async function updateEmployee(id, emp, roleId) {
  const res = await adminApi.put(`/api/admin/employees/${id}`, emp, {
    params: { roleId },
  });
  return res.data;
}

// 刪除員工
export async function deleteEmployee(id) {
  await adminApi.delete(`/api/admin/employees/${id}`);
}

//================================================================
//會員管理 API
// 1. 取得會員列表（支援分頁）
export function fetchUsers(params) {
  return adminApi.get("/api/admin/users", { params }).then((res) => res.data);
}

export function searchUsers(params) {
  return adminApi
    .get("/api/admin/users/search", { params })
    .then((res) => res.data);
}

export function updateUser(id, payload) {
  return adminApi
    .put(`/api/admin/users/${id}`, payload)
    .then((res) => res.data);
}

export function updateUserStatus(id, status) {
  return adminApi
    .put(`/api/admin/users/${id}/status`, null, { params: { status } })
    .then((res) => res.data);
}
