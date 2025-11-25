package tw.com.ispan.mapper;

import java.util.List;
import java.util.stream.Collectors;

import tw.com.ispan.domain.ShowTicketPricesBean;
import tw.com.ispan.dto.ShowTicketPricesResponseDTO;

/**
 * ShowTicketPrices Entity 和 DTO 之間的映射器
 */
public class ShowTicketPricesMapper {

    /**
     * 將 Entity 轉換為 ResponseDTO
     */
    public static ShowTicketPricesResponseDTO toResponseDTO(ShowTicketPricesBean entity) {
        if (entity == null) {
            return null;
        }
        
        ShowTicketPricesResponseDTO dto = new ShowTicketPricesResponseDTO();
        dto.setId(entity.getId());
        dto.setShowId(entity.getShowId());
        
        // TicketPackage 相關資訊 (避免 lazy loading)
        if (entity.getTicketPackage() != null) {
            dto.setTicketPackageId(entity.getTicketPackage().getId());
            dto.setTicketPackageName(entity.getTicketPackage().getPackageName());
            dto.setTicketPackageCode(entity.getTicketPackage().getPackageCode());
        }
        
        // 場次時間資訊
        dto.setStartTime(entity.getStartTime());
        dto.setEndTime(entity.getEndTime());
        
        // 電影/廳別資訊
        dto.setMovieDuration(entity.getMovieDuration());
        dto.setScreenBasePrice(entity.getScreenBasePrice());
        
        // 計價相關
        dto.setTicketAdjustment(entity.getTicketAdjustment());
        dto.setEarlyBird(entity.getEarlyBird());
        dto.setEarlyBirdAdjustment(entity.getEarlyBirdAdjustment());
        dto.setDurationSurcharge(entity.getDurationSurcharge());
        dto.setFinalPrice(entity.getFinalPrice());
        
        // 可售狀態
        dto.setAvailable(entity.getAvailable());
        
        // 時間戳記
        dto.setCalculatedAt(entity.getCalculatedAt());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        
        return dto;
    }

    /**
     * 將 Entity 列表轉換為 ResponseDTO 列表
     */
    public static List<ShowTicketPricesResponseDTO> toResponseDTOList(List<ShowTicketPricesBean> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(ShowTicketPricesMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}
