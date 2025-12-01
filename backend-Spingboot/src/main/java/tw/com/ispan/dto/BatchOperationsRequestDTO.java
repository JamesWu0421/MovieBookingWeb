package tw.com.ispan.dto;

/**
 * BatchOperations 請求 DTO
 * 用於接收前端傳來的批次操作資料
 */
public class BatchOperationsRequestDTO {
    
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
    
    // 無參數建構子
    public BatchOperationsRequestDTO() {
    }
    
    // 全參數建構子
    public BatchOperationsRequestDTO(Integer operatorId, String operationType, String status, 
                                     String description, Integer totalItems, 
                                     Integer successCount, Integer failCount) {
        this.operatorId = operatorId;
        this.operationType = operationType;
        this.status = status;
        this.description = description;
        this.totalItems = totalItems;
        this.successCount = successCount;
        this.failCount = failCount;
    }
    
    @Override
    public String toString() {
        return "BatchOperationsRequestDTO [operatorId=" + operatorId + 
               ", operationType=" + operationType + 
               ", status=" + status + 
               ", description=" + description + 
               ", totalItems=" + totalItems + 
               ", successCount=" + successCount + 
               ", failCount=" + failCount + "]";
    }
    
    // Getter / Setter
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