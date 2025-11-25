package com.example.movie.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 用戶消費報表 DTO
 */
public class ReportUserConsumptionDTO {
    private Integer userId;             // 用戶 ID
    private String username;            // 用戶名稱
    private BigDecimal totalSpending;   // 總消費金額
    private Integer totalTickets;       // 總票數
    private Integer totalOrders;        // 總訂單數
    private LocalDate lastVisitDate;    // 最後訪問日期

    // Constructors
    public ReportUserConsumptionDTO() {
    }

    public ReportUserConsumptionDTO(Integer userId, String username, BigDecimal totalSpending,
                                   Integer totalTickets, Integer totalOrders, LocalDate lastVisitDate) {
        this.userId = userId;
        this.username = username;
        this.totalSpending = totalSpending;
        this.totalTickets = totalTickets;
        this.totalOrders = totalOrders;
        this.lastVisitDate = lastVisitDate;
    }

    // Getters and Setters
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public BigDecimal getTotalSpending() {
        return totalSpending;
    }

    public void setTotalSpending(BigDecimal totalSpending) {
        this.totalSpending = totalSpending;
    }

    public Integer getTotalTickets() {
        return totalTickets;
    }

    public void setTotalTickets(Integer totalTickets) {
        this.totalTickets = totalTickets;
    }

    public Integer getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(Integer totalOrders) {
        this.totalOrders = totalOrders;
    }

    public LocalDate getLastVisitDate() {
        return lastVisitDate;
    }

    public void setLastVisitDate(LocalDate lastVisitDate) {
        this.lastVisitDate = lastVisitDate;
    }

    @Override
    public String toString() {
        return "ReportUserConsumptionDTO{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", totalSpending=" + totalSpending +
                ", totalTickets=" + totalTickets +
                ", totalOrders=" + totalOrders +
                ", lastVisitDate=" + lastVisitDate +
                '}';
    }
}
