package tw.com.ispan.controller;

import tw.com.ispan.dto.BatchTicketsTempRequestDTO;
import tw.com.ispan.dto.BatchTicketsTempResponseDTO;
import tw.com.ispan.service.BatchTicketsTempService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * BatchTicketsTemp Controller
 * 處理 batch_tickets_temp 相關 HTTP 請求
 */
@RestController
@RequestMapping("/api/batch-tickets-temp")
// 如需 CORS：@CrossOrigin(origins = "*")
public class BatchTicketsTempController {

    @Autowired
    private BatchTicketsTempService batchTicketsTempService;

    /**
     * 建立暫存票券
     * POST /api/batch-tickets-temp
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createTicketTemp(
            @RequestBody BatchTicketsTempRequestDTO requestDTO) {

        try {
            BatchTicketsTempResponseDTO dto =
                    batchTicketsTempService.createTicketTemp(requestDTO);

            Map<String, Object> resp = new HashMap<>();
            resp.put("success", true);
            resp.put("message", "暫存票券建立成功");
            resp.put("data", dto);

            return ResponseEntity.status(HttpStatus.CREATED).body(resp);
        } catch (Exception e) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("message", "暫存票券建立失敗: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
        }
    }

    /**
     * 依 ID 查詢
     * GET /api/batch-tickets-temp/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getTicketTempById(@PathVariable Integer id) {
        BatchTicketsTempResponseDTO dto = batchTicketsTempService.getById(id);

        Map<String, Object> resp = new HashMap<>();
        if (dto != null) {
            resp.put("success", true);
            resp.put("message", "查詢成功");
            resp.put("data", dto);
            return ResponseEntity.ok(resp);
        } else {
            resp.put("success", false);
            resp.put("message", "查無此暫存票券");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resp);
        }
    }

    /**
     * 查詢全部
     * GET /api/batch-tickets-temp
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllTicketTemps() {
        List<BatchTicketsTempResponseDTO> list = batchTicketsTempService.getAll();

        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("message", "查詢成功");
        resp.put("data", list);
        resp.put("total", list.size());

        return ResponseEntity.ok(resp);
    }

    /**
     * 依 batchId 查詢
     * GET /api/batch-tickets-temp/batch/{batchId}
     */
    @GetMapping("/batch/{batchId}")
    public ResponseEntity<Map<String, Object>> getByBatchId(@PathVariable Integer batchId) {
        List<BatchTicketsTempResponseDTO> list = batchTicketsTempService.getByBatchId(batchId);

        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("message", "查詢成功");
        resp.put("data", list);
        resp.put("total", list.size());

        return ResponseEntity.ok(resp);
    }

    /**
     * 依 batchSessionId 查詢
     * GET /api/batch-tickets-temp/session/{batchSessionId}
     */
    @GetMapping("/session/{batchSessionId}")
    public ResponseEntity<Map<String, Object>> getByBatchSessionId(
            @PathVariable Integer batchSessionId) {

        List<BatchTicketsTempResponseDTO> list =
                batchTicketsTempService.getByBatchSessionId(batchSessionId);

        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("message", "查詢成功");
        resp.put("data", list);
        resp.put("total", list.size());

        return ResponseEntity.ok(resp);
    }

    /**
     * 依狀態查詢
     * GET /api/batch-tickets-temp/status/{status}
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<Map<String, Object>> getByStatus(@PathVariable String status) {
        List<BatchTicketsTempResponseDTO> list =
                batchTicketsTempService.getByStatus(status);

        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("message", "查詢成功");
        resp.put("data", list);
        resp.put("total", list.size());

        return ResponseEntity.ok(resp);
    }

    /**
     * 更新暫存票券
     * PUT /api/batch-tickets-temp/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateTicketTemp(
            @PathVariable Integer id,
            @RequestBody BatchTicketsTempRequestDTO requestDTO) {

        try {
            BatchTicketsTempResponseDTO dto =
                    batchTicketsTempService.updateTicketTemp(id, requestDTO);

            Map<String, Object> resp = new HashMap<>();
            if (dto != null) {
                resp.put("success", true);
                resp.put("message", "暫存票券更新成功");
                resp.put("data", dto);
                return ResponseEntity.ok(resp);
            } else {
                resp.put("success", false);
                resp.put("message", "查無此暫存票券或更新失敗");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resp);
            }
        } catch (Exception e) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("message", "暫存票券更新失敗: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
        }
    }

    /**
     * 刪除暫存票券
     * DELETE /api/batch-tickets-temp/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteTicketTemp(@PathVariable Integer id) {
        boolean deleted = batchTicketsTempService.deleteTicketTemp(id);

        Map<String, Object> resp = new HashMap<>();
        if (deleted) {
            resp.put("success", true);
            resp.put("message", "暫存票券刪除成功");
            return ResponseEntity.ok(resp);
        } else {
            resp.put("success", false);
            resp.put("message", "暫存票券刪除失敗");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resp);
        }
    }
}
