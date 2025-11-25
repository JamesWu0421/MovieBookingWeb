package tw.com.ispan.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import tw.com.ispan.domain.PackageItemsBean;
import tw.com.ispan.domain.TicketPackageBean;
import tw.com.ispan.dto.PackageItemsRequestDTO;
import tw.com.ispan.dto.PackageItemsResponseDTO;
import tw.com.ispan.mapper.PackageItemsMapper;
import tw.com.ispan.repository.PackageItemsRepository;
import tw.com.ispan.repository.TicketPackageRepository;

@Service
@Transactional
public class PackageItemsService {
  
  @Autowired
  private PackageItemsRepository repository;
  
  @Autowired
  private TicketPackageRepository ticketPackageRepository;

  /**
   * 創建套餐項目 (使用 DTO)
   */
  public PackageItemsResponseDTO createPackageItems(PackageItemsRequestDTO requestDTO) {
    // 查詢關聯的 TicketPackage
    TicketPackageBean ticketPackage = ticketPackageRepository.findById(requestDTO.getPackageId())
        .orElseThrow(() -> new RuntimeException("找不到指定的套餐,ID: " + requestDTO.getPackageId()));
    
    // 將 DTO 轉換為 Entity
    PackageItemsBean entity = PackageItemsMapper.toEntity(requestDTO);
    entity.setTicketPackage(ticketPackage);
    entity.setCreatedAt(LocalDateTime.now());
    entity.setUpdatedAt(LocalDateTime.now());
    
    // 保存並返回 ResponseDTO
    PackageItemsBean saved = repository.save(entity);
    return PackageItemsMapper.toResponseDTO(saved);
  }

  /**
   * 獲取所有套餐項目 (返回 DTO List)
   */
  public List<PackageItemsResponseDTO> getAllPackageItems() {
    return repository.findAll().stream()
        .map(PackageItemsMapper::toResponseDTO)
        .collect(Collectors.toList());
  }

  /**
   * 根據 ID 獲取套餐項目 (返回 DTO)
   */
  public Optional<PackageItemsResponseDTO> getPackageItemsById(Long id) {
    return repository.findById(id)
        .map(PackageItemsMapper::toResponseDTO);
  }

  /**
   * 更新套餐項目 (使用 DTO)
   */
  public Optional<PackageItemsResponseDTO> updatePackageItems(Long id, PackageItemsRequestDTO requestDTO) {
    Optional<PackageItemsBean> oldData = repository.findById(id);
    
    if (oldData.isPresent()) {
      PackageItemsBean entity = oldData.get();
      
      // 如果 packageId 有變更,更新關聯的 TicketPackage
      if (requestDTO.getPackageId() != null && 
          !requestDTO.getPackageId().equals(entity.getTicketPackage().getId())) {
        TicketPackageBean ticketPackage = ticketPackageRepository.findById(requestDTO.getPackageId())
            .orElseThrow(() -> new RuntimeException("找不到指定的套餐,ID: " + requestDTO.getPackageId()));
        entity.setTicketPackage(ticketPackage);
      }
      
      // 更新其他屬性
      PackageItemsMapper.updateEntityFromDTO(entity, requestDTO);
      
      // @PreUpdate 會自動更新 updatedAt
      PackageItemsBean updated = repository.save(entity);
      return Optional.of(PackageItemsMapper.toResponseDTO(updated));
    }
    
    return Optional.empty();
  }

  /**
   * 刪除套餐項目
   */
  public boolean deletePackageItems(Long id) {
    if (repository.existsById(id)) {
      repository.deleteById(id);
      return true;
    }
    return false;
  }

  // ===== 以下是保持向後兼容的方法(直接使用 Entity) =====
  
  /**
   * @deprecated 請使用 createPackageItems(PackageItemsRequestDTO)
   */
  @Deprecated
  public PackageItemsBean createPackageItemsLegacy(PackageItemsBean packageItems) {
    packageItems.setCreatedAt(LocalDateTime.now());
    packageItems.setUpdatedAt(LocalDateTime.now());
    return repository.save(packageItems);
  }

  /**
   * @deprecated 請使用 getAllPackageItems() 返回 DTO
   */
  @Deprecated
  public List<PackageItemsBean> getAllPackageItemsLegacy() {
    return repository.findAll();
  }

  /**
   * @deprecated 請使用 getPackageItemsById(Long) 返回 DTO
   */
  @Deprecated
  public Optional<PackageItemsBean> getAllPackageItemsByIdLegacy(Long id) {
    return repository.findById(id);
  }

  /**
   * @deprecated 請使用 updatePackageItems(Long, PackageItemsRequestDTO)
   */
  @Deprecated
  public Optional<PackageItemsBean> updatePackageItemsLegacy(Long id, PackageItemsBean packageItemsDetail) {
    Optional<PackageItemsBean> oldData = repository.findById(id);
    if (oldData.isPresent()) {
      PackageItemsBean packageItems = oldData.get();

      if (packageItemsDetail.getTicketPackage() != null) {
        packageItems.setTicketPackage(packageItemsDetail.getTicketPackage());
      }
      if (packageItemsDetail.getItemType() != null) {
        packageItems.setItemType(packageItemsDetail.getItemType());
      }
      if (packageItemsDetail.getItemName() != null) {
        packageItems.setItemName(packageItemsDetail.getItemName());
      }
      if (packageItemsDetail.getItemSpec() != null) {
        packageItems.setItemSpec(packageItemsDetail.getItemSpec());
      }
      packageItems.setQuantity(packageItemsDetail.getQuantity());
      packageItems.setDisplayOrder(packageItemsDetail.getDisplayOrder());
      
      PackageItemsBean updated = repository.save(packageItems);
      return Optional.of(updated);
    }
    return Optional.empty();
  }
}
