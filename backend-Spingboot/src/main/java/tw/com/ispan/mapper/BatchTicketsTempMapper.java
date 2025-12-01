package tw.com.ispan.mapper;

import tw.com.ispan.domain.BatchTicketsTempBean;
import tw.com.ispan.dto.BatchTicketsTempRequestDTO;
import tw.com.ispan.dto.BatchTicketsTempResponseDTO;

/**
 * BatchTicketsTemp Entity / DTO 轉換工具
 */
public class BatchTicketsTempMapper {

    /** RequestDTO -> Entity(新建) */
    public static BatchTicketsTempBean toEntity(BatchTicketsTempRequestDTO dto) {
        if (dto == null) return null;

        BatchTicketsTempBean bean = new BatchTicketsTempBean();
        bean.setBatchId(dto.getBatchId());
        bean.setBatchSessionId(dto.getBatchSessionId());
        bean.setTicketPackagesId(dto.getTicketPackagesId());
        bean.setStatus(dto.getStatus());
        bean.setErrorMessage(dto.getErrorMessage());
        return bean;
    }

    /** 用 RequestDTO 更新已存在的 Entity(不改 id) */
    public static void updateEntityFromDTO(BatchTicketsTempBean bean,
                                           BatchTicketsTempRequestDTO dto) {
        if (bean == null || dto == null) return;

        bean.setBatchId(dto.getBatchId());
        bean.setBatchSessionId(dto.getBatchSessionId());
        bean.setTicketPackagesId(dto.getTicketPackagesId());
        bean.setStatus(dto.getStatus());
        bean.setErrorMessage(dto.getErrorMessage());
    }

    /** Entity -> ResponseDTO */
    public static BatchTicketsTempResponseDTO toResponseDTO(BatchTicketsTempBean bean) {
        if (bean == null) return null;

        BatchTicketsTempResponseDTO dto = new BatchTicketsTempResponseDTO();
        dto.setId(bean.getId());
        dto.setBatchId(bean.getBatchId());
        dto.setBatchSessionId(bean.getBatchSessionId());
        dto.setTicketPackagesId(bean.getTicketPackagesId());
        dto.setStatus(bean.getStatus());
        dto.setErrorMessage(bean.getErrorMessage());
        return dto;
    }
}