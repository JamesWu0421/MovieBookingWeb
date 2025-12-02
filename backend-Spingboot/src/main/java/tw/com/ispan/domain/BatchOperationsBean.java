package tw.com.ispan.domain;

import jakarta.persistence.*;
import tw.com.ispan.entity.EmpEntity;
import java.time.LocalDateTime;

/**
 * BatchOperations 實體類 (方案2：同時保留 operatorId + operator)
 * 對應 batch_operations 資料表
 */
@Entity
@Table(name = "batch_operations", schema = "dbo")
public class BatchOperationsBean {

    /** 批次操作ID (主鍵) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "batch_id")
    private Integer batchId;

    // ========== 方案2：同時保留兩個屬性 ==========
    
    /** 操作員ID - 只讀欄位（用於高效查詢） */
    @Column(name = "operator_id", insertable = false, updatable = false)
    private Integer operatorId;

    /** 操作員關聯 - 用於讀寫操作 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operator_id", referencedColumnName = "id")
    private EmpEntity operator;
    
    // ===========================================

    /** 操作類型 */
    @Column(name = "operation_type")
    private String operationType;

    /** 狀態 */
    @Column(name = "status")
    private String status;

    /** 描述 */
    @Column(name = "description")
    private String description;

    /** 建立時間 */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /** 執行時間 */
    @Column(name = "executed_at")
    private LocalDateTime executedAt;

    /** 完成時間 */
    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    /** 總項目數 */
    @Column(name = "total_items")
    private Integer totalItems;

    /** 成功數量 */
    @Column(name = "success_count")
    private Integer successCount;

    /** 失敗數量 */
    @Column(name = "fail_count")
    private Integer failCount;

    // ========== 建構子 ==========
    
    public BatchOperationsBean() {
    }

    // ========== Getter / Setter ==========

    public Integer getBatchId() {
        return batchId;
    }

    public void setBatchId(Integer batchId) {
        this.batchId = batchId;
    }

    public Integer getOperatorId() {
        return operatorId;
    }

    // ⚠️ 注意：不要有 setOperatorId()，因為它是 read-only
    // 要設定操作員請使用 setOperator(EmpEntity)

    public EmpEntity getOperator() {
        return operator;
    }

    public void setOperator(EmpEntity operator) {
        this.operator = operator;
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
}