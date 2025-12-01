package tw.com.ispan.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

/**
 * BatchOperations 回應 DTO
 * 用於回傳批次操作資料給前端
 */
public class BatchOperationsResponseDTO {
    
    /**
     * 批次操作ID
     */
    private Integer batchId;
    
    /**
     * 操作員ID
     */
    private Integer operatorId;
    
    /**
     * 操作類型
     */
    private String operationType;
    
    /**
     * 狀態
     */
    private String status;
    
    /**
     * 描述
     */
    private String description;
    
    /**
     * 建立時間
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    /**
     * 執行時間
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime executedAt;
    
    /**
     * 完成時間
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime completedAt;
    
    /**
     * 總項目數
     */
    private Integer totalItems;
    
    /**
     * 成功數量
     */
    private Integer successCount;
    
    /**
     * 失敗數量
     */
    private Integer failCount;
    
    /**
     * 失敗率 (計算屬性)
     */
    private Double failureRate;
    
    /**
     * 成功率 (計算屬性)
     */
    private Double successRate;
    
    // 無參數建構子
    public BatchOperationsResponseDTO() {
    }
    
    // 全參數建構子
    public BatchOperationsResponseDTO(Integer batchId, Integer operatorId, String operationType, 
                                      String status, String description, LocalDateTime createdAt, 
                                      LocalDateTime executedAt, LocalDateTime completedAt, 
                                      Integer totalItems, Integer successCount, Integer failCount, 
                                      Double failureRate, Double successRate) {
        this.batchId = batchId;
        this.operatorId = operatorId;
        this.operationType = operationType;
        this.status = status;
        this.description = description;
        this.createdAt = createdAt;
        this.executedAt = executedAt;
        this.completedAt = completedAt;
        this.totalItems = totalItems;
        this.successCount = successCount;
        this.failCount = failCount;
        this.failureRate = failureRate;
        this.successRate = successRate;
    }
    
    @Override
    public String toString() {
        return "BatchOperationsResponseDTO [batchId=" + batchId + 
               ", operatorId=" + operatorId + 
               ", operationType=" + operationType + 
               ", status=" + status + 
               ", description=" + description + 
               ", createdAt=" + createdAt + 
               ", executedAt=" + executedAt + 
               ", completedAt=" + completedAt + 
               ", totalItems=" + totalItems + 
               ", successCount=" + successCount + 
               ", failCount=" + failCount + 
               ", successRate=" + successRate + 
               ", failureRate=" + failureRate + "]";
    }
    
    // Getter / Setter
    public Integer getBatchId() {
        return batchId;
    }
    
    public void setBatchId(Integer batchId) {
        this.batchId = batchId;
    }
    
    public Integer getOperatorId() {
        return operatorId;
    }
    
    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }
    
    public String getOperationType() {
        return operationType;
    }
    
    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getExecutedAt() {
        return executedAt;
    }
    
    public void setExecutedAt(LocalDateTime executedAt) {
        this.executedAt = executedAt;
    }
    
    public LocalDateTime getCompletedAt() {
        return completedAt;
    }
    
    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
    
    public Integer getTotalItems() {
        return totalItems;
    }
    
    public void setTotalItems(Integer totalItems) {
        this.totalItems = totalItems;
    }
    
    public Integer getSuccessCount() {
        return successCount;
    }
    
    public void setSuccessCount(Integer successCount) {
        this.successCount = successCount;
    }
    
    public Integer getFailCount() {
        return failCount;
    }
    
    public void setFailCount(Integer failCount) {
        this.failCount = failCount;
    }
    
    public Double getFailureRate() {
        return failureRate;
    }
    
    public void setFailureRate(Double failureRate) {
        this.failureRate = failureRate;
    }
    
    public Double getSuccessRate() {
        return successRate;
    }
    
    public void setSuccessRate(Double successRate) {
        this.successRate = successRate;
    }
}