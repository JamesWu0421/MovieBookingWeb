<template>
  <div>
    <el-row :gutter="20" style="margin-bottom:16px;">
      <el-col :span="18">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="開始日期"
          end-placeholder="結束日期"
          @change="fetchData"
        ></el-date-picker>
      </el-col>
      <el-col :span="6" style="text-align:right;">
        <el-button @click="fetchData">重新整理</el-button>
        <el-button type="primary" style="margin-left:8px;" @click="exportCSV">匯出 CSV</el-button>
        <el-button type="success" style="margin-left:8px;" @click="exportExcel">匯出 Excel</el-button>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="4" v-for="card in cards" :key="card.title">
        <el-card shadow="hover">
          <h4>{{ card.title }}</h4>
          <p class="value">{{ card.value }}</p>
          <small>{{ card.desc }}</small>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top:18px;">
      <el-col :span="16">
        <el-card>
          <h4>銷售趨勢</h4>
          <div ref="salesChart" style="height:340px;"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <h4>電影類型分布</h4>
          <div ref="genreChart" style="height:340px;"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import * as echarts from 'echarts'

const dateRange = ref([null, null])
const cards = ref([])

const salesChart = ref(null)
const genreChart = ref(null)
let salesChartInstance = null
let genreChartInstance = null

function mockFetch(start, end) {
  // 回傳假資料，實際用後端 API
  return {
    stats: [
      { title: '今日來客', value: Math.floor(Math.random()*600)+100, desc: '比昨日' },
      { title: '今日訂單', value: Math.floor(Math.random()*200)+20, desc: '比昨日' },
      { title: '今日營收', value: '$' + (Math.floor(Math.random()*20000)+2000), desc: '比上週' },
      { title: '今日上映電影', value: Math.floor(Math.random()*30)+1, desc: '本週上新' },
      { title: '平均上座率', value: Math.floor(Math.random()*50)+50 + '%', desc: '過去30天平均' }
    ],
    salesTrend: {
      months: ['6月','7月','8月','9月','10月','11月'],
      boxOffice: [5000,7000,6500,8000,9000,11000],
      orders: [50,70,65,80,95,120]
    },
    genreDist: [
      { value: 45, name: '動作' },
      { value: 20, name: '愛情' },
      { value: 15, name: '喜劇' },
      { value: 10, name: '恐怖' },
      { value: 10, name: '科幻' }
    ]
  }
}

function renderCharts(data) {
  if (!salesChartInstance) salesChartInstance = echarts.init(salesChart.value)
  if (!genreChartInstance) genreChartInstance = echarts.init(genreChart.value)

  salesChartInstance.setOption({
    tooltip: {},
    legend: { bottom: 0 },
    xAxis: { type: 'category', data: data.salesTrend.months },
    yAxis: { type: 'value' },
    series: [
      { name: '票房', data: data.salesTrend.boxOffice, type: 'line', smooth: true },
      { name: '訂單數', data: data.salesTrend.orders, type: 'line', smooth: true }
    ]
  })

  genreChartInstance.setOption({
    tooltip: {},
    legend: { bottom: 0 },
    series: [
      { type: 'pie', radius: '60%', data: data.genreDist }
    ]
  })
}

function fetchData() {
  // 範例：使用 dateRange 做查詢參數
  const [start, end] = dateRange.value || []
  const data = mockFetch(start, end)
  cards.value = data.stats
  renderCharts(data)
}

onMounted(() => {
  fetchData()
  window.addEventListener('resize', () => {
    if (salesChartInstance) salesChartInstance.resize()
    if (genreChartInstance) genreChartInstance.resize()
  })
})

function exportCSV() {
  // Export current cards + salesTrend to CSV
  const headers = ['指標','數值','備註']
  const rows = cards.value.map(c => [c.title, c.value, c.desc])
  let csv = headers.join(',') + '\n'
  rows.forEach(r => { csv += r.join(',') + '\n' })
  // add sales trend
  csv += '\n月份,' + (cards.value.length ? '' : '') + '\n'
  const sales = mockFetch().salesTrend
  csv += '月份,' + sales.months.join(',') + '\n'
  csv += '票房,' + sales.boxOffice.join(',') + '\n'
  csv += '訂單數,' + sales.orders.join(',') + '\n'

  const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' })
  const link = document.createElement('a')
  const url = URL.createObjectURL(blob)
  link.setAttribute('href', url)
  link.setAttribute('download', 'report.csv')
  link.click()
  URL.revokeObjectURL(url)
}

async function exportExcel() {
  // use xlsx to generate a proper excel file
  const XLSX = (await import('xlsx')).default
  const wb = XLSX.utils.book_new()
  // Sheet 1: 指標
  const statRows = cards.value.map(c => ({ 指標: c.title, 數值: c.value, 備註: c.desc }))
  const ws1 = XLSX.utils.json_to_sheet(statRows)
  XLSX.utils.book_append_sheet(wb, ws1, '指標')

  // Sheet 2: 票房趨勢
  const sales = mockFetch().salesTrend
  const ws2 = XLSX.utils.aoa_to_sheet([['月份', ...sales.months], ['票房', ...sales.boxOffice], ['訂單數', ...sales.orders]])
  XLSX.utils.book_append_sheet(wb, ws2, '趨勢')

  const wbout = XLSX.write(wb, { bookType: 'xlsx', type: 'array' })
  const blob = new Blob([wbout], { type: 'application/octet-stream' })
  const link = document.createElement('a')
  const url = URL.createObjectURL(blob)
  link.href = url
  link.download = 'report.xlsx'
  link.click()
  URL.revokeObjectURL(url)
}

</script>

<style scoped>
.value {
  font-size: 20px;
  font-weight: 700;
  margin: 8px 0;
}
</style>
