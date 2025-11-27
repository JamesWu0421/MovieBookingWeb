package tw.com.ispan.controller;

import tw.com.ispan.dto.BatchOperationsRequestDTO;
import tw.com.ispan.dto.BatchOperationsResponseDTO;
import tw.com.ispan.service.BatchOperationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * BatchOperations Controller
 * 處理批次操作相關的 HTTP 請求
 */
@CrossOrigin(origins = "*", allowCredentials = "false")
@RestController
@RequestMapping("/api/batch-operations")
 // 允許跨域請求，生產環境應該設定具體的來源
public class BatchOperationsController {
    
    @Autowired
    private BatchOperationsService batchOperationsService;
    
    /**
     * 創建批次操作
     * POST /api/batch-operations
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createBatchOperation(
            @RequestBody BatchOperationsRequestDTO requestDTO) {
        try {
            BatchOperationsResponseDTO responseDTO = batchOperationsService
                    .createBatchOperation(requestDTO);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "批次操作創建成功");
            response.put("data", responseDTO);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "批次操作創建失敗: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 根據批次ID查詢批次操作
     * GET /api/batch-operations/{batchId}
     */
    @GetMapping("/{batchId}")
    public ResponseEntity<Map<String, Object>> getBatchOperationById(
            @PathVariable Integer batchId) {
        BatchOperationsResponseDTO responseDTO = batchOperationsService
                .getBatchOperationById(batchId);
        
        Map<String, Object> response = new HashMap<>();
        if (responseDTO != null) {
            response.put("success", true);
            response.put("message", "查詢成功");
            response.put("data", responseDTO);
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "查無此批次操作記錄");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    
    /**
     * 查詢所有批次操作
     * GET /api/batch-operations
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllBatchOperations() {
        List<BatchOperationsResponseDTO> responseDTOs = batchOperationsService
                .getAllBatchOperations();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "查詢成功");
        response.put("data", responseDTOs);
        response.put("total", responseDTOs.size());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 根據操作員ID查詢批次操作
     * GET /api/batch-operations/operator/{operatorId}
     */
    @GetMapping("/operator/{operatorId}")
    public ResponseEntity<Map<String, Object>> getBatchOperationsByOperatorId(
            @PathVariable Integer operatorId) {
        List<BatchOperationsResponseDTO> responseDTOs = batchOperationsService
                .getBatchOperationsByOperatorId(operatorId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "查詢成功");
        response.put("data", responseDTOs);
        response.put("total", responseDTOs.size());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 根據狀態查詢批次操作
     * GET /api/batch-operations/status/{status}
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<Map<String, Object>> getBatchOperationsByStatus(
            @PathVariable String status) {
        List<BatchOperationsResponseDTO> responseDTOs = batchOperationsService
                .getBatchOperationsByStatus(status);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "查詢成功");
        response.put("data", responseDTOs);
        response.put("total", responseDTOs.size());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 根據操作類型查詢批次操作
     * GET /api/batch-operations/type/{operationType}
     */
    @GetMapping("/type/{operationType}")
    public ResponseEntity<Map<String, Object>> getBatchOperationsByOperationType(
            @PathVariable String operationType) {
        List<BatchOperationsResponseDTO> responseDTOs = batchOperationsService
                .getBatchOperationsByOperationType(operationType);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "查詢成功");
        response.put("data", responseDTOs);
        response.put("total", responseDTOs.size());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 根據操作員ID和狀態查詢批次操作
     * GET /api/batch-operations/operator/{operatorId}/status/{status}
     */
    @GetMapping("/operator/{operatorId}/status/{status}")
    public ResponseEntity<Map<String, Object>> getBatchOperationsByOperatorIdAndStatus(
            @PathVariable Integer operatorId,
            @PathVariable String status) {
        List<BatchOperationsResponseDTO> responseDTOs = batchOperationsService
                .getBatchOperationsByOperatorIdAndStatus(operatorId, status);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "查詢成功");
        response.put("data", responseDTOs);
        response.put("total", responseDTOs.size());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 根據時間範圍查詢批次操作
     * GET /api/batch-operations/date-range?startDate=2024-01-01T00:00:00&endDate=2024-12-31T23:59:59
     */
    @GetMapping("/date-range")
    public ResponseEntity<Map<String, Object>> getBatchOperationsByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        LocalDateTime start = LocalDateTime.parse(startDate);
        LocalDateTime end = LocalDateTime.parse(endDate);
        
        List<BatchOperationsResponseDTO> responseDTOs = batchOperationsService
                .getBatchOperationsByDateRange(start, end);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "查詢成功");
        response.put("data", responseDTOs);
        response.put("total", responseDTOs.size());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 更新批次操作
     * PUT /api/batch-operations/{batchId}
     */
    @PutMapping("/{batchId}")
    public ResponseEntity<Map<String, Object>> updateBatchOperation(
            @PathVariable Integer batchId,
            @RequestBody BatchOperationsRequestDTO requestDTO) {
        try {
            BatchOperationsResponseDTO responseDTO = batchOperationsService
                    .updateBatchOperation(batchId, requestDTO);
            
            Map<String, Object> response = new HashMap<>();
            if (responseDTO != null) {
                response.put("success", true);
                response.put("message", "批次操作更新成功");
                response.put("data", responseDTO);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "查無此批次操作記錄或更新失敗");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "批次操作更新失敗: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 更新批次操作狀態
     * PATCH /api/batch-operations/{batchId}/status?status=COMPLETED
     */
    @PatchMapping("/{batchId}/status")
    public ResponseEntity<Map<String, Object>> updateBatchOperationStatus(
            @PathVariable Integer batchId,
            @RequestParam String status) {
        boolean updated = batchOperationsService.updateBatchOperationStatus(batchId, status);
        
        Map<String, Object> response = new HashMap<>();
        if (updated) {
            response.put("success", true);
            response.put("message", "狀態更新成功");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "狀態更新失敗");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    
    /**
     * 開始執行批次操作
     * POST /api/batch-operations/{batchId}/start?totalItems=1000
     */
    @PostMapping("/{batchId}/start")
    public ResponseEntity<Map<String, Object>> startBatchOperation(
            @PathVariable Integer batchId,
            @RequestParam Integer totalItems) {
        boolean started = batchOperationsService.startBatchOperation(batchId, totalItems);
        
        Map<String, Object> response = new HashMap<>();
        if (started) {
            response.put("success", true);
            response.put("message", "批次操作已開始執行");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "批次操作啟動失敗");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 完成批次操作
     * POST /api/batch-operations/{batchId}/complete?successCount=950&failCount=50
     */
    @PostMapping("/{batchId}/complete")
    public ResponseEntity<Map<String, Object>> completeBatchOperation(
            @PathVariable Integer batchId,
            @RequestParam Integer successCount,
            @RequestParam Integer failCount) {
        boolean completed = batchOperationsService
                .completeBatchOperation(batchId, successCount, failCount);
        
        Map<String, Object> response = new HashMap<>();
        if (completed) {
            response.put("success", true);
            response.put("message", "批次操作已完成");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "批次操作完成記錄失敗");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 刪除批次操作
     * DELETE /api/batch-operations/{batchId}
     */
    @DeleteMapping("/{batchId}")
    public ResponseEntity<Map<String, Object>> deleteBatchOperation(
            @PathVariable Integer batchId) {
        boolean deleted = batchOperationsService.deleteBatchOperation(batchId);
        
        Map<String, Object> response = new HashMap<>();
        if (deleted) {
            response.put("success", true);
            response.put("message", "批次操作刪除成功");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "批次操作刪除失敗");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}