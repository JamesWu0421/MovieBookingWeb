<template>
  <div class="reports-container">
    <el-card class="header-card">
      <div class="header-title">
        <h2>📊 營收報表</h2>
        <el-button-group>
          <el-button
            type="primary"
            @click="exportReport('csv')"
            :loading="exporting"
          >
            <el-icon><Download /></el-icon>
            匯出 CSV
          </el-button>
          <el-button
            type="success"
            @click="exportReport('excel')"
            :loading="exporting"
          >
            <el-icon><Document /></el-icon>
            匯出 Excel
          </el-button>
        </el-button-group>
      </div>
    </el-card>

    <!-- 篩選條件 -->
    <el-card class="filter-card">
      <el-form :model="filters" label-width="80px">
        <el-row :gutter="20">
          <!-- 日期快速選擇按鈕 -->
          <el-col :span="24" class="quick-date-buttons">
            <el-button-group>
              <el-button
                @click="setQuickDate('today')"
                :type="quickDateType === 'today' ? 'primary' : 'default'"
              >
                今天
              </el-button>
              <el-button
                @click="setQuickDate('month')"
                :type="quickDateType === 'month' ? 'primary' : 'default'"
              >
                本月
              </el-button>
              <el-button
                @click="setQuickDate('quarter')"
                :type="quickDateType === 'quarter' ? 'primary' : 'default'"
              >
                本季
              </el-button>
              <el-button
                @click="setQuickDate('year')"
                :type="quickDateType === 'year' ? 'primary' : 'default'"
              >
                本年
              </el-button>
            </el-button-group>
          </el-col>

          <!-- 日期區間 -->
          <el-col :span="12">
            <el-form-item label="日期區間">
              <el-date-picker
                v-model="dateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="開始日期"
                end-placeholder="結束日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                @change="onDateChange"
              />
            </el-form-item>
          </el-col>

          <!-- 電影多選 -->
          <el-col :span="12">
            <el-form-item label="電影">
              <el-select
                v-model="filters.movieIds"
                multiple
                collapse-tags
                collapse-tags-tooltip
                placeholder="全部電影"
                style="width: 100%"
              >
                <el-option
                  v-for="movie in movieOptions"
                  :key="movie.id"
                  :label="movie.title"
                  :value="movie.id"
                />
              </el-select>
            </el-form-item>
          </el-col>

          <!-- 影廳多選 -->
          <el-col :span="12">
            <el-form-item label="影廳">
              <el-select
                v-model="filters.screenIds"
                multiple
                collapse-tags
                collapse-tags-tooltip
                placeholder="全部影廳"
                style="width: 100%"
              >
                <el-option
                  v-for="screen in screenOptions"
                  :key="screen.id"
                  :label="`${screen.name} (${screen.screen_type || '一般廳'})`"
                  :value="screen.id"
                />
              </el-select>
            </el-form-item>
          </el-col>

          <!-- 票種多選 -->
          <el-col :span="12">
            <el-form-item label="票種">
              <el-select
                v-model="filters.ticketTypes"
                multiple
                collapse-tags
                collapse-tags-tooltip
                placeholder="全部票種"
                style="width: 100%"
              >
                <el-option label="成人票" value="adult" />
                <el-option label="學生票" value="student" />
                <el-option label="優待票" value="senior" />
                <el-option label="兒童票" value="child" />
              </el-select>
            </el-form-item>
          </el-col>

          <!-- 查詢按鈕 -->
          <el-col :span="24">
            <el-form-item>
              <el-button type="primary" @click="fetchReport" :loading="loading">
                <el-icon><Search /></el-icon>
                查詢
              </el-button>
              <el-button @click="resetFilters">
                <el-icon><Refresh /></el-icon>
                重置
              </el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-card>

    <!-- KPI 卡片區 -->
    <el-row :gutter="20" class="kpi-cards" v-if="reportData.overview">
      <el-col :span="24" :sm="12" :lg="5">
        <div class="kpi-card">
          <div
            class="kpi-icon"
            style="
              background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            "
          >
            <el-icon><Money /></el-icon>
          </div>
          <div class="kpi-content">
            <div class="kpi-title">總營收</div>
            <div class="kpi-value">
              $ {{ formatNumber(reportData.overview.totalRevenue) }}
            </div>
          </div>
        </div>
      </el-col>

      <el-col :span="24" :sm="12" :lg="5">
        <div class="kpi-card">
          <div
            class="kpi-icon"
            style="
              background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
            "
          >
            <el-icon><Tickets /></el-icon>
          </div>
          <div class="kpi-content">
            <div class="kpi-title">售出票數</div>
            <div class="kpi-value">
              {{ formatNumber(reportData.overview.totalTickets) }}
            </div>
          </div>
        </div>
      </el-col>

      <el-col :span="24" :sm="12" :lg="4">
        <div class="kpi-card">
          <div
            class="kpi-icon"
            style="
              background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
            "
          >
            <el-icon><Film /></el-icon>
          </div>
          <div class="kpi-content">
            <div class="kpi-title">場次數量</div>
            <div class="kpi-value">
              {{ formatNumber(reportData.overview.showtimesCount) }}
            </div>
          </div>
        </div>
      </el-col>

      <el-col :span="24" :sm="12" :lg="5">
        <div class="kpi-card">
          <div
            class="kpi-icon"
            style="
              background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
            "
          >
            <el-icon><PriceTag /></el-icon>
          </div>
          <div class="kpi-content">
            <div class="kpi-title">平均票價</div>
            <div class="kpi-value">
              $ {{ formatNumber(reportData.overview.averageTicketPrice) }}
            </div>
          </div>
        </div>
      </el-col>

      <el-col :span="24" :sm="12" :lg="5">
        <div class="kpi-card">
          <div
            class="kpi-icon"
            style="
              background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
            "
          >
            <el-icon><TrendCharts /></el-icon>
          </div>
          <div class="kpi-content">
            <div class="kpi-title">每日平均營收</div>
            <div class="kpi-value">
              $ {{ formatNumber(reportData.overview.dailyAvgRevenue) }}
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 圖表區域 -->
    <el-row :gutter="20" class="charts-row">
      <!-- 折線圖：營收趨勢 -->
      <el-col :span="24">
        <el-card class="chart-card">
          <template #header>
            <div class="chart-header">
              <span>📈 營收趨勢</span>
            </div>
          </template>
          <div ref="trendChart" class="chart-container"></div>
        </el-card>
      </el-col>

      <!-- 圓餅圖：影廳類型分布 -->
      <el-col :span="24" :md="8">
        <el-card class="chart-card">
          <template #header>
            <div class="chart-header">
              <span>🎬 影廳類型分布</span>
            </div>
          </template>
          <div ref="screenTypeChart" class="chart-container-small"></div>
        </el-card>
      </el-col>

      <!-- 圓餅圖：票種分布 -->
      <el-col :span="24" :md="8">
        <el-card class="chart-card">
          <template #header>
            <div class="chart-header">
              <span>🎫 票種分布</span>
            </div>
          </template>
          <div ref="ticketTypeChart" class="chart-container-small"></div>
        </el-card>
      </el-col>

      <!-- 圓餅圖：電影營收分布 -->
      <el-col :span="24" :md="8">
        <el-card class="chart-card">
          <template #header>
            <div class="chart-header">
              <span>🎞️ 電影營收分布</span>
            </div>
          </template>
          <div ref="movieDistChart" class="chart-container-small"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- Top 5 電影排行表 -->
    <el-card class="table-card">
      <template #header>
        <div class="chart-header">
          <span>🏆 Top 5 電影排名</span>
        </div>
      </template>
      <el-table :data="reportData.topMovies" style="width: 100%" stripe>
        <el-table-column prop="rank" label="排名" width="80" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.rank === 1" type="danger" effect="dark"
              >🥇 {{ row.rank }}</el-tag
            >
            <el-tag v-else-if="row.rank === 2" type="warning" effect="dark"
              >🥈 {{ row.rank }}</el-tag
            >
            <el-tag v-else-if="row.rank === 3" type="success" effect="dark"
              >🥉 {{ row.rank }}</el-tag
            >
            <span v-else>{{ row.rank }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="movieTitle" label="電影" min-width="200" />
        <el-table-column
          prop="totalRevenue"
          label="總營收"
          width="150"
          align="right"
        >
          <template #default="{ row }">
            $ {{ formatNumber(row.totalRevenue) }}
          </template>
        </el-table-column>
        <el-table-column
          prop="ticketsSold"
          label="售票數"
          width="120"
          align="right"
        >
          <template #default="{ row }">
            {{ formatNumber(row.ticketsSold) }}
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from "vue";
import { ElMessage } from "element-plus";
import {
  Download,
  Search,
  Refresh,
  Money,
  Tickets,
  Film,
  PriceTag,
  TrendCharts,
  Document,
} from "@element-plus/icons-vue";
import * as echarts from "echarts";
import reportService from "../services/reportService";
import api from "../services/api";

// 數據
const loading = ref(false);
const exporting = ref(false);
const dateRange = ref([]);
const quickDateType = ref("today"); // 🆕 預設為「今天」

const filters = reactive({
  movieIds: [],
  screenIds: [],
  ticketTypes: [],
});

const reportData = reactive({
  overview: null,
  trendData: [],
  screenTypeDistribution: [],
  ticketTypeDistribution: [],
  movieDistribution: [],
  topMovies: [],
});

const movieOptions = ref([]);
const screenOptions = ref([]);

// ECharts 實例
const trendChart = ref(null);
const screenTypeChart = ref(null);
const ticketTypeChart = ref(null);
const movieDistChart = ref(null);

let trendChartInstance = null;
let screenTypeChartInstance = null;
let ticketTypeChartInstance = null;
let movieDistChartInstance = null;

// 🆕 取得當天日期的函數
const getTodayDateRange = () => {
  const today = new Date();
  const formattedDate = formatDate(today);
  return [formattedDate, formattedDate];
};

// 初始化
onMounted(async () => {
  // 🆕 預設日期為當天 00:00 到 23:59
  dateRange.value = getTodayDateRange();

  // 暫時註解掉電影和影廳選項載入（因為後端沒有這兩個 API）
  // loadMovieOptions().catch(() => {
  //   console.log('電影選項載入失敗，將只顯示全部電影');
  // });
  // loadScreenOptions().catch(() => {
  //   console.log('影廳選項載入失敗，將只顯示全部影廳');
  // });

  // 不自動載入報表，等用戶點擊「查詢」
});

// 快速日期選擇
const setQuickDate = (type) => {
  quickDateType.value = type;
  const now = new Date();
  let startDate, endDate;

  if (type === "today") {
    // 🆕 今天
    startDate = now;
    endDate = now;
  } else if (type === "month") {
    // 本月
    startDate = new Date(now.getFullYear(), now.getMonth(), 1);
    endDate = now;
  } else if (type === "quarter") {
    // 本季
    const quarter = Math.floor(now.getMonth() / 3);
    startDate = new Date(now.getFullYear(), quarter * 3, 1);
    endDate = now;
  } else if (type === "year") {
    // 本年
    startDate = new Date(now.getFullYear(), 0, 1);
    endDate = now;
  }

  dateRange.value = [formatDate(startDate), formatDate(endDate)];
};

// 日期變更時清除快速選擇
const onDateChange = () => {
  quickDateType.value = "";
};

// 格式化日期
const formatDate = (date) => {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");
  return `${year}-${month}-${day}`;
};

// 格式化數字
const formatNumber = (value) => {
  if (!value) return "0";
  return Number(value).toLocaleString("zh-TW", { maximumFractionDigits: 2 });
};

// 載入電影選項
const loadMovieOptions = async () => {
  try {
    const response = await api.get("/movies");
    movieOptions.value = response.data;
  } catch (error) {
    console.error("載入電影選項失敗:", error);
  }
};

// 載入影廳選項
const loadScreenOptions = async () => {
  try {
    const response = await api.get("/screens");
    screenOptions.value = response.data;
  } catch (error) {
    console.error("載入影廳選項失敗:", error);
  }
};

// 查詢報表
const fetchReport = async () => {
  if (!dateRange.value || dateRange.value.length !== 2) {
    ElMessage.warning("請選擇日期區間");
    return;
  }

  loading.value = true;
  try {
    const params = {
      startDate: dateRange.value[0],
      endDate: dateRange.value[1],
      movieIds: filters.movieIds.length > 0 ? filters.movieIds : undefined,
      screenIds: filters.screenIds.length > 0 ? filters.screenIds : undefined,
      ticketTypes:
        filters.ticketTypes.length > 0 ? filters.ticketTypes : undefined,
    };

    const response = await reportService.getComprehensiveReport(params);

    // 更新數據
    Object.assign(reportData, response);

    // 等待 DOM 更新後繪製圖表
    await nextTick();
    renderCharts();

    ElMessage.success("報表載入成功");
  } catch (error) {
    console.error("載入報表失敗:", error);
    ElMessage.error("載入報表失敗");
  } finally {
    loading.value = false;
  }
};

// 重置篩選條件
const resetFilters = () => {
  filters.movieIds = [];
  filters.screenIds = [];
  filters.ticketTypes = [];
  setQuickDate("today"); // 🆕 重置為今天
};

// 繪製所有圖表
const renderCharts = () => {
  renderTrendChart();
  renderScreenTypeChart();
  renderTicketTypeChart();
  renderMovieDistChart();
};

// 繪製趨勢折線圖
const renderTrendChart = () => {
  if (!trendChart.value) return;

  if (!trendChartInstance) {
    trendChartInstance = echarts.init(trendChart.value);
  }

  const dates = reportData.trendData.map((item) => item.date);
  const revenues = reportData.trendData.map((item) => item.revenue);
  const tickets = reportData.trendData.map((item) => item.tickets);

  const option = {
    tooltip: {
      trigger: "axis",
      axisPointer: {
        type: "cross",
      },
    },
    legend: {
      data: ["營收", "售票數"],
    },
    grid: {
      left: "3%",
      right: "4%",
      bottom: "3%",
      containLabel: true,
    },
    xAxis: {
      type: "category",
      boundaryGap: false,
      data: dates,
    },
    yAxis: [
      {
        type: "value",
        name: "營收 ($)",
        position: "left",
      },
      {
        type: "value",
        name: "售票數",
        position: "right",
      },
    ],
    series: [
      {
        name: "營收",
        type: "line",
        smooth: true,
        data: revenues,
        yAxisIndex: 0,
        itemStyle: {
          color: "#667eea",
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: "rgba(102, 126, 234, 0.3)" },
            { offset: 1, color: "rgba(102, 126, 234, 0)" },
          ]),
        },
      },
      {
        name: "售票數",
        type: "line",
        smooth: true,
        data: tickets,
        yAxisIndex: 1,
        itemStyle: {
          color: "#f5576c",
        },
      },
    ],
  };

  trendChartInstance.setOption(option);
};

