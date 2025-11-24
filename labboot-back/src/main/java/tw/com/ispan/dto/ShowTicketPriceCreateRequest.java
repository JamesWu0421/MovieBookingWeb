package tw.com.ispan.dto;

public class ShowTicketPriceCreateRequest {

    private Integer showId;
    private Long ticketPackageId;
    private Boolean isAvailable;

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
}

