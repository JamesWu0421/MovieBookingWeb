package tw.com.ispan.controller;

import tw.com.ispan.dto.BatchSessionsTempRequestDTO;
import tw.com.ispan.dto.BatchSessionsTempResponseDTO;
import tw.com.ispan.service.BatchSessionsTempService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * BatchSessionsTemp Controller
 * 處理 batch_sessions_temp 相關 HTTP 請求
 */
@RestController
@RequestMapping("/api/batch-sessions-temp")
// 如需 CORS 再加:@CrossOrigin(origins = "*")
public class BatchSessionsTempController {

    @Autowired
    private BatchSessionsTempService batchSessionsTempService;

    /**
     * 建立暫存場次
     * POST /api/batch-sessions-temp
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createSessionTemp(
            @RequestBody BatchSessionsTempRequestDTO requestDTO) {

        try {
            BatchSessionsTempResponseDTO responseDTO =
                    batchSessionsTempService.createSessionTemp(requestDTO);

            Map<String, Object> resp = new HashMap<>();
            resp.put("success", true);
            resp.put("message", "暫存場次建立成功");
            resp.put("data", responseDTO);

            return ResponseEntity.status(HttpStatus.CREATED).body(resp);
        } catch (Exception e) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("message", "暫存場次建立失敗: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
        }
    }

    /**
     * 依 ID 查詢
     * GET /api/batch-sessions-temp/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getSessionTempById(@PathVariable Integer id) {
        BatchSessionsTempResponseDTO dto = batchSessionsTempService.getById(id);

        Map<String, Object> resp = new HashMap<>();
        if (dto != null) {
            resp.put("success", true);
            resp.put("message", "查詢成功");
            resp.put("data", dto);
            return ResponseEntity.ok(resp);
        } else {
            resp.put("success", false);
            resp.put("message", "查無此暫存場次");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resp);
        }
    }

    /**
     * 查詢全部
     * GET /api/batch-sessions-temp
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllSessionTemps() {
        List<BatchSessionsTempResponseDTO> list = batchSessionsTempService.getAll();

        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("message", "查詢成功");
        resp.put("data", list);
        resp.put("total", list.size());

        return ResponseEntity.ok(resp);
    }

    /**
     * 依 batchId 查詢
     * GET /api/batch-sessions-temp/batch/{batchId}
     */
    @GetMapping("/batch/{batchId}")
    public ResponseEntity<Map<String, Object>> getByBatchId(@PathVariable Integer batchId) {
        List<BatchSessionsTempResponseDTO> list = batchSessionsTempService.getByBatchId(batchId);

        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("message", "查詢成功");
        resp.put("data", list);
        resp.put("total", list.size());

        return ResponseEntity.ok(resp);
    }

    /**
     * 依狀態查詢
     * GET /api/batch-sessions-temp/status/{status}
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<Map<String, Object>> getByStatus(@PathVariable String status) {
        List<BatchSessionsTempResponseDTO> list = batchSessionsTempService.getByStatus(status);

        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("message", "查詢成功");
        resp.put("data", list);
        resp.put("total", list.size());

        return ResponseEntity.ok(resp);
    }

    /**
     * 更新暫存場次
     * PUT /api/batch-sessions-temp/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateSessionTemp(
            @PathVariable Integer id,
            @RequestBody BatchSessionsTempRequestDTO requestDTO) {

        try {
            BatchSessionsTempResponseDTO dto =
                    batchSessionsTempService.updateSessionTemp(id, requestDTO);

            Map<String, Object> resp = new HashMap<>();
            if (dto != null) {
                resp.put("success", true);
                resp.put("message", "暫存場次更新成功");
                resp.put("data", dto);
                return ResponseEntity.ok(resp);
            } else {
                resp.put("success", false);
                resp.put("message", "查無此暫存場次或更新失敗");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resp);
            }
        } catch (Exception e) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("message", "暫存場次更新失敗: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
        }
    }

    /**
     * 刪除暫存場次
     * DELETE /api/batch-sessions-temp/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteSessionTemp(@PathVariable Integer id) {
        boolean deleted = batchSessionsTempService.deleteSessionTemp(id);

        Map<String, Object> resp = new HashMap<>();
        if (deleted) {
            resp.put("success", true);
            resp.put("message", "暫存場次刪除成功");
            return ResponseEntity.ok(resp);
        } else {
            resp.put("success", false);
            resp.put("message", "暫存場次刪除失敗");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resp);
        }
    }
}
