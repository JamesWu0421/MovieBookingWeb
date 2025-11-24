package tw.com.ispan.domain;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.LocalTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "show_ticket_prices")
public class ShowTicketPricesBean {

    // ========== PK ==========
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;   // 對應 BIGINT IDENTITY(1,1)

    // ========== Foreign Keys ==========

    /**
     * show_id int
     * 場次ID，透過這個可以找到 movie 和 screen（在 Service 用 showId 去查 shows）
     */
    @Column(name = "show_id", nullable = false)
    private Integer showId;  // 對應 INT NOT NULL

    /**
     * ticket_package_id BIGINT
     * 使用 LAZY + JsonBackReference 避免序列化時觸發 lazy loading / 迴圈
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_package_id", referencedColumnName = "id", nullable = false)
    @JsonBackReference
    private TicketPackageBean ticketPackage;

    // ========== 場次 / 電影 / 廳別資料（從其他表算出來後存進來） ==========

    /**
     * 場次開始時間（從 shows.show_time 取得，用於判斷早場）
     */
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    /**
     * 場次結束時間（從 shows.end_time 取得）
     */
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    /**
     * 電影時長（分鐘），從 shows → movies.runtime_minutes 取得
     */
    @Column(name = "movie_duration", nullable = false)
    private Integer movieDuration;

    /**
     * 廳別基礎價（從 shows → screens.base_price 取得）
     */
    @Column(name = "screen_base_price", nullable = false)
    private Integer screenBasePrice;

    // ========== 計價相關欄位（計算結果） ==========

    /**
     * 票種調整金額（例如學生票、優待票差額）
     */
    @Column(name = "ticket_adjustment", nullable = false)
    private Integer ticketAdjustment = 0;   // 對應 DEFAULT 0

    /**
     * 是否早鳥
     */
    @Column(name = "is_early_bird", nullable = false)
    private Boolean earlyBird = false;      // 對應 BIT NOT NULL DEFAULT 0

    /**
     * 早鳥加價 / 減價
     */
    @Column(name = "early_bird_adjustment", nullable = false)
    private Integer earlyBirdAdjustment = 0;

    /**
     * 片長加價（金額），例如超過 150 分鐘加價
     */
    @Column(name = "duration_surcharge", nullable = false)
    private Integer durationSurcharge = 0;

    /**
     * 最終票價（screen_base_price + 各種調整）
     */
    @Column(name = "final_price", nullable = false)
    private Integer finalPrice;

    /**
     * 此組價格是否目前可售
     */
    @Column(name = "is_available", nullable = false)
    private Boolean available = true;       // 對應 BIT NOT NULL DEFAULT 1

    // ========== 時間欄位 ==========

    /**
     * 此筆價格是何時算出來的（可用 DB default GETDATE 或在 Service 手動塞）
     */
    @Column(name = "calculated_at", nullable = false)
    private LocalDateTime calculatedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ========== Constructors ==========

    public ShowTicketPricesBean() {
    }

    public ShowTicketPricesBean(
            Integer showId,
            TicketPackageBean ticketPackage,
            LocalTime startTime,
            LocalTime endTime,
            Integer movieDuration,
            Integer screenBasePrice,
            Integer ticketAdjustment,
            Boolean earlyBird,
            Integer earlyBirdAdjustment,
            Integer durationSurcharge,
            Integer finalPrice,
            Boolean available,
            LocalDateTime calculatedAt
    ) {
        this.showId = showId;
        this.ticketPackage = ticketPackage;
        this.startTime = startTime;
        this.endTime = endTime;
        this.movieDuration = movieDuration;
        this.screenBasePrice = screenBasePrice;
        this.ticketAdjustment = ticketAdjustment;
        this.earlyBird = earlyBird;
        this.earlyBirdAdjustment = earlyBirdAdjustment;
        this.durationSurcharge = durationSurcharge;
        this.finalPrice = finalPrice;
        this.available = available;
        this.calculatedAt = calculatedAt;
    }

    // ========== Getters / Setters ==========

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

    public TicketPackageBean getTicketPackage() {
        return ticketPackage;
    }

    public void setTicketPackage(TicketPackageBean ticketPackage) {
        this.ticketPackage = ticketPackage;
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

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @JsonProperty("ticketPackageId")
    public Long getTicketPackageId() {
        return ticketPackage != null ? ticketPackage.getId() : null;
    }

    @JsonProperty("ticketPackageName")
    public String getTicketPackageName() {
        return ticketPackage != null ? ticketPackage.getPackageName() : null;
    }

    @JsonProperty("ticketPackageCode")
    public String getTicketPackageCode() {
        return ticketPackage != null ? ticketPackage.getPackageCode() : null;
    }

    // 看需求要不要覆寫 toString，通常會避開 lazy 關聯（ticketPackage）
//    @Override
//    public String toString() {
//        return "ShowTicketPricesBean{id=" + id
//                + ", showId=" + showId
//                + ", startTime=" + startTime
//                + ", endTime=" + endTime
//                + ", movieDuration=" + movieDuration
//                + ", screenBasePrice=" + screenBasePrice
//                + ", ticketAdjustment=" + ticketAdjustment
//                + ", earlyBird=" + earlyBird
//                + ", earlyBirdAdjustment=" + earlyBirdAdjustment
//                + ", durationSurcharge=" + durationSurcharge
//                + ", finalPrice=" + finalPrice
//                + ", available=" + available
//                + ", calculatedAt=" + calculatedAt
//                + ", createdAt=" + createdAt
//                + ", updatedAt=" + updatedAt + '}';
//    }
}
