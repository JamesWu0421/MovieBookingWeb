package tw.com.ispan.service;

import tw.com.ispan.domain.BatchSessionsTempBean;
import tw.com.ispan.dto.BatchSessionsTempRequestDTO;
import tw.com.ispan.dto.BatchSessionsTempResponseDTO;
import tw.com.ispan.mapper.BatchSessionsTempMapper;
import tw.com.ispan.repository.BatchSessionsTempRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * BatchSessionsTemp Service
 */
@Service
public class BatchSessionsTempService {

    @Autowired
    private BatchSessionsTempRepository batchSessionsTempRepository;

    /** 建立暫存場次 */
    @Transactional
    public BatchSessionsTempResponseDTO createSessionTemp(BatchSessionsTempRequestDTO requestDTO) {
        BatchSessionsTempBean bean = BatchSessionsTempMapper.toEntity(requestDTO);

        // 預設狀態
        if (bean.getStatus() == null) {
            bean.setStatus("pending");
        }

        BatchSessionsTempBean saved = batchSessionsTempRepository.save(bean);
        return BatchSessionsTempMapper.toResponseDTO(saved);
    }

    /** 依 ID 查詢 */
    public BatchSessionsTempResponseDTO getById(Integer id) {
        Optional<BatchSessionsTempBean> opt = batchSessionsTempRepository.findById(id);
        return opt.map(BatchSessionsTempMapper::toResponseDTO).orElse(null);
    }

    /** 查全部 */
    public List<BatchSessionsTempResponseDTO> getAll() {
        return batchSessionsTempRepository.findAll().stream()
                .map(BatchSessionsTempMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /** 依 batchId 查詢 */
    public List<BatchSessionsTempResponseDTO> getByBatchId(Integer batchId) {
        return batchSessionsTempRepository.findByBatchId(batchId).stream()
                .map(BatchSessionsTempMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /** 依狀態查詢 */
    public List<BatchSessionsTempResponseDTO> getByStatus(String status) {
        return batchSessionsTempRepository.findByStatus(status).stream()
                .map(BatchSessionsTempMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /** 更新暫存場次 */
    @Transactional
    public BatchSessionsTempResponseDTO updateSessionTemp(Integer id,
                                                          BatchSessionsTempRequestDTO requestDTO) {
        Optional<BatchSessionsTempBean> opt = batchSessionsTempRepository.findById(id);
        if (!opt.isPresent()) {
            return null;
        }
        BatchSessionsTempBean existing = opt.get();
        BatchSessionsTempMapper.updateEntityFromDTO(existing, requestDTO);
        BatchSessionsTempBean updated = batchSessionsTempRepository.save(existing);
        return BatchSessionsTempMapper.toResponseDTO(updated);
    }

    /** 刪除暫存場次 */
    @Transactional
    public boolean deleteSessionTemp(Integer id) {
        if (batchSessionsTempRepository.existsById(id)) {
            batchSessionsTempRepository.deleteById(id);
            return true;
        }
        return false;
    }
}