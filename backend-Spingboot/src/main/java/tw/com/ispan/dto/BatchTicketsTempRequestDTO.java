package tw.com.ispan.dto;

/**
 * 建立 / 更新 batch_tickets_temp 用的請求 DTO
 */
public class BatchTicketsTempRequestDTO {

    private Integer batchId;
    private Integer batchSessionId;
    private Long ticketPackagesId;
    private String status;        // 可為 null -> service 預設 pending
    private String errorMessage;  // 可為 null

    public BatchTicketsTempRequestDTO() {
    }

    // ===== Getter / Setter =====

    public Integer getBatchId() {
        return batchId;
    }

    public void setBatchId(Integer batchId) {
        this.batchId = batchId;
    }

    public Integer getBatchSessionId() {
        return batchSessionId;
    }

    public void setBatchSessionId(Integer batchSessionId) {
        this.batchSessionId = batchSessionId;
    }

    public Long getTicketPackagesId() {
        return ticketPackagesId;
    }

    public void setTicketPackagesId(Long ticketPackagesId) {
        this.ticketPackagesId = ticketPackagesId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}






