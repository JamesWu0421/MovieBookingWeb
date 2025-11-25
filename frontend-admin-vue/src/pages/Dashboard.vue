<template>
  <div class="dashboard-container">
    <!-- 頁面標題 -->
    <div class="page-header">
      <div class="header-left">
        <h1 class="page-title">📊 營運儀表板</h1>
        <el-tag type="info" size="large">{{ currentDateTime }}</el-tag>
      </div>
      <div class="header-right">
        <el-button type="primary" :icon="Refresh" @click="refreshData" :loading="loading">
          重新整理
        </el-button>
      </div>
    </div>

    <!-- 今日關鍵指標 -->
    <div class="section-title">
      <el-icon><TrendCharts /></el-icon>
      <span>今日關鍵指標</span>
    </div>
    <el-row :gutter="20" class="kpi-row">
      <el-col :xs="24" :sm="12" :md="8" :lg="4" v-for="card in todayStats" :key="card.title">
        <div class="kpi-card" :style="{ borderLeft: `4px solid ${card.color}` }">
          <div class="kpi-icon" :style="{ background: card.color }">
            <component :is="card.icon"></component>
          </div>
          <div class="kpi-content">
            <div class="kpi-title">{{ card.title }}</div>
            <div class="kpi-value">{{ card.value }}</div>
            <div class="kpi-desc" :class="card.trend">
              <el-icon v-if="card.trend === 'up'"><CaretTop /></el-icon>
              <el-icon v-if="card.trend === 'down'"><CaretBottom /></el-icon>
              {{ card.desc }}
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 本週趨勢 & 待處理事項 -->
    <el-row :gutter="20" class="content-row">
      <el-col :xs="24" :lg="16">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>📈 本週營收趨勢</span>
              <el-radio-group v-model="trendType" size="small">
                <el-radio-button label="revenue">營收</el-radio-button>
                <el-radio-button label="orders">訂單數</el-radio-button>
                <el-radio-button label="customers">來客數</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div ref="weekTrendChart" class="chart-container"></div>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="8">
        <el-card class="todo-card">
          <template #header>
            <div class="card-header">
              <span>⚠️ 待處理事項</span>
              <el-badge :value="pendingCount" class="badge" type="danger" />
            </div>
          </template>
          <div class="todo-list">
            <div class="todo-item" v-for="item in todoItems" :key="item.id" @click="handleTodoClick(item)">
              <div class="todo-icon" :style="{ background: item.color }">
                <el-icon><component :is="item.icon" /></el-icon>
              </div>
              <div class="todo-content">
                <div class="todo-title">{{ item.title }}</div>
                <div class="todo-desc">{{ item.desc }}</div>
              </div>
              <el-badge :value="item.count" v-if="item.count > 0" />
            </div>
            <el-empty v-if="todoItems.length === 0" description="沒有待處理事項" :image-size="80" />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 熱門電影 & 影廳分布 -->
    <el-row :gutter="20" class="content-row">
      <el-col :xs="24" :lg="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>🎬 熱門電影 TOP 5</span>
            </div>
          </template>
          <div ref="topMoviesChart" class="chart-container-small"></div>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>🎥 影廳類型分布</span>
            </div>
          </template>
          <div ref="screenTypeChart" class="chart-container-small"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 快捷操作 -->
    <div class="section-title">
      <el-icon><Operation /></el-icon>
      <span>快捷操作</span>
    </div>
    <el-row :gutter="20" class="shortcut-row">
      <el-col :xs="12" :sm="8" :md="6" :lg="4" v-for="shortcut in shortcuts" :key="shortcut.title">
        <div class="shortcut-card" @click="handleShortcut(shortcut.path)">
          <div class="shortcut-icon" :style="{ background: shortcut.color }">
            <el-icon><component :is="shortcut.icon" /></el-icon>
          </div>
          <div class="shortcut-title">{{ shortcut.title }}</div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  Refresh, TrendCharts, Money, Tickets, User, Film, 
  CaretTop, CaretBottom, Warning, ShoppingCart, Bell,
  Operation, Plus, Document, Setting, DataAnalysis
} from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import api from '../services/api'

const router = useRouter()
const loading = ref(false)
const currentDateTime = ref('')
const trendType = ref('revenue')

// 今日統計數據
const todayStats = ref([
  { 
    title: '今日營收', 
    value: '$0', 
    desc: '比昨日', 
    trend: 'up',
    color: '#667eea',
    icon: Money
  },
  { 
    title: '今日訂單', 
    value: '0', 
    desc: '比昨日', 
    trend: 'up',
    color: '#f093fb',
    icon: ShoppingCart
  },
  { 
    title: '今日來客', 
    value: '0', 
    desc: '比昨日', 
    trend: 'down',
    color: '#4facfe',
    icon: User
  },
  { 
    title: '今日場次', 
    value: '0', 
    desc: '已上映', 
    trend: '',
    color: '#43e97b',
    icon: Film
  },
  { 
    title: '平均票價', 
    value: '$0', 
    desc: '本週平均', 
    trend: '',
    color: '#fa709a',
    icon: Tickets
  }
])

