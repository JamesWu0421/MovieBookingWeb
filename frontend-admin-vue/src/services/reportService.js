// src/services/reportService.js
import api from './api';

const reportService = {
  /**
   * 【新】獲取完整的視覺化銷售報表(用於新的報表頁面)
   * @param {Object} params - 查詢參數
   * @param {string} params.startDate - 開始日期 (YYYY-MM-DD)
   * @param {string} params.endDate - 結束日期 (YYYY-MM-DD)
   * @param {Array<number>} params.movieIds - 電影ID列表
   * @param {Array<number>} params.screenIds - 影廳ID列表
   * @param {Array<string>} params.ticketTypes - 票種列表
   */
  getComprehensiveReport(params) {
    return api.get('/reports/comprehensive', { params });
  },

  /**
   * 【增強版】匯出完整報表 - 支援 CSV 和 Excel 格式
   * @param {Object} params - 查詢參數
   * @param {string} params.format - 匯出格式: 'csv' 或 'excel'
   * @param {string} params.startDate - 開始日期 (YYYY-MM-DD)
   * @param {string} params.endDate - 結束日期 (YYYY-MM-DD)
   * @param {Array<number>} params.movieIds - 電影ID列表 (可選)
   * @param {Array<number>} params.screenIds - 影廳ID列表 (可選)
   * @param {Array<string>} params.ticketTypes - 票種列表 (可選)
   */
  exportComprehensiveReport(params) {
    return api.get('/admin/reports/export/comprehensive', {
      params,
      responseType: 'blob'
    });
  },

  /**
   * 【舊版】匯出簡單銷售報表(僅 CSV)
   * 保留向下相容性
   */
  exportSalesReport(params) {
    return api.get('/admin/reports/export/sales', {
      params,
      responseType: 'blob'
    });
  }
};

export default reportService;