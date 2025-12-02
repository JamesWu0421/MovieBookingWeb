package tw.com.ispan.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tw.com.ispan.domain.BatchOperationsBean;
import tw.com.ispan.domain.BatchSessionsTempBean;
import tw.com.ispan.domain.BatchTicketsTempBean;
import tw.com.ispan.domain.ShowBean;
import tw.com.ispan.dto.BatchOperationsRequestDTO;
import tw.com.ispan.dto.BatchOperationsResponseDTO;
import tw.com.ispan.entity.EmpEntity;
import tw.com.ispan.mapper.BatchOperationsMapper;
import tw.com.ispan.repository.BatchOperationsRepository;
import tw.com.ispan.repository.BatchSessionsTempRepository;
import tw.com.ispan.repository.BatchTicketsTempRepository;
import tw.com.ispan.repository.rollpermission.EmpRepository; // 🔹 新增

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * BatchOperations Service
 * 業務邏輯層，處理批次操作相關業務
 */
@Service
public class BatchOperationsService {

    @Autowired
    private BatchOperationsRepository batchOperationsRepository;

    // 🔹 新增：注入 EmpRepository
    @Autowired
    private EmpRepository empRepository;

    @Autowired
    private BatchSessionsTempRepository batchSessionsTempRepository;

    @Autowired
    private BatchTicketsTempRepository batchTicketsTempRepository;

    @Autowired
    private ShowService showService;

    @Autowired
    private ShowTicketPricesService showTicketPricesService;

    /**
     * 創建批次操作記錄
     */
    @Transactional
    public BatchOperationsResponseDTO createBatchOperation(BatchOperationsRequestDTO requestDTO) {
        // DTO -> Entity
        BatchOperationsBean bean = BatchOperationsMapper.toEntity(requestDTO);

        // 🔹 新增：設置 operator 關聯
        if (requestDTO.getOperatorId() != null) {
            EmpEntity operator = empRepository.findById(requestDTO.getOperatorId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "找不到操作員：" + requestDTO.getOperatorId()));
            bean.setOperator(operator);
        }

        // 建立時間（如果 DB 沒有 default GETDATE()，這行很重要）
        if (bean.getCreatedAt() == null) {
            bean.setCreatedAt(LocalDateTime.now());
        }

        // 儲存到資料庫
        BatchOperationsBean savedBean = batchOperationsRepository.save(bean);

