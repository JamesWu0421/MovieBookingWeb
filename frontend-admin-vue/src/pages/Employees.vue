<template>
  <div class="employees-page">
    <el-row class="toolbar">
      <el-col :span="12">
        <el-input
          placeholder="搜尋姓名 / Email"
          v-model="query"
          @keyup.enter="fetchList"
          clearable
        />
      </el-col>
      <el-col :span="12" style="text-align: right">
        <el-button type="primary" @click="openCreate">新增員工</el-button>
      </el-col>
    </el-row>

    <el-table :data="list" class="employees-table">
      <el-table-column type="index" label="" width="80" />
      <el-table-column prop="empName" label="姓名" />
      <el-table-column prop="empEmail" label="Email" />
      <el-table-column prop="empPhone" label="手機號碼" width="160" />

      <!-- 顯示第一個角色名稱（如果有） -->
      <el-table-column label="角色" width="140">
        <template #default="{ row }">
          <span v-if="row.roles && row.roles.length">
            {{ row.roles[0].roleName }}
          </span>
          <span v-else>—</span>
        </template>
      </el-table-column>

      <el-table-column label="狀態" width="100">
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

    <!-- 新增/編輯對話框 -->
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

const form = ref({
  id: null,
  empName: "",
  empEmail: "",
  empPhone: "",
  status: 1, // 1=啟用, 0=停用, 2=封鎖
  roleId: null, // 對應後端 controller 的 ?roleId=
});

const plainPassword = ref(""); // 新增時用的原始密碼

// 表單驗證
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
  try {
    if (query.value && query.value.trim()) {
      list.value = await searchEmployees(query.value.trim());
    } else {
      list.value = await fetchEmployees();
    }
  } catch (error) {
    console.error("Failed to fetch employees:", error);
    ElMessage.error("載入員工列表失敗");
  }
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
      const payload = {
        empName: form.value.empName,
        empEmail: form.value.empEmail,
        empPhone: form.value.empPhone,
        status: form.value.status,
      };
      await updateEmployee(form.value.id, payload, form.value.roleId);
      ElMessage.success("更新成功");
    } else {
      const payload = {
        empName: form.value.empName,
        empEmail: form.value.empEmail,
        empPhone: form.value.empPhone,
        status: form.value.status,
        plainPassword: plainPassword.value || "123456",
      };
      await createEmployee(payload, form.value.roleId);
      ElMessage.success("新增成功");
    }
    dialogVisible.value = false;
    fetchList();
  } catch (error) {
    console.error("Failed to save employee:", error);
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
    console.error("Failed to delete employee:", error);

    // 從後端回傳取狀態碼 & 訊息
    const status = error.response?.status;
    const backendMsg = error.response?.data?.message || "";

    if (status === 403 && backendMsg.includes("Cannot delete ADMIN")) {
      ElMessage.error("不可以刪除擁有 ADMIN 權限的員工");
    } else if (status === 404 || backendMsg.includes("Employee not found")) {
      ElMessage.error("員工不存在，可能已被刪除");
    } else {
      ElMessage.error("刪除失敗，請稍後再試");
    }
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

.employees-table {
  width: 100%;
}
</style>
