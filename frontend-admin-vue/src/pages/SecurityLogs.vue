<template>
  <div>
    <el-row style="margin-bottom: 12px">
      <el-col :span="12">
        <el-input
          placeholder="搜尋員工 / 操作類型"
          v-model="query"
          @keyup.enter="fetchList"
          clearable
        />
      </el-col>
      <el-col :span="12" style="text-align: right">
        <el-button type="primary" @click="openExport">匯出日誌</el-button>
      </el-col>
    </el-row>

    <el-table :data="list" style="width: 100%">
      <el-table-column prop="logId" label="日誌ID" width="100" />
      <el-table-column prop="empId" label="員工ID" width="100" />
      <el-table-column prop="employeeName" label="員工姓名" width="120" />
      <el-table-column prop="roleId" label="角色ID" width="100" />
      <el-table-column prop="roleName" label="角色名稱" width="120" />
      <el-table-column prop="actionType" label="操作類型" width="120">
        <template #default="{ row }">
          <el-tag v-if="row.actionType === 'CREATE'" type="success"
            >新增</el-tag
          >
          <el-tag v-else-if="row.actionType === 'UPDATE'" type="primary"
            >修改</el-tag
          >
          <el-tag v-else-if="row.actionType === 'DELETE'" type="danger"
            >刪除</el-tag
          >
          <el-tag v-else-if="row.actionType === 'VIEW'" type="info"
            >檢視</el-tag
          >
          <el-tag v-else type="warning">{{ row.actionType }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="queryParams" label="查詢參數" width="200">
        <template #default="{ row }">
          <el-button link type="primary" @click="viewParams(row)"
            >檢視</el-button
          >
        </template>
      </el-table-column>
      <el-table-column prop="accessTime" label="訪問時間" width="180" />
      <el-table-column label="操作" width="100" fixed="right">
        <template #default="{ row }">
          <el-button size="small" type="danger" @click="remove(row)"
            >刪除</el-button
          >
        </template>
      </el-table-column>
    </el-table>

    <div style="margin-top: 12px; text-align: right">
      <el-pagination
        background
        layout="prev, pager, next"
        :total="total"
        :page-size="pageSize"
        @current-change="onPageChange"
      />
    </div>

    <!-- 檢視查詢參數對話框 -->
    <el-dialog v-model="paramsDialogVisible" title="查詢參數詳情" width="700px">
      <div style="background-color: #f5f5f5; padding: 12px; border-radius: 4px">
        <pre style="margin: 0; white-space: pre-wrap; word-wrap: break-word">{{
          paramsContent
        }}</pre>
      </div>
      <template #footer>
        <el-button @click="paramsDialogVisible = false">關閉</el-button>
        <el-button type="primary" @click="copyParams">複製</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
// TODO: 需要創建 auditLogService 來調用後端 API
// import auditLogService from '@/services/auditLogService'

const list = ref([]);
const total = ref(0);
const page = ref(1);
const pageSize = ref(10);
const query = ref("");
const paramsDialogVisible = ref(false);
const paramsContent = ref("");

async function fetchList() {
  // TODO: 實際調用後端 API
  // const params = {
  //   page: page.value,
  //   size: pageSize.value
  // }
  // if (query.value && query.value.trim()) {
  //   params.q = query.value
  // }

  // try {
  //   const res = await auditLogService.list(params)
  //   if (res && res.data) {
  //     list.value = res.data.data || res.data || []
  //     total.value = res.data.total || list.value.length
  //   }
  // } catch (error) {
  //   console.error('Failed to fetch audit logs:', error)
  //   ElMessage.error('載入安全日誌失敗')
  // }

  // 模擬數據
  list.value = [
    {
      logId: 1,
      empId: 1,
      employeeName: "王小明",
      roleId: 1,
      roleName: "管理員",
      actionType: "CREATE",
      queryParams: '{"username":"user001","email":"user001@example.com"}',
      accessTime: "2024-11-17 09:30:00",
    },
    {
      logId: 2,
      empId: 2,
      employeeName: "李小美",
      roleId: 2,
      roleName: "經理",
      actionType: "UPDATE",
      queryParams: '{"id":5,"status":"ACTIVE"}',
      accessTime: "2024-11-17 10:15:00",
    },
    {
      logId: 3,
      empId: 1,
      employeeName: "王小明",
      roleId: 1,
      roleName: "管理員",
      actionType: "DELETE",
      queryParams: '{"id":10}',
      accessTime: "2024-11-17 11:45:00",
    },
    {
      logId: 4,
      empId: 3,
      employeeName: "陳小華",
      roleId: 3,
      roleName: "客服",
      actionType: "VIEW",
      queryParams: '{"page":1,"size":10}',
      accessTime: "2024-11-17 12:20:00",
    },
    {
      logId: 5,
      empId: 4,
      employeeName: "黃小王",
      roleId: 4,
      roleName: "入場人員",
      actionType: "CREATE",
      queryParams: '{"ticket_id":"T001","user_id":15}',
      accessTime: "2024-11-17 14:00:00",
    },
  ];
  total.value = list.value.length;
}

function onPageChange(p) {
  page.value = p;
  fetchList();
}

function viewParams(row) {
  try {
    const params = JSON.parse(row.queryParams);
    paramsContent.value = JSON.stringify(params, null, 2);
  } catch (e) {
    paramsContent.value = row.queryParams;
  }
  paramsDialogVisible.value = true;
}

function copyParams() {
  navigator.clipboard.writeText(paramsContent.value);
  ElMessage.success("已複製到剪貼板");
}

function openExport() {
  // TODO: 實現匯出功能，可匯出為 CSV 或 Excel
  ElMessage.info("匯出功能開發中");
}

async function remove(row) {
  await ElMessageBox.confirm(
    `確認刪除此安全日誌記錄？(日誌ID: ${row.logId})`,
    "提示",
    { type: "warning" }
  );

  // TODO: 實際調用後端 API
  // try {
  //   await auditLogService.delete(row.logId)
  //   ElMessage.success('刪除成功')
  //   fetchList()
  // } catch (error) {
  //   console.error('Failed to delete audit log:', error)
  //   ElMessage.error('刪除失敗')
  // }

  console.log("刪除日誌:", row.logId);
  ElMessage.success("刪除成功");
  fetchList();
}

onMounted(fetchList);
</script>
