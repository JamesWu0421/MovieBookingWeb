package tw.com.ispan.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import tw.com.ispan.dto.ReportMovieRankingDTO;
import tw.com.ispan.dto.ReportSalesDTO;
import tw.com.ispan.dto.ReportUserConsumptionDTO;

@Service
public class ReportService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // ============================================================
    // 工具方法
    // ============================================================
    private BigDecimal toBigDecimal(Object value) {
        if (value == null)
            return BigDecimal.ZERO;
        if (value instanceof BigDecimal)
            return (BigDecimal) value;
        if (value instanceof Number)
            return new BigDecimal(((Number) value).toString());
        return BigDecimal.ZERO;
    }

    // ============================================================
    // 舊功能 — 全部保留，避免破壞 Controller
    // ============================================================

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        BigDecimal todayRevenue = jdbcTemplate.queryForObject("""
                    SELECT COALESCE(SUM(total_amount), 0)
                    FROM orders
                    WHERE order_status='COMPLETED'
                    AND CAST(order_time AS DATE)=CAST(GETDATE() AS DATE)
                """, BigDecimal.class);
        stats.put("todayRevenue", todayRevenue);

        BigDecimal monthRevenue = jdbcTemplate.queryForObject("""
                    SELECT COALESCE(SUM(total_amount), 0)
                    FROM orders
                    WHERE order_status='COMPLETED'
                    AND YEAR(order_time)=YEAR(GETDATE())
                    AND MONTH(order_time)=MONTH(GETDATE())
                """, BigDecimal.class);
        stats.put("monthRevenue", monthRevenue);

        Integer totalUsers = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users WHERE status=1", Integer.class);
        stats.put("totalUsers", totalUsers);

        Integer todayOrders = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM orders WHERE CAST(order_time AS DATE)=CAST(GETDATE() AS DATE)",
                Integer.class);
        stats.put("todayOrders", todayOrders);

        return stats;
    }

    public List<ReportSalesDTO> getSalesReport(LocalDate startDate, LocalDate endDate) {
        String sql = """
                    SELECT
                        CAST(order_time AS DATE) AS period,
                        SUM(total_amount) AS totalRevenue,
                        COUNT(*) AS totalOrders,
                        (SELECT COUNT(*) FROM order_details od WHERE od.order_id=o.id) AS totalTickets,
                        AVG(total_amount) AS averageOrderValue
                    FROM orders o
                    WHERE order_status='COMPLETED'
                      AND order_time >= ?
                      AND order_time < ?
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
                startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay());
    }

    public List<ReportSalesDTO> getMonthlySalesReport(int year) {
        String sql = """
                    SELECT
                        FORMAT(order_time,'yyyy-MM') AS period,
                        SUM(total_amount) AS totalRevenue,
                        COUNT(*) AS totalOrders,
                        (SELECT COUNT(*) FROM order_details od WHERE od.order_id=o.id) AS totalTickets,
                        AVG(total_amount) AS averageOrderValue
                    FROM orders o
                    WHERE order_status='COMPLETED'
                      AND YEAR(order_time)=?
                    GROUP BY FORMAT(order_time,'yyyy-MM')
                    ORDER BY period DESC
                """;

        return jdbcTemplate.query(sql,
                (rs, rn) -> {
                    ReportSalesDTO dto = new ReportSalesDTO();
                    dto.setPeriod(rs.getString("period"));
                    dto.setTotalRevenue(rs.getBigDecimal("totalRevenue"));
                    dto.setTotalOrders(rs.getInt("totalOrders"));
                    dto.setTotalTickets(rs.getInt("totalTickets"));
                    dto.setAverageOrderValue(rs.getBigDecimal("averageOrderValue"));
                    return dto;
                }, year);
    }

    public List<ReportMovieRankingDTO> getMovieRanking(
            LocalDate startDate, LocalDate endDate, int limit) {

        String sql = """
                    SELECT TOP (?)
                        m.id AS movieId,
                        m.title AS movieTitle,
                        COUNT(od.id) AS totalTickets,
                        SUM(od.ticket_price) AS totalRevenue,
                        COUNT(DISTINCT s.id) AS showCount
                    FROM movies m
                    JOIN shows s ON s.movie_id = m.id
                    JOIN orders o ON o.show_id = s.id
                    JOIN order_details od ON od.order_id=o.id
                    WHERE o.order_status='COMPLETED'
                      AND o.order_time >= ?
                      AND o.order_time < ?
                    GROUP BY m.id, m.title
                    ORDER BY totalTickets DESC
                """;

        return jdbcTemplate.query(sql,
                (rs, rn) -> {
                    ReportMovieRankingDTO dto = new ReportMovieRankingDTO();
                    dto.setMovieId(rs.getInt("movieId"));
                    dto.setMovieTitle(rs.getString("movieTitle"));
                    dto.setTotalTickets(rs.getInt("totalTickets"));
                    dto.setTotalRevenue(rs.getInt("totalRevenue"));
                    dto.setShowCount(rs.getInt("showCount"));
                    return dto;
                },
                limit, startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay());
    }

    public List<ReportUserConsumptionDTO> getUserConsumptionRanking(int limit) {

        String sql = """
                    SELECT TOP (?)
                        u.id AS userId,
                        u.username,
                        COALESCE(SUM(o.total_amount),0) AS totalSpending,
                        COALESCE(COUNT(od.id),0) AS totalTickets,
                        COALESCE(COUNT(DISTINCT o.id),0) AS totalOrders,
                        MAX(CAST(o.order_time AS DATE)) AS lastVisitDate
                    FROM users u
                    LEFT JOIN orders o ON o.user_id=u.id AND o.order_status='COMPLETED'
                    LEFT JOIN order_details od ON od.order_id=o.id
                    GROUP BY u.id,u.username
                    ORDER BY totalSpending DESC
                """;

        return jdbcTemplate.query(sql,
                (rs, rn) -> {
                    ReportUserConsumptionDTO dto = new ReportUserConsumptionDTO();
                    dto.setUserId(rs.getInt("userId"));
                    dto.setUsername(rs.getString("username"));
                    dto.setTotalSpending(rs.getBigDecimal("totalSpending"));
                    dto.setTotalTickets(rs.getInt("totalTickets"));
                    dto.setTotalOrders(rs.getInt("totalOrders"));
                    if (rs.getDate("lastVisitDate") != null)
                        dto.setLastVisitDate(rs.getDate("lastVisitDate").toLocalDate());
                    return dto;
                }, limit);
    }

    public List<Map<String, Object>> getScreenUsageReport(LocalDate startDate, LocalDate endDate) {
        String sql = """
                    SELECT
                        sc.name AS screenName,
                        COUNT(DISTINCT s.id) AS totalShows,
                        SUM(
                            (SELECT COUNT(*)
                             FROM order_details od
                             JOIN orders o2 ON od.order_id=o2.id
                             WHERE o2.show_id=s.id
                               AND o2.order_status='COMPLETED')
                        ) AS soldSeats,
                        sc.total_seats * COUNT(DISTINCT s.id) AS totalAvailableSeats,
                        CAST(
                            (SUM(
                                (SELECT COUNT(*)
                                 FROM order_details od
                                 JOIN orders o2 ON od.order_id=o2.id
                                 WHERE o2.show_id=s.id
                                   AND o2.order_status='COMPLETED')
                            ) *100.0) /
                            (sc.total_seats * COUNT(DISTINCT s.id))
                        AS DECIMAL(5,2)) AS occupancyRate
                    FROM screens sc
                    LEFT JOIN shows s ON s.screen_id=sc.id
                    WHERE s.show_date >= ?
                      AND s.show_date <= ?
                    GROUP BY sc.id, sc.name, sc.total_seats
                    ORDER BY occupancyRate DESC
                """;

        return jdbcTemplate.queryForList(sql, startDate, endDate);
    }

    // ============================================================
    // 🔥🔥🔥 新功能 — 視覺化報表（order_time 版本）
    // ============================================================

    public Map<String, Object> getComprehensiveSalesReport(
            LocalDate startDate, LocalDate endDate,
            List<Integer> movieIds, List<Integer> screenIds,
            List<String> ticketTypes) {

        Map<String, Object> report = new HashMap<>();

        report.put("overview", getSalesOverview(startDate, endDate, movieIds, screenIds, ticketTypes));
        report.put("trendData", getTrendData(startDate, endDate, movieIds, screenIds, ticketTypes));
        report.put("monthlyTrendData", getMonthlyTrendData(
        startDate, endDate, movieIds, screenIds, ticketTypes));
        report.put("ticketTypeDistribution",
                getTicketTypeDistribution(startDate, endDate, movieIds, screenIds, ticketTypes));
        report.put("screenTypeDistribution",
                getScreenTypeDistribution(startDate, endDate, movieIds, screenIds, ticketTypes));
        report.put("movieDistribution", getMovieDistribution(startDate, endDate, movieIds, screenIds, ticketTypes));
        report.put("topMovies", getTopMoviesForChart(startDate, endDate, movieIds, screenIds, ticketTypes, 5));

        return report;
    }

    // ============================================================
    // 🔶 KPI 顯示（order_time）
    // ============================================================
    private Map<String, Object> getSalesOverview(
            LocalDate startDate, LocalDate endDate,
            List<Integer> movieIds, List<Integer> screenIds,
            List<String> ticketTypes) {

        StringBuilder sql = new StringBuilder("""
                    SELECT
                        SUM(o.total_amount) AS totalRevenue,
                        COUNT(od.id) AS totalTickets,
                        COUNT(DISTINCT s.id) AS showCount
                    FROM orders o
                    JOIN order_details od ON o.id=od.order_id
                    JOIN shows s ON o.show_id=s.id
                    JOIN movies m ON s.movie_id=m.id
                    JOIN screens sc ON s.screen_id=sc.id
                    WHERE o.order_status='COMPLETED'
                """);

        List<Object> params = new ArrayList<>();

        sql.append(" AND o.order_time >= ? ");
        params.add(startDate.atStartOfDay());

        sql.append(" AND o.order_time < ? ");
        params.add(endDate.plusDays(1).atStartOfDay());

        if (movieIds != null && !movieIds.isEmpty()) {
            sql.append(" AND s.movie_id IN (");
            sql.append(movieIds.stream().map(String::valueOf).collect(Collectors.joining(",")));
            sql.append(") ");
        }

        if (screenIds != null && !screenIds.isEmpty()) {
            sql.append(" AND s.screen_id IN (");
            sql.append(screenIds.stream().map(String::valueOf).collect(Collectors.joining(",")));
            sql.append(") ");
        }

        if (ticketTypes != null && !ticketTypes.isEmpty()) {
            sql.append(" AND od.ticket_type IN (");
            sql.append(ticketTypes.stream().map(t -> "?").collect(Collectors.joining(",")));
            sql.append(") ");
            params.addAll(ticketTypes);
        }

        return jdbcTemplate.queryForObject(sql.toString(), params.toArray(), (rs, rn) -> {
            Map<String, Object> data = new HashMap<>();

            BigDecimal totalRevenue = toBigDecimal(rs.getObject("totalRevenue"));
            int totalTickets = rs.getInt("totalTickets");
            int showCount = rs.getInt("showCount");

            data.put("totalRevenue", totalRevenue);
            data.put("totalTickets", totalTickets);
            data.put("showtimesCount", showCount);

            data.put("averageTicketPrice",
                    totalTickets > 0 ? totalRevenue.divide(new BigDecimal(totalTickets), 2, RoundingMode.HALF_UP)
                            : BigDecimal.ZERO);

            long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
            data.put("dailyAvgRevenue",
                    days > 0 ? totalRevenue.divide(new BigDecimal(days), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO);

            return data;
        });
    }

    // ============================================================
    // 🔶 每日趨勢（order_time）
    // ============================================================
    private List<Map<String, Object>> getTrendData(
            LocalDate startDate, LocalDate endDate,
            List<Integer> movieIds, List<Integer> screenIds,
            List<String> ticketTypes) {

        StringBuilder sql = new StringBuilder("""
                    SELECT
                        CONVERT(VARCHAR(10), o.order_time, 23) AS date,
                        SUM(o.total_amount) AS revenue,
                        COUNT(od.id) AS tickets
                    FROM orders o
                    JOIN order_details od ON o.id=od.order_id
                    JOIN shows s ON o.show_id=s.id
                    WHERE o.order_status='COMPLETED'
                """);

        List<Object> params = new ArrayList<>();

        sql.append(" AND o.order_time >= ? ");
        params.add(startDate.atStartOfDay());

        sql.append(" AND o.order_time < ? ");
        params.add(endDate.plusDays(1).atStartOfDay());

        if (movieIds != null && !movieIds.isEmpty()) {
            sql.append(" AND s.movie_id IN (");
            sql.append(movieIds.stream().map(String::valueOf).collect(Collectors.joining(",")));
            sql.append(") ");
        }

        if (screenIds != null && !screenIds.isEmpty()) {
            sql.append(" AND s.screen_id IN (");
            sql.append(screenIds.stream().map(String::valueOf).collect(Collectors.joining(",")));
            sql.append(") ");
        }

        if (ticketTypes != null && !ticketTypes.isEmpty()) {
            sql.append(" AND od.ticket_type IN (");
            sql.append(ticketTypes.stream().map(t -> "?").collect(Collectors.joining(",")));
            sql.append(") ");
            params.addAll(ticketTypes);
        }

        sql.append(" GROUP BY CONVERT(VARCHAR(10), o.order_time, 23) ");
        sql.append(" ORDER BY date ");

        return jdbcTemplate.queryForList(sql.toString(), params.toArray());
    }

    // ============================================================
    // 🔷 月度趨勢（使用 order_time）
    // ============================================================
    private List<Map<String, Object>> getMonthlyTrendData(
            LocalDate startDate, LocalDate endDate,
            List<Integer> movieIds, List<Integer> screenIds,
            List<String> ticketTypes) {

        StringBuilder sql = new StringBuilder("""
                    SELECT
                        FORMAT(o.order_time, 'yyyy-MM') AS month,
                        SUM(o.total_amount) AS revenue,
                        COUNT(od.id) AS tickets
                    FROM orders o
                    JOIN order_details od ON o.id = od.order_id
                    JOIN shows s ON o.show_id = s.id
                    WHERE o.order_status = 'COMPLETED'
                """);

        List<Object> params = new ArrayList<>();

        sql.append(" AND o.order_time >= ? ");
        params.add(startDate.atStartOfDay());

        sql.append(" AND o.order_time < ? ");
        params.add(endDate.plusDays(1).atStartOfDay());

        if (movieIds != null && !movieIds.isEmpty()) {
            sql.append(" AND s.movie_id IN (" +
                    movieIds.stream().map(String::valueOf).collect(Collectors.joining(",")) +
                    ") ");
        }
        if (screenIds != null && !screenIds.isEmpty()) {
            sql.append(" AND s.screen_id IN (" +
                    screenIds.stream().map(String::valueOf).collect(Collectors.joining(",")) +
                    ") ");
        }
        if (ticketTypes != null && !ticketTypes.isEmpty()) {
            sql.append(" AND od.ticket_type IN (");
            sql.append(ticketTypes.stream().map(t -> "?").collect(Collectors.joining(",")));
            sql.append(") ");
            params.addAll(ticketTypes);
        }

        sql.append(" GROUP BY FORMAT(o.order_time, 'yyyy-MM') ORDER BY month ");

        return jdbcTemplate.queryForList(sql.toString(), params.toArray());
    }

    // ============================================================
    // 🔶 票種分布
    // ============================================================
    private List<Map<String, Object>> getTicketTypeDistribution(
            LocalDate startDate, LocalDate endDate,
            List<Integer> movieIds, List<Integer> screenIds,
            List<String> ticketTypes) {

        StringBuilder sql = new StringBuilder("""
                    SELECT
                        ISNULL(od.ticket_type, '一般票') AS name,
                        COUNT(od.id) AS value
                    FROM orders o
                    JOIN order_details od ON o.id=od.order_id
                    JOIN shows s ON o.show_id=s.id
                    WHERE o.order_status='COMPLETED'
                """);

        List<Object> params = new ArrayList<>();

        sql.append(" AND o.order_time >= ? ");
        params.add(startDate.atStartOfDay());

        sql.append(" AND o.order_time < ? ");
        params.add(endDate.plusDays(1).atStartOfDay());

        if (movieIds != null && !movieIds.isEmpty()) {
            sql.append(" AND s.movie_id IN (");
            sql.append(movieIds.stream().map(String::valueOf).collect(Collectors.joining(",")));
            sql.append(") ");
        }

        if (screenIds != null && !screenIds.isEmpty()) {
            sql.append(" AND s.screen_id IN (");
            sql.append(screenIds.stream().map(String::valueOf).collect(Collectors.joining(",")));
            sql.append(") ");
        }

        if (ticketTypes != null && !ticketTypes.isEmpty()) {
            sql.append(" AND od.ticket_type IN (");
            sql.append(ticketTypes.stream().map(t -> "?").collect(Collectors.joining(",")));
            sql.append(") ");
            params.addAll(ticketTypes);
        }

        sql.append(" GROUP BY od.ticket_type ORDER BY value DESC ");

        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql.toString(), params.toArray());

        BigDecimal total = result.stream()
                .map(r -> toBigDecimal(r.get("value")))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (total.compareTo(BigDecimal.ZERO) > 0) {
            for (Map<String, Object> r : result) {
                BigDecimal value = toBigDecimal(r.get("value"));
                r.put("percentage",
                        value.divide(total, 4, RoundingMode.HALF_UP)
                                .multiply(new BigDecimal(100)));
            }
        }

        return result;
    }

    // ============================================================
    // 🔶 影廳類型分布
    // ============================================================
    private List<Map<String, Object>> getScreenTypeDistribution(
            LocalDate startDate, LocalDate endDate,
            List<Integer> movieIds, List<Integer> screenIds,
            List<String> ticketTypes) {

        StringBuilder sql = new StringBuilder("""
                    SELECT
                        ISNULL(sc.screen_type, '一般廳') AS name,
                        SUM(o.total_amount) AS value
                    FROM orders o
                    JOIN order_details od ON o.id=od.order_id
                    JOIN shows s ON o.show_id=s.id
                    JOIN screens sc ON s.screen_id=sc.id
                    WHERE o.order_status='COMPLETED'
                """);

        List<Object> params = new ArrayList<>();

        sql.append(" AND o.order_time >= ? ");
        params.add(startDate.atStartOfDay());

        sql.append(" AND o.order_time < ? ");
        params.add(endDate.plusDays(1).atStartOfDay());

        if (movieIds != null && !movieIds.isEmpty()) {
            sql.append(" AND s.movie_id IN (");
            sql.append(movieIds.stream().map(String::valueOf).collect(Collectors.joining(",")));
            sql.append(") ");
        }

        if (screenIds != null && !screenIds.isEmpty()) {
            sql.append(" AND s.screen_id IN (");
            sql.append(screenIds.stream().map(String::valueOf).collect(Collectors.joining(",")));
            sql.append(") ");
        }

        if (ticketTypes != null && !ticketTypes.isEmpty()) {
            sql.append(" AND od.ticket_type IN (");
            sql.append(ticketTypes.stream().map(t -> "?").collect(Collectors.joining(",")));
            sql.append(") ");
            params.addAll(ticketTypes);
        }

        sql.append(" GROUP BY sc.screen_type ORDER BY value DESC ");

        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql.toString(), params.toArray());

        BigDecimal total = result.stream()
                .map(r -> toBigDecimal(r.get("value")))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (total.compareTo(BigDecimal.ZERO) > 0) {
            for (Map<String, Object> r : result) {
                BigDecimal value = toBigDecimal(r.get("value"));
                r.put("percentage",
                        value.divide(total, 4, RoundingMode.HALF_UP)
                                .multiply(new BigDecimal(100)));
            }
        }

        return result;
    }

    // ============================================================
    // 🔶 電影營收分布（TOP 10）
    // ============================================================
    private List<Map<String, Object>> getMovieDistribution(
            LocalDate startDate, LocalDate endDate,
            List<Integer> movieIds, List<Integer> screenIds,
            List<String> ticketTypes) {

        StringBuilder sql = new StringBuilder("""
                    SELECT TOP 10
                        m.title AS name,
                        SUM(o.total_amount) AS value
                    FROM orders o
                    JOIN order_details od ON o.id = od.order_id
                    JOIN shows s ON o.show_id = s.id
                    JOIN movies m ON s.movie_id = m.id
                    WHERE o.order_status = 'COMPLETED'
                """);

        List<Object> params = new ArrayList<>();

        sql.append(" AND o.order_time >= ? ");
        params.add(startDate.atStartOfDay());

        sql.append(" AND o.order_time < ? ");
        params.add(endDate.plusDays(1).atStartOfDay());

        if (movieIds != null && !movieIds.isEmpty()) {
            sql.append(" AND s.movie_id IN (");
            sql.append(movieIds.stream().map(String::valueOf).collect(Collectors.joining(",")));
            sql.append(") ");
        }

        if (screenIds != null && !screenIds.isEmpty()) {
            sql.append(" AND s.screen_id IN (");
            sql.append(screenIds.stream().map(String::valueOf).collect(Collectors.joining(",")));
            sql.append(") ");
        }

        if (ticketTypes != null && !ticketTypes.isEmpty()) {
            sql.append(" AND od.ticket_type IN (");
            sql.append(ticketTypes.stream().map(t -> "?").collect(Collectors.joining(",")));
            sql.append(") ");
            params.addAll(ticketTypes);
        }

        sql.append(" GROUP BY m.title ORDER BY value DESC ");

        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql.toString(), params.toArray());

        BigDecimal total = result.stream()
                .map(r -> toBigDecimal(r.get("value")))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (total.compareTo(BigDecimal.ZERO) > 0) {
            for (Map<String, Object> r : result) {
                BigDecimal value = toBigDecimal(r.get("value"));
                r.put("percentage",
                        value.divide(total, 4, RoundingMode.HALF_UP)
                                .multiply(new BigDecimal(100)));
            }
        }

        return result;
    }

    // ============================================================
    // 🔶 TOP N 電影
    // ============================================================
    private List<Map<String, Object>> getTopMoviesForChart(
            LocalDate startDate, LocalDate endDate,
            List<Integer> movieIds, List<Integer> screenIds,
            List<String> ticketTypes, int topN) {

        StringBuilder sql = new StringBuilder("""
                    SELECT TOP (%d)
                        m.title AS movieTitle,
                        SUM(o.total_amount) AS totalRevenue,
                        COUNT(od.id) AS ticketsSold
                    FROM orders o
                    JOIN order_details od ON o.id = od.order_id
                    JOIN shows s ON o.show_id = s.id
                    JOIN movies m ON s.movie_id = m.id
                    WHERE o.order_status = 'COMPLETED'
                """.formatted(topN));

        List<Object> params = new ArrayList<>();

        sql.append(" AND o.order_time >= ? ");
        params.add(startDate.atStartOfDay());

        sql.append(" AND o.order_time < ? ");
        params.add(endDate.plusDays(1).atStartOfDay());

        if (movieIds != null && !movieIds.isEmpty()) {
            sql.append(" AND s.movie_id IN (");
            sql.append(movieIds.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(",")));
            sql.append(") ");
        }

        if (screenIds != null && !screenIds.isEmpty()) {
            sql.append(" AND s.screen_id IN (");
            sql.append(screenIds.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(",")));
            sql.append(") ");
        }

        if (ticketTypes != null && !ticketTypes.isEmpty()) {
            sql.append(" AND od.ticket_type IN (");
            sql.append(ticketTypes.stream()
                    .map(t -> "?")
                    .collect(Collectors.joining(",")));
            sql.append(") ");
            params.addAll(ticketTypes);
        }

        sql.append(" GROUP BY m.title ORDER BY totalRevenue DESC ");

        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql.toString(), params.toArray());

        int rank = 1;
        for (Map<String, Object> row : result) {
            row.put("rank", rank++);
        }

        return result;
    }
}
