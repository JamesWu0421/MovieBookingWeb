<template>
  <div>
    <el-row style="margin-bottom: 12px">
      <el-col :span="12">
        <el-input
          placeholder="搜尋工號 / 姓名 / Email"
          v-model="query"
          @keyup.enter="fetchList"
          clearable
        />
      </el-col>
      <el-col :span="12" style="text-align: right">
        <el-button type="primary" @click="openCreate">新增員工</el-button>
      </el-col>
    </el-row>

    <el-table :data="list" style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="employeeNumber" label="工號" width="120" />
      <el-table-column prop="name" label="姓名" width="120" />
      <el-table-column prop="email" label="Email" width="200" />
      <el-table-column prop="gender" label="性別" width="80">
        <template #default="{ row }">
          <el-tag v-if="row.gender === 'MALE'" type="primary">男</el-tag>
          <el-tag v-else-if="row.gender === 'FEMALE'" type="danger">女</el-tag>
          <el-tag v-else type="info">其他</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="permission" label="權限" width="120" />
      <el-table-column prop="phone" label="手機號碼" width="130" />
      <el-table-column label="狀態" width="100">
        <template #default="{ row }">
          <el-tag v-if="row.status === 'ACTIVE'" type="success">在職</el-tag>
          <el-tag v-else-if="row.status === 'INACTIVE'" type="info"
            >離職</el-tag
          >
          <el-tag v-else-if="row.status === 'BLOCKED'" type="danger"
            >停職</el-tag
          >
        </template>
      </el-table-column>
      <el-table-column prop="hireDate" label="入職日期" width="120" />
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="openEdit(row)">編輯</el-button>
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

    <!-- 新增/編輯對話框 -->
    <el-dialog v-model="dialogVisible" title="員工資料" width="600px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="工號" prop="employeeNumber">
          <el-input
            v-model="form.employeeNumber"
            placeholder="請輸入工號"
          ></el-input>
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="請輸入姓名"></el-input>
        </el-form-item>
        <el-form-item label="Email" prop="email">
          <el-input
            v-model="form.email"
            placeholder="example@domain.com"
          ></el-input>
        </el-form-item>
        <el-form-item label="性別" prop="gender">
          <el-select
            v-model="form.gender"
            placeholder="請選擇性別"
            style="width: 100%"
          >
            <el-option label="男" value="MALE" />
            <el-option label="女" value="FEMALE" />
            <el-option label="其他" value="OTHER" />
          </el-select>
        </el-form-item>
        <el-form-item label="權限" prop="permission">
          <el-select
            v-model="form.permission"
            placeholder="請選擇權限"
            style="width: 100%"
          >
            <el-option label="管理員" value="ADMIN" />
            <el-option label="經理" value="MANAGER" />
            <el-option label="客服" value="CUSTOMER_SERVICE" />
            <el-option label="入場人員" value="ENTRY_STAFF" />
          </el-select>
        </el-form-item>
        <el-form-item label="手機號碼" prop="phone">
          <el-input v-model="form.phone" placeholder="09xx-xxx-xxx"></el-input>
        </el-form-item>
        <el-form-item label="入職日期" prop="hireDate">
          <el-date-picker
            v-model="form.hireDate"
            type="date"
            placeholder="請選擇入職日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            :clearable="true"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="狀態" prop="status">
          <el-select
            v-model="form.status"
            placeholder="請選擇狀態"
            style="width: 100%"
          >
            <el-option label="在職" value="ACTIVE">
              <el-tag type="success" size="small">在職</el-tag>
            </el-option>
            <el-option label="離職" value="INACTIVE">
              <el-tag type="info" size="small">離職</el-tag>
            </el-option>
            <el-option label="停職" value="BLOCKED">
              <el-tag type="danger" size="small">停職</el-tag>
            </el-option>
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
// TODO: 需要創建 employeeService 來調用後端 API
// import employeeService from '@/services/employeeService'

const list = ref([]);
const total = ref(0);
const page = ref(1);
const pageSize = ref(10);
const query = ref("");
const dialogVisible = ref(false);
const formRef = ref(null);

const form = ref({
  id: null,
  employeeNumber: "",
  name: "",
  email: "",
  gender: "OTHER",
  permission: "",
  phone: "",
  hireDate: "",
  status: "ACTIVE",
});

