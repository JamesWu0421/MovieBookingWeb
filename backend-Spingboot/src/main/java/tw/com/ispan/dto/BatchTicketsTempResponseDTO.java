package tw.com.ispan.dto;

/**
 * 回傳 batch_tickets_temp 用的 DTO
 */
public class BatchTicketsTempResponseDTO {

    private Integer id;
    private Integer batchId;
    private Integer batchSessionId;
    private Long ticketPackagesId;
    private String status;
    private String errorMessage;

    public BatchTicketsTempResponseDTO() {
    }

    // ===== Getter / Setter =====

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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