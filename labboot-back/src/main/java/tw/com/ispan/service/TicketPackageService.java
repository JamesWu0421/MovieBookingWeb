package tw.com.ispan.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
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
    public Optional<TicketPackageBean> updateTicketPackage(Long id, TicketPackageBean ticketPackageDetails) {
        Optional<TicketPackageBean> oldData = repository.findById(id);
        if (oldData.isPresent()) {
            TicketPackageBean ticketPackage = oldData.get();

            // 更新基本欄位
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
            
            // ⭐ 新增：更新 enableEarlyBird 欄位（前端有傳送但原本的 Service 沒處理）
            ticketPackage.setEnableEarlyBird(ticketPackageDetails.getEnableEarlyBird());
            
            // ⭐ 新增：更新 packageItems 欄位（前端有傳送但原本的 Service 沒處理）
            // 注意：根據你的 Bean 設計，這裡可能需要調整
            // 如果是 String (JSON)：
            if (ticketPackageDetails.getPackageItems() != null) {
                ticketPackage.setPackageItems(ticketPackageDetails.getPackageItems());
            }
            // 如果是 List<PackageItem> 關聯：
            // if (ticketPackageDetails.getPackageItems() != null) {
            //     ticketPackage.getPackageItems().clear();
            //     ticketPackage.getPackageItems().addAll(ticketPackageDetails.getPackageItems());
            // }
            
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