// 待處理事項
const todoItems = ref([])
const pendingCount = ref(0)

// 快捷操作
const shortcuts = [
  { title: '新增訂單', icon: Plus, color: '#667eea', path: '/orders' },
  { title: '排片管理', icon: Film, color: '#f093fb', path: '/showtimes' },
  { title: '會員管理', icon: User, color: '#4facfe', path: '/members' },
  { title: '電影管理', icon: DataAnalysis, color: '#43e97b', path: '/movies' },
  { title: '查看報表', icon: Document, color: '#fa709a', path: '/reports' },
  { title: '系統設定', icon: Setting, color: '#764ba2', path: '/security' }
]

// ECharts 實例
const weekTrendChart = ref(null)
const topMoviesChart = ref(null)
const screenTypeChart = ref(null)

let weekTrendChartInstance = null
let topMoviesChartInstance = null
let screenTypeChartInstance = null

// 更新時間
const updateTime = () => {
  const now = new Date()
  const options = {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    weekday: 'long',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
    hour12: false
  }
  currentDateTime.value = now.toLocaleString('zh-TW', options)
}

// 定時器
let timeInterval = null

onMounted(() => {
  updateTime()
  timeInterval = setInterval(updateTime, 1000)
  
  fetchDashboardData()
  
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  if (timeInterval) clearInterval(timeInterval)
  window.removeEventListener('resize', handleResize)
  
  if (weekTrendChartInstance) weekTrendChartInstance.dispose()
  if (topMoviesChartInstance) topMoviesChartInstance.dispose()
  if (screenTypeChartInstance) screenTypeChartInstance.dispose()
})

// 處理視窗大小變化
const handleResize = () => {
  if (weekTrendChartInstance) weekTrendChartInstance.resize()
  if (topMoviesChartInstance) topMoviesChartInstance.resize()
  if (screenTypeChartInstance) screenTypeChartInstance.resize()
}

// 獲取儀表板數據
const fetchDashboardData = async () => {
  loading.value = true
  try {
    // 使用模擬數據（實際應該調用後端 API）
    const mockData = generateMockData()
    
    // 更新今日統計
    updateTodayStats(mockData.todayStats)
    
    // 更新待處理事項
    updateTodoItems(mockData.todoItems)
    
    // 繪製圖表
    setTimeout(() => {
      renderWeekTrendChart(mockData.weekTrend)
      renderTopMoviesChart(mockData.topMovies)
      renderScreenTypeChart(mockData.screenTypes)
    }, 100)
    
  } catch (error) {
    console.error('獲取數據失敗:', error)
    ElMessage.error('數據載入失敗')
  } finally {
    loading.value = false
  }
}

// 生成模擬數據
const generateMockData = () => {
  return {
    todayStats: {
      revenue: { value: 147400, change: '+15.2%', trend: 'up' },
      orders: { value: 218, change: '+8.5%', trend: 'up' },
      customers: { value: 445, change: '-3.2%', trend: 'down' },
      showtimes: { value: 45, change: '', trend: '' },
      avgPrice: { value: 676, change: '', trend: '' }
    },
    weekTrend: {
      dates: ['週一', '週二', '週三', '週四', '週五', '週六', '週日'],
      revenue: [85000, 92000, 78000, 105000, 147400, 165000, 158000],
      orders: [125, 138, 115, 156, 218, 245, 232],
      customers: [280, 305, 265, 350, 445, 520, 495]
    },
    topMovies: [
      { name: '復仇者聯盟', value: 45000 },
      { name: '玩命關頭10', value: 38000 },
      { name: '蜘蛛人', value: 32000 },
      { name: '星際大戰', value: 25000 },
      { name: '哈利波特', value: 18000 }
    ],
    screenTypes: [
      { name: 'IMAX', value: 45000 },
      { name: '4DX', value: 32000 },
      { name: 'VIP廳', value: 28000 },
      { name: '一般廳', value: 42400 }
    ],
    todoItems: [
      { id: 1, title: '待審核訂單', desc: '有新的訂單需要確認', count: 3, icon: ShoppingCart, color: '#f093fb' },
      { id: 2, title: '場次衝突', desc: '2個場次時間重疊', count: 2, icon: Warning, color: '#fa709a' },
      { id: 3, title: '系統通知', desc: '有新的系統公告', count: 1, icon: Bell, color: '#4facfe' }
    ]
  }
}

// 更新今日統計
const updateTodayStats = (stats) => {
  todayStats.value[0].value = `$${stats.revenue.value.toLocaleString()}`
  todayStats.value[0].desc = `${stats.revenue.change} 比昨日`
  todayStats.value[0].trend = stats.revenue.trend
  
  todayStats.value[1].value = stats.orders.value.toString()
  todayStats.value[1].desc = `${stats.orders.change} 比昨日`
  todayStats.value[1].trend = stats.orders.trend
  
  todayStats.value[2].value = stats.customers.value.toString()
  todayStats.value[2].desc = `${stats.customers.change} 比昨日`
  todayStats.value[2].trend = stats.customers.trend
  
  todayStats.value[3].value = stats.showtimes.value.toString()
  todayStats.value[4].value = `$${stats.avgPrice.value}`
}

