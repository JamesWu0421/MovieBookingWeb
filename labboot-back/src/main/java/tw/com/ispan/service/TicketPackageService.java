package tw.com.ispan.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import tw.com.ispan.domain.PackageItemsBean;
import tw.com.ispan.domain.TicketPackageBean;
import tw.com.ispan.dto.TicketPackageRequestDTO;
import tw.com.ispan.dto.TicketPackageResponseDTO;
import tw.com.ispan.mapper.TicketPackageMapper;
import tw.com.ispan.repository.PackageItemsRepository;
import tw.com.ispan.repository.TicketPackageRepository;

@Service
@Transactional
public class TicketPackageService {

    @Autowired
    private TicketPackageRepository repository;
    
    @Autowired
    private PackageItemsRepository packageItemsRepository;

    /**
     * 創建套餐 (使用 DTO) - 修復版
     */
    public TicketPackageResponseDTO createTicketPackage(TicketPackageRequestDTO requestDTO) {
        // 將 DTO 轉換為 Entity
        TicketPackageBean entity = TicketPackageMapper.toEntity(requestDTO);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        
        // ⭐ 優先處理完整的 packageItems 物件
        if (requestDTO.getPackageItems() != null && !requestDTO.getPackageItems().isEmpty()) {
            for (TicketPackageRequestDTO.PackageItemDTO itemDTO : requestDTO.getPackageItems()) {
                PackageItemsBean item = new PackageItemsBean();
                item.setItemType(itemDTO.getItemType());
                item.setItemName(itemDTO.getItemName());
                item.setItemSpec(itemDTO.getItemSpec());
                item.setQuantity(itemDTO.getQuantity() != null ? itemDTO.getQuantity() : 1);
                item.setDisplayOrder(itemDTO.getDisplayOrder() != null ? itemDTO.getDisplayOrder() : 1);
                item.setCreatedAt(LocalDateTime.now());
                item.setUpdatedAt(LocalDateTime.now());
                
                // 建立雙向關聯
                entity.addPackageItem(item);
            }
        }
        // 如果沒有 packageItems,才使用 packageItemIds (向後兼容)
        else if (requestDTO.getPackageItemIds() != null && !requestDTO.getPackageItemIds().isEmpty()) {
            for (Long itemId : requestDTO.getPackageItemIds()) {
                PackageItemsBean item = packageItemsRepository.findById(itemId)
                    .orElseThrow(() -> new RuntimeException("找不到項目,ID: " + itemId));
                entity.addPackageItem(item);
            }
        }
        
        // 保存並返回 ResponseDTO
        TicketPackageBean saved = repository.save(entity);
        return TicketPackageMapper.toResponseDTO(saved);
    }

    /**
     * 獲取所有套餐 (返回 DTO List)
     */
    public List<TicketPackageResponseDTO> getAllTicketPackages() {
        List<TicketPackageBean> entities = repository.findAll();
        return TicketPackageMapper.toResponseDTOList(entities);
    }

    /**
     * 根據 ID 獲取套餐 (返回 DTO)
     */
    public Optional<TicketPackageResponseDTO> getTicketPackageById(Long id) {
        return repository.findById(id)
                .map(TicketPackageMapper::toResponseDTO);
    }

