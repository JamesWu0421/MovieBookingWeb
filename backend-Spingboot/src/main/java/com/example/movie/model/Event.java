package com.example.movie.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50)
    private String category; // 'promotion', 'announcement'

    @Column(nullable = false, length = 200)
    private String name;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String notes;

    @Column(name = "image_url", length = 300)
    private String imageUrl;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "discount_type", length = 20)
    private String discountType; // 'percentage', 'fixed', 'lottery'

    @Column(name = "discount_value", precision = 10, scale = 2)
    private BigDecimal discountValue;

    // 🆕 新增：獎品說明（抽獎活動用）
    @Column(name = "prize_description", columnDefinition = "NVARCHAR(MAX)")
    private String prizeDescription;

    // 🆕 新增：抽獎資格（抽獎活動用）
    @Column(name = "lottery_requirement", length = 200)
    private String lotteryRequirement;

    @Column(name = "min_amount")
    private Integer minAmount;

    @Column(name = "max_usage_per_user")
    private Integer maxUsagePerUser;

    @Column(name = "current_usage_count")
    private Integer currentUsageCount = 0;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "requires_coupon")
    private Boolean requiresCoupon = false;

    @Column(name = "coupon_code", length = 50)
    private String couponCode;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public String getDiscountType() {
        return discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public BigDecimal getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(BigDecimal discountValue) {
        this.discountValue = discountValue;
    }

    // 🆕 新增 Getter/Setter
    public String getPrizeDescription() {
        return prizeDescription;
    }

    public void setPrizeDescription(String prizeDescription) {
        this.prizeDescription = prizeDescription;
    }

    public String getLotteryRequirement() {
        return lotteryRequirement;
    }

    public void setLotteryRequirement(String lotteryRequirement) {
        this.lotteryRequirement = lotteryRequirement;
    }

    public Integer getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(Integer minAmount) {
        this.minAmount = minAmount;
    }

    public Integer getMaxUsagePerUser() {
        return maxUsagePerUser;
    }

    public void setMaxUsagePerUser(Integer maxUsagePerUser) {
        this.maxUsagePerUser = maxUsagePerUser;
    }

    public Integer getCurrentUsageCount() {
        return currentUsageCount;
    }

    public void setCurrentUsageCount(Integer currentUsageCount) {
        this.currentUsageCount = currentUsageCount;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Boolean getRequiresCoupon() {
        return requiresCoupon;
    }

    public void setRequiresCoupon(Boolean requiresCoupon) {
        this.requiresCoupon = requiresCoupon;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
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

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (currentUsageCount == null) {
            currentUsageCount = 0;
        }
        if (isActive == null) {
            isActive = true;
        }
        if (requiresCoupon == null) {
            requiresCoupon = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}