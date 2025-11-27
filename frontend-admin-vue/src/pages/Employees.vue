<template>
  <div class="employees-page">
    <el-row class="toolbar" style="margin-bottom: 12px">
      <el-col :span="12">
        <el-input
          placeholder="搜尋姓名 / Email"
          v-model="query"
          @keyup.enter="fetchList"
          clearable
          @clear="fetchList"
        />
      </el-col>
      <el-col :span="12" style="text-align: right">
        <el-button type="primary" @click="openCreate">新增員工</el-button>
      </el-col>
    </el-row>

    <el-table :data="list" style="width: 100%" v-loading="loading">
      <el-table-column type="index" label="#" width="80" />
      <el-table-column prop="empName" label="姓名" />
      <el-table-column prop="empEmail" label="Email" />
      <el-table-column prop="empPhone" label="手機號碼" />

      <el-table-column label="角色">
        <template #default="{ row }">
          <span v-if="row.roles && row.roles.length">
            {{ row.roles[0].roleName }}
          </span>
          <span v-else>—</span>
        </template>
      </el-table-column>

      <el-table-column label="狀態">
        <template #default="{ row }">
          <el-tag v-if="row.status === 1" type="success">啟用</el-tag>
          <el-tag v-else-if="row.status === 0" type="info">停用</el-tag>
          <el-tag v-else type="danger">封鎖</el-tag>
        </template>
      </el-table-column>

      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="openEdit(row)">編輯</el-button>
          <el-button size="small" type="danger" @click="remove(row)"
            >刪除</el-button
          >
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

    <el-dialog v-model="dialogVisible" title="員工資料" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="90px">
        <el-form-item label="姓名" prop="empName">
          <el-input v-model="form.empName" placeholder="請輸入姓名" />
        </el-form-item>
        <el-form-item label="Email" prop="empEmail">
          <el-input v-model="form.empEmail" placeholder="example@domain.com" />
        </el-form-item>
        <el-form-item label="手機" prop="empPhone">
          <el-input v-model="form.empPhone" placeholder="09xx-xxx-xxx" />
        </el-form-item>
        <el-form-item label="密碼" v-if="!form.id">
          <el-input
            v-model="plainPassword"
            type="password"
            placeholder="預設密碼，例如 123456"
          />
        </el-form-item>
        <el-form-item label="角色" prop="roleId">
          <el-select
            v-model="form.roleId"
            placeholder="請選擇角色"
            style="width: 100%"
          >
            <el-option label="管理員" :value="1" />
            <el-option label="經理" :value="2" />
            <el-option label="客服" :value="3" />
            <el-option label="入場人員" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="狀態" prop="status">
          <el-select
            v-model="form.status"
            placeholder="請選擇狀態"
            style="width: 100%"
          >
            <el-option label="啟用" :value="1" />
            <el-option label="停用" :value="0" />
            <el-option label="封鎖" :value="2" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="save">儲存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  fetchEmployees,
  searchEmployees,
  createEmployee,
  updateEmployee,
  deleteEmployee,
} from "../api/auth";

const list = ref([]);
const query = ref("");
const dialogVisible = ref(false);
const formRef = ref(null);
const loading = ref(false);

const page = ref(1);
const pageSize = ref(10);
const total = ref(0);

const form = ref({
  id: null,
  empName: "",
  empEmail: "",
  empPhone: "",
  status: 1,
  roleId: null,
});

const plainPassword = ref("");

const rules = {
  empName: [{ required: true, message: "請輸入姓名", trigger: "blur" }],
  empEmail: [
    { required: true, message: "請輸入 Email", trigger: "blur" },
    { type: "email", message: "Email 格式不正確", trigger: ["blur", "change"] },
  ],
  empPhone: [{ required: true, message: "請輸入手機號碼", trigger: "blur" }],
  roleId: [{ required: true, message: "請選擇角色", trigger: "change" }],
  status: [{ required: true, message: "請選擇狀態", trigger: "change" }],
};

async function fetchList() {
  loading.value = true;
  try {
    let resp;
    if (query.value && query.value.trim()) {
      resp = await searchEmployees({
        keyword: query.value.trim(),
        page: page.value - 1,
        size: pageSize.value,
      });
    } else {
      resp = await fetchEmployees({
        page: page.value - 1,
        size: pageSize.value,
      });
    }
    list.value = resp.content || [];
    total.value = resp.totalElements || 0;
  } catch (error) {
    console.error("取得員工資料失敗:", error);
    ElMessage.error("載入員工列表失敗");
  } finally {
    loading.value = false;
  }
}

function handlePageChange(newPage) {
  page.value = newPage; // 設為你點的頁碼
  fetchList(); // 重新請求當前頁資料
}

function openCreate() {
  form.value = {
    id: null,
    empName: "",
    empEmail: "",
    empPhone: "",
    status: 1,
    roleId: null,
  };
  plainPassword.value = "";
  dialogVisible.value = true;
}

function openEdit(row) {
  form.value = {
    id: row.id,
    empName: row.empName,
    empEmail: row.empEmail,
    empPhone: row.empPhone,
    status: row.status,
    roleId: row.roles && row.roles.length ? row.roles[0].id : null,
  };
  plainPassword.value = "";
  dialogVisible.value = true;
}

async function save() {
  await formRef.value.validate();
  try {
    if (form.value.id) {
      await updateEmployee(
        form.value.id,
        {
          empName: form.value.empName,
          empEmail: form.value.empEmail,
          empPhone: form.value.empPhone,
          status: form.value.status,
        },
        form.value.roleId
      );
      ElMessage.success("更新成功");
    } else {
      await createEmployee(
        {
          empName: form.value.empName,
          empEmail: form.value.empEmail,
          empPhone: form.value.empPhone,
          status: form.value.status,
          empPasswordHash: plainPassword.value || "123456",
        },
        form.value.roleId
      );
      ElMessage.success("新增成功");
    }
    dialogVisible.value = false;
    fetchList();
  } catch (error) {
    console.error("儲存失敗:", error);
    ElMessage.error("儲存失敗");
  }
}

async function remove(row) {
  await ElMessageBox.confirm(
    `確認刪除員工「${row.empName}」 (${row.empEmail})？`,
    "提示",
    { type: "warning" }
  );
  try {
    await deleteEmployee(row.id);
    ElMessage.success("刪除成功");
    fetchList();
  } catch (error) {
    console.error("刪除失敗:", error);
    ElMessage.error("刪除失敗");
  }
}

onMounted(fetchList);
</script>

<style scoped>
.employees-page {
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
