<template>
  <div class="notifications-container">
    <el-card>
      <template #header>
        <div class="header-actions">
          <h2>通知管理</h2>
          <div class="button-group">
            <el-button type="success" :icon="MagicStick" @click="openQuickCreateDialog">快速創建</el-button>
            <el-button type="primary" :icon="Plus" @click="openCreateDialog">新增通知</el-button>
          </div>
        </div>
      </template>

      <!-- 搜尋和篩選 -->
      <div class="filter-section">
        <el-input
          v-model="searchQuery"
          placeholder="搜尋通知標題"
          :prefix-icon="Search"
          clearable
          @clear="fetchList"
          @keyup.enter="fetchList"
          style="width: 300px; margin-right: 10px;"
        />
        <el-select
          v-model="filterType"
          placeholder="篩選類型"
          clearable
          @change="fetchList"
          style="width: 200px; margin-right: 10px;"
        >
          <el-option label="全部" value="" />
          <el-option label="系統通知" value="SYSTEM" />
          <el-option label="優惠活動" value="PROMOTION" />
          <el-option label="電影通知" value="MOVIE" />
          <el-option label="訂單通知" value="ORDER" />
        </el-select>
        <el-button type="primary" :icon="Search" @click="fetchList">搜尋</el-button>
      </div>

      <!-- 通知列表 -->
      <el-table
        :data="notifications"
        v-loading="loading"
        stripe
        style="width: 100%; margin-top: 20px;"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="type" label="類型" width="120">
          <template #default="{ row }">
            <el-tag :type="getTypeTagType(row.type)">
              {{ getTypeLabel(row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="標題" min-width="200" />
        <el-table-column prop="content" label="內容" min-width="250" show-overflow-tooltip />
        <el-table-column prop="relatedType" label="關聯類型" width="100" />
        <el-table-column prop="relatedId" label="關聯ID" width="100" />
        <el-table-column prop="isActive" label="狀態" width="100">
          <template #default="{ row }">
            <el-tag :type="row.isActive ? 'success' : 'info'">
              {{ row.isActive ? '啟用' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="創建時間" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" :icon="Promotion" @click="openSendDialog(row)">
              推送
            </el-button>
            <el-button type="warning" size="small" :icon="Edit" @click="openEditDialog(row)">
              編輯
            </el-button>
            <el-button type="danger" size="small" :icon="Delete" @click="handleDelete(row.id)">
              刪除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分頁 -->
      <div style="margin-top: 20px; text-align: right;">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="fetchList"
          @current-change="fetchList"
        />
      </div>
    </el-card>

    <!-- 快速創建對話框 -->
    <el-dialog
      v-model="quickCreateDialogVisible"
      title="快速創建通知"
      width="600px"
      @close="resetQuickCreateForm"
    >
      <el-form :model="quickCreateForm" label-width="120px">
        <el-form-item label="來源類型">
          <el-radio-group v-model="quickCreateForm.sourceType" @change="loadSources">
            <el-radio label="EVENT">活動/優惠</el-radio>
            <el-radio label="MOVIE">電影</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item v-if="quickCreateForm.sourceType === 'EVENT'" label="活動分類">
          <el-select v-model="quickCreateForm.eventCategory" placeholder="選擇分類" @change="loadSources">
            <el-option label="全部" value="all" />
            <el-option label="優惠活動" value="promotion" />
            <el-option label="系統公告" value="announcement" />
          </el-select>
        </el-form-item>

        <el-form-item label="選擇項目">
          <el-select
            v-model="quickCreateForm.sourceId"
            placeholder="請選擇"
            @change="onSourceChange"
            v-loading="sourcesLoading"
          >
            <el-option
              v-for="source in sources"
              :key="source.id"
              :label="quickCreateForm.sourceType === 'EVENT' ? source.name : source.title"
              :value="source.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="預覽標題">
          <el-input v-model="quickPreviewTitle" disabled />
        </el-form-item>

        <el-form-item label="預覽內容">
          <el-input v-model="quickPreviewContent" type="textarea" :rows="3" disabled />
        </el-form-item>

        <el-form-item label="推送方式">
          <el-radio-group v-model="quickCreateForm.pushType">
            <el-radio label="all">推送給所有用戶</el-radio>
            <el-radio label="specific">推送給指定用戶</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item v-if="quickCreateForm.pushType === 'specific'" label="用戶ID">
          <el-input
            v-model="quickCreateForm.userIdsInput"
            placeholder="輸入用戶ID，用逗號分隔，例如: 1,2,3"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="quickCreateDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmQuickCreate" :loading="submitting">
          確認創建並推送
        </el-button>
      </template>
    </el-dialog>

    <!-- 新增/編輯對話框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '編輯通知' : '新增通知'"
      width="600px"
      @close="resetForm"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="類型" prop="type">
          <el-select v-model="form.type" placeholder="請選擇類型">
            <el-option label="系統通知" value="SYSTEM" />
            <el-option label="優惠活動" value="PROMOTION" />
            <el-option label="電影通知" value="MOVIE" />
            <el-option label="訂單通知" value="ORDER" />
          </el-select>
        </el-form-item>

        <el-form-item label="標題" prop="title">
          <el-input v-model="form.title" placeholder="請輸入標題" />
        </el-form-item>

        <el-form-item label="內容" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="4"
            placeholder="請輸入內容"
          />
        </el-form-item>

        <el-form-item label="關聯類型">
          <el-input v-model="form.relatedType" placeholder="選填，例如: EVENT, MOVIE, ORDER" />
        </el-form-item>

        <el-form-item label="關聯ID">
          <el-input v-model="form.relatedId" placeholder="選填" />
        </el-form-item>

        <el-form-item label="狀態">
          <el-switch v-model="form.isActive" active-text="啟用" inactive-text="停用" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">確定</el-button>
      </template>
    </el-dialog>

    <!-- 推送對話框 -->
    <el-dialog v-model="sendDialogVisible" title="推送通知" width="500px">
      <el-form :model="sendForm" label-width="120px">
        <el-form-item label="推送方式">
          <el-radio-group v-model="sendForm.type">
            <el-radio label="all">推送給所有用戶</el-radio>
            <el-radio label="specific">推送給指定用戶</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item v-if="sendForm.type === 'specific'" label="用戶ID">
          <el-input
            v-model="sendForm.userIdsInput"
            placeholder="輸入用戶ID，用逗號分隔，例如: 1,2,3"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="sendDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmSend" :loading="submitting">確認推送</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Plus, Edit, Delete, Search, Promotion, MagicStick } from '@element-plus/icons-vue';
import notificationService from '../services/notificationService';

// 列表數據
const notifications = ref([]);
const loading = ref(false);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const searchQuery = ref('');
const filterType = ref('');

// 快速創建
const quickCreateDialogVisible = ref(false);
const quickCreateForm = reactive({
  sourceType: 'EVENT',
  sourceId: null,
  eventCategory: 'all',
  pushType: 'all',
  userIdsInput: ''
});
const sources = ref([]);
const sourcesLoading = ref(false);
const selectedSource = ref(null);

// 對話框
const dialogVisible = ref(false);
const sendDialogVisible = ref(false);
const isEdit = ref(false);
const submitting = ref(false);

// 表單
const form = reactive({
  type: 'SYSTEM',
  title: '',
  content: '',
  relatedType: '',
  relatedId: '',
  isActive: true
});

const sendForm = reactive({
  notificationId: null,
  type: 'all',
  userIdsInput: ''
});

const rules = {
  type: [{ required: true, message: '請選擇類型', trigger: 'change' }],
  title: [{ required: true, message: '請輸入標題', trigger: 'blur' }],
  content: [{ required: true, message: '請輸入內容', trigger: 'blur' }]
};

// 快速創建預覽
const quickPreviewTitle = computed(() => {
  if (!selectedSource.value) return '';
  
  if (quickCreateForm.sourceType === 'EVENT') {
    return selectedSource.value.name || '';
  } else {
    return `新片上映：${selectedSource.value.title || ''}`;
  }
});

const quickPreviewContent = computed(() => {
  if (!selectedSource.value) return '';
  
  if (quickCreateForm.sourceType === 'EVENT') {
    return selectedSource.value.description || '';
  } else {
    const title = selectedSource.value.title || '';
    const desc = selectedSource.value.description || '';
    return `${title} 即將上映！${desc ? ' ' + desc : ''}`;
  }
});

// 獲取列表
const fetchList = async () => {
  loading.value = true;
  try {
    const data = await notificationService.getNotifications(
      currentPage.value,
      pageSize.value,
      searchQuery.value,
      filterType.value
    );
    
    console.log('獲取到的數據:', data);
    
    // 確保 notifications 是陣列
    if (data && Array.isArray(data.notifications)) {
      notifications.value = data.notifications;
      total.value = data.totalItems || 0;
    } else {
      console.error('返回的數據格式不正確:', data);
      notifications.value = [];
      total.value = 0;
      ElMessage.warning('數據格式不正確');
    }
  } catch (error) {
    console.error('載入通知失敗:', error);
    ElMessage.error('載入通知失敗: ' + (error.message || '未知錯誤'));
    notifications.value = [];
    total.value = 0;
  } finally {
    loading.value = false;
  }
};

// 打開快速創建對話框
const openQuickCreateDialog = () => {
  quickCreateDialogVisible.value = true;
  loadSources();
};

// 載入來源列表
const loadSources = async () => {
  sourcesLoading.value = true;
  sources.value = [];
  selectedSource.value = null;
  quickCreateForm.sourceId = null;
  
  try {
    if (quickCreateForm.sourceType === 'EVENT') {
      const category = quickCreateForm.eventCategory === 'all' ? '' : quickCreateForm.eventCategory;
      sources.value = await notificationService.getEventSources(category);
    } else {
      sources.value = await notificationService.getMovieSources(true);
    }
    
    console.log('載入的來源:', sources.value);
  } catch (error) {
    console.error('載入來源失敗:', error);
    ElMessage.error('載入列表失敗: ' + (error.message || '未知錯誤'));
  } finally {
    sourcesLoading.value = false;
  }
};

// 選擇來源時
const onSourceChange = () => {
  selectedSource.value = sources.value.find(s => s.id === quickCreateForm.sourceId);
};

// 確認快速創建
const confirmQuickCreate = async () => {
  if (!quickCreateForm.sourceId) {
    ElMessage.warning('請選擇一個項目');
    return;
  }

  submitting.value = true;
  try {
    const data = {
      sourceType: quickCreateForm.sourceType,
      sourceId: quickCreateForm.sourceId,
      pushType: quickCreateForm.pushType,
      userIds: quickCreateForm.pushType === 'specific' 
        ? quickCreateForm.userIdsInput.split(',').map(id => parseInt(id.trim())).filter(id => !isNaN(id))
        : null
    };

    console.log('快速創建請求數據:', data);

    await notificationService.quickCreate(data);
    ElMessage.success('通知創建並推送成功');
    quickCreateDialogVisible.value = false;
    fetchList();
  } catch (error) {
    console.error('快速創建失敗:', error);
    ElMessage.error('創建失敗: ' + (error.message || '未知錯誤'));
  } finally {
    submitting.value = false;
  }
};

// 重置快速創建表單
const resetQuickCreateForm = () => {
  Object.assign(quickCreateForm, {
    sourceType: 'EVENT',
    sourceId: null,
    eventCategory: 'all',
    pushType: 'all',
    userIdsInput: ''
  });
  sources.value = [];
  selectedSource.value = null;
};

// 打開新增對話框
const openCreateDialog = () => {
  isEdit.value = false;
  dialogVisible.value = true;
};

// 打開編輯對話框
const openEditDialog = (row) => {
  isEdit.value = true;
  Object.assign(form, row);
  dialogVisible.value = true;
};

// 提交表單
const handleSubmit = async () => {
  submitting.value = true;
  try {
    if (isEdit.value) {
      await notificationService.updateNotification(form.id, form);
      ElMessage.success('更新成功');
    } else {
      await notificationService.createNotification(form);
      ElMessage.success('創建成功');
    }
    dialogVisible.value = false;
    fetchList();
  } catch (error) {
    console.error('操作失敗:', error);
    ElMessage.error('操作失敗: ' + (error.message || '未知錯誤'));
  } finally {
    submitting.value = false;
  }
};

// 刪除
const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('確定要刪除這個通知嗎？', '警告', {
      confirmButtonText: '確定',
      cancelButtonText: '取消',
      type: 'warning'
    });
    
    await notificationService.deleteNotification(id);
    ElMessage.success('刪除成功');
    fetchList();
  } catch (error) {
    if (error !== 'cancel') {
      console.error('刪除失敗:', error);
      ElMessage.error('刪除失敗: ' + (error.message || '未知錯誤'));
    }
  }
};

// 打開推送對話框
const openSendDialog = (row) => {
  sendForm.notificationId = row.id;
  sendForm.type = 'all';
  sendForm.userIdsInput = '';
  sendDialogVisible.value = true;
};

// 確認推送
const confirmSend = async () => {
  submitting.value = true;
  try {
    const data = {
      notificationId: sendForm.notificationId,
      type: sendForm.type,
      userIds: sendForm.type === 'specific'
        ? sendForm.userIdsInput.split(',').map(id => parseInt(id.trim())).filter(id => !isNaN(id))
        : null
    };

    await notificationService.sendNotification(data);
    ElMessage.success('推送成功');
    sendDialogVisible.value = false;
  } catch (error) {
    console.error('推送失敗:', error);
    ElMessage.error('推送失敗: ' + (error.message || '未知錯誤'));
  } finally {
    submitting.value = false;
  }
};

// 重置表單
const resetForm = () => {
  Object.assign(form, {
    type: 'SYSTEM',
    title: '',
    content: '',
    relatedType: '',
    relatedId: '',
    isActive: true
  });
};

// 工具函數
const getTypeLabel = (type) => {
  const labels = {
    SYSTEM: '系統',
    PROMOTION: '優惠',
    MOVIE: '電影',
    ORDER: '訂單'
  };
  return labels[type] || type;
};

const getTypeTagType = (type) => {
  const types = {
    SYSTEM: 'info',
    PROMOTION: 'success',
    MOVIE: 'warning',
    ORDER: 'primary'
  };
  return types[type] || 'info';
};

const formatDateTime = (dateTime) => {
  if (!dateTime) return '';
  return new Date(dateTime).toLocaleString('zh-TW');
};

// 初始化
onMounted(() => {
  fetchList();
});
</script>

<style scoped>
.notifications-container {
  padding: 20px;
}

.header-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions h2 {
  margin: 0;
}

.button-group {
  display: flex;
  gap: 10px;
}

.filter-section {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}
</style>