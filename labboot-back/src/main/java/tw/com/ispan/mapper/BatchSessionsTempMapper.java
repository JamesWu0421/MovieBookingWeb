package tw.com.ispan.mapper;

import tw.com.ispan.domain.BatchSessionsTempBean;
import tw.com.ispan.dto.BatchSessionsTempRequestDTO;
import tw.com.ispan.dto.BatchSessionsTempResponseDTO;

/**
 * BatchSessionsTemp 的 Entity / DTO 轉換工具
 */
public class BatchSessionsTempMapper {

    /** RequestDTO -> Entity（新建） */
    public static BatchSessionsTempBean toEntity(BatchSessionsTempRequestDTO dto) {
        if (dto == null) return null;

        BatchSessionsTempBean bean = new BatchSessionsTempBean();
        bean.setMovieId(dto.getMovieId());
        bean.setScreenId(dto.getScreenId());
        bean.setShowDate(dto.getShowDate());
        bean.setShowTime(dto.getShowTime());
        bean.setEndTime(dto.getEndTime());
        bean.setBatchId(dto.getBatchId());
        bean.setStatus(dto.getStatus());           // 可以為 null，Service 裡補 default
        bean.setErrorMessage(dto.getErrorMessage());
        return bean;
    }

    /** 用 RequestDTO 更新已存在的 Entity（不改 id） */
    public static void updateEntityFromDTO(BatchSessionsTempBean bean,
                                           BatchSessionsTempRequestDTO dto) {
        if (bean == null || dto == null) return;

        bean.setMovieId(dto.getMovieId());
        bean.setScreenId(dto.getScreenId());
        bean.setShowDate(dto.getShowDate());
        bean.setShowTime(dto.getShowTime());
        bean.setEndTime(dto.getEndTime());
        bean.setBatchId(dto.getBatchId());
        bean.setStatus(dto.getStatus());
        bean.setErrorMessage(dto.getErrorMessage());
    }

    /** Entity -> ResponseDTO */
    public static BatchSessionsTempResponseDTO toResponseDTO(BatchSessionsTempBean bean) {
        if (bean == null) return null;

        BatchSessionsTempResponseDTO dto = new BatchSessionsTempResponseDTO();
        dto.setId(bean.getId());
        dto.setMovieId(bean.getMovieId());
        dto.setScreenId(bean.getScreenId());
        dto.setShowDate(bean.getShowDate());
        dto.setShowTime(bean.getShowTime());
        dto.setEndTime(bean.getEndTime());
        dto.setBatchId(bean.getBatchId());
        dto.setStatus(bean.getStatus());
        dto.setErrorMessage(bean.getErrorMessage());
        return dto;
    }
}