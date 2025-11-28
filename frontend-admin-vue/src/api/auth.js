// api/auth.js
import request from "../utils/request";
import axios from "axios";

const BASE_URL = "http://localhost:8080";

// 登入不需要 token，所以單獨建立
const authClient = axios.create({ baseURL: BASE_URL });

// ===== 認證 API =====

export async function adminLogin(email, password) {
  const res = await authClient.post("/api/admin/auth/login", {
    email,
    password,
  });
  return res.data;
}

// ===== 員工管理 API (改用 request) =====

export async function fetchEmployees(params) {
  const res = await request.get("/api/admin/employees", { params });
  return res.data;
}

export async function searchEmployees({ keyword, page, size }) {
  return request
    .get("/api/admin/employees/search", {
      params: { keyword, page, size },
    })
    .then((res) => res.data);
}

export async function createEmployee(emp, roleId) {
  const res = await request.post("/api/admin/employees", emp, {
    params: { roleId },
  });
  return res.data;
}

export async function updateEmployee(id, emp, roleId) {
  const res = await request.put(`/api/admin/employees/${id}`, emp, {
    params: { roleId },
  });
  return res.data;
}

export async function deleteEmployee(id) {
  await request.delete(`/api/admin/employees/${id}`);
}

// ===== 會員管理 API =====

export function fetchUsers(params) {
  return request.get("/api/admin/users", { params }).then((res) => res.data);
}

export function searchUsers(params) {
  return request
    .get("/api/admin/users/search", { params })
    .then((res) => res.data);
}

export function updateUser(id, payload) {
  return request.put(`/api/admin/users/${id}`, payload).then((res) => res.data);
}

export function updateUserStatus(id, status) {
  return request
    .put(`/api/admin/users/${id}/status`, null, { params: { status } })
    .then((res) => res.data);
}

// ===== 日誌管理 API =====

export function getAllLogs() {
  return request.get("/api/admin/logs").then((res) => res.data);
}

export function getRecentLogs() {
  return request.get("/api/admin/logs/recent").then((res) => res.data);
}

export function getTodayLogs() {
  return request.get("/api/admin/logs/today").then((res) => res.data);
}

export function getLastWeekLogs() {
  return request.get("/api/admin/logs/last-week").then((res) => res.data);
}

export function getLastMonthLogs() {
  return request.get("/api/admin/logs/last-month").then((res) => res.data);
}

export function getLogsByEmployee(email) {
  return request
    .get("/api/admin/logs/employee", { params: { email } })
    .then((res) => res.data);
}

export function getLogsByRole(roleName) {
  return request
    .get("/api/admin/logs/role", { params: { roleName } })
    .then((res) => res.data);
}

export function getLogsByActionType(actionType) {
  return request
    .get("/api/admin/logs/action-type", { params: { actionType } })
    .then((res) => res.data);
}

export function getLogsByDateRange(start, end) {
  return request
    .get("/api/admin/logs/date-range", { params: { start, end } })
    .then((res) => res.data);
}

export function searchLogs(keyword) {
  return request
    .get("/api/admin/logs/search", { params: { keyword } })
    .then((res) => res.data);
}

export function getLogsByEmployeeAndDateRange(email, start, end) {
  return request
    .get("/api/admin/logs/employee/date-range", {
      params: { email, start, end },
    })
    .then((res) => res.data);
}

export function getLogsByRoleAndDateRange(roleName, start, end) {
  return request
    .get("/api/admin/logs/role/date-range", {
      params: { roleName, start, end },
    })
    .then((res) => res.data);
}

export function getLogsByActionTypeAndDateRange(actionType, start, end) {
  return request
    .get("/api/admin/logs/action-type/date-range", {
      params: { actionType, start, end },
    })
    .then((res) => res.data);
}

export function deleteLog(logId) {
  return request.delete(`/api/admin/logs/${logId}`).then((res) => res.data);
}