// 繪製影廳類型圓餅圖
const renderScreenTypeChart = () => {
  if (!screenTypeChart.value) return;

  if (!screenTypeChartInstance) {
    screenTypeChartInstance = echarts.init(screenTypeChart.value);
  }

  const data = reportData.screenTypeDistribution.map((item) => ({
    name: item.name,
    value: item.value,
  }));

  const option = {
    tooltip: {
      trigger: "item",
      formatter: "{b}: ${c} ({d}%)",
    },
    legend: {
      orient: "vertical",
      left: "left",
    },
    series: [
      {
        name: "影廳類型",
        type: "pie",
        radius: "70%",
        data: data,
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: "rgba(0, 0, 0, 0.5)",
          },
        },
      },
    ],
  };

  screenTypeChartInstance.setOption(option);
};

// 繪製票種圓餅圖
const renderTicketTypeChart = () => {
  if (!ticketTypeChart.value) return;

  if (!ticketTypeChartInstance) {
    ticketTypeChartInstance = echarts.init(ticketTypeChart.value);
  }

  const data = reportData.ticketTypeDistribution.map((item) => ({
    name: item.name,
    value: item.value,
  }));

  const option = {
    tooltip: {
      trigger: "item",
      formatter: "{b}: {c} 張 ({d}%)",
    },
    legend: {
      orient: "vertical",
      left: "left",
    },
    series: [
      {
        name: "票種",
        type: "pie",
        radius: "70%",
        data: data,
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: "rgba(0, 0, 0, 0.5)",
          },
        },
      },
    ],
  };

  ticketTypeChartInstance.setOption(option);
};