// 表單驗證規則
const rules = {
  employeeNumber: [
    { required: true, message: "請輸入工號", trigger: "blur" },
    { min: 4, max: 20, message: "工號長度應為 4-20 個字元", trigger: "blur" },
  ],
  name: [
    { required: true, message: "請輸入姓名", trigger: "blur" },
    { min: 2, max: 50, message: "姓名長度應為 2-50 個字元", trigger: "blur" },
  ],
  email: [
    { required: true, message: "請輸入 Email", trigger: "blur" },
    { type: "email", message: "Email 格式不正確", trigger: ["blur", "change"] },
  ],
  phone: [
    { required: true, message: "請輸入手機號碼", trigger: "blur" },
    {
      pattern: /^09\d{8}$/,
      message: "請輸入有效的台灣手機號碼 (09xxxxxxxx)",
      trigger: "blur",
    },
  ],
  gender: [{ required: true, message: "請選擇性別", trigger: "change" }],
  permission: [{ required: true, message: "請選擇權限", trigger: "change" }],
  hireDate: [{ required: true, message: "請選擇入職日期", trigger: "change" }],
  status: [{ required: true, message: "請選擇狀態", trigger: "change" }],
};

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
  //   const res = await employeeService.list(params)
  //   if (res && res.data) {
  //     list.value = res.data.data || res.data || []
  //     total.value = res.data.total || list.value.length
  //   }
  // } catch (error) {
  //   console.error('Failed to fetch employees:', error)
  //   ElMessage.error('載入員工列表失敗')
  // }

  // 模擬數據
  list.value = [
    {
      id: 1,
      employeeNumber: "EMP001",
      name: "王小明",
      email: "wang.xiaoming@example.com",
      gender: "MALE",
      permission: "管理員",
      phone: "0912345678",
      hireDate: "2020-03-15",
      status: "ACTIVE",
    },
    {
      id: 2,
      employeeNumber: "EMP002",
      name: "李小美",
      email: "li.xiaomei@example.com",
      gender: "FEMALE",
      permission: "經理",
      phone: "0923456789",
      hireDate: "2021-07-22",
      status: "ACTIVE",
    },
    {
      id: 3,
      employeeNumber: "EMP003",
      name: "陳小華",
      email: "chen.xiaohua@example.com",
      gender: "OTHER",
      permission: "客服",
      phone: "0934567890",
      hireDate: "2022-01-10",
      status: "BLOCKED",
    },
  ];
  total.value = list.value.length;
}

function onPageChange(p) {
  page.value = p;
  fetchList();
}

function openCreate() {
  form.value = {
    id: null,
    employeeNumber: "",
    name: "",
    email: "",
    gender: "OTHER",
    permission: "",
    phone: "",
    hireDate: "",
    status: "ACTIVE",
  };
  dialogVisible.value = true;
}

function openEdit(row) {
  form.value = Object.assign({}, row);
  dialogVisible.value = true;
}

async function save() {
  // 表單驗證
  await formRef.value.validate();

  // TODO: 實際調用後端 API
  // try {
  //   if (form.value.id) {
  //     await employeeService.update(form.value.id, form.value)
  //     ElMessage.success('更新成功')
  //   } else {
  //     await employeeService.create(form.value)
  //     ElMessage.success('新增成功')
  //   }
  //   dialogVisible.value = false
  //   fetchList()
  // } catch (error) {
  //   console.error('Failed to save employee:', error)
  //   ElMessage.error('儲存失敗')
  // }

  console.log("儲存員工:", form.value);
  ElMessage.success(form.value.id ? "更新成功" : "新增成功");
  dialogVisible.value = false;
  fetchList();
}

async function remove(row) {
  await ElMessageBox.confirm(
    `確認刪除員工「${row.name}」(${row.employeeNumber})？`,
    "提示",
    { type: "warning" }
  );

  // TODO: 實際調用後端 API
  // try {
  //   await employeeService.delete(row.id)
  //   ElMessage.success('刪除成功')
  //   fetchList()
  // } catch (error) {
  //   console.error('Failed to delete employee:', error)
  //   ElMessage.error('刪除失敗')
  // }

  console.log("刪除員工:", row.id);
  ElMessage.success("刪除成功");
  fetchList();
}

onMounted(fetchList);
</script>
