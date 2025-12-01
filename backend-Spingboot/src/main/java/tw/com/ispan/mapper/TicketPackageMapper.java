package tw.com.ispan.mapper;

import java.util.List;
import java.util.stream.Collectors;

import tw.com.ispan.domain.TicketPackageBean;
import tw.com.ispan.dto.TicketPackageRequestDTO;
import tw.com.ispan.dto.TicketPackageResponseDTO;

/**
 * TicketPackage Entity 和 DTO 之間的映射器
 */
public class TicketPackageMapper {

    /**
     * 將 Entity 轉換為 ResponseDTO
     */
    public static TicketPackageResponseDTO toResponseDTO(TicketPackageBean entity) {
        if (entity == null) {
            return null;
        }
        
        TicketPackageResponseDTO dto = new TicketPackageResponseDTO();
        dto.setId(entity.getId());
        dto.setPackageType(entity.getPackageType());
        dto.setPackageName(entity.getPackageName());
        dto.setPackageCode(entity.getPackageCode());
        dto.setPriceAdjustment(entity.getPriceAdjustment());
        dto.setEarlyBirdAdjustment(entity.getEarlyBirdAdjustment());
        dto.setImageUrl(entity.getImageUrl());
        dto.setIsActive(entity.getIsActive());
        dto.setDisplayOrder(entity.getDisplayOrder());
        dto.setValidFrom(entity.getValidFrom());
        dto.setValidUntil(entity.getValidUntil());
        dto.setEnableEarlyBird(entity.getEnableEarlyBird());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        
        // 轉換 PackageItems
        if (entity.getPackageItems() != null) {
            dto.setPackageItems(
                entity.getPackageItems().stream()
                    .map(PackageItemsMapper::toResponseDTO)
                    .collect(Collectors.toList())
            );
        }
        
        return dto;
    }

    /**
     * 將 RequestDTO 轉換為 Entity (用於創建)
     */
    public static TicketPackageBean toEntity(TicketPackageRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        
        TicketPackageBean entity = new TicketPackageBean();
        entity.setPackageType(dto.getPackageType());
        entity.setPackageName(dto.getPackageName());
        entity.setPackageCode(dto.getPackageCode());
        entity.setPriceAdjustment(dto.getPriceAdjustment() != null ? dto.getPriceAdjustment() : 0);
        entity.setEarlyBirdAdjustment(dto.getEarlyBirdAdjustment() != null ? dto.getEarlyBirdAdjustment() : 0);
        entity.setImageUrl(dto.getImageUrl());
        entity.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        entity.setDisplayOrder(dto.getDisplayOrder() != null ? dto.getDisplayOrder() : 0);
        entity.setValidFrom(dto.getValidFrom());
        entity.setValidUntil(dto.getValidUntil());
        entity.setEnableEarlyBird(dto.getEnableEarlyBird() != null ? dto.getEnableEarlyBird() : true);
        
        return entity;
    }

    /**
     * 更新 Entity 的屬性(用於更新操作)
     */
    public static void updateEntityFromDTO(TicketPackageBean entity, TicketPackageRequestDTO dto) {
        if (entity == null || dto == null) {
            return;
        }
        
        if (dto.getPackageType() != null) {
            entity.setPackageType(dto.getPackageType());
        }
        if (dto.getPackageName() != null) {
            entity.setPackageName(dto.getPackageName());
        }
        if (dto.getPackageCode() != null) {
            entity.setPackageCode(dto.getPackageCode());
        }
        if (dto.getPriceAdjustment() != null) {
            entity.setPriceAdjustment(dto.getPriceAdjustment());
        }
        if (dto.getEarlyBirdAdjustment() != null) {
            entity.setEarlyBirdAdjustment(dto.getEarlyBirdAdjustment());
        }
        if (dto.getImageUrl() != null) {
            entity.setImageUrl(dto.getImageUrl());
        }
        if (dto.getIsActive() != null) {
            entity.setIsActive(dto.getIsActive());
        }
        if (dto.getDisplayOrder() != null) {
            entity.setDisplayOrder(dto.getDisplayOrder());
        }
        if (dto.getValidFrom() != null) {
            entity.setValidFrom(dto.getValidFrom());
        }
        if (dto.getValidUntil() != null) {
            entity.setValidUntil(dto.getValidUntil());
        }
        if (dto.getEnableEarlyBird() != null) {
            entity.setEnableEarlyBird(dto.getEnableEarlyBird());
        }
    }

    /**
     * 將 Entity 列表轉換為 ResponseDTO 列表
     */
    public static List<TicketPackageResponseDTO> toResponseDTOList(List<TicketPackageBean> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(TicketPackageMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}
