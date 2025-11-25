package tw.com.ispan.dto;

/**
 * 創建 ShowTicketPrices 的請求 DTO
 * 只需要 showId, ticketPackageId, isAvailable
 * 其他資料會自動從資料庫計算
 */
public class ShowTicketPricesRequestDTO {

    private Integer showId;
    private Long ticketPackageId;
    private Boolean isAvailable;

    // Constructors
    public ShowTicketPricesRequestDTO() {
    }

    public ShowTicketPricesRequestDTO(Integer showId, Long ticketPackageId, Boolean isAvailable) {
        this.showId = showId;
        this.ticketPackageId = ticketPackageId;
        this.isAvailable = isAvailable;
    }

    // Getters and Setters
    public Integer getShowId() {
        return showId;
    }

    public void setShowId(Integer showId) {
        this.showId = showId;
    }

    public Long getTicketPackageId() {
        return ticketPackageId;
    }

    public void setTicketPackageId(Long ticketPackageId) {
        this.ticketPackageId = ticketPackageId;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    @Override
    public String toString() {
        return "ShowTicketPricesRequestDTO{" +
                "showId=" + showId +
                ", ticketPackageId=" + ticketPackageId +
                ", isAvailable=" + isAvailable +
                '}';
    }
}
