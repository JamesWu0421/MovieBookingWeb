package tw.com.ispan.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 用戶消費報表 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportUserConsumptionDTO {
    private Integer userId; // 用戶 ID
    private String username; // 用戶名稱
    private BigDecimal totalSpending; // 總消費金額
    private Integer totalTickets; // 總票數
    private Integer totalOrders; // 總訂單數
    private LocalDate lastVisitDate; // 最後訪問日期
}