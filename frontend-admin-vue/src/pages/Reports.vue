<template>
  <div class="reports-container">
    <el-card class="header-card">
      <h2>營收報表</h2>
    </el-card>

    <!-- 日期選擇區 -->
    <el-card class="filter-card" style="margin-top: 20px;">
      <el-row :gutter="20">
        <el-col :span="8">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="開始日期"
            end-placeholder="結束日期"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-col>
        <el-col :span="4">
          <el-button type="primary" @click="fetchReports">查詢</el-button>
          <el-button @click="exportReport">匯出</el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 儀表板統計卡片 -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-title">今日營收</div>
          <div class="stat-value">$ {{ dashboardStats.todayRevenue || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-title">本月營收</div>
          <div class="stat-value">$ {{ dashboardStats.monthRevenue || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-title">今日訂單數</div>
          <div class="stat-value">{{ dashboardStats.todayOrders || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-title">總會員數</div>
          <div class="stat-value">{{ dashboardStats.totalUsers || 0 }}</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 營收趨勢圖 -->
    <el-card style="margin-top: 20px;">
      <h3>營收趨勢</h3>
      <div ref="salesChartRef" style="width: 100%; height: 400px;"></div>
    </el-card>

    <!-- 電影排行榜 -->
    <el-card style="margin-top: 20px;">
      <h3>電影票房排行 TOP 10</h3>
      <el-table :data="movieRanking" style="width: 100%">
        <el-table-column prop="rank" label="排名" width="80" align="center"/>
        <el-table-column prop="movieTitle" label="電影名稱"/>
        <el-table-column prop="totalRevenue" label="總營收" width="150" align="right">
          <template #default="{ row }">
            $ {{ formatNumber(row.totalRevenue) }}
          </template>
        </el-table-column>
        <el-table-column prop="totalTickets" label="售票數" width="120" align="right"/>
        <el-table-column prop="avgTicketPrice" label="平均票價" width="120" align="right">
          <template #default="{ row }">
            $ {{ formatNumber(row.avgTicketPrice) }}
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 用戶消費排行 -->
    <el-card style="margin-top: 20px;">
      <h3>高消費會員 TOP 20</h3>
      <el-table :data="userConsumption" style="width: 100%">
        <el-table-column prop="rank" label="排名" width="80" align="center"/>
        <el-table-column prop="username" label="會員名稱"/>
        <el-table-column prop="email" label="Email"/>
        <el-table-column prop="totalSpent" label="總消費金額" width="150" align="right">
          <template #default="{ row }">
            $ {{ formatNumber(row.totalSpent) }}
          </template>
        </el-table-column>
        <el-table-column prop="orderCount" label="訂單數" width="100" align="right"/>
        <el-table-column prop="avgOrderValue" label="平均訂單金額" width="150" align="right">
          <template #default="{ row }">
            $ {{ formatNumber(row.avgOrderValue) }}
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 影廳使用率 -->
    <el-card style="margin-top: 20px;">
      <h3>影廳使用率</h3>
      <div ref="screenChartRef" style="width: 100%; height: 400px;"></div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import axios from 'axios'

const API_BASE = 'http://localhost:8080/api/reports'

// 資料狀態
const dateRange = ref([])
const dashboardStats = ref({})
const salesData = ref([])
const movieRanking = ref([])
const userConsumption = ref([])
const screenUsage = ref([])

// 圖表引用
const salesChartRef = ref(null)
const screenChartRef = ref(null)
let salesChart = null
let screenChart = null

// 初始化日期範圍為本月
const initDateRange = () => {
  const now = new Date()
  const year = now.getFullYear()
  const month = String(now.getMonth() + 1).padStart(2, '0')
  const startDate = `${year}-${month}-01`
  const endDate = now.toISOString().split('T')[0]
  dateRange.value = [startDate, endDate]
}

// 格式化數字
const formatNumber = (num) => {
  if (!num) return '0'
  return Number(num).toLocaleString('zh-TW', { minimumFractionDigits: 0, maximumFractionDigits: 2 })
}

// 載入儀表板統計
const fetchDashboardStats = async () => {
  try {
    const res = await axios.get(`${API_BASE}/dashboard`)
    if (res.data) {
      dashboardStats.value = res.data
    }
  } catch (error) {
    console.error('Failed to fetch dashboard stats:', error)
  }
}

// 載入營收報表
const fetchSalesReport = async () => {
  if (!dateRange.value || dateRange.value.length !== 2) {
    ElMessage.warning('請選擇日期範圍')
    return
  }

  try {
    const [startDate, endDate] = dateRange.value
    const res = await axios.get(`${API_BASE}/sales`, {
      params: { startDate, endDate }
    })
    if (res.data) {
      salesData.value = res.data
      await nextTick()
      renderSalesChart()
    }
  } catch (error) {
    console.error('Failed to fetch sales report:', error)
    ElMessage.error('載入營收報表失敗')
  }
}

// 載入電影排行
const fetchMovieRanking = async () => {
  if (!dateRange.value || dateRange.value.length !== 2) return

  try {
    const [startDate, endDate] = dateRange.value
    const res = await axios.get(`${API_BASE}/movies/ranking`, {
      params: { startDate, endDate, limit: 10 }
    })
    if (res.data) {
      movieRanking.value = res.data.map((item, index) => ({
        ...item,
        rank: index + 1
      }))
    }
  } catch (error) {
    console.error('Failed to fetch movie ranking:', error)
  }
}

// 載入用戶消費排行
const fetchUserConsumption = async () => {
  try {
    const res = await axios.get(`${API_BASE}/users/consumption`, {
      params: { limit: 20 }
    })
    if (res.data) {
      userConsumption.value = res.data.map((item, index) => ({
        ...item,
        rank: index + 1
      }))
    }
  } catch (error) {
    console.error('Failed to fetch user consumption:', error)
  }
}

// 載入影廳使用率
const fetchScreenUsage = async () => {
  if (!dateRange.value || dateRange.value.length !== 2) return

  try {
    const [startDate, endDate] = dateRange.value
    const res = await axios.get(`${API_BASE}/screens/usage`, {
      params: { startDate, endDate }
    })
    if (res.data) {
      screenUsage.value = res.data
      await nextTick()
      renderScreenChart()
    }
  } catch (error) {
    console.error('Failed to fetch screen usage:', error)
  }
}

// 渲染營收趨勢圖
const renderSalesChart = () => {
  if (!salesChartRef.value) return

  if (!salesChart) {
    salesChart = echarts.init(salesChartRef.value)
  }

  const option = {
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['營收', '訂單數']
    },
    xAxis: {
      type: 'category',
      data: salesData.value.map(item => item.period)
    },
    yAxis: [
      {
        type: 'value',
        name: '營收 (元)',
        position: 'left'
      },
      {
        type: 'value',
        name: '訂單數',
        position: 'right'
      }
    ],
    series: [
      {
        name: '營收',
        type: 'line',
        data: salesData.value.map(item => item.totalRevenue),
        smooth: true,
        areaStyle: {
          opacity: 0.3
        }
      },
      {
        name: '訂單數',
        type: 'bar',
        yAxisIndex: 1,
        data: salesData.value.map(item => item.totalOrders)
      }
    ]
  }

  salesChart.setOption(option)
}

// 渲染影廳使用率圖
const renderScreenChart = () => {
  if (!screenChartRef.value) return

  if (!screenChart) {
    screenChart = echarts.init(screenChartRef.value)
  }

  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    legend: {
      data: ['使用率 (%)', '營收']
    },
    xAxis: {
      type: 'category',
      data: screenUsage.value.map(item => item.screenName || `影廳 ${item.screenId}`)
    },
    yAxis: [
      {
        type: 'value',
        name: '使用率 (%)',
        max: 100
      },
      {
        type: 'value',
        name: '營收 (元)'
      }
    ],
    series: [
      {
        name: '使用率 (%)',
        type: 'bar',
        data: screenUsage.value.map(item => item.occupancyRate || 0)
      },
      {
        name: '營收',
        type: 'line',
        yAxisIndex: 1,
        data: screenUsage.value.map(item => item.totalRevenue || 0)
      }
    ]
  }

  screenChart.setOption(option)
}

// 查詢所有報表
const fetchReports = async () => {
  await Promise.all([
    fetchSalesReport(),
    fetchMovieRanking(),
    fetchScreenUsage()
  ])
}

// 匯出報表
const exportReport = async () => {
  if (!dateRange.value || dateRange.value.length !== 2) {
    ElMessage.warning('請選擇日期範圍')
    return
  }

  try {
    const [startDate, endDate] = dateRange.value
    const response = await axios.get(`${API_BASE}/export/sales`, {
      params: { startDate, endDate },
      responseType: 'blob'
    })

    const url = window.URL.createObjectURL(new Blob([response.data]))
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', `sales_report_${startDate}_${endDate}.csv`)
    document.body.appendChild(link)
    link.click()
    link.remove()

    ElMessage.success('匯出成功')
  } catch (error) {
    console.error('Failed to export report:', error)
    ElMessage.error('匯出失敗')
  }
}

// 監聽視窗大小變化
const handleResize = () => {
  salesChart?.resize()
  screenChart?.resize()
}

// 初始化
onMounted(async () => {
  initDateRange()
  await fetchDashboardStats()
  await fetchUserConsumption()
  await fetchReports()

  window.addEventListener('resize', handleResize)
})

// 清理
import { onUnmounted } from 'vue'
onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  salesChart?.dispose()
  screenChart?.dispose()
})
</script>

<style scoped>
.reports-container {
  padding: 20px;
}

.header-card h2 {
  margin: 0;
  color: #303133;
}

.stat-card {
  text-align: center;
  padding: 20px;
}

.stat-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 10px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #409EFF;
}

.filter-card {
  padding: 20px;
}
</style>
