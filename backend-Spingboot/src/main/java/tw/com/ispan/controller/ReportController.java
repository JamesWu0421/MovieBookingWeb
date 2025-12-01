package tw.com.ispan.controller;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import tw.com.ispan.dto.ReportMovieRankingDTO;
import tw.com.ispan.dto.ReportSalesDTO;
import tw.com.ispan.dto.ReportUserConsumptionDTO;
import tw.com.ispan.service.ReportService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 報表管理 Controller
 * 提供各種營運報表 API
 */
@RestController
@RequestMapping("/api/admin/reports")
@PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
public class ReportController {

    @Autowired
    private ReportService reportService;

    // =============================================
    // 原有功能(完整保留)
    // =============================================

    /**
     * 獲得儀表板統計數據
     * GET /api/reports/dashboard
     */
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = reportService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }

    /**
     * 查詢營收報表(依日期範圍)
     * GET /api/reports/sales?startDate=2024-11-01&endDate=2024-11-13
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
     */
    @GetMapping("/sales/today")
    public ResponseEntity<ReportSalesDTO> getTodaySales() {
        LocalDate today = LocalDate.now();
        List<ReportSalesDTO> report = reportService.getSalesReport(today, today);

        if (report.isEmpty()) {
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
     */
    @GetMapping("/sales/this-month")
    public ResponseEntity<ReportSalesDTO> getThisMonthSales() {
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());

        List<ReportSalesDTO> dailyReport = reportService.getSalesReport(startOfMonth, endOfMonth);

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
                            java.math.RoundingMode.HALF_UP));
        } else {
            monthReport.setAverageOrderValue(java.math.BigDecimal.ZERO);
        }

        return ResponseEntity.ok(monthReport);
    }

    /**
     * 查詢電影熱門排行
     * GET
     * /api/reports/movies/ranking?startDate=2024-11-01&endDate=2024-11-13&limit=10
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

    /**
     * 查詢用戶消費排行
     * GET /api/reports/users/consumption?limit=20
     */
    @GetMapping("/users/consumption")
    public ResponseEntity<List<ReportUserConsumptionDTO>> getUserConsumptionRanking(
            @RequestParam(defaultValue = "20") int limit) {
        List<ReportUserConsumptionDTO> ranking = reportService.getUserConsumptionRanking(limit);
        return ResponseEntity.ok(ranking);
    }

    /**
     * 查詢影廳使用率報表
     * GET /api/reports/screens/usage?startDate=2024-11-01&endDate=2024-11-13
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
     */
    @GetMapping("/screens/usage/this-month")
    public ResponseEntity<List<Map<String, Object>>> getScreenUsageThisMonth() {
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());

        List<Map<String, Object>> report = reportService.getScreenUsageReport(startOfMonth, endOfMonth);
        return ResponseEntity.ok(report);
    }

    /**
     * 查詢指定日期範圍的完整統計摘要
     * GET /api/reports/summary?startDate=2024-11-01&endDate=2024-11-13
     */
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getSummaryReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        Map<String, Object> summary = new HashMap<>();

        List<ReportSalesDTO> salesReport = reportService.getSalesReport(startDate, endDate);
        summary.put("salesData", salesReport);

        java.math.BigDecimal totalRevenue = salesReport.stream()
                .map(ReportSalesDTO::getTotalRevenue)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        summary.put("totalRevenue", totalRevenue);

        int totalOrders = salesReport.stream()
                .mapToInt(ReportSalesDTO::getTotalOrders)
                .sum();
        summary.put("totalOrders", totalOrders);

        int totalTickets = salesReport.stream()
                .mapToInt(ReportSalesDTO::getTotalTickets)
                .sum();
        summary.put("totalTickets", totalTickets);

        List<ReportMovieRankingDTO> movieRanking = reportService.getMovieRanking(startDate, endDate, 5);
        summary.put("topMovies", movieRanking);

        List<Map<String, Object>> screenUsage = reportService.getScreenUsageReport(startDate, endDate);
        summary.put("screenUsage", screenUsage);

        summary.put("startDate", startDate.toString());
        summary.put("endDate", endDate.toString());

        return ResponseEntity.ok(summary);
    }

    /**
     * 查詢本月完整統計摘要
     * GET /api/reports/summary/this-month
     */
    @GetMapping("/summary/this-month")
    public ResponseEntity<Map<String, Object>> getSummaryThisMonth() {
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());

        return getSummaryReport(startOfMonth, endOfMonth);
    }

    /**
     * 【新】獲取完整的視覺化銷售報表(包含 KPI、圖表數據)
     * GET
     * /api/reports/comprehensive?startDate=2025-11-01&endDate=2025-11-30&movieIds=1,2&screenIds=1&ticketTypes=adult,student
     * 
     * 回傳:
     * - overview: KPI 總覽(總營收、售票數、場次數、平均票價、日均營收)
     * - trendData: 趨勢數據(每日營收和票數)
     * - screenTypeDistribution: 影廳類型分布
     * - ticketTypeDistribution: 票種分布
     * - movieDistribution: 電影營收分布
     * - topMovies: Top 5 電影
     */
    @GetMapping("/comprehensive")
    public ResponseEntity<Map<String, Object>> getComprehensiveSalesReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) List<Integer> movieIds,
            @RequestParam(required = false) List<Integer> screenIds,
            @RequestParam(required = false) List<String> ticketTypes) {

        try {
            // 如果沒有指定日期,預設為本月
            if (startDate == null) {
                startDate = LocalDate.now().withDayOfMonth(1);
            }
            if (endDate == null) {
                endDate = LocalDate.now();
            }

            Map<String, Object> report = reportService.getComprehensiveSalesReport(
                    startDate, endDate, movieIds, screenIds, ticketTypes);

            return ResponseEntity.ok(report);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    // =============================================
    // 🆕 增強的匯出功能:支援 CSV 和 Excel,並包含篩選條件
    // =============================================

    /**
     * 【增強版】匯出營收報表為 CSV 或 Excel
     * GET
     * /api/reports/export/comprehensive?format=excel&startDate=2025-11-01&endDate=2025-11-30&movieIds=1,2
     * 
     * @param format      匯出格式: "csv" 或 "excel" (預設 csv)
     * @param startDate   開始日期
     * @param endDate     結束日期
     * @param movieIds    篩選電影 ID (可選)
     * @param screenIds   篩選影廳 ID (可選)
     * @param ticketTypes 篩選票種 (可選)
     */
    @GetMapping("/export/comprehensive")
    public ResponseEntity<byte[]> exportComprehensiveReport(
            @RequestParam(defaultValue = "csv") String format,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) List<Integer> movieIds,
            @RequestParam(required = false) List<Integer> screenIds,
            @RequestParam(required = false) List<String> ticketTypes) throws IOException {

        // 取得完整報表數據
        Map<String, Object> reportData = reportService.getComprehensiveSalesReport(
                startDate, endDate, movieIds, screenIds, ticketTypes);

        String fileName = String.format("營收報表_%s至%s",
                startDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")),
                endDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")));

        if ("excel".equalsIgnoreCase(format)) {
            // 匯出 Excel
            byte[] excelBytes = generateExcelReport(reportData, startDate, endDate);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment",
                    URLEncoder.encode(fileName + ".xlsx", StandardCharsets.UTF_8));

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelBytes);
        } else {
            // 匯出 CSV
            String csv = generateCSVReport(reportData, startDate, endDate);
            byte[] csvBytes = csv.getBytes(StandardCharsets.UTF_8);

            // 加入 BOM 讓 Excel 正確識別 UTF-8
            byte[] bom = new byte[] { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF };
            byte[] csvWithBom = new byte[bom.length + csvBytes.length];
            System.arraycopy(bom, 0, csvWithBom, 0, bom.length);
            System.arraycopy(csvBytes, 0, csvWithBom, bom.length, csvBytes.length);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(new MediaType("text", "csv", StandardCharsets.UTF_8));
            headers.setContentDispositionFormData("attachment",
                    URLEncoder.encode(fileName + ".csv", StandardCharsets.UTF_8));

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(csvWithBom);
        }
    }

    /**
     * 生成 CSV 報表內容
     */
    @SuppressWarnings("unchecked")
    private String generateCSVReport(Map<String, Object> reportData, LocalDate startDate, LocalDate endDate) {
        StringBuilder csv = new StringBuilder();

        // 標題
        csv.append("=== 營收報表 ===\n");
        csv.append("報表期間:,").append(startDate).append(" 至 ").append(endDate).append("\n");
        csv.append("產生時間:,").append(LocalDate.now()).append("\n\n");

        // KPI 總覽
        Map<String, Object> overview = (Map<String, Object>) reportData.get("overview");
        if (overview != null) {
            csv.append("=== KPI 總覽 ===\n");
            csv.append("總營收,").append(overview.get("totalRevenue")).append("\n");
            csv.append("總票數,").append(overview.get("totalTickets")).append("\n");
            csv.append("場次數,").append(overview.get("showtimesCount")).append("\n");
            csv.append("平均票價,").append(overview.get("averageTicketPrice")).append("\n");
            csv.append("每日平均營收,").append(overview.get("dailyAvgRevenue")).append("\n\n");
        }

        // 每日營收趨勢
        List<Map<String, Object>> trendData = (List<Map<String, Object>>) reportData.get("trendData");
        if (trendData != null && !trendData.isEmpty()) {
            csv.append("=== 每日營收趨勢 ===\n");
            csv.append("日期,營收,票數\n");
            for (Map<String, Object> trend : trendData) {
                csv.append(trend.get("date")).append(",")
                        .append(trend.get("revenue")).append(",")
                        .append(trend.get("tickets")).append("\n");
            }
            csv.append("\n");
        }

        // Top 5 電影
        List<Map<String, Object>> topMovies = (List<Map<String, Object>>) reportData.get("topMovies");
        if (topMovies != null && !topMovies.isEmpty()) {
            csv.append("=== Top 5 電影排名 ===\n");
            csv.append("排名,電影名稱,營收,票數\n");
            for (Map<String, Object> movie : topMovies) {
                csv.append(movie.get("rank")).append(",")
                        .append("\"").append(movie.get("movieTitle")).append("\",")
                        .append(movie.get("totalRevenue")).append(",")
                        .append(movie.get("ticketsSold")).append("\n");
            }
            csv.append("\n");
        }

        // 影廳類型分布
        List<Map<String, Object>> screenTypeDist = (List<Map<String, Object>>) reportData.get("screenTypeDistribution");
        if (screenTypeDist != null && !screenTypeDist.isEmpty()) {
            csv.append("=== 影廳類型分布 ===\n");
            csv.append("影廳類型,營收\n");
            for (Map<String, Object> dist : screenTypeDist) {
                csv.append(dist.get("name")).append(",")
                        .append(dist.get("value")).append("\n");
            }
            csv.append("\n");
        }

        // 票種分布
        List<Map<String, Object>> ticketTypeDist = (List<Map<String, Object>>) reportData.get("ticketTypeDistribution");
        if (ticketTypeDist != null && !ticketTypeDist.isEmpty()) {
            csv.append("=== 票種分布 ===\n");
            csv.append("票種,票數\n");
            for (Map<String, Object> dist : ticketTypeDist) {
                csv.append(dist.get("name")).append(",")
                        .append(dist.get("value")).append("\n");
            }
        }

        return csv.toString();
    }

    /**
     * 生成 Excel 報表內容
     */
    private byte[] generateExcelReport(Map<String, Object> reportData, LocalDate startDate, LocalDate endDate)
            throws IOException {
        Workbook workbook = new XSSFWorkbook();

        // 創建樣式
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle titleStyle = createTitleStyle(workbook);
        CellStyle numberStyle = createNumberStyle(workbook);

        // 【工作表1】總覽
        Sheet overviewSheet = workbook.createSheet("總覽");
        createOverviewSheet(overviewSheet, reportData, startDate, endDate, titleStyle, headerStyle, numberStyle);

        // 【工作表2】每日趨勢
        Sheet trendSheet = workbook.createSheet("每日趨勢");
        createTrendSheet(trendSheet, reportData, headerStyle, numberStyle);

        // 【工作表3】電影排名
        Sheet movieSheet = workbook.createSheet("電影排名");
        createMovieSheet(movieSheet, reportData, headerStyle, numberStyle);

        // 【工作表4】分布統計
        Sheet distributionSheet = workbook.createSheet("分布統計");
        createDistributionSheet(distributionSheet, reportData, headerStyle, numberStyle);

        // 輸出為 byte array
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream.toByteArray();
    }

    /**
     * 創建總覽工作表
     */
    @SuppressWarnings("unchecked")
    private void createOverviewSheet(Sheet sheet, Map<String, Object> reportData,
            LocalDate startDate, LocalDate endDate,
            CellStyle titleStyle, CellStyle headerStyle, CellStyle numberStyle) {
        int rowNum = 0;

        // 標題
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("營收報表總覽");
        titleCell.setCellStyle(titleStyle);
        rowNum++;

        // 報表期間
        Row periodRow = sheet.createRow(rowNum++);
        periodRow.createCell(0).setCellValue("報表期間:");
        periodRow.createCell(1).setCellValue(startDate + " 至 " + endDate);

        Row dateRow = sheet.createRow(rowNum++);
        dateRow.createCell(0).setCellValue("產生時間:");
        dateRow.createCell(1).setCellValue(LocalDate.now().toString());
        rowNum++;

        // KPI 數據
        Map<String, Object> overview = (Map<String, Object>) reportData.get("overview");
        if (overview != null) {
            Row kpiHeaderRow = sheet.createRow(rowNum++);
            Cell kpiHeader = kpiHeaderRow.createCell(0);
            kpiHeader.setCellValue("KPI 指標");
            kpiHeader.setCellStyle(headerStyle);
            Cell kpiValueHeader = kpiHeaderRow.createCell(1);
            kpiValueHeader.setCellValue("數值");
            kpiValueHeader.setCellStyle(headerStyle);

            createDataRow(sheet, rowNum++, "總營收", overview.get("totalRevenue"), numberStyle);
            createDataRow(sheet, rowNum++, "總票數", overview.get("totalTickets"), numberStyle);
            createDataRow(sheet, rowNum++, "場次數", overview.get("showtimesCount"), numberStyle);
            createDataRow(sheet, rowNum++, "平均票價", overview.get("averageTicketPrice"), numberStyle);
            createDataRow(sheet, rowNum++, "每日平均營收", overview.get("dailyAvgRevenue"), numberStyle);
        }

        // 調整欄寬
        sheet.setColumnWidth(0, 4000);
        sheet.setColumnWidth(1, 4000);
    }

    /**
     * 創建趨勢工作表
     */
    @SuppressWarnings("unchecked")
    private void createTrendSheet(Sheet sheet, Map<String, Object> reportData,
            CellStyle headerStyle, CellStyle numberStyle) {
        int rowNum = 0;

        // 標題行
        Row headerRow = sheet.createRow(rowNum++);
        createHeaderCell(headerRow, 0, "日期", headerStyle);
        createHeaderCell(headerRow, 1, "營收", headerStyle);
        createHeaderCell(headerRow, 2, "票數", headerStyle);

        // 數據行
        List<Map<String, Object>> trendData = (List<Map<String, Object>>) reportData.get("trendData");
        if (trendData != null) {
            for (Map<String, Object> trend : trendData) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(String.valueOf(trend.get("date")));
                createNumberCell(row, 1, trend.get("revenue"), numberStyle);
                createNumberCell(row, 2, trend.get("tickets"), numberStyle);
            }
        }

        // 調整欄寬
        sheet.setColumnWidth(0, 3000);
        sheet.setColumnWidth(1, 3500);
        sheet.setColumnWidth(2, 3000);
    }

    /**
     * 創建電影排名工作表
     */
    @SuppressWarnings("unchecked")
    private void createMovieSheet(Sheet sheet, Map<String, Object> reportData,
            CellStyle headerStyle, CellStyle numberStyle) {
        int rowNum = 0;

        // 標題行
        Row headerRow = sheet.createRow(rowNum++);
        createHeaderCell(headerRow, 0, "排名", headerStyle);
        createHeaderCell(headerRow, 1, "電影名稱", headerStyle);
        createHeaderCell(headerRow, 2, "營收", headerStyle);
        createHeaderCell(headerRow, 3, "票數", headerStyle);

        // 數據行
        List<Map<String, Object>> topMovies = (List<Map<String, Object>>) reportData.get("topMovies");
        if (topMovies != null) {
            for (Map<String, Object> movie : topMovies) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(String.valueOf(movie.get("rank")));
                row.createCell(1).setCellValue(String.valueOf(movie.get("movieTitle")));
                createNumberCell(row, 2, movie.get("totalRevenue"), numberStyle);
                createNumberCell(row, 3, movie.get("ticketsSold"), numberStyle);
            }
        }

        // 調整欄寬
        sheet.setColumnWidth(0, 2000);
        sheet.setColumnWidth(1, 6000);
        sheet.setColumnWidth(2, 3500);
        sheet.setColumnWidth(3, 3000);
    }

    /**
     * 創建分布統計工作表
     */
    @SuppressWarnings("unchecked")
    private void createDistributionSheet(Sheet sheet, Map<String, Object> reportData,
            CellStyle headerStyle, CellStyle numberStyle) {
        int rowNum = 0;

        // 影廳類型分布
        Row screenTypeHeader = sheet.createRow(rowNum++);
        Cell screenTypeTitle = screenTypeHeader.createCell(0);
        screenTypeTitle.setCellValue("影廳類型分布");
        screenTypeTitle.setCellStyle(headerStyle);

        Row screenTypeColHeader = sheet.createRow(rowNum++);
        createHeaderCell(screenTypeColHeader, 0, "影廳類型", headerStyle);
        createHeaderCell(screenTypeColHeader, 1, "營收", headerStyle);

        List<Map<String, Object>> screenTypeDist = (List<Map<String, Object>>) reportData.get("screenTypeDistribution");
        if (screenTypeDist != null) {
            for (Map<String, Object> dist : screenTypeDist) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(String.valueOf(dist.get("name")));
                createNumberCell(row, 1, dist.get("value"), numberStyle);
            }
        }
        rowNum++;

        // 票種分布
        Row ticketTypeHeader = sheet.createRow(rowNum++);
        Cell ticketTypeTitle = ticketTypeHeader.createCell(0);
        ticketTypeTitle.setCellValue("票種分布");
        ticketTypeTitle.setCellStyle(headerStyle);

        Row ticketTypeColHeader = sheet.createRow(rowNum++);
        createHeaderCell(ticketTypeColHeader, 0, "票種", headerStyle);
        createHeaderCell(ticketTypeColHeader, 1, "票數", headerStyle);

        List<Map<String, Object>> ticketTypeDist = (List<Map<String, Object>>) reportData.get("ticketTypeDistribution");
        if (ticketTypeDist != null) {
            for (Map<String, Object> dist : ticketTypeDist) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(String.valueOf(dist.get("name")));
                createNumberCell(row, 1, dist.get("value"), numberStyle);
            }
        }

        // 調整欄寬
        sheet.setColumnWidth(0, 4000);
        sheet.setColumnWidth(1, 3500);
    }

    // === Excel 樣式輔助方法 ===

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 16);
        style.setFont(font);
        return style;
    }

    private CellStyle createNumberStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setDataFormat(workbook.createDataFormat().getFormat("#,##0.00"));
        return style;
    }

    private void createHeaderCell(Row row, int column, String value, CellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private void createDataRow(Sheet sheet, int rowNum, String label, Object value, CellStyle numberStyle) {
        Row row = sheet.createRow(rowNum);
        row.createCell(0).setCellValue(label);
        createNumberCell(row, 1, value, numberStyle);
    }

    private void createNumberCell(Row row, int column, Object value, CellStyle style) {
        Cell cell = row.createCell(column);
        if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
            cell.setCellStyle(style);
        } else if (value instanceof BigDecimal) {
            cell.setCellValue(((BigDecimal) value).doubleValue());
            cell.setCellStyle(style);
        } else {
            cell.setCellValue(String.valueOf(value));
        }
    }
}