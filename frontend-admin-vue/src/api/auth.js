import axios from "axios";
import request from "../utils/request";

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
export async function fetchEmployees() {
  const res = await adminApi.get("/api/admin/employees");
  return res.data;
}

// 用關鍵字搜尋員工
export async function searchEmployees(keyword) {
  const res = await adminApi.get("/api/admin/employees/search", {
    params: { keyword },
  });
  return res.data;
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
// params: { page, size }
export function fetchUsers(params) {
  // 後端: GET /api/admin/users?page=1&size=10
  return request.get("/api/admin/users", { params }).then((res) => res.data);
}

// 2. 會員搜尋（username / email / phoneNumber + 分頁）
// params: { keyword, page, size }
export function searchUsers(params) {
  // 後端: GET /api/admin/users/search?keyword=xxx&page=1&size=10
  return request
    .get("/api/admin/users/search", { params })
    .then((res) => res.data);
}

// 3. 更新會員基本資料（後台編輯用，不改密碼）
export function updateUser(id, payload) {
  // 後端: PUT /api/admin/users/{id}
  // payload 對應 AdminUserUpdateRequest:
  // { email, phoneNumber, nickname, gender, birthday, avatarUrl, status }
  return request.put(`/api/admin/users/${id}`, payload).then((res) => res.data);
}

// 4. 更新會員狀態（啟用 / 停權）
export function updateUserStatus(id, status) {
  // 後端: PUT /api/admin/users/{id}/status?status=1
  return request
    .put(`/api/admin/users/${id}/status`, null, {
      params: { status },
    })
    .then((res) => res.data);
}