// 繪製電影營收圓餅圖
const renderMovieDistChart = () => {
  if (!movieDistChart.value) return;

  if (!movieDistChartInstance) {
    movieDistChartInstance = echarts.init(movieDistChart.value);
  }

  const data = reportData.movieDistribution.map((item) => ({
    name: item.name,
    value: item.value,
  }));

  const option = {
    tooltip: {
      trigger: "item",
      formatter: "{b}: ${c} ({d}%)",
    },
    legend: {
      orient: "vertical",
      left: "left",
      type: "scroll",
    },
    series: [
      {
        name: "電影營收",
        type: "pie",
        radius: "70%",
        data: data,
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: "rgba(0, 0, 0, 0.5)",
          },
        },
      },
    ],
  };

  movieDistChartInstance.setOption(option);
};

// 匯出報表 - 支援多種格式
const exportReport = async (format = "csv") => {
  if (!dateRange.value || dateRange.value.length !== 2) {
    ElMessage.warning("請選擇日期區間");
    return;
  }

  exporting.value = true;
  try {
    const params = {
      format: format, // 'csv' 或 'excel'
      startDate: dateRange.value[0],
      endDate: dateRange.value[1],
      movieIds:
        filters.movieIds.length > 0 ? filters.movieIds.join(",") : undefined,
      screenIds:
        filters.screenIds.length > 0 ? filters.screenIds.join(",") : undefined,
      ticketTypes:
        filters.ticketTypes.length > 0
          ? filters.ticketTypes.join(",")
          : undefined,
    };

    // 🆕 使用新的增強 API
    const response = await reportService.exportComprehensiveReport(params);

    // 決定檔案副檔名
    const extension = format === "excel" ? "xlsx" : "csv";
    const fileName = `營收報表_${dateRange.value[0]}_${dateRange.value[1]}.${extension}`;

    // 建立下載連結
    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement("a");
    link.href = url;
    link.setAttribute("download", fileName);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);

    ElMessage.success(`報表已匯出為 ${format.toUpperCase()} 格式`);
  } catch (error) {
    console.error("匯出報表失敗:", error);
    ElMessage.error("匯出報表失敗");
  } finally {
    exporting.value = false;
  }
};
</script>

<style scoped>
.reports-container {
  padding: 20px;
}

.header-card {
  margin-bottom: 20px;
}

.header-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-title h2 {
  margin: 0;
}

.filter-card {
  margin-bottom: 20px;
}

.quick-date-buttons {
  margin-bottom: 15px;
}

.kpi-cards {
  margin-bottom: 20px;
}

.kpi-card {
  display: flex;
  align-items: center;
  padding: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s;
}

.kpi-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

.kpi-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 30px;
  color: white;
  margin-right: 15px;
}

.kpi-content {
  flex: 1;
}

.kpi-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 5px;
}

.kpi-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.charts-row {
  margin-bottom: 20px;
}

.chart-card {
  margin-bottom: 20px;
}

.chart-header {
  font-weight: bold;
  font-size: 16px;
}

.chart-container {
  width: 100%;
  height: 400px;
}

.chart-container-small {
  width: 100%;
  height: 350px;
}

.table-card {
  margin-bottom: 20px;
}

@media (max-width: 768px) {
  .kpi-card {
    margin-bottom: 15px;
  }

  .chart-container {
    height: 300px;
  }

  .chart-container-small {
    height: 250px;
  }
}
</style>
