package tw.com.ispan.controller;

import lombok.RequiredArgsConstructor;
import tw.com.ispan.service.PaymentService;
import tw.com.ispan.service.SystemNotificationService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 管理員通知測試控制器
 * 
 * 這個控制器提供了測試各種自動通知功能的端點
 * 在實際生產環境中，這些功能應該整合到對應的業務邏輯中
 * 
 * 用途：方便測試和演示自動通知功能
 */
@RestController
@RequestMapping("/api/admin/notifications")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminNotificationController {

    private final PaymentService paymentService;
    private final SystemNotificationService systemNotificationService;

    // ==================== 支付通知測試端點 ====================

    /**
     * 測試：發送支付成功通知
     * 
     * POST /api/admin/notifications/test/payment-success
     * Body: {
     * "paymentId": 1,
     * "userId": 1,
     * "orderId": 1,
     * "amount": 350.00
     * }
     */
    @PostMapping("/test/payment-success")
    public ResponseEntity<Map<String, String>> testPaymentSuccess(@RequestBody Map<String, Object> request) {
        Integer paymentId = (Integer) request.get("paymentId");
        Integer userId = (Integer) request.get("userId");
        Integer orderId = (Integer) request.get("orderId");
        Double amount = ((Number) request.get("amount")).doubleValue();

        paymentService.handlePaymentSuccess(paymentId, userId, orderId, amount);

        Map<String, String> response = new HashMap<>();
        response.put("message", "支付成功通知已發送");
        response.put("type", "PAYMENT");
        response.put("status", "SUCCESS");
        return ResponseEntity.ok(response);
    }

    /**
     * 測試：發送支付失敗通知
     * 
     * POST /api/admin/notifications/test/payment-failure
     * Body: {
     * "paymentId": 1,
     * "userId": 1,
     * "orderId": 1,
     * "reason": "餘額不足"
     * }
     */
    @PostMapping("/test/payment-failure")
    public ResponseEntity<Map<String, String>> testPaymentFailure(@RequestBody Map<String, Object> request) {
        Integer paymentId = (Integer) request.get("paymentId");
        Integer userId = (Integer) request.get("userId");
        Integer orderId = (Integer) request.get("orderId");
        String reason = (String) request.get("reason");

        paymentService.handlePaymentFailure(paymentId, userId, orderId, reason);

        Map<String, String> response = new HashMap<>();
        response.put("message", "支付失敗通知已發送");
        response.put("type", "PAYMENT");
        response.put("status", "FAILURE");
        return ResponseEntity.ok(response);
    }

    /**
     * 測試：發送退款成功通知
     * 
     * POST /api/admin/notifications/test/refund-success
     * Body: {
     * "paymentId": 1,
     * "userId": 1,
     * "orderId": 1,
     * "refundAmount": 350.00
     * }
     */
    @PostMapping("/test/refund-success")
    public ResponseEntity<Map<String, String>> testRefundSuccess(@RequestBody Map<String, Object> request) {
        Integer paymentId = (Integer) request.get("paymentId");
        Integer userId = (Integer) request.get("userId");
        Integer orderId = (Integer) request.get("orderId");
        Double refundAmount = ((Number) request.get("refundAmount")).doubleValue();

        paymentService.handleRefundSuccess(paymentId, userId, orderId, refundAmount);

        Map<String, String> response = new HashMap<>();
        response.put("message", "退款成功通知已發送");
        response.put("type", "PAYMENT");
        response.put("status", "REFUND_SUCCESS");
        return ResponseEntity.ok(response);
    }

    /**
     * 測試：發送支付超時通知
     * 
     * POST /api/admin/notifications/test/payment-timeout
     * Body: {
     * "paymentId": 1,
     * "userId": 1,
     * "orderId": 1
     * }
     */
    @PostMapping("/test/payment-timeout")
    public ResponseEntity<Map<String, String>> testPaymentTimeout(@RequestBody Map<String, Object> request) {
        Integer paymentId = (Integer) request.get("paymentId");
        Integer userId = (Integer) request.get("userId");
        Integer orderId = (Integer) request.get("orderId");

        paymentService.handlePaymentTimeout(paymentId, userId, orderId);

        Map<String, String> response = new HashMap<>();
        response.put("message", "支付超時通知已發送");
        response.put("type", "PAYMENT");
        response.put("status", "TIMEOUT");
        return ResponseEntity.ok(response);
    }

    // ==================== 系統通知測試端點 ====================

    /**
     * 發送系統維護通知（推送給所有用戶）
     * 
     * POST /api/admin/notifications/system/maintenance
     * Body: {
     * "startTime": "2024-11-30 02:00",
     * "endTime": "2024-11-30 06:00",
     * "reason": "系統升級和數據庫優化"
     * }
     */
    @PostMapping("/system/maintenance")
    public ResponseEntity<Map<String, String>> notifyMaintenance(@RequestBody Map<String, String> request) {
        String startTime = request.get("startTime");
        String endTime = request.get("endTime");
        String reason = request.get("reason");

        systemNotificationService.notifySystemMaintenance(startTime, endTime, reason);

        Map<String, String> response = new HashMap<>();
        response.put("message", "系統維護通知已發送給所有用戶");
        response.put("type", "SYSTEM");
        response.put("subType", "MAINTENANCE");
        return ResponseEntity.ok(response);
    }

    /**
     * 發送緊急系統通知（推送給所有用戶）
     * 
     * POST /api/admin/notifications/system/emergency
     * Body: {
     * "title": "支付系統異常",
     * "content": "目前支付系統出現異常，工程師正在緊急處理中..."
     * }
     */
    @PostMapping("/system/emergency")
    public ResponseEntity<Map<String, String>> notifyEmergency(@RequestBody Map<String, String> request) {
        String title = request.get("title");
        String content = request.get("content");

        systemNotificationService.notifyEmergency(title, content);

        Map<String, String> response = new HashMap<>();
        response.put("message", "緊急通知已發送給所有用戶");
        response.put("type", "SYSTEM");
        response.put("subType", "EMERGENCY");
        return ResponseEntity.ok(response);
    }

    /**
     * 發送系統升級完成通知（推送給所有用戶）
     * 
     * POST /api/admin/notifications/system/upgrade
     * Body: {
     * "version": "2.0.0",
     * "features": "1. 新增會員積分系統\n2. 優化訂票流程\n3. 支持多種支付方式"
     * }
     */
    @PostMapping("/system/upgrade")
    public ResponseEntity<Map<String, String>> notifyUpgrade(@RequestBody Map<String, String> request) {
        String version = request.get("version");
        String features = request.get("features");

        systemNotificationService.notifySystemUpgrade(version, features);

        Map<String, String> response = new HashMap<>();
        response.put("message", "系統升級通知已發送給所有用戶");
        response.put("type", "SYSTEM");
        response.put("subType", "UPGRADE");
        return ResponseEntity.ok(response);
    }

    /**
     * 發送服務恢復通知（推送給所有用戶）
     * 
     * POST /api/admin/notifications/system/service-restored
     */
    @PostMapping("/system/service-restored")
    public ResponseEntity<Map<String, String>> notifyServiceRestored() {
        systemNotificationService.notifyServiceRestored();

        Map<String, String> response = new HashMap<>();
        response.put("message", "服務恢復通知已發送給所有用戶");
        response.put("type", "SYSTEM");
        response.put("subType", "SERVICE_RESTORED");
        return ResponseEntity.ok(response);
    }

    /**
     * 發送節假日營業時間調整通知（推送給所有用戶）
     * 
     * POST /api/admin/notifications/system/holiday-schedule
     * Body: {
     * "holiday": "春節",
     * "adjustedHours": "1/28-1/30：10:00-22:00\n1/31-2/2：休息"
     * }
     */
    @PostMapping("/system/holiday-schedule")
    public ResponseEntity<Map<String, String>> notifyHolidaySchedule(@RequestBody Map<String, String> request) {
        String holiday = request.get("holiday");
        String adjustedHours = request.get("adjustedHours");

        systemNotificationService.notifyHolidaySchedule(holiday, adjustedHours);

        Map<String, String> response = new HashMap<>();
        response.put("message", "節假日通知已發送給所有用戶");
        response.put("type", "SYSTEM");
        response.put("subType", "HOLIDAY");
        return ResponseEntity.ok(response);
    }

    /**
     * 發送安全提醒通知（推送給所有用戶）
     * 
     * POST /api/admin/notifications/system/security-alert
     * Body: {
     * "title": "密碼安全提醒",
     * "content": "近期發現部分用戶賬號遭遇暴力破解，請立即更新您的密碼..."
     * }
     */
    @PostMapping("/system/security-alert")
    public ResponseEntity<Map<String, String>> notifySecurityAlert(@RequestBody Map<String, String> request) {
        String title = request.get("title");
        String content = request.get("content");

        systemNotificationService.notifySecurityAlert(title, content);

        Map<String, String> response = new HashMap<>();
        response.put("message", "安全提醒已發送給所有用戶");
        response.put("type", "SYSTEM");
        response.put("subType", "SECURITY");
        return ResponseEntity.ok(response);
    }

    // ==================== 通用測試端點 ====================

    /**
     * 獲取所有可用的測試端點列表
     * 
     * GET /api/admin/notifications/test/endpoints
     */
    @GetMapping("/test/endpoints")
    public ResponseEntity<Map<String, Object>> getTestEndpoints() {
        Map<String, Object> endpoints = new HashMap<>();

        // 支付通知端點
        Map<String, String> paymentEndpoints = new HashMap<>();
        paymentEndpoints.put("支付成功", "POST /api/admin/notifications/test/payment-success");
        paymentEndpoints.put("支付失敗", "POST /api/admin/notifications/test/payment-failure");
        paymentEndpoints.put("退款成功", "POST /api/admin/notifications/test/refund-success");
        paymentEndpoints.put("支付超時", "POST /api/admin/notifications/test/payment-timeout");
        endpoints.put("支付通知測試", paymentEndpoints);

        // 系統通知端點
        Map<String, String> systemEndpoints = new HashMap<>();
        systemEndpoints.put("系統維護", "POST /api/admin/notifications/system/maintenance");
        systemEndpoints.put("緊急通知", "POST /api/admin/notifications/system/emergency");
        systemEndpoints.put("系統升級", "POST /api/admin/notifications/system/upgrade");
        systemEndpoints.put("服務恢復", "POST /api/admin/notifications/system/service-restored");
        systemEndpoints.put("節假日通知", "POST /api/admin/notifications/system/holiday-schedule");
        systemEndpoints.put("安全提醒", "POST /api/admin/notifications/system/security-alert");
        endpoints.put("系統通知", systemEndpoints);

        return ResponseEntity.ok(endpoints);
    }
}

