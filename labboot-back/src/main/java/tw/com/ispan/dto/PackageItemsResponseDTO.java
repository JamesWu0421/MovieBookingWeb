package tw.com.ispan.dto;

import java.time.LocalDateTime;

/**
 * 用於返回給前端的 PackageItems 響應 DTO
 */
public class PackageItemsResponseDTO {
    
    private Long id;
    private Long packageId;
    private String itemType;
    private String itemName;
    private String itemSpec;
    private int quantity;
    private int displayOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public PackageItemsResponseDTO() {
    }

    public PackageItemsResponseDTO(Long id, Long packageId, String itemType, String itemName, 
                                   String itemSpec, int quantity, int displayOrder, 
                                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.packageId = packageId;
        this.itemType = itemType;
        this.itemName = itemName;
        this.itemSpec = itemSpec;
        this.quantity = quantity;
        this.displayOrder = displayOrder;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPackageId() {
        return packageId;
    }

    public void setPackageId(Long packageId) {
        this.packageId = packageId;
    }

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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
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
        return "PackageItemsResponseDTO{" +
                "id=" + id +
                ", packageId=" + packageId +
                ", itemType='" + itemType + '\'' +
                ", itemName='" + itemName + '\'' +
                ", itemSpec='" + itemSpec + '\'' +
                ", quantity=" + quantity +
                ", displayOrder=" + displayOrder +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
