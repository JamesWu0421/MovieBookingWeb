package com.example.movie.service;

import com.example.movie.dto.ReportMovieRankingDTO;
import com.example.movie.dto.ReportSalesDTO;
import com.example.movie.dto.ReportUserConsumptionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // =============================================
    // 輔助方法：安全地轉換 Number 到 BigDecimal
    // =============================================

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        if (value instanceof Number) {
            return new BigDecimal(((Number) value).toString());
        }
        return BigDecimal.ZERO;
    }

    // =============================================
    // 原有功能（保留）
    // =============================================

    public List<ReportSalesDTO> getSalesReport(LocalDate startDate, LocalDate endDate) {
        String sql = """
                    SELECT
                        CAST(order_time AS DATE) as period,
                        SUM(total_amount) as totalRevenue,
                        COUNT(*) as totalOrders,
                        SUM((SELECT COUNT(*) FROM order_details WHERE order_id = orders.id)) as totalTickets,
                        AVG(total_amount) as averageOrderValue
                    FROM orders
                    WHERE order_status = 'completed'
                        AND order_time >= ?
                        AND order_time <= ?
                    GROUP BY CAST(order_time AS DATE)
                    ORDER BY period DESC
                """;

        return jdbcTemplate.query(sql,
                (rs, rowNum) -> {
                    ReportSalesDTO dto = new ReportSalesDTO();
                    dto.setPeriod(rs.getString("period"));
                    dto.setTotalRevenue(rs.getBigDecimal("totalRevenue"));
                    dto.setTotalOrders(rs.getInt("totalOrders"));
                    dto.setTotalTickets(rs.getInt("totalTickets"));
                    dto.setAverageOrderValue(rs.getBigDecimal("averageOrderValue"));
                    return dto;
                },
                startDate, endDate);
    }

    public List<ReportSalesDTO> getMonthlySalesReport(int year) {
        String sql = """
                    SELECT
                        FORMAT(order_time, 'yyyy-MM') as period,
                        SUM(total_amount) as totalRevenue,
                        COUNT(*) as totalOrders,
                        SUM((SELECT COUNT(*) FROM order_details WHERE order_id = orders.id)) as totalTickets,
                        AVG(total_amount) as averageOrderValue
                    FROM orders
                    WHERE order_status = 'completed'
                        AND YEAR(order_time) = ?
                    GROUP BY FORMAT(order_time, 'yyyy-MM')
                    ORDER BY period DESC
                """;

        return jdbcTemplate.query(sql,
                (rs, rowNum) -> {
                    ReportSalesDTO dto = new ReportSalesDTO();
                    dto.setPeriod(rs.getString("period"));
                    dto.setTotalRevenue(rs.getBigDecimal("totalRevenue"));
                    dto.setTotalOrders(rs.getInt("totalOrders"));
                    dto.setTotalTickets(rs.getInt("totalTickets"));
                    dto.setAverageOrderValue(rs.getBigDecimal("averageOrderValue"));
                    return dto;
                },
                year);
    }

    public List<ReportMovieRankingDTO> getMovieRanking(LocalDate startDate, LocalDate endDate, int limit) {
        String sql = """
                    SELECT TOP (?)
                        m.id as movieId,
                        m.title as movieTitle,
                        COUNT(DISTINCT od.id) as totalTickets,
                        SUM(od.ticket_price) as totalRevenue,
                        COUNT(DISTINCT s.id) as showCount
                    FROM movies m
                    JOIN shows s ON s.movie_id = m.id
                    JOIN orders o ON o.show_id = s.id
                    JOIN order_details od ON od.order_id = o.id
                    WHERE o.order_status = 'completed'
                        AND o.order_time >= ?
                        AND o.order_time <= ?
                    GROUP BY m.id, m.title
                    ORDER BY totalTickets DESC
                """;

        return jdbcTemplate.query(sql,
                (rs, rowNum) -> {
                    ReportMovieRankingDTO dto = new ReportMovieRankingDTO();
                    dto.setMovieId(rs.getInt("movieId"));
                    dto.setMovieTitle(rs.getString("movieTitle"));
                    dto.setTotalTickets(rs.getInt("totalTickets"));
                    dto.setTotalRevenue(rs.getInt("totalRevenue"));
                    dto.setShowCount(rs.getInt("showCount"));
                    return dto;
                },
                limit, startDate, endDate);
    }

    public List<ReportUserConsumptionDTO> getUserConsumptionRanking(int limit) {
        String sql = """
                    SELECT TOP (?)
                        u.id as userId,
                        u.username,
                        COALESCE(SUM(o.total_amount), 0) as totalSpending,
                        COALESCE(COUNT(od.id), 0) as totalTickets,
                        COALESCE(COUNT(DISTINCT o.id), 0) as totalOrders,
                        MAX(CAST(o.order_time AS DATE)) as lastVisitDate
                    FROM users u
                    LEFT JOIN orders o ON o.user_id = u.id AND o.order_status = 'completed'
                    LEFT JOIN order_details od ON od.order_id = o.id
                    GROUP BY u.id, u.username
                    ORDER BY totalSpending DESC
                """;

        return jdbcTemplate.query(sql,
                (rs, rowNum) -> {
                    ReportUserConsumptionDTO dto = new ReportUserConsumptionDTO();
                    dto.setUserId(rs.getInt("userId"));
                    dto.setUsername(rs.getString("username"));
                    dto.setTotalSpending(rs.getBigDecimal("totalSpending"));
                    dto.setTotalTickets(rs.getInt("totalTickets"));
                    dto.setTotalOrders(rs.getInt("totalOrders"));

                    java.sql.Date sqlDate = rs.getDate("lastVisitDate");
                    if (sqlDate != null) {
                        dto.setLastVisitDate(sqlDate.toLocalDate());
                    }

                    return dto;
                },
                limit);
    }

    public List<Map<String, Object>> getScreenUsageReport(LocalDate startDate, LocalDate endDate) {
        String sql = """
                    SELECT
                        sc.name as screenName,
                        COUNT(DISTINCT s.id) as totalShows,
                        SUM((SELECT COUNT(*) FROM order_details od
                             JOIN orders o ON od.order_id = o.id
                             WHERE o.show_id = s.id AND o.order_status = 'completed')) as soldSeats,
                        sc.total_seats * COUNT(DISTINCT s.id) as totalAvailableSeats,
                        CAST(
                            (SUM((SELECT COUNT(*) FROM order_details od
                                  JOIN orders o ON od.order_id = o.id
                                  WHERE o.show_id = s.id AND o.order_status = 'completed')) * 100.0)
                            / (sc.total_seats * COUNT(DISTINCT s.id))
                        AS DECIMAL(5,2)) as occupancyRate
                    FROM screens sc
                    LEFT JOIN shows s ON s.screen_id = sc.id
                    WHERE s.show_date >= ? AND s.show_date <= ?
                    GROUP BY sc.id, sc.name, sc.total_seats
                    ORDER BY occupancyRate DESC
                """;

        return jdbcTemplate.queryForList(sql, startDate, endDate);
    }

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new java.util.HashMap<>();

        String todaySql = """
                    SELECT COALESCE(SUM(total_amount), 0) as todayRevenue
                    FROM orders
                    WHERE order_status = 'completed'
                        AND CAST(order_time AS DATE) = CAST(GETDATE() AS DATE)
                """;
        BigDecimal todayRevenue = jdbcTemplate.queryForObject(todaySql, BigDecimal.class);
        stats.put("todayRevenue", todayRevenue);

        String monthSql = """
                    SELECT COALESCE(SUM(total_amount), 0) as monthRevenue
                    FROM orders
                    WHERE order_status = 'completed'
                        AND YEAR(order_time) = YEAR(GETDATE())
                        AND MONTH(order_time) = MONTH(GETDATE())
                """;
        BigDecimal monthRevenue = jdbcTemplate.queryForObject(monthSql, BigDecimal.class);
        stats.put("monthRevenue", monthRevenue);

        String userSql = "SELECT COUNT(*) FROM users WHERE status = 1";
        Integer totalUsers = jdbcTemplate.queryForObject(userSql, Integer.class);
        stats.put("totalUsers", totalUsers);

        String orderSql = """
                    SELECT COUNT(*)
                    FROM orders
                    WHERE CAST(order_time AS DATE) = CAST(GETDATE() AS DATE)
                """;
        Integer todayOrders = jdbcTemplate.queryForObject(orderSql, Integer.class);
        stats.put("todayOrders", todayOrders);

        return stats;
    }

    // =============================================
    // 新增功能：完整視覺化報表
    // =============================================

    /**
     * 獲取完整的視覺化銷售報表（包含 KPI、圖表數據等）
     */
    public Map<String, Object> getComprehensiveSalesReport(LocalDate startDate, LocalDate endDate,
            List<Integer> movieIds, List<Integer> screenIds,
            List<String> ticketTypes) {
        Map<String, Object> report = new java.util.HashMap<>();

        // 1. KPI 總覽數據
        report.put("overview", getSalesOverview(startDate, endDate, movieIds, screenIds, ticketTypes));

        // 2. 趨勢數據（每日）
        report.put("trendData", getTrendData(startDate, endDate, movieIds, screenIds, ticketTypes));

        // 3. 影廳類型分布
        report.put("screenTypeDistribution",
                getScreenTypeDistribution(startDate, endDate, movieIds, screenIds, ticketTypes));

        // 4. 票種分布
        report.put("ticketTypeDistribution",
                getTicketTypeDistribution(startDate, endDate, movieIds, screenIds, ticketTypes));

        // 5. 電影營收分布
        report.put("movieDistribution", getMovieDistribution(startDate, endDate, movieIds, screenIds, ticketTypes));

        // 6. Top 5 電影
        report.put("topMovies", getTopMoviesForChart(startDate, endDate, movieIds, screenIds, ticketTypes, 5));

        return report;
    }

    /**
     * 獲取 KPI 總覽數據
     */
    private Map<String, Object> getSalesOverview(LocalDate startDate, LocalDate endDate,
            List<Integer> movieIds, List<Integer> screenIds,
            List<String> ticketTypes) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append("  ISNULL(SUM(o.total_amount), 0) AS totalRevenue, ");
        sql.append("  ISNULL(COUNT(od.id), 0) AS totalTickets, ");
        sql.append("  ISNULL(COUNT(DISTINCT s.id), 0) AS showtimesCount ");
        sql.append("FROM orders o ");
        sql.append("INNER JOIN order_details od ON o.id = od.order_id ");
        sql.append("INNER JOIN shows s ON o.show_id = s.id ");
        sql.append("INNER JOIN movies m ON s.movie_id = m.id ");
        sql.append("INNER JOIN screens sc ON s.screen_id = sc.id ");
        sql.append("WHERE o.order_status = 'completed' ");

        List<Object> params = new ArrayList<>();
        if (startDate != null) {
            sql.append("  AND s.show_date >= ? ");
            params.add(startDate);
        }
        if (endDate != null) {
            sql.append("  AND s.show_date <= ? ");
            params.add(endDate);
        }
        if (movieIds != null && !movieIds.isEmpty()) {
            sql.append("  AND s.movie_id IN (")
                    .append(String.join(",", movieIds.stream().map(String::valueOf).toArray(String[]::new)))
                    .append(") ");
        }
        if (screenIds != null && !screenIds.isEmpty()) {
            sql.append("  AND s.screen_id IN (")
                    .append(String.join(",", screenIds.stream().map(String::valueOf).toArray(String[]::new)))
                    .append(") ");
        }
        if (ticketTypes != null && !ticketTypes.isEmpty()) {
            sql.append("  AND od.ticket_type IN (");
            sql.append(ticketTypes.stream().map(t -> "?").collect(Collectors.joining(",")));
            sql.append(") ");
            params.addAll(ticketTypes);
        }

        return jdbcTemplate.queryForObject(sql.toString(), params.toArray(), (rs, rowNum) -> {
            Map<String, Object> overview = new java.util.HashMap<>();
            BigDecimal totalRevenue = toBigDecimal(rs.getObject("totalRevenue"));
            Integer totalTickets = rs.getInt("totalTickets");
            Integer showtimesCount = rs.getInt("showtimesCount");

            overview.put("totalRevenue", totalRevenue);
            overview.put("totalTickets", totalTickets);
            overview.put("showtimesCount", showtimesCount);

            // 計算平均票價
            BigDecimal averageTicketPrice = BigDecimal.ZERO;
            if (totalTickets > 0) {
                averageTicketPrice = totalRevenue.divide(new BigDecimal(totalTickets), 2, RoundingMode.HALF_UP);
            }
            overview.put("averageTicketPrice", averageTicketPrice);

            // 計算每日平均營收
            BigDecimal dailyAvgRevenue = BigDecimal.ZERO;
            if (startDate != null && endDate != null) {
                long daysBetween = ChronoUnit.DAYS.between(startDate, endDate) + 1;
                if (daysBetween > 0) {
                    dailyAvgRevenue = totalRevenue.divide(new BigDecimal(daysBetween), 2, RoundingMode.HALF_UP);
                }
            }
            overview.put("dailyAvgRevenue", dailyAvgRevenue);

            return overview;
        });
    }

    /**
     * 獲取趨勢數據(每日營收和票數)
     */
    private List<Map<String, Object>> getTrendData(LocalDate startDate, LocalDate endDate,
            List<Integer> movieIds, List<Integer> screenIds,
            List<String> ticketTypes) {

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append("  CONVERT(VARCHAR(10), s.show_date, 23) AS date, ");
        sql.append("  ISNULL(SUM(o.total_amount), 0) AS revenue, ");
        sql.append("  ISNULL(COUNT(od.id), 0) AS tickets ");
        sql.append("FROM shows s ");
        sql.append("LEFT JOIN orders o ON s.id = o.show_id AND o.order_status = 'completed' ");
        sql.append("LEFT JOIN order_details od ON o.id = od.order_id ");
        sql.append("WHERE 1=1 ");

        List<Object> params = new ArrayList<>();

        // 日期範圍
        if (startDate != null) {
            sql.append(" AND s.show_date >= ? ");
            params.add(startDate);
        }
        if (endDate != null) {
            sql.append(" AND s.show_date <= ? ");
            params.add(endDate);
        }

        // 電影篩選
        if (movieIds != null && !movieIds.isEmpty()) {
            sql.append(" AND s.movie_id IN (")
                    .append(movieIds.stream().map(String::valueOf).collect(Collectors.joining(",")))
                    .append(") ");
        }

        // 影廳篩選
        if (screenIds != null && !screenIds.isEmpty()) {
            sql.append(" AND s.screen_id IN (")
                    .append(screenIds.stream().map(String::valueOf).collect(Collectors.joining(",")))
                    .append(") ");
        }

        // 票種篩選
        if (ticketTypes != null && !ticketTypes.isEmpty()) {
            sql.append(" AND od.ticket_type IN (");
            sql.append(ticketTypes.stream().map(t -> "?").collect(Collectors.joining(",")));
            sql.append(") ");
            params.addAll(ticketTypes);
        }

        sql.append("GROUP BY CONVERT(VARCHAR(10), s.show_date, 23) ");
        sql.append("ORDER BY date");

        return jdbcTemplate.queryForList(sql.toString(), params.toArray());
    }

    /**
     * 獲取影廳類型分布
     */
    private List<Map<String, Object>> getScreenTypeDistribution(LocalDate startDate, LocalDate endDate,
            List<Integer> movieIds, List<Integer> screenIds,
            List<String> ticketTypes) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append("  ISNULL(sc.screen_type, '一般廳') AS name, ");
        sql.append("  ISNULL(SUM(o.total_amount), 0) AS value ");
        sql.append("FROM orders o ");
        sql.append("INNER JOIN order_details od ON o.id = od.order_id ");
        sql.append("INNER JOIN shows s ON o.show_id = s.id ");
        sql.append("INNER JOIN screens sc ON s.screen_id = sc.id ");
        sql.append("WHERE o.order_status = 'completed' ");

        List<Object> params = new ArrayList<>();
        if (startDate != null) {
            sql.append("  AND s.show_date >= ? ");
            params.add(startDate);
        }
        if (endDate != null) {
            sql.append("  AND s.show_date <= ? ");
            params.add(endDate);
        }
        if (movieIds != null && !movieIds.isEmpty()) {
            sql.append("  AND s.movie_id IN (")
                    .append(String.join(",", movieIds.stream().map(String::valueOf).toArray(String[]::new)))
                    .append(") ");
        }
        if (screenIds != null && !screenIds.isEmpty()) {
            sql.append("  AND s.screen_id IN (")
                    .append(String.join(",", screenIds.stream().map(String::valueOf).toArray(String[]::new)))
                    .append(") ");
        }
        if (ticketTypes != null && !ticketTypes.isEmpty()) {
            sql.append("  AND od.ticket_type IN (");
            sql.append(ticketTypes.stream().map(t -> "?").collect(Collectors.joining(",")));
            sql.append(") ");
            params.addAll(ticketTypes);
        }

        sql.append("GROUP BY sc.screen_type ");
        sql.append("ORDER BY value DESC ");

        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql.toString(), params.toArray());

        // 計算百分比 - 使用安全的類型轉換
        BigDecimal total = result.stream()
                .map(row -> toBigDecimal(row.get("value")))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (total.compareTo(BigDecimal.ZERO) > 0) {
            result.forEach(row -> {
                BigDecimal value = toBigDecimal(row.get("value"));
                BigDecimal percentage = value.divide(total, 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
                row.put("percentage", percentage);
            });
        }

        return result;
    }

    /**
     * 獲取票種分布
     */
    private List<Map<String, Object>> getTicketTypeDistribution(LocalDate startDate, LocalDate endDate,
            List<Integer> movieIds, List<Integer> screenIds,
            List<String> ticketTypes) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append("  ISNULL(od.ticket_type, '一般票') AS name, ");
        sql.append("  ISNULL(COUNT(od.id), 0) AS value ");
        sql.append("FROM orders o ");
        sql.append("INNER JOIN order_details od ON o.id = od.order_id ");
        sql.append("INNER JOIN shows s ON o.show_id = s.id ");
        sql.append("WHERE o.order_status = 'completed' ");

        List<Object> params = new ArrayList<>();
        if (startDate != null) {
            sql.append("  AND s.show_date >= ? ");
            params.add(startDate);
        }
        if (endDate != null) {
            sql.append("  AND s.show_date <= ? ");
            params.add(endDate);
        }
        if (movieIds != null && !movieIds.isEmpty()) {
            sql.append("  AND s.movie_id IN (")
                    .append(String.join(",", movieIds.stream().map(String::valueOf).toArray(String[]::new)))
                    .append(") ");
        }
        if (screenIds != null && !screenIds.isEmpty()) {
            sql.append("  AND s.screen_id IN (")
                    .append(String.join(",", screenIds.stream().map(String::valueOf).toArray(String[]::new)))
                    .append(") ");
        }
        if (ticketTypes != null && !ticketTypes.isEmpty()) {
            sql.append("  AND od.ticket_type IN (");
            sql.append(ticketTypes.stream().map(t -> "?").collect(Collectors.joining(",")));
            sql.append(") ");
            params.addAll(ticketTypes);
        }

        sql.append("GROUP BY od.ticket_type ");
        sql.append("ORDER BY value DESC ");

        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql.toString(), params.toArray());

        // 計算百分比 - 使用安全的類型轉換
        BigDecimal total = result.stream()
                .map(row -> toBigDecimal(row.get("value")))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (total.compareTo(BigDecimal.ZERO) > 0) {
            result.forEach(row -> {
                BigDecimal value = toBigDecimal(row.get("value"));
                BigDecimal percentage = value.divide(total, 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
                row.put("percentage", percentage);
            });
        }

        return result;
    }

    /**
     * 獲取電影營收分布
     */
    private List<Map<String, Object>> getMovieDistribution(LocalDate startDate, LocalDate endDate,
            List<Integer> movieIds, List<Integer> screenIds,
            List<String> ticketTypes) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT TOP 10 ");
        sql.append("  m.title AS name, ");
        sql.append("  ISNULL(SUM(o.total_amount), 0) AS value ");
        sql.append("FROM orders o ");
        sql.append("INNER JOIN order_details od ON o.id = od.order_id ");
        sql.append("INNER JOIN shows s ON o.show_id = s.id ");
        sql.append("INNER JOIN movies m ON s.movie_id = m.id ");
        sql.append("WHERE o.order_status = 'completed' ");

        List<Object> params = new ArrayList<>();
        if (startDate != null) {
            sql.append("  AND s.show_date >= ? ");
            params.add(startDate);
        }
        if (endDate != null) {
            sql.append("  AND s.show_date <= ? ");
            params.add(endDate);
        }
        if (movieIds != null && !movieIds.isEmpty()) {
            sql.append("  AND s.movie_id IN (")
                    .append(String.join(",", movieIds.stream().map(String::valueOf).toArray(String[]::new)))
                    .append(") ");
        }
        if (screenIds != null && !screenIds.isEmpty()) {
            sql.append("  AND s.screen_id IN (")
                    .append(String.join(",", screenIds.stream().map(String::valueOf).toArray(String[]::new)))
                    .append(") ");
        }
        if (ticketTypes != null && !ticketTypes.isEmpty()) {
            sql.append("  AND od.ticket_type IN (");
            sql.append(ticketTypes.stream().map(t -> "?").collect(Collectors.joining(",")));
            sql.append(") ");
            params.addAll(ticketTypes);
        }

        sql.append("GROUP BY m.title ");
        sql.append("ORDER BY value DESC ");

        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql.toString(), params.toArray());

        // 計算百分比 - 使用安全的類型轉換
        BigDecimal total = result.stream()
                .map(row -> toBigDecimal(row.get("value")))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (total.compareTo(BigDecimal.ZERO) > 0) {
            result.forEach(row -> {
                BigDecimal value = toBigDecimal(row.get("value"));
                BigDecimal percentage = value.divide(total, 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
                row.put("percentage", percentage);
            });
        }

        return result;
    }

    /**
     * 獲取 Top N 電影（用於圖表）
     */
    private List<Map<String, Object>> getTopMoviesForChart(LocalDate startDate, LocalDate endDate,
            List<Integer> movieIds, List<Integer> screenIds,
            List<String> ticketTypes, int topN) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT TOP ").append(topN).append(" ");
        sql.append("  m.title AS movieTitle, ");
        sql.append("  ISNULL(SUM(o.total_amount), 0) AS totalRevenue, ");
        sql.append("  ISNULL(COUNT(od.id), 0) AS ticketsSold ");
        sql.append("FROM movies m ");
        sql.append("INNER JOIN shows s ON m.id = s.movie_id ");
        sql.append("LEFT JOIN orders o ON s.id = o.show_id AND o.order_status = 'completed' ");
        sql.append("LEFT JOIN order_details od ON o.id = od.order_id ");
        sql.append("WHERE 1=1 ");

        List<Object> params = new ArrayList<>();
        if (startDate != null) {
            sql.append("  AND s.show_date >= ? ");
            params.add(startDate);
        }
        if (endDate != null) {
            sql.append("  AND s.show_date <= ? ");
            params.add(endDate);
        }
        if (movieIds != null && !movieIds.isEmpty()) {
            sql.append("  AND s.movie_id IN (")
                    .append(String.join(",", movieIds.stream().map(String::valueOf).toArray(String[]::new)))
                    .append(") ");
        }
        if (screenIds != null && !screenIds.isEmpty()) {
            sql.append("  AND s.screen_id IN (")
                    .append(String.join(",", screenIds.stream().map(String::valueOf).toArray(String[]::new)))
                    .append(") ");
        }
        if (ticketTypes != null && !ticketTypes.isEmpty()) {
            sql.append("  AND od.ticket_type IN (");
            sql.append(ticketTypes.stream().map(t -> "?").collect(Collectors.joining(",")));
            sql.append(") ");
            params.addAll(ticketTypes);
        }

        sql.append("GROUP BY m.title ");
        sql.append("ORDER BY totalRevenue DESC ");

        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql.toString(), params.toArray());

        // 添加排名
        for (int i = 0; i < result.size(); i++) {
            result.get(i).put("rank", i + 1);
        }

        return result;
    }
}