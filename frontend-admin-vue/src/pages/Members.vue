<template>
  <div>
    <el-row style="margin-bottom: 12px">
      <el-col :span="12">
        <el-input
          placeholder="搜尋帳號 / 稱號 / Email"
          v-model="query"
          @keyup.enter="fetchList"
          clearable
        />
      </el-col>
      <el-col :span="12" style="text-align: right">
        <el-button type="primary" @click="openCreate">新增會員</el-button>
      </el-col>
    </el-row>

    <el-table :data="list" style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="帳號" width="130" />
      <el-table-column prop="nickname" label="稱號" width="120" />
      <el-table-column prop="email" label="Email" width="200" />
      <el-table-column prop="gender" label="性別" width="80">
        <template #default="{ row }">
          <el-tag v-if="row.gender === 'MALE'" type="primary">男</el-tag>
          <el-tag v-else-if="row.gender === 'FEMALE'" type="danger">女</el-tag>
          <el-tag v-else type="info">其他</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="birthday" label="生日" width="120" />
      <el-table-column prop="phone" label="手機號碼" width="130" />
      <el-table-column label="狀態" width="100">
        <template #default="{ row }">
          <el-tag v-if="row.status === 'ACTIVE'" type="success">啟用</el-tag>
          <el-tag v-else-if="row.status === 'INACTIVE'" type="info"
            >未啟用</el-tag
          >
          <el-tag v-else-if="row.status === 'BLOCKED'" type="danger"
            >封鎖</el-tag
          >
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="建立時間" width="160" />
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
    <el-dialog v-model="dialogVisible" title="會員資料" width="600px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="帳號" prop="username">
          <el-input v-model="form.username" placeholder="請輸入帳號"></el-input>
        </el-form-item>
        <el-form-item label="稱號" prop="nickname">
          <el-input
            v-model="form.nickname"
            placeholder="請輸入稱號/暱稱"
          ></el-input>
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
        <el-form-item label="生日" prop="birthday">
          <el-date-picker
            v-model="form.birthday"
            type="date"
            placeholder="請選擇生日"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            :disabled-date="disabledDate"
            :clearable="true"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="手機號碼" prop="phone">
          <el-input v-model="form.phone" placeholder="09xx-xxx-xxx"></el-input>
        </el-form-item>
        <el-form-item label="狀態" prop="status">
          <el-select
            v-model="form.status"
            placeholder="請選擇狀態"
            style="width: 100%"
          >
            <el-option label="啟用" value="ACTIVE">
              <el-tag type="success" size="small">啟用</el-tag>
            </el-option>
            <el-option label="未啟用" value="INACTIVE">
              <el-tag type="info" size="small">未啟用</el-tag>
            </el-option>
            <el-option label="封鎖" value="BLOCKED">
              <el-tag type="danger" size="small">封鎖</el-tag>
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
// TODO: 需要創建 userService 來調用後端 API
// import userService from '@/services/userService'

const list = ref([]);
const total = ref(0);
const page = ref(1);
const pageSize = ref(10);
const query = ref("");
const dialogVisible = ref(false);
const formRef = ref(null);

const form = ref({
  id: null,
  username: "",
  nickname: "",
  email: "",
  gender: "OTHER",
  birthday: "",
  phone: "",
  status: "ACTIVE",
});

// 限制日期範圍：不能選擇未來日期，且年齡需在 6-120 歲之間
const disabledDate = (time) => {
  const today = new Date();
  const minDate = new Date();
  minDate.setFullYear(today.getFullYear() - 120);
  const maxDate = new Date();
  maxDate.setFullYear(today.getFullYear() - 6);

  return (
    time.getTime() > maxDate.getTime() || time.getTime() < minDate.getTime()
  );
};

// 表單驗證規則
const rules = {
  username: [
    { required: true, message: "請輸入帳號", trigger: "blur" },
    { min: 4, max: 30, message: "帳號長度應為 4-30 個字元", trigger: "blur" },
  ],
  nickname: [
    { required: true, message: "請輸入稱號", trigger: "blur" },
    { min: 2, max: 50, message: "稱號長度應為 2-50 個字元", trigger: "blur" },
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
  birthday: [{ required: true, message: "請選擇生日", trigger: "change" }],
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
  //   const res = await userService.list(params)
  //   if (res && res.data) {
  //     list.value = res.data.data || res.data || []
  //     total.value = res.data.total || list.value.length
  //   }
  // } catch (error) {
  //   console.error('Failed to fetch users:', error)
  //   ElMessage.error('載入會員列表失敗')
  // }

  // 模擬數據
  list.value = [
    {
      id: 1,
      username: "user001",
      nickname: "小明",
      email: "xiaoming@example.com",
      gender: "MALE",
      birthday: "1995-03-15",
      phone: "0912345678",
      status: "ACTIVE",
      createdAt: "2024-11-10 10:00:00",
    },
    {
      id: 2,
      username: "user002",
      nickname: "小美",
      email: "xiaomei@example.com",
      gender: "FEMALE",
      birthday: "1998-07-22",
      phone: "0923456789",
      status: "INACTIVE",
      createdAt: "2024-11-12 14:30:00",
    },
    {
      id: 3,
      username: "user003",
      nickname: "小華",
      email: "xiaohua@example.com",
      gender: "OTHER",
      birthday: "2000-01-10",
      phone: "0934567890",
      status: "BLOCKED",
      createdAt: "2024-11-14 09:15:00",
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
    username: "",
    nickname: "",
    email: "",
    gender: "OTHER",
    birthday: "",
    phone: "",
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
  //     await userService.update(form.value.id, form.value)
  //     ElMessage.success('更新成功')
  //   } else {
  //     await userService.create(form.value)
  //     ElMessage.success('新增成功')
  //   }
  //   dialogVisible.value = false
  //   fetchList()
  // } catch (error) {
  //   console.error('Failed to save user:', error)
  //   ElMessage.error('儲存失敗')
  // }

  console.log("儲存會員:", form.value);
  ElMessage.success(form.value.id ? "更新成功" : "新增成功");
  dialogVisible.value = false;
  fetchList();
}

async function remove(row) {
  await ElMessageBox.confirm(
    `確認刪除會員「${row.nickname}」(${row.username})？`,
    "提示",
    { type: "warning" }
  );

  // TODO: 實際調用後端 API
  // try {
  //   await userService.delete(row.id)
  //   ElMessage.success('刪除成功')
  //   fetchList()
  // } catch (error) {
  //   console.error('Failed to delete user:', error)
  //   ElMessage.error('刪除失敗')
  // }

  console.log("刪除會員:", row.id);
  ElMessage.success("刪除成功");
  fetchList();
}

onMounted(fetchList);
</script>
