package tw.com.ispan.dto;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * ShowTicketPrices 的響應 DTO
 */
public class ShowTicketPricesResponseDTO {
    
    private Long id;
    private Integer showId;
    
    // TicketPackage 相關資訊
    private Long ticketPackageId;
    private String ticketPackageName;
    private String ticketPackageCode;
    
    // 場次時間資訊
    private LocalTime startTime;
    private LocalTime endTime;
    
    // 電影/廳別資訊
    private Integer movieDuration;
    private Integer screenBasePrice;
    
    // 計價相關
    private Integer ticketAdjustment;
    private Boolean earlyBird;
    private Integer earlyBirdAdjustment;
    private Integer durationSurcharge;
    private Integer finalPrice;
    
    // 可售狀態
    private Boolean available;
    
    // 時間戳記
    private LocalDateTime calculatedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public ShowTicketPricesResponseDTO() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getTicketPackageName() {
        return ticketPackageName;
    }

    public void setTicketPackageName(String ticketPackageName) {
        this.ticketPackageName = ticketPackageName;
    }

    public String getTicketPackageCode() {
        return ticketPackageCode;
    }

    public void setTicketPackageCode(String ticketPackageCode) {
        this.ticketPackageCode = ticketPackageCode;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public Integer getMovieDuration() {
        return movieDuration;
    }

    public void setMovieDuration(Integer movieDuration) {
        this.movieDuration = movieDuration;
    }

    public Integer getScreenBasePrice() {
        return screenBasePrice;
    }

    public void setScreenBasePrice(Integer screenBasePrice) {
        this.screenBasePrice = screenBasePrice;
    }

    public Integer getTicketAdjustment() {
        return ticketAdjustment;
    }

    public void setTicketAdjustment(Integer ticketAdjustment) {
        this.ticketAdjustment = ticketAdjustment;
    }

    public Boolean getEarlyBird() {
        return earlyBird;
    }

    public void setEarlyBird(Boolean earlyBird) {
        this.earlyBird = earlyBird;
    }

    public Integer getEarlyBirdAdjustment() {
        return earlyBirdAdjustment;
    }

    public void setEarlyBirdAdjustment(Integer earlyBirdAdjustment) {
        this.earlyBirdAdjustment = earlyBirdAdjustment;
    }

    public Integer getDurationSurcharge() {
        return durationSurcharge;
    }

    public void setDurationSurcharge(Integer durationSurcharge) {
        this.durationSurcharge = durationSurcharge;
    }

    public Integer getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(Integer finalPrice) {
        this.finalPrice = finalPrice;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public LocalDateTime getCalculatedAt() {
        return calculatedAt;
    }

    public void setCalculatedAt(LocalDateTime calculatedAt) {
        this.calculatedAt = calculatedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "ShowTicketPricesResponseDTO{" +
                "id=" + id +
                ", showId=" + showId +
                ", ticketPackageId=" + ticketPackageId +
                ", ticketPackageName='" + ticketPackageName + '\'' +
                ", ticketPackageCode='" + ticketPackageCode + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", movieDuration=" + movieDuration +
                ", screenBasePrice=" + screenBasePrice +
                ", ticketAdjustment=" + ticketAdjustment +
                ", earlyBird=" + earlyBird +
                ", earlyBirdAdjustment=" + earlyBirdAdjustment +
                ", durationSurcharge=" + durationSurcharge +
                ", finalPrice=" + finalPrice +
                ", available=" + available +
                ", calculatedAt=" + calculatedAt +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
