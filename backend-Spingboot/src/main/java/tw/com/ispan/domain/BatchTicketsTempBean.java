package tw.com.ispan.domain;

import jakarta.persistence.*;

/**
 * 對應資料表 batch_tickets_temp
 */
@Entity
@Table(name = "batch_tickets_temp", schema = "dbo")   // schema 視你的 DB 調整
public class BatchTicketsTempBean {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "batch_id")
    private Integer batchId;

    @Column(name = "batch_session_id")
    private Integer batchSessionId;

    @Column(name = "ticket_packages_id")
    private Long ticketPackagesId;

    @Column(name = "status")
    private String status;

    @Column(name = "error_message")
    private String errorMessage;

    public BatchTicketsTempBean() {
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