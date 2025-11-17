package com.example.movie.controller;

import com.example.movie.dto.ReportMovieRankingDTO;
import com.example.movie.dto.ReportSalesDTO;
import com.example.movie.dto.ReportUserConsumptionDTO;
import com.example.movie.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 報表管理 Controller
 * 提供各種營運報表 API
 */
@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ReportController {

    @Autowired
    private ReportService reportService;

    // =============================================
    // 儀表板 Dashboard
    // =============================================

    /**
     * 取得儀表板統計數據
     * GET /api/reports/dashboard
     * 
     * 回傳：
     * - todayRevenue: 今日營收
     * - monthRevenue: 本月營收
     * - totalUsers: 總會員數
     * - todayOrders: 今日訂單數
     */
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = reportService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }

    // =============================================
    // 營收報表 Sales Reports
    // =============================================

    /**
     * 查詢營收報表（依日期範圍）
     * GET /api/reports/sales?startDate=2024-11-01&endDate=2024-11-13
     * 
     * @param startDate 開始日期（必填）
     * @param endDate 結束日期（必填）
     * @return 每日營收統計列表
     */
    @GetMapping("/sales")
    public ResponseEntity<List<ReportSalesDTO>> getSalesReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<ReportSalesDTO> report = reportService.getSalesReport(startDate, endDate);
        return ResponseEntity.ok(report);
    }

    /**
     * 查詢月度營收報表
     * GET /api/reports/sales/monthly?year=2024
     * 
     * @param year 年份（選填，預設為 2024）
     * @return 每月營收統計列表
     */
    @GetMapping("/sales/monthly")
    public ResponseEntity<List<ReportSalesDTO>> getMonthlySalesReport(
            @RequestParam(defaultValue = "2024") int year) {
        List<ReportSalesDTO> report = reportService.getMonthlySalesReport(year);
        return ResponseEntity.ok(report);
    }

    /**
     * 查詢今日營收
     * GET /api/reports/sales/today
     * 
     * @return 今日營收統計
     */
    @GetMapping("/sales/today")
    public ResponseEntity<ReportSalesDTO> getTodaySales() {
        LocalDate today = LocalDate.now();
        List<ReportSalesDTO> report = reportService.getSalesReport(today, today);
        
        if (report.isEmpty()) {
            // 今日無資料，回傳空統計
            ReportSalesDTO emptyReport = new ReportSalesDTO();
            emptyReport.setPeriod(today.toString());
            emptyReport.setTotalRevenue(java.math.BigDecimal.ZERO);
            emptyReport.setTotalOrders(0);
            emptyReport.setTotalTickets(0);
            emptyReport.setAverageOrderValue(java.math.BigDecimal.ZERO);
            return ResponseEntity.ok(emptyReport);
        }
        
        return ResponseEntity.ok(report.get(0));
    }

    /**
     * 查詢本月營收
     * GET /api/reports/sales/this-month
     * 
     * @return 本月營收統計
     */
    @GetMapping("/sales/this-month")
    public ResponseEntity<ReportSalesDTO> getThisMonthSales() {
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());
        
        List<ReportSalesDTO> dailyReport = reportService.getSalesReport(startOfMonth, endOfMonth);
        
        // 彙總本月所有資料
        ReportSalesDTO monthReport = new ReportSalesDTO();
        monthReport.setPeriod(now.getYear() + "-" + String.format("%02d", now.getMonthValue()));
        
        java.math.BigDecimal totalRevenue = java.math.BigDecimal.ZERO;
        int totalOrders = 0;
        int totalTickets = 0;
        
        for (ReportSalesDTO daily : dailyReport) {
            totalRevenue = totalRevenue.add(daily.getTotalRevenue());
            totalOrders += daily.getTotalOrders();
            totalTickets += daily.getTotalTickets();
        }
        
        monthReport.setTotalRevenue(totalRevenue);
        monthReport.setTotalOrders(totalOrders);
        monthReport.setTotalTickets(totalTickets);
        
        if (totalOrders > 0) {
            monthReport.setAverageOrderValue(
                totalRevenue.divide(
                    java.math.BigDecimal.valueOf(totalOrders), 
                    2, 
                    java.math.RoundingMode.HALF_UP
                )
            );
        } else {
            monthReport.setAverageOrderValue(java.math.BigDecimal.ZERO);
        }
        
        return ResponseEntity.ok(monthReport);
    }

    // =============================================
    // 電影排行報表 Movie Reports
    // =============================================

    /**
     * 查詢電影熱門排行
     * GET /api/reports/movies/ranking?startDate=2024-11-01&endDate=2024-11-13&limit=10
     * 
     * @param startDate 開始日期（必填）
     * @param endDate 結束日期（必填）
     * @param limit 排行數量（選填，預設 10）
     * @return 電影排行列表
     */
    @GetMapping("/movies/ranking")
    public ResponseEntity<List<ReportMovieRankingDTO>> getMovieRanking(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "10") int limit) {
        List<ReportMovieRankingDTO> ranking = reportService.getMovieRanking(startDate, endDate, limit);
        return ResponseEntity.ok(ranking);
    }

    /**
     * 查詢本月電影熱門排行
     * GET /api/reports/movies/ranking/this-month?limit=10
     * 
     * @param limit 排行數量（選填，預設 10）
     * @return 電影排行列表
     */
    @GetMapping("/movies/ranking/this-month")
    public ResponseEntity<List<ReportMovieRankingDTO>> getMovieRankingThisMonth(
            @RequestParam(defaultValue = "10") int limit) {
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());
        
        List<ReportMovieRankingDTO> ranking = reportService.getMovieRanking(startOfMonth, endOfMonth, limit);
        return ResponseEntity.ok(ranking);
    }

    // =============================================
    // 用戶消費報表 User Reports
    // =============================================

    /**
     * 查詢用戶消費排行
     * GET /api/reports/users/consumption?limit=20
     * 
     * @param limit 排行數量（選填，預設 20）
     * @return 用戶消費排行列表
     */
    @GetMapping("/users/consumption")
    public ResponseEntity<List<ReportUserConsumptionDTO>> getUserConsumptionRanking(
            @RequestParam(defaultValue = "20") int limit) {
        List<ReportUserConsumptionDTO> ranking = reportService.getUserConsumptionRanking(limit);
        return ResponseEntity.ok(ranking);
    }

    // =============================================
    // 影廳使用率報表 Screen Reports
    // =============================================

    /**
     * 查詢影廳使用率報表
     * GET /api/reports/screens/usage?startDate=2024-11-01&endDate=2024-11-13
     * 
     * @param startDate 開始日期（必填）
     * @param endDate 結束日期（必填）
     * @return 影廳使用率列表
     */
    @GetMapping("/screens/usage")
    public ResponseEntity<List<Map<String, Object>>> getScreenUsageReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Map<String, Object>> report = reportService.getScreenUsageReport(startDate, endDate);
        return ResponseEntity.ok(report);
    }

    /**
     * 查詢本月影廳使用率
     * GET /api/reports/screens/usage/this-month
     * 
     * @return 影廳使用率列表
     */
    @GetMapping("/screens/usage/this-month")
    public ResponseEntity<List<Map<String, Object>>> getScreenUsageThisMonth() {
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());
        
        List<Map<String, Object>> report = reportService.getScreenUsageReport(startOfMonth, endOfMonth);
        return ResponseEntity.ok(report);
    }

    // =============================================
    // 綜合統計報表 Summary Reports
    // =============================================

    /**
     * 查詢指定日期範圍的完整統計摘要
     * GET /api/reports/summary?startDate=2024-11-01&endDate=2024-11-13
     * 
     * @param startDate 開始日期（必填）
     * @param endDate 結束日期（必填）
     * @return 統計摘要資料
     */
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getSummaryReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        Map<String, Object> summary = new HashMap<>();
        
        // 營收統計
        List<ReportSalesDTO> salesReport = reportService.getSalesReport(startDate, endDate);
        summary.put("salesData", salesReport);
        
        // 計算總營收
        java.math.BigDecimal totalRevenue = salesReport.stream()
            .map(ReportSalesDTO::getTotalRevenue)
            .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        summary.put("totalRevenue", totalRevenue);
        
        // 計算總訂單數
        int totalOrders = salesReport.stream()
            .mapToInt(ReportSalesDTO::getTotalOrders)
            .sum();
        summary.put("totalOrders", totalOrders);
        
        // 計算總票數
        int totalTickets = salesReport.stream()
            .mapToInt(ReportSalesDTO::getTotalTickets)
            .sum();
        summary.put("totalTickets", totalTickets);
        
        // 電影排行（Top 5）
        List<ReportMovieRankingDTO> movieRanking = reportService.getMovieRanking(startDate, endDate, 5);
        summary.put("topMovies", movieRanking);
        
        // 影廳使用率
        List<Map<String, Object>> screenUsage = reportService.getScreenUsageReport(startDate, endDate);
        summary.put("screenUsage", screenUsage);
        
        summary.put("startDate", startDate.toString());
        summary.put("endDate", endDate.toString());
        
        return ResponseEntity.ok(summary);
    }

    /**
     * 查詢本月完整統計摘要
     * GET /api/reports/summary/this-month
     * 
     * @return 本月統計摘要
     */
    @GetMapping("/summary/this-month")
    public ResponseEntity<Map<String, Object>> getSummaryThisMonth() {
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());
        
        return getSummaryReport(startOfMonth, endOfMonth);
    }

    // =============================================
    // 匯出報表 Export (可選功能)
    // =============================================

    /**
     * 匯出營收報表為 CSV（未來可擴充）
     * GET /api/reports/export/sales?startDate=2024-11-01&endDate=2024-11-13
     */
    @GetMapping("/export/sales")
    public ResponseEntity<String> exportSalesReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        List<ReportSalesDTO> report = reportService.getSalesReport(startDate, endDate);
        
        // 建立 CSV 內容
        StringBuilder csv = new StringBuilder();
        csv.append("日期,總營收,訂單數,票數,平均訂單金額\n");
        
        for (ReportSalesDTO row : report) {
            csv.append(row.getPeriod()).append(",")
               .append(row.getTotalRevenue()).append(",")
               .append(row.getTotalOrders()).append(",")
               .append(row.getTotalTickets()).append(",")
               .append(row.getAverageOrderValue()).append("\n");
        }
        
        return ResponseEntity.ok()
            .header("Content-Type", "text/csv; charset=UTF-8")
            .header("Content-Disposition", "attachment; filename=sales_report.csv")
            .body(csv.toString());
    }
}
