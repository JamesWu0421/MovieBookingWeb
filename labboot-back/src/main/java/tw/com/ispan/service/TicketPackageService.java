package tw.com.ispan.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import tw.com.ispan.domain.PackageItemsBean;
import tw.com.ispan.domain.TicketPackageBean;
import tw.com.ispan.repository.TicketPackageRepository;

@Service
@Transactional
public class TicketPackageService {

    @Autowired
    private TicketPackageRepository repository;

    // ==================== Create ====================
    public TicketPackageBean createTicketPackage(TicketPackageBean ticketPackage) {
        ticketPackage.setCreatedAt(LocalDateTime.now());
        ticketPackage.setUpdatedAt(LocalDateTime.now());
        
        // ⭐ 確保新增時也設定正確的關聯
        if (ticketPackage.getPackageItems() != null) {
            for (PackageItemsBean item : ticketPackage.getPackageItems()) {
                item.setTicketPackage(ticketPackage);
            }
        }
        
        return repository.save(ticketPackage);
    }

    // ==================== Read All ====================
    public List<TicketPackageBean> getAllTicketPackages() {
        return repository.findAll();
    }

    // ==================== Read by ID ====================
    public Optional<TicketPackageBean> getTicketPackageById(Long id) {
        return repository.findById(id);
    }

    // ==================== Update ====================
    // ⭐⭐⭐ 這是關鍵修復！⭐⭐⭐
    public Optional<TicketPackageBean> updateTicketPackage(Long id, TicketPackageBean ticketPackageDetails) {
        Optional<TicketPackageBean> oldData = repository.findById(id);
        if (oldData.isPresent()) {
            TicketPackageBean ticketPackage = oldData.get();

            // 更新基本欄位（保持不變）
            ticketPackage.setPackageType(ticketPackageDetails.getPackageType());
            ticketPackage.setPackageName(ticketPackageDetails.getPackageName());
            ticketPackage.setPackageCode(ticketPackageDetails.getPackageCode());
            ticketPackage.setPriceAdjustment(ticketPackageDetails.getPriceAdjustment());
            ticketPackage.setEarlyBirdAdjustment(ticketPackageDetails.getEarlyBirdAdjustment());
            ticketPackage.setImageUrl(ticketPackageDetails.getImageUrl());
            ticketPackage.setIsActive(ticketPackageDetails.getIsActive());
            ticketPackage.setDisplayOrder(ticketPackageDetails.getDisplayOrder());
            ticketPackage.setValidFrom(ticketPackageDetails.getValidFrom());
            ticketPackage.setValidUntil(ticketPackageDetails.getValidUntil());
            ticketPackage.setEnableEarlyBird(ticketPackageDetails.getEnableEarlyBird());
            
            // ⭐⭐⭐ 關鍵修復：正確處理 packageItems ⭐⭐⭐
            if (ticketPackageDetails.getPackageItems() != null) {
                // 1. 清除舊的項目（orphanRemoval = true 會自動刪除資料庫記錄）
                ticketPackage.getPackageItems().clear();
                
                // 2. 添加新的項目，並設定正確的雙向關聯
                for (PackageItemsBean newItem : ticketPackageDetails.getPackageItems()) {
                    // 確保是新記錄（不帶 id）
                    newItem.setId(null);
                    // ⭐ 最關鍵：設定父對象引用
                    newItem.setTicketPackage(ticketPackage);
                    // 添加到集合
                    ticketPackage.getPackageItems().add(newItem);
                }
            }
            
            ticketPackage.setUpdatedAt(LocalDateTime.now());

            return Optional.of(repository.save(ticketPackage));
        }
        return Optional.empty();
    }

    // ==================== Delete ====================
    public boolean deleteTicketPackage(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }
}




