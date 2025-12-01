package tw.com.ispan.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "ticket_packages")
public class TicketPackageBean {


  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  
  @Column(name = "package_type")
  private String packageType;

  @Column(name = "package_name")
  private String packageName;

  @Column(name = "package_code")
  private String packageCode;

  @Column(name = "price_adjustment")
  private int priceAdjustment;

  @Column(name = "early_bird_adjustment")
  private int earlyBirdAdjustment;

  @Column(name = "image_url")
  private String imageUrl;

  @Column(name = "is_active")
  private Boolean isActive;

  @Column(name = "display_order")
  private int displayOrder; 

  @Column(name = "valid_from")
  private LocalDateTime validFrom;

  @Column(name = "valid_until")
  private LocalDateTime validUntil;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @Column(name = "enable_early_bird")
  private Boolean enableEarlyBird = true;

  @OneToMany(mappedBy = "ticketPackage", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference
  private List<PackageItemsBean> packageItems = new ArrayList<>();

  @OneToMany(mappedBy = "ticketPackage", cascade = CascadeType.ALL, orphanRemoval = true)
@JsonManagedReference
private List<ShowTicketPricesBean> showTicketPrices;


  @Override
  public String toString() {
    return "TicketPackageBean [id=" + id + ", packageType=" + packageType + ", packageName=" + packageName
        + ", packageCode=" + packageCode + ", priceAdjustment=" + priceAdjustment + ", earlyBirdAdjustment="
        + earlyBirdAdjustment + ", imageUrl=" + imageUrl + ", isActive=" + isActive + ", displayOrder=" + displayOrder
        + ", validFrom=" + validFrom + ", validUntil=" + validUntil + ", createdAt=" + createdAt + ", updatedAt="
        + updatedAt + ", enableEarlyBird=" + enableEarlyBird + ", packageItems=" + packageItems + ", showTicketPrices="
        + showTicketPrices + "]";
  }

  



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

  public int getPriceAdjustment() {
    return priceAdjustment;
  }

  public void setPriceAdjustment(int priceAdjustment) {
    this.priceAdjustment = priceAdjustment;
  }

  public int getEarlyBirdAdjustment() {
    return earlyBirdAdjustment;
  }

  public void setEarlyBirdAdjustment(int earlyBirdAdjustment) {
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

  public int getDisplayOrder() {
    return displayOrder;
  }

  public void setDisplayOrder(int displayOrder) {
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

  public List<PackageItemsBean> getPackageItems() {
    return packageItems;
  }

  public void setPackageItems(List<PackageItemsBean> packageItems) {
    this.packageItems = packageItems;
  }

  public Boolean getEnableEarlyBird() {
    return enableEarlyBird;
}

public void setEnableEarlyBird(Boolean enableEarlyBird) {
    this.enableEarlyBird = enableEarlyBird;
}

  // 添加無參數建構子
public TicketPackageBean() {
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

public void addPackageItem(PackageItemsBean item) {
      packageItems.add(item);
      item.setTicketPackage(this);
  }

  public void removePackageItem(PackageItemsBean item) {
      packageItems.remove(item);
      item.setTicketPackage(null);
  }

  
}