        // Entity -> ResponseDTO
        return BatchOperationsMapper.toResponseDTO(savedBean);
    }

    /**
     * 根據批次ID查詢批次操作
     */
    public BatchOperationsResponseDTO getBatchOperationById(Integer batchId) {
        Optional<BatchOperationsBean> optional = batchOperationsRepository.findById(batchId);
        return optional.map(BatchOperationsMapper::toResponseDTO).orElse(null);
    }

    /**
     * 查詢所有批次操作
     */
    public List<BatchOperationsResponseDTO> getAllBatchOperations() {
        List<BatchOperationsBean> beans = batchOperationsRepository.findAllByOrderByCreatedAtDesc();
        return beans.stream()
                .map(BatchOperationsMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * 根據操作員ID查詢批次操作
     */
    public List<BatchOperationsResponseDTO> getBatchOperationsByOperatorId(Integer operatorId) {
        List<BatchOperationsBean> beans = batchOperationsRepository
                .findByOperatorIdOrderByCreatedAtDesc(operatorId);
        return beans.stream()
                .map(BatchOperationsMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * 根據狀態查詢批次操作
     */
    public List<BatchOperationsResponseDTO> getBatchOperationsByStatus(String status) {
        List<BatchOperationsBean> beans = batchOperationsRepository
                .findByStatusOrderByCreatedAtDesc(status);
        return beans.stream()
                .map(BatchOperationsMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * 根據操作類型查詢批次操作
     */
    public List<BatchOperationsResponseDTO> getBatchOperationsByOperationType(String operationType) {
        List<BatchOperationsBean> beans = batchOperationsRepository
                .findByOperationTypeOrderByCreatedAtDesc(operationType);
        return beans.stream()
                .map(BatchOperationsMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * 根據操作員ID和狀態查詢批次操作
     */
    public List<BatchOperationsResponseDTO> getBatchOperationsByOperatorIdAndStatus(
            Integer operatorId, String status) {
        List<BatchOperationsBean> beans = batchOperationsRepository
                .findByOperatorIdAndStatusOrderByCreatedAtDesc(operatorId, status);
        return beans.stream()
                .map(BatchOperationsMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * 根據時間範圍查詢批次操作
     */
    public List<BatchOperationsResponseDTO> getBatchOperationsByDateRange(
            LocalDateTime startDate, LocalDateTime endDate) {
        List<BatchOperationsBean> beans = batchOperationsRepository
                .findByCreatedAtBetweenOrderByCreatedAtDesc(startDate, endDate);
        return beans.stream()
                .map(BatchOperationsMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

        /**
     * 更新批次操作（整筆更新）
     */
    @Transactional
    public BatchOperationsResponseDTO updateBatchOperation(Integer batchId,
                                                           BatchOperationsRequestDTO requestDTO) {
        Optional<BatchOperationsBean> optional = batchOperationsRepository.findById(batchId);
        if (!optional.isPresent()) {
            return null;
        }

        BatchOperationsBean existingBean = optional.get();

        // 🔹 新增：如果 DTO 有新的 operatorId，更新 operator 關聯
        if (requestDTO.getOperatorId() != null) {
            EmpEntity operator = empRepository.findById(requestDTO.getOperatorId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "找不到操作員：" + requestDTO.getOperatorId()));
            existingBean.setOperator(operator);
        }

        // 用 Mapper 把 DTO 的值塞回 Entity（注意不要改 batchId / createdAt）
        BatchOperationsMapper.updateEntityFromDTO(existingBean, requestDTO);

        BatchOperationsBean updatedBean = batchOperationsRepository.save(existingBean);
        return BatchOperationsMapper.toResponseDTO(updatedBean);
    }

    /**
     * 更新批次操作狀態（單純改 status 用）
     */
    @Transactional
    public boolean updateBatchOperationStatus(Integer batchId, String status) {
        return batchOperationsRepository.findById(batchId)
                .map(bean -> {
                    bean.setStatus(status);
                    batchOperationsRepository.save(bean);
                    return true;
                })
                .orElse(false);
    }

    // =====================================================================
    // 🔴 這裡開始是「真正執行批次內容」的邏輯
    // =====================================================================

    /**
     * 執行 IMPORT 類型的批次：
     * 1. 從 batch_sessions_temp 生成 Show（場次）
     * 2. 從 batch_tickets_temp 生成 ShowTicketPrices（票價）
     * 3. 更新 temp table 的 status / errorMessage
     * 4. 更新 batch_operations 的 successCount / failCount / totalItems / status
     */
    @Transactional
    public BatchOperationsResponseDTO executeImportBatch(Integer batchId) {

        BatchOperationsBean batch = batchOperationsRepository.findById(batchId)
                .orElseThrow(() -> new IllegalArgumentException("找不到批次：" + batchId));

        // 1. 標記為執行中
        batch.setStatus("EXECUTING");
        batch.setExecutedAt(LocalDateTime.now());
        batchOperationsRepository.save(batch);

        // 2. 讀取 temp table
        List<BatchSessionsTempBean> sessionTemps =
                batchSessionsTempRepository.findByBatchId(batchId);
        List<BatchTicketsTempBean> ticketTemps =
                batchTicketsTempRepository.findByBatchId(batchId);

        int totalItems = sessionTemps.size() + ticketTemps.size();
        int successCount = 0;
        int failCount = 0;

        // 暫存：temp session id -> 真實 showId
        Map<Integer, Integer> tempSessionIdToShowId = new HashMap<>();

        // 3. 先處理場次 temp
        for (BatchSessionsTempBean temp : sessionTemps) {
            try {
                ShowBean createdShow = showService.createShowFromBatchSessionTemp(temp);

                temp.setStatus("SUCCESS");
                temp.setErrorMessage(null);
                batchSessionsTempRepository.save(temp);

                // 記下對應：之後票價要用
                tempSessionIdToShowId.put(temp.getId(), createdShow.getId());

                successCount++;
            } catch (Exception ex) {
                temp.setStatus("FAILED");
                temp.setErrorMessage(ex.getMessage());
                batchSessionsTempRepository.save(temp);
                failCount++;
            }
        }

        // 4. 再處理票價 temp
        for (BatchTicketsTempBean temp : ticketTemps) {
            try {
                Integer showId = tempSessionIdToShowId.get(temp.getBatchSessionId());
                if (showId == null) {
                    throw new IllegalStateException(
                            "找不到對應的 showId，batchSessionId=" + temp.getBatchSessionId());
                }

                showTicketPricesService.createPriceFromBatchTicketTemp(temp, showId);

                temp.setStatus("SUCCESS");
                temp.setErrorMessage(null);
                batchTicketsTempRepository.save(temp);

                successCount++;
            } catch (Exception ex) {
                temp.setStatus("FAILED");
                temp.setErrorMessage(ex.getMessage());
                batchTicketsTempRepository.save(temp);
                failCount++;
            }
        }
            // 5. 更新主批次結果
        batch.setTotalItems(totalItems);
        batch.setSuccessCount(successCount);
        batch.setFailCount(failCount);
        batch.setCompletedAt(LocalDateTime.now());
        batch.setStatus(failCount > 0 ? "FAILED" : "COMPLETED");

        BatchOperationsBean saved = batchOperationsRepository.save(batch);
        return BatchOperationsMapper.toResponseDTO(saved);
    }

    /**
     * 「開始批次」按鈕對應的服務
     * 目前直接呼叫 executeImportBatch。
     * totalItems 參數可以忽略，前端有傳也沒關係，實際以後端自己算為準。
     */
    @Transactional
    public boolean startBatchOperation(Integer batchId, Integer totalItems) {
        executeImportBatch(batchId);
        return true;
    }

    /**
     * 完成批次操作（如果之後有手動 override 成功 / 失敗數用途，可保留）
     */
    @Transactional
    public boolean completeBatchOperation(Integer batchId, Integer successCount, Integer failCount) {
        return batchOperationsRepository.findById(batchId)
                .map(bean -> {
                    bean.setStatus("COMPLETED");
                    bean.setCompletedAt(LocalDateTime.now());
                    bean.setSuccessCount(successCount);
                    bean.setFailCount(failCount);
                    batchOperationsRepository.save(bean);
                    return true;
                })
                .orElse(false);
    }

    /**
     * 刪除批次操作
     */
    @Transactional
    public boolean deleteBatchOperation(Integer batchId) {
        if (batchOperationsRepository.existsById(batchId)) {
            batchOperationsRepository.deleteById(batchId);
            return true;
        }
        return false;
    }
}