/*
 * ===========================================
 * 使用說明：
 * ===========================================
 * 
 * 1. 這個控制器提供了測試各種自動通知的端點
 * 2. 可以用 Postman 或前端頁面來測試這些功能
 * 3. 在實際環境中：
 * - 支付通知應該在支付回調或支付處理邏輯中自動觸發
 * - 訂單通知應該在 OrderService 中自動觸發
 * - 系統通知可以在管理後台手動觸發，或通過定時任務觸發
 * 
 * ===========================================
 * Postman 測試示例：
 * ===========================================
 * 
 * 測試支付成功通知：
 * POST http://localhost:8080/api/admin/notifications/test/payment-success
 * Content-Type: application/json
 * 
 * {
 * "paymentId": 1,
 * "userId": 1,
 * "orderId": 1,
 * "amount": 350.00
 * }
 * 
 * ---
 * 
 * 測試系統維護通知：
 * POST http://localhost:8080/api/admin/notifications/system/maintenance
 * Content-Type: application/json
 * 
 * {
 * "startTime": "2024-11-30 02:00",
 * "endTime": "2024-11-30 06:00",
 * "reason": "系統升級和數據庫優化"
 * }
 * 
 * ===========================================
 * 安全提醒：
 * ===========================================
 * 
 * 在生產環境中，這些測試端點應該：
 * 1. 加上認證檢查（只有管理員可以訪問）
 * 2. 加上日誌記錄（記錄誰發送了什麼通知）
 * 3. 加上權限控制（不同管理員有不同權限）
 * 
 * 示例：
 * 
 * @PreAuthorize("hasRole('ADMIN')")
 * 
 * @PostMapping("/system/emergency")
 * public ResponseEntity<?> notifyEmergency(...) {
 * // 記錄操作日誌
 * logger.info("管理員 {} 發送緊急通知", getCurrentUser());
 * // ...
 * }
 */
