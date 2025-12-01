package tw.com.ispan.service;

import tw.com.ispan.domain.BatchTicketsTempBean;
import tw.com.ispan.dto.BatchTicketsTempRequestDTO;
import tw.com.ispan.dto.BatchTicketsTempResponseDTO;
import tw.com.ispan.mapper.BatchTicketsTempMapper;
import tw.com.ispan.repository.BatchTicketsTempRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * BatchTicketsTemp Service
 */
@Service
public class BatchTicketsTempService {

    @Autowired
    private BatchTicketsTempRepository batchTicketsTempRepository;

    /** 建立暫存票券 */
    @Transactional
    public BatchTicketsTempResponseDTO createTicketTemp(BatchTicketsTempRequestDTO requestDTO) {
        BatchTicketsTempBean bean = BatchTicketsTempMapper.toEntity(requestDTO);

        // 預設狀態
        if (bean.getStatus() == null) {
            bean.setStatus("pending");
        }

        BatchTicketsTempBean saved = batchTicketsTempRepository.save(bean);
        return BatchTicketsTempMapper.toResponseDTO(saved);
    }

    /** 依 ID 查詢 */
    public BatchTicketsTempResponseDTO getById(Integer id) {
        Optional<BatchTicketsTempBean> opt = batchTicketsTempRepository.findById(id);
        return opt.map(BatchTicketsTempMapper::toResponseDTO).orElse(null);
    }

    /** 查全部 */
    public List<BatchTicketsTempResponseDTO> getAll() {
        return batchTicketsTempRepository.findAll().stream()
                .map(BatchTicketsTempMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /** 依 batchId 查詢 */
    public List<BatchTicketsTempResponseDTO> getByBatchId(Integer batchId) {
        return batchTicketsTempRepository.findByBatchId(batchId).stream()
                .map(BatchTicketsTempMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /** 依 batchSessionId 查詢 */
    public List<BatchTicketsTempResponseDTO> getByBatchSessionId(Integer batchSessionId) {
        return batchTicketsTempRepository.findByBatchSessionId(batchSessionId).stream()
                .map(BatchTicketsTempMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /** 依狀態查詢 */
    public List<BatchTicketsTempResponseDTO> getByStatus(String status) {
        return batchTicketsTempRepository.findByStatus(status).stream()
                .map(BatchTicketsTempMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /** 更新暫存票券 */
    @Transactional
    public BatchTicketsTempResponseDTO updateTicketTemp(Integer id,
                                                        BatchTicketsTempRequestDTO requestDTO) {
        Optional<BatchTicketsTempBean> opt = batchTicketsTempRepository.findById(id);
        if (!opt.isPresent()) {
            return null;
        }
        BatchTicketsTempBean existing = opt.get();
        BatchTicketsTempMapper.updateEntityFromDTO(existing, requestDTO);
        BatchTicketsTempBean updated = batchTicketsTempRepository.save(existing);
        return BatchTicketsTempMapper.toResponseDTO(updated);
    }

    /** 刪除暫存票券 */
    @Transactional
    public boolean deleteTicketTemp(Integer id) {
        if (batchTicketsTempRepository.existsById(id)) {
            batchTicketsTempRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
