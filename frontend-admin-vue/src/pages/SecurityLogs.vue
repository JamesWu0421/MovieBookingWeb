<template>
<<<<<<< HEAD
  <div class="logs-page">
    <el-row class="toolbar" style="margin-bottom: 12px">
      <el-col :span="8">
        <el-select
          v-model="filterType"
          placeholder="選擇查詢類型"
          style="width: 100%"
          @change="onFilterTypeChange"
        >
          <el-option label="最近 10 筆" value="recent" />
          <el-option label="今天" value="today" />
          <el-option label="最近 7 天" value="week" />
          <el-option label="最近 30 天" value="month" />
          <el-option label="依員工 Email" value="employee" />
          <el-option label="依角色" value="role" />
          <el-option label="依操作類型" value="actionType" />
          <el-option label="所有日誌" value="all" />
        </el-select>
      </el-col>

      <!-- 員工 Email 輸入 -->
      <el-col :span="8" v-if="filterType === 'employee'">
        <el-input
          v-model="filterValue"
          placeholder="輸入員工 Email"
          clearable
          @keyup.enter="fetchList"
          @clear="fetchList"
        />
      </el-col>

      <!-- 角色選擇 -->
      <el-col :span="8" v-if="filterType === 'role'">
        <el-select
          v-model="filterValue"
          placeholder="選擇角色"
          style="width: 100%"
        >
          <el-option label="管理員 (ADMIN)" value="ADMIN" />
          <el-option label="經理 (MANAGER)" value="MANAGER" />
          <el-option label="客服 (SUPPORT_SERVICE)" value="SUPPORT_SERVICE" />
          <el-option label="入場人員 (ENTRYSTAFF)" value="ENTRYSTAFF" />
        </el-select>
      </el-col>

      <!-- 操作類型選擇 -->
      <el-col :span="8" v-if="filterType === 'actionType'">
        <el-select
          v-model="filterValue"
          placeholder="選擇操作類型"
          style="width: 100%"
        >
          <el-option label="新增 (POST)" value="POST" />
          <el-option label="修改 (PUT)" value="PUT" />
          <el-option label="刪除 (DELETE)" value="DELETE" />
          <el-option label="部分更新 (PATCH)" value="PATCH" />
        </el-select>
      </el-col>

      <el-col :span="8" style="text-align: right">
        <el-button type="primary" @click="fetchList" :loading="loading">
          查詢
        </el-button>
        <el-button @click="resetFilter">重置</el-button>
      </el-col>
    </el-row>

    <el-table :data="list" style="width: 100%" v-loading="loading">
      <el-table-column type="index" label="#" width="80" />

      <el-table-column label="員工Email" width="200">
        <template #default="{ row }">
          {{ row.employee?.empEmail || "-" }}
        </template>
      </el-table-column>

      <el-table-column label="員工姓名" width="120">
        <template #default="{ row }">
          {{ row.employee?.empName || "-" }}
        </template>
      </el-table-column>

      <el-table-column label="角色" width="150">
        <template #default="{ row }">
          <el-tag :type="getRoleTagType(row.role?.roleName)">
            {{ row.role?.roleName || "-" }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column label="操作類型">
        <template #default="{ row }">
          <el-tag :type="getActionTagType(row.actionType)">
            {{ row.actionType }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column label="訪問時間">
        <template #default="{ row }">
          {{ formatDateTime(row.accessTime) }}
        </template>
      </el-table-column>

      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="viewParams(row)">詳情</el-button>
          <el-button size="small" type="danger" @click="remove(row)">
            刪除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-wrapper" style="margin-top: 12px; text-align: right">
      <el-pagination
        background
        layout="prev, pager, next, jumper, ->, total"
        :current-page="page"
        :page-size="pageSize"
        :total="total"
        @current-change="handlePageChange"
      />
    </div>

    <!-- 檢視日誌詳情對話框 -->
    <el-dialog v-model="dialogVisible" title="日誌詳情" width="1000px">
      <el-descriptions :column="1" border v-if="currentLog">
        <el-descriptions-item label="日誌 ID">
          {{ currentLog.logId }}
        </el-descriptions-item>
        <el-descriptions-item label="操作員工">
          {{ currentLog.employee?.empName }} ({{
            currentLog.employee?.empEmail
          }})
        </el-descriptions-item>
        <el-descriptions-item label="角色">
          <el-tag :type="getRoleTagType(currentLog.role?.roleName)">
            {{ currentLog.role?.roleName }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="操作類型">
          <el-tag :type="getActionTagType(currentLog.actionType)">
            {{ currentLog.actionType }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="操作時間">
          {{ formatDateTime(currentLog.accessTime) }}
        </el-descriptions-item>
        <el-descriptions-item label="請求參數">
          <pre
            style="
              margin: 0;
              white-space: pre-wrap;
              word-wrap: break-word;
              background: #f5f5f5;
              padding: 8px;
              border-radius: 4px;
              max-height: 300px;
              overflow-y: auto;
            "
            >{{ currentLog.queryParams }}</pre
          >
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="dialogVisible = false">關閉</el-button>
        <el-button type="primary" @click="copyParams">複製參數</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  getAllLogs,
  getRecentLogs,
  getTodayLogs,
  getLastWeekLogs,
  getLastMonthLogs,
  getLogsByEmployee,
  getLogsByRole,
  getLogsByActionType,
  deleteLog,
} from "../api/auth";

const list = ref([]);
const dialogVisible = ref(false);
const loading = ref(false);
const currentLog = ref(null);

const page = ref(1);
const pageSize = ref(10);
const total = ref(0);

const filterType = ref("recent");
const filterValue = ref("");

// 查詢日誌列表
async function fetchList() {
  // 檢查必填欄位
  if (
    ["employee", "role", "actionType"].includes(filterType.value) &&
    !filterValue.value
  ) {
    ElMessage.warning("請輸入查詢條件");
    return;
  }

  loading.value = true;

  try {
    let response;

    switch (filterType.value) {
      case "recent":
        response = await getRecentLogs();
        break;
      case "today":
        response = await getTodayLogs();
        break;
      case "week":
        response = await getLastWeekLogs();
        break;
      case "month":
        response = await getLastMonthLogs();
        break;
      case "employee":
        response = await getLogsByEmployee(filterValue.value);
        break;
      case "role":
        response = await getLogsByRole(filterValue.value);
        break;
      case "actionType":
        response = await getLogsByActionType(filterValue.value);
        break;
      case "all":
        response = await getAllLogs();
        break;
      default:
        response = await getRecentLogs();
    }

    list.value = response;
    total.value = response.length;
  } catch (error) {
    console.error("載入日誌失敗:", error);
    ElMessage.error(
      "載入日誌失敗: " + (error.response?.data?.message || error.message)
    );
  } finally {
    loading.value = false;
  }
}

// 分頁切換
function handlePageChange(newPage) {
  page.value = newPage;
  fetchList();
}

// 篩選類型改變時清空篩選值
function onFilterTypeChange() {
  filterValue.value = "";
}

// 重置篩選
function resetFilter() {
  filterType.value = "recent";
  filterValue.value = "";
  fetchList();
}

// 檢視詳情
function viewParams(row) {
  currentLog.value = row;
  dialogVisible.value = true;
}

// 複製參數
function copyParams() {
  if (currentLog.value?.queryParams) {
    navigator.clipboard.writeText(currentLog.value.queryParams);
    ElMessage.success("已複製到剪貼板");
  }
}

// 刪除日誌
async function remove(row) {
  await ElMessageBox.confirm(
    `確認刪除此日誌記錄？(日誌ID: ${row.logId})`,
    "提示",
    { type: "warning" }
  );

  try {
    await deleteLog(row.logId);
    ElMessage.success("刪除成功");
    fetchList();
  } catch (error) {
    console.error("刪除失敗:", error);
    ElMessage.error(
      "刪除失敗: " + (error.response?.data?.message || error.message)
    );
  }
}

// 角色標籤顏色
function getRoleTagType(roleName) {
  const typeMap = {
    ADMIN: "danger",
    MANAGER: "warning",
    SUPPORT_SERVICE: "info",
    ENTRYSTAFF: "success",
  };
  return typeMap[roleName] || "info";
}

// 操作類型標籤顏色
function getActionTagType(actionType) {
  if (!actionType) return "info";
  if (actionType.includes("POST")) return "success";
  if (actionType.includes("PUT")) return "primary";
  if (actionType.includes("DELETE")) return "danger";
  if (actionType.includes("PATCH")) return "warning";
  return "info";
}

// 格式化時間
function formatDateTime(dateTime) {
  if (!dateTime) return "-";
  return new Date(dateTime).toLocaleString("zh-TW", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
    second: "2-digit",
  });
}

onMounted(fetchList);
=======
  <el-card>
    <h3>安全日誌</h3>
    <p>此頁面為功能佔位，之後可依需求串接 API 與加入表格/表單。</p>
  </el-card>
</template>

<script setup>
>>>>>>> 2eeb41fca6023512bb0be263c6f19e7e5ff7905e
</script>

<style scoped>
.logs-page {
  width: 100%;
  box-sizing: border-box;
}
.toolbar {
  margin-bottom: 12px;
}
.el-table {
  width: 100%;
}
</style>
