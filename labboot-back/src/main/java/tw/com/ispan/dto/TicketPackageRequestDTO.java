package tw.com.ispan.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用於接收前端創建/更新 TicketPackage 的請求 DTO
 */
public class TicketPackageRequestDTO {
    
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
    
    // ⭐ 新增：直接接收完整的 packageItems 物件陣列
    private List<PackageItemDTO> packageItems;
    
    // 保留舊的 (向後兼容，但優先使用 packageItems)
    private List<Long> packageItemIds;

    // ⭐ 內部類：用於接收前端傳來的 packageItem 資料
    public static class PackageItemDTO {
        private String itemType;
        private String itemName;
        private String itemSpec;
        private Integer quantity;
        private Integer displayOrder;

        // Getters and Setters
        public String getItemType() {
            return itemType;
        }

        public void setItemType(String itemType) {
            this.itemType = itemType;
        }

        public String getItemName() {
            return itemName;
        }

        public void setItemName(String itemName) {
            this.itemName = itemName;
        }

        public String getItemSpec() {
            return itemSpec;
        }

        public void setItemSpec(String itemSpec) {
            this.itemSpec = itemSpec;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        public Integer getDisplayOrder() {
            return displayOrder;
        }

        public void setDisplayOrder(Integer displayOrder) {
            this.displayOrder = displayOrder;
        }

        @Override
        public String toString() {
            return "PackageItemDTO{" +
                    "itemType='" + itemType + '\'' +
                    ", itemName='" + itemName + '\'' +
                    ", itemSpec='" + itemSpec + '\'' +
                    ", quantity=" + quantity +
                    ", displayOrder=" + displayOrder +
                    '}';
        }
    }

    // Constructors
    public TicketPackageRequestDTO() {
    }

    // Getters and Setters
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

    public List<PackageItemDTO> getPackageItems() {
        return packageItems;
    }

    public void setPackageItems(List<PackageItemDTO> packageItems) {
        this.packageItems = packageItems;
    }

    public List<Long> getPackageItemIds() {
        return packageItemIds;
    }

    public void setPackageItemIds(List<Long> packageItemIds) {
        this.packageItemIds = packageItemIds;
    }

    @Override
    public String toString() {
        return "TicketPackageRequestDTO{" +
                "packageType='" + packageType + '\'' +
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
                ", packageItems=" + packageItems +
                ", packageItemIds=" + packageItemIds +
                '}';
    }
}


















