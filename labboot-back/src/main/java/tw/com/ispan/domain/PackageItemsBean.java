package tw.com.ispan.domain;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "package_items")
public class PackageItemsBean {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "package_id", referencedColumnName = "id")
@JsonBackReference
private TicketPackageBean ticketPackage;

  @Column(name = "item_type")
  private String itemType;

  @Column(name = "item_name")
  private String itemName;

  @Column(name = "item_spec")
  private String itemSpec;

  @Column(name = "quantity")
  private int quantity;

  @Column(name = "display_order")
  private int displayOrder;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public TicketPackageBean getTicketPackage() {
    return ticketPackage;
  }

  public void setTicketPackage(TicketPackageBean ticketPackage) {
    this.ticketPackage = ticketPackage;
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

  public PackageItemsBean() {
}


// 添加自動設定時間的方法
@PrePersist
protected void onCreate() {
    createdAt = LocalDateTime.now();
    updatedAt = LocalDateTime.now();
}

@PreUpdate
protected void onUpdate() {
     updatedAt = LocalDateTime.now();
}




  @Override
  public String toString() {
    return "PackageItems [id=" + id + ", packageId=" + (ticketPackage != null ? ticketPackage.getId() : null) + ", itemType=" + itemType + ", itemName="
        + itemName + ", itemSpec=" + itemSpec + ", quantity=" + quantity + ", displayOrder=" + displayOrder
        + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "]";
  }

  








}
