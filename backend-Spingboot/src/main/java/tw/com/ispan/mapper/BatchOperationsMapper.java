package tw.com.ispan.mapper;

import tw.com.ispan.domain.BatchOperationsBean;
import tw.com.ispan.dto.BatchOperationsRequestDTO;
import tw.com.ispan.dto.BatchOperationsResponseDTO;
import tw.com.ispan.entity.EmpEntity;

import java.time.LocalDateTime;

/**
 * BatchOperations Entity 和 DTO 之間的映射器
 */
public class BatchOperationsMapper {

    /**
     * 將 Entity 轉換為 ResponseDTO
     * @param entity Entity 物件
     * @return ResponseDTO
     */
    public static BatchOperationsResponseDTO toResponseDTO(BatchOperationsBean entity) {
        if (entity == null) {
            return null;
        }
        
        BatchOperationsResponseDTO dto = new BatchOperationsResponseDTO();
        
        dto.setBatchId(entity.getBatchId());
        
        // ✅ 方案2：直接使用 operatorId 欄位（效能最佳，不會觸發關聯查詢）
        dto.setOperatorId(entity.getOperatorId());
        
        dto.setOperationType(entity.getOperationType());
        dto.setStatus(entity.getStatus());
        dto.setDescription(entity.getDescription());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setExecutedAt(entity.getExecutedAt());
        dto.setCompletedAt(entity.getCompletedAt());
        dto.setTotalItems(entity.getTotalItems());
        dto.setSuccessCount(entity.getSuccessCount());
        dto.setFailCount(entity.getFailCount());
        
        // 計算成功率和失敗率
        if (entity.getTotalItems() != null && entity.getTotalItems() > 0) {
            double successRate = (entity.getSuccessCount() * 100.0) / entity.getTotalItems();
            double failureRate = (entity.getFailCount() * 100.0) / entity.getTotalItems();
            dto.setSuccessRate(Math.round(successRate * 100.0) / 100.0);
            dto.setFailureRate(Math.round(failureRate * 100.0) / 100.0);
        } else {
            dto.setSuccessRate(0.0);
            dto.setFailureRate(0.0);
        }
        
        return dto;
    }

    /**
     * 將 RequestDTO 轉換為 Entity (用於創建)
     * 🔹 注意：這個方法只設置基本屬性，operator 需要在 Service 層設置
     * @param dto RequestDTO
     * @return Entity
     */
    public static BatchOperationsBean toEntity(BatchOperationsRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        
        BatchOperationsBean entity = new BatchOperationsBean();
        
        // 🔹 不再直接設置 operatorId，因為它是 insertable=false, updatable=false
        // entity.setOperatorId(dto.getOperatorId());
        
        entity.setOperationType(dto.getOperationType());
        entity.setStatus(dto.getStatus());
        entity.setDescription(dto.getDescription());
        entity.setCreatedAt(LocalDateTime.now());
        entity.setTotalItems(dto.getTotalItems());
        entity.setSuccessCount(dto.getSuccessCount() != null ? dto.getSuccessCount() : 0);
        entity.setFailCount(dto.getFailCount() != null ? dto.getFailCount() : 0);
        
        return entity;
    }

    /**
     * 更新 Entity 的屬性 (用於更新操作)
     * 🔹 注意：operator 需要在 Service 層單獨處理
     * @param entity 要更新的 Entity
     * @param dto 包含新數據的 DTO
     */
    public static void updateEntityFromDTO(BatchOperationsBean entity, BatchOperationsRequestDTO dto) {
        if (entity == null || dto == null) {
            return;
        }
        
        // 🔹 不再直接更新 operatorId
        // if (dto.getOperatorId() != null) {
        //     entity.setOperatorId(dto.getOperatorId());
        // }
        
        if (dto.getOperationType() != null) {
            entity.setOperationType(dto.getOperationType());
        }
        if (dto.getStatus() != null) {
            entity.setStatus(dto.getStatus());
        }
        if (dto.getDescription() != null) {
            entity.setDescription(dto.getDescription());
        }
        if (dto.getTotalItems() != null) {
            entity.setTotalItems(dto.getTotalItems());
        }
        if (dto.getSuccessCount() != null) {
            entity.setSuccessCount(dto.getSuccessCount());
        }
        if (dto.getFailCount() != null) {
            entity.setFailCount(dto.getFailCount());
        }
    }
}