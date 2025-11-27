<template>
<<<<<<< HEAD
  <div class="users-page">
    <el-row class="toolbar" align="middle">
      <el-col :span="12">
        <el-input
          placeholder="搜尋帳號 / Email / 手機"
          v-model="query"
          @keyup.enter="handleSearch"
          clearable
          @clear="handleSearch"
        />
      </el-col>
      <!-- 後台不創建會員，所以這裡先不放「新增」按鈕 -->
      <el-col :span="12" style="text-align: right">
        <!-- 之後如果要加「匯出」之類的功能，可以放這裡 -->
      </el-col>
    </el-row>

    <el-table :data="list" class="users-table" v-loading="loading">
      <el-table-column type="index" label="#" width="60" />
      <el-table-column prop="username" label="帳號" width="140" />
      <el-table-column prop="nickname" label="暱稱" width="140" />
      <el-table-column prop="email" label="Email" min-width="200" />
      <el-table-column prop="phoneNumber" label="手機號碼" width="150" />
      <el-table-column prop="gender" label="性別" width="80" />
      <el-table-column prop="birthday" label="生日" width="120" />
      <el-table-column prop="createdAt" label="註冊時間" min-width="180" />

      <el-table-column label="狀態" width="100">
        <template #default="{ row }">
          <el-tag v-if="row.status === 1" type="success">啟用</el-tag>
          <el-tag v-else-if="row.status === 0" type="info">未驗證</el-tag>
          <el-tag v-else type="danger">停權</el-tag>
        </template>
      </el-table-column>

      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="openEdit(row)">編輯</el-button>
          <el-button size="small" type="warning" @click="toggleStatus(row)">
            {{ row.status === 2 ? "解除停權" : "停權" }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分頁 -->
    <div class="pagination-wrapper">
      <el-pagination
        background
        layout="prev, pager, next, jumper, ->, total"
        :current-page="page"
        :page-size="pageSize"
        :total="total"
        @current-change="handlePageChange"
      />
    </div>

    <!-- 編輯對話框（不改密碼、不改帳號） -->
    <el-dialog v-model="dialogVisible" title="會員資料" width="520px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="90px">
        <el-form-item label="帳號">
          <el-input v-model="form.username" disabled />
        </el-form-item>
        <el-form-item label="Email" prop="email">
          <el-input v-model="form.email" placeholder="example@domain.com" />
        </el-form-item>
        <el-form-item label="手機" prop="phoneNumber">
          <el-input v-model="form.phoneNumber" placeholder="09xx-xxx-xxx" />
        </el-form-item>
        <el-form-item label="暱稱" prop="nickname">
          <el-input v-model="form.nickname" placeholder="暱稱" />
        </el-form-item>
        <el-form-item label="性別" prop="gender">
          <el-select v-model="form.gender" placeholder="請選擇性別">
            <el-option label="男" value="male" />
            <el-option label="女" value="female" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>
        <el-form-item label="生日" prop="birthday">
          <el-date-picker
            v-model="form.birthday"
            type="date"
            placeholder="選擇生日"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="狀態" prop="status">
          <el-select v-model="form.status" placeholder="請選擇狀態">
            <el-option label="未驗證" :value="0" />
            <el-option label="啟用" :value="1" />
            <el-option label="停權" :value="2" />
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
// 這裡只引入「會員管理用」API，實作放在另一個檔案
import {
  fetchUsers, // (page, pageSize) 或 (params)
  searchUsers, // (keyword, page, pageSize)
  updateUser, // (id, payload)
  updateUserStatus, // (id, status)
} from "../api/auth"; // 檔名你自己決定

const list = ref([]);
const query = ref("");
const loading = ref(false);

const page = ref(1);
const pageSize = ref(10);
const total = ref(0);

const dialogVisible = ref(false);
const formRef = ref(null);

const form = ref({
  id: null,
  username: "",
  email: "",
  phoneNumber: "",
  nickname: "",
  gender: "",
  birthday: "",
  avatarUrl: "",
  status: 1,
});

// 表單驗證（以 email / phone 等為主）
const rules = {
  email: [
    { required: true, message: "請輸入 Email", trigger: "blur" },
    { type: "email", message: "Email 格式不正確", trigger: ["blur", "change"] },
  ],
  phoneNumber: [{ required: true, message: "請輸入手機號碼", trigger: "blur" }],
  nickname: [{ required: false, message: "", trigger: "blur" }],
};

async function loadData() {
  loading.value = true;
  try {
    if (query.value && query.value.trim()) {
      // 搜尋 + 分頁
      const resp = await searchUsers({
        keyword: query.value.trim(),
        page: page.value - 1,
        size: pageSize.value,
      });
      list.value = resp.content || resp.list || resp; // 看你後端回什麼
      total.value = resp.totalElements ?? resp.total ?? list.value.length;
    } else {
      const resp = await fetchUsers({
        page: page.value - 1,
        size: pageSize.value,
      });
      list.value = resp.content || resp.list || resp;
      total.value = resp.totalElements ?? resp.total ?? list.value.length;
    }
  } catch (error) {
    console.error("Failed to fetch users:", error);
    ElMessage.error("載入會員列表失敗");
  } finally {
    loading.value = false;
  }
}

function handleSearch() {
  page.value = 1;
  loadData();
}

function handlePageChange(newPage) {
  page.value = newPage;
  loadData();
}

function openEdit(row) {
  form.value = {
    id: row.id,
    username: row.username,
    email: row.email,
    phoneNumber: row.phoneNumber,
    nickname: row.nickname,
    gender: row.gender,
    birthday: row.birthday,
    avatarUrl: row.avatarUrl,
    status: row.status,
  };
  dialogVisible.value = true;
}

async function save() {
  await formRef.value.validate();
  try {
    const payload = {
      email: form.value.email,
      phoneNumber: form.value.phoneNumber,
      nickname: form.value.nickname,
      gender: form.value.gender,
      birthday: form.value.birthday,
      avatarUrl: form.value.avatarUrl,
      status: form.value.status,
    };
    await updateUser(form.value.id, payload);
    ElMessage.success("更新成功");
    dialogVisible.value = false;
    loadData();
  } catch (error) {
    console.error("Failed to update user:", error);
    ElMessage.error("儲存失敗");
  }
}

async function toggleStatus(row) {
  const targetStatus = row.status === 2 ? 1 : 2; // 2=停權 -> 1=啟用; 其他 -> 2=停權
  const actionText = targetStatus === 2 ? "停權" : "解除停權";

  await ElMessageBox.confirm(
    `確認要對會員「${row.username || row.email}」執行【${actionText}】嗎？`,
    "提示",
    { type: "warning" }
  );

  try {
    await updateUserStatus(row.id, targetStatus);
    ElMessage.success(`${actionText}成功`);
    loadData();
  } catch (error) {
    console.error("Failed to update user status:", error);
    ElMessage.error(`${actionText}失敗`);
  }
}

onMounted(loadData);
=======
  <el-card>
    <h3>會員管理</h3>
    <p>此頁面為功能佔位，之後可依需求串接 API 與加入表格/表單。</p>
  </el-card>
</template>

<script setup>
>>>>>>> 2eeb41fca6023512bb0be263c6f19e7e5ff7905e
</script>

<style scoped>
.users-page {
  width: 100%;
  box-sizing: border-box;
}

.toolbar {
  margin-bottom: 12px;
}

.users-table {
  width: 100%;
}

.pagination-wrapper {
  margin-top: 12px;
  text-align: right;
}
</style>
