package com.example.movie.dto;

import java.math.BigDecimal;

/**
 * 營收報表 DTO
 */
public class ReportSalesDTO {
    private String period;              // 日期或月份 (例: 2024-11-13 或 2024-11)
    private BigDecimal totalRevenue;    // 總營收
    private Integer totalOrders;        // 訂單數
    private Integer totalTickets;       // 票數
    private BigDecimal averageOrderValue; // 平均訂單金額

    // Constructors
    public ReportSalesDTO() {
    }

    public ReportSalesDTO(String period, BigDecimal totalRevenue, Integer totalOrders, 
                         Integer totalTickets, BigDecimal averageOrderValue) {
        this.period = period;
        this.totalRevenue = totalRevenue;
        this.totalOrders = totalOrders;
        this.totalTickets = totalTickets;
        this.averageOrderValue = averageOrderValue;
    }

    // Getters and Setters
    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public Integer getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(Integer totalOrders) {
        this.totalOrders = totalOrders;
    }

    public Integer getTotalTickets() {
        return totalTickets;
    }

    public void setTotalTickets(Integer totalTickets) {
        this.totalTickets = totalTickets;
    }

    public BigDecimal getAverageOrderValue() {
        return averageOrderValue;
    }

    public void setAverageOrderValue(BigDecimal averageOrderValue) {
        this.averageOrderValue = averageOrderValue;
    }

    @Override
    public String toString() {
        return "ReportSalesDTO{" +
                "period='" + period + '\'' +
                ", totalRevenue=" + totalRevenue +
                ", totalOrders=" + totalOrders +
                ", totalTickets=" + totalTickets +
                ", averageOrderValue=" + averageOrderValue +
                '}';
    }
}
