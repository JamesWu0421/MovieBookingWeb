package com.example.movie.service;

import com.example.movie.dto.ReportMovieRankingDTO;
import com.example.movie.dto.ReportSalesDTO;
import com.example.movie.dto.ReportUserConsumptionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

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
            startDate, endDate
        );
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
            year
        );
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
            limit, startDate, endDate
        );
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
            limit
        );
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
}