    /**
     * 更新套餐 (使用 DTO) - 修復版
     */
    public Optional<TicketPackageResponseDTO> updateTicketPackage(Long id, TicketPackageRequestDTO requestDTO) {
        Optional<TicketPackageBean> oldData = repository.findById(id);
        
        if (oldData.isPresent()) {
            TicketPackageBean entity = oldData.get();
            
            // 更新基本欄位
            TicketPackageMapper.updateEntityFromDTO(entity, requestDTO);
            
            // ⭐ 優先處理完整的 packageItems 物件
            if (requestDTO.getPackageItems() != null) {
                // 清除舊的關聯 (orphanRemoval = true 會自動刪除)
                entity.getPackageItems().clear();
                
                // 添加新的 packageItems
                for (TicketPackageRequestDTO.PackageItemDTO itemDTO : requestDTO.getPackageItems()) {
                    PackageItemsBean item = new PackageItemsBean();
                    item.setItemType(itemDTO.getItemType());
                    item.setItemName(itemDTO.getItemName());
                    item.setItemSpec(itemDTO.getItemSpec());
                    item.setQuantity(itemDTO.getQuantity() != null ? itemDTO.getQuantity() : 1);
                    item.setDisplayOrder(itemDTO.getDisplayOrder() != null ? itemDTO.getDisplayOrder() : 1);
                    item.setCreatedAt(LocalDateTime.now());
                    item.setUpdatedAt(LocalDateTime.now());
                    
                    // 建立雙向關聯
                    entity.addPackageItem(item);
                }
            }
            // 如果沒有 packageItems,才使用 packageItemIds (向後兼容)
            else if (requestDTO.getPackageItemIds() != null) {
                // 清除舊的關聯
                entity.getPackageItems().clear();
                
                // 添加新的關聯
                for (Long itemId : requestDTO.getPackageItemIds()) {
                    PackageItemsBean item = packageItemsRepository.findById(itemId)
                        .orElseThrow(() -> new RuntimeException("找不到項目,ID: " + itemId));
                    entity.addPackageItem(item);
                }
            }
            
            entity.setUpdatedAt(LocalDateTime.now());
            
            TicketPackageBean updated = repository.save(entity);
            return Optional.of(TicketPackageMapper.toResponseDTO(updated));
        }
        
        return Optional.empty();
    }

    /**
     * 刪除套餐
     */
    public boolean deleteTicketPackage(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }

    // ===== 以下是保持向後兼容的方法(直接使用 Entity) =====
    
    /**
     * @deprecated 請使用 createTicketPackage(TicketPackageRequestDTO)
     */
    @Deprecated
    public TicketPackageBean createTicketPackageLegacy(TicketPackageBean ticketPackage) {
        ticketPackage.setCreatedAt(LocalDateTime.now());
        ticketPackage.setUpdatedAt(LocalDateTime.now());
        
        if (ticketPackage.getPackageItems() != null) {
            for (PackageItemsBean item : ticketPackage.getPackageItems()) {
                item.setTicketPackage(ticketPackage);
            }
        }
        
        return repository.save(ticketPackage);
    }

    /**
     * @deprecated 請使用 getAllTicketPackages() 返回 DTO
     */
    @Deprecated
    public List<TicketPackageBean> getAllTicketPackagesLegacy() {
        return repository.findAll();
    }

    /**
     * @deprecated 請使用 getTicketPackageById(Long) 返回 DTO
     */
    @Deprecated
    public Optional<TicketPackageBean> getTicketPackageByIdLegacy(Long id) {
        return repository.findById(id);
    }

    /**
     * @deprecated 請使用 updateTicketPackage(Long, TicketPackageRequestDTO)
     */
    @Deprecated
    public Optional<TicketPackageBean> updateTicketPackageLegacy(Long id, TicketPackageBean ticketPackageDetails) {
        Optional<TicketPackageBean> oldData = repository.findById(id);
        if (oldData.isPresent()) {
            TicketPackageBean ticketPackage = oldData.get();

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
            
            if (ticketPackageDetails.getPackageItems() != null) {
                ticketPackage.getPackageItems().clear();
                
                for (PackageItemsBean newItem : ticketPackageDetails.getPackageItems()) {
                    newItem.setId(null);
                    newItem.setTicketPackage(ticketPackage);
                    ticketPackage.getPackageItems().add(newItem);
                }
            }
            
            ticketPackage.setUpdatedAt(LocalDateTime.now());

            return Optional.of(repository.save(ticketPackage));
        }
        return Optional.empty();
    }
}