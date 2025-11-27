package com.example.movie.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

/**
 * 營收報表 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportSalesDTO {
    private String period; // 日期或月份 (例: 2024-11-13 或 2024-11)
    private BigDecimal totalRevenue; // 總營收
    private Integer totalOrders; // 訂單數
    private Integer totalTickets; // 票數
    private BigDecimal averageOrderValue; // 平均訂單金額
}