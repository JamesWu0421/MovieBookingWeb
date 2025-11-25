package tw.com.ispan.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用於返回給前端的 TicketPackage 響應 DTO
 */
public class TicketPackageResponseDTO {
    
    private Long id;
    private String packageType;
    private String packageName;
    private String packageCode;
    private Integer priceAdjustment;
    private Integer earlyBirdAdjustment;
    private String imageUrl;
    private Boolean isActive;
    private Integer displayOrder;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    private Boolean enableEarlyBird;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 關聯的 PackageItems (使用 ResponseDTO)
    private List<PackageItemsResponseDTO> packageItems;

    // Constructors
    public TicketPackageResponseDTO() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPackageType() {
        return packageType;
    }

    public void setPackageType(String packageType) {
        this.packageType = packageType;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public Integer getPriceAdjustment() {
        return priceAdjustment;
    }

    public void setPriceAdjustment(Integer priceAdjustment) {
        this.priceAdjustment = priceAdjustment;
    }

    public Integer getEarlyBirdAdjustment() {
        return earlyBirdAdjustment;
    }

    public void setEarlyBirdAdjustment(Integer earlyBirdAdjustment) {
        this.earlyBirdAdjustment = earlyBirdAdjustment;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public LocalDateTime getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(LocalDateTime validFrom) {
        this.validFrom = validFrom;
    }

    public LocalDateTime getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(LocalDateTime validUntil) {
        this.validUntil = validUntil;
    }

    public Boolean getEnableEarlyBird() {
        return enableEarlyBird;
    }

    public void setEnableEarlyBird(Boolean enableEarlyBird) {
        this.enableEarlyBird = enableEarlyBird;
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

    public List<PackageItemsResponseDTO> getPackageItems() {
        return packageItems;
    }

    public void setPackageItems(List<PackageItemsResponseDTO> packageItems) {
        this.packageItems = packageItems;
    }

    @Override
    public String toString() {
        return "TicketPackageResponseDTO{" +
                "id=" + id +
                ", packageType='" + packageType + '\'' +
                ", packageName='" + packageName + '\'' +
                ", packageCode='" + packageCode + '\'' +
                ", priceAdjustment=" + priceAdjustment +
                ", earlyBirdAdjustment=" + earlyBirdAdjustment +
                ", imageUrl='" + imageUrl + '\'' +
                ", isActive=" + isActive +
                ", displayOrder=" + displayOrder +
                ", validFrom=" + validFrom +
                ", validUntil=" + validUntil +
                ", enableEarlyBird=" + enableEarlyBird +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", packageItems=" + packageItems +
                '}';
    }
}
