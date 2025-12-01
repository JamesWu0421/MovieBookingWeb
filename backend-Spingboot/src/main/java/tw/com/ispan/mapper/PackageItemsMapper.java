package tw.com.ispan.mapper;

import tw.com.ispan.domain.PackageItemsBean;
import tw.com.ispan.dto.PackageItemsRequestDTO;
import tw.com.ispan.dto.PackageItemsResponseDTO;

/**
 * PackageItems Entity 和 DTO 之間的映射器
 */
public class PackageItemsMapper {

    /**
     * 將 Entity 轉換為 ResponseDTO
     */
    public static PackageItemsResponseDTO toResponseDTO(PackageItemsBean entity) {
        if (entity == null) {
            return null;
        }
        
        PackageItemsResponseDTO dto = new PackageItemsResponseDTO();
        dto.setId(entity.getId());
        dto.setPackageId(entity.getTicketPackage() != null ? entity.getTicketPackage().getId() : null);
        dto.setItemType(entity.getItemType());
        dto.setItemName(entity.getItemName());
        dto.setItemSpec(entity.getItemSpec());
        dto.setQuantity(entity.getQuantity());
        dto.setDisplayOrder(entity.getDisplayOrder());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        
        return dto;
    }

    /**
     * 將 RequestDTO 轉換為 Entity (用於創建)
     * 注意:這個方法不設置 TicketPackageBean,需要在 Service 層處理
     */
    public static PackageItemsBean toEntity(PackageItemsRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        
        PackageItemsBean entity = new PackageItemsBean();
        entity.setItemType(dto.getItemType());
        entity.setItemName(dto.getItemName());
        entity.setItemSpec(dto.getItemSpec());
        entity.setQuantity(dto.getQuantity());
        entity.setDisplayOrder(dto.getDisplayOrder());
        // ticketPackage 需要在 Service 層通過 packageId 查詢後設置
        
        return entity;
    }

    /**
     * 更新 Entity 的屬性(用於更新操作)
     * 注意:這個方法不更新 TicketPackageBean,需要在 Service 層處理
     */
    public static void updateEntityFromDTO(PackageItemsBean entity, PackageItemsRequestDTO dto) {
        if (entity == null || dto == null) {
            return;
        }
        
        if (dto.getItemType() != null) {
            entity.setItemType(dto.getItemType());
        }
        if (dto.getItemName() != null) {
            entity.setItemName(dto.getItemName());
        }
        if (dto.getItemSpec() != null) {
            entity.setItemSpec(dto.getItemSpec());
        }
        entity.setQuantity(dto.getQuantity());
        entity.setDisplayOrder(dto.getDisplayOrder());
        // ticketPackage 的更新需要在 Service 層處理
    }
}