// 更新待處理事項
const updateTodoItems = (items) => {
  todoItems.value = items
  pendingCount.value = items.reduce((sum, item) => sum + item.count, 0)
}

// 繪製本週趨勢圖
const renderWeekTrendChart = (data) => {
  if (!weekTrendChart.value) return
  
  if (!weekTrendChartInstance) {
    weekTrendChartInstance = echarts.init(weekTrendChart.value)
  }
  
  const seriesData = {
    revenue: { name: '營收 ($)', data: data.revenue, color: '#667eea' },
    orders: { name: '訂單數', data: data.orders, color: '#f093fb' },
    customers: { name: '來客數', data: data.customers, color: '#4facfe' }
  }
  
  const currentSeries = seriesData[trendType.value]
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: data.dates,
      axisTick: { alignWithLabel: true }
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: currentSeries.name,
        type: 'bar',
        data: currentSeries.data,
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: currentSeries.color },
            { offset: 1, color: currentSeries.color + '80' }
          ]),
          borderRadius: [8, 8, 0, 0]
        },
        emphasis: {
          itemStyle: {
            color: currentSeries.color
          }
        }
      }
    ]
  }
  
  weekTrendChartInstance.setOption(option)
}

// 繪製熱門電影圖表
const renderTopMoviesChart = (data) => {
  if (!topMoviesChart.value) return
  
  if (!topMoviesChartInstance) {
    topMoviesChartInstance = echarts.init(topMoviesChart.value)
  }
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'value'
    },
    yAxis: {
      type: 'category',
      data: data.map(item => item.name)
    },
    series: [
      {
        name: '營收',
        type: 'bar',
        data: data.map(item => item.value),
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
            { offset: 0, color: '#667eea' },
            { offset: 1, color: '#764ba2' }
          ]),
          borderRadius: [0, 8, 8, 0]
        }
      }
    ]
  }
  
  topMoviesChartInstance.setOption(option)
}

// 繪製影廳類型圖表
const renderScreenTypeChart = (data) => {
  if (!screenTypeChart.value) return
  
  if (!screenTypeChartInstance) {
    screenTypeChartInstance = echarts.init(screenTypeChart.value)
  }
  
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: ${c} ({d}%)'
    },
    legend: {
      bottom: '5%',
      left: 'center'
    },
    series: [
      {
        name: '影廳類型',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 20,
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: data
      }
    ]
  }
  
  screenTypeChartInstance.setOption(option)
}

// 重新整理數據
const refreshData = () => {
  fetchDashboardData()
  ElMessage.success('數據已更新')
}

// 處理待辦事項點擊
const handleTodoClick = (item) => {
  ElMessage.info(`查看：${item.title}`)
  // 這裡可以根據不同類型跳轉到對應頁面
}

// 處理快捷操作點擊
const handleShortcut = (path) => {
  router.push(path)
}
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
  background: #f5f7fa;
  min-height: calc(100vh - 60px);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.page-title {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin: 24px 0 16px;
}

.kpi-row {
  margin-bottom: 24px;
}

.kpi-card {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  display: flex;
  align-items: center;
  gap: 16px;
  cursor: pointer;
  transition: all 0.3s;
  height: 100%;
}

.kpi-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.kpi-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: white;
  flex-shrink: 0;
}

.kpi-content {
  flex: 1;
  min-width: 0;
}

.kpi-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.kpi-value {
  font-size: 26px;
  font-weight: 700;
  color: #303133;
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.kpi-desc {
  font-size: 12px;
  color: #909399;
  display: flex;
  align-items: center;
  gap: 4px;
}

.kpi-desc.up {
  color: #67c23a;
}

.kpi-desc.down {
  color: #f56c6c;
}

.content-row {
  margin-bottom: 24px;
}

.chart-card {
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  font-size: 16px;
}

.chart-container {
  width: 100%;
  height: 320px;
}

.chart-container-small {
  width: 100%;
  height: 300px;
}

.todo-card {
  height: 100%;
}

.todo-list {
  max-height: 320px;
  overflow-y: auto;
}

.todo-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.3s;
  margin-bottom: 8px;
}

.todo-item:hover {
  background: #f5f7fa;
}

.todo-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: white;
  flex-shrink: 0;
}

.todo-content {
  flex: 1;
  min-width: 0;
}

.todo-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.todo-desc {
  font-size: 12px;
  color: #909399;
}

.shortcut-row {
  margin-bottom: 24px;
}

.shortcut-card {
  background: white;
  padding: 24px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
}

.shortcut-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.shortcut-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: white;
  margin: 0 auto 12px;
}

.shortcut-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    gap: 12px;
  }
  
  .header-left {
    flex-direction: column;
    width: 100%;
  }
  
  .header-right {
    width: 100%;
  }
  
  .kpi-card {
    margin-bottom: 12px;
  }
  
  .chart-container {
    height: 280px;
  }
  
  .chart-container-small {
    height: 260px;
  }
}
</style>