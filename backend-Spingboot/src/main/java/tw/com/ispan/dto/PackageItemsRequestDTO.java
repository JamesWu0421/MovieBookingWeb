package tw.com.ispan.dto;

/**
 * 用於接收前端創建/更新 PackageItems 的請求 DTO
 */
public class PackageItemsRequestDTO {
    
    private Long packageId;
    private String itemType;
    private String itemName;
    private String itemSpec;
    private int quantity;
    private int displayOrder;

    // Constructors
    public PackageItemsRequestDTO() {
    }

    public PackageItemsRequestDTO(Long packageId, String itemType, String itemName, 
                                  String itemSpec, int quantity, int displayOrder) {
        this.packageId = packageId;
        this.itemType = itemType;
        this.itemName = itemName;
        this.itemSpec = itemSpec;
        this.quantity = quantity;
        this.displayOrder = displayOrder;
    }

    // Getters and Setters
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

    @Override
    public String toString() {
        return "PackageItemsRequestDTO{" +
                "packageId=" + packageId +
                ", itemType='" + itemType + '\'' +
                ", itemName='" + itemName + '\'' +
                ", itemSpec='" + itemSpec + '\'' +
                ", quantity=" + quantity +
                ", displayOrder=" + displayOrder +
                '}';
    }
}






