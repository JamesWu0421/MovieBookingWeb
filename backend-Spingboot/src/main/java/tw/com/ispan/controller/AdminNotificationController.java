package tw.com.ispan.controller;

import lombok.RequiredArgsConstructor;
import tw.com.ispan.dto.*;
import tw.com.ispan.model.Event;
import tw.com.ispan.model.Movie;
import tw.com.ispan.model.Notification;
import tw.com.ispan.repository.EventRepository;
import tw.com.ispan.repository.MovieRepository;
import tw.com.ispan.service.NotificationService;
import tw.com.ispan.service.PaymentService;
import tw.com.ispan.service.SystemNotificationService;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理員通知控制器 - 整合所有後台通知管理功能
 * 
 * 功能包括:
 * 1. 通知的 CRUD 操作
 * 2. 通知推送功能
 * 3. 快速創建通知
 * 4. 支付通知測試
 * 5. 系統通知發送
 */
@RestController
@RequestMapping("/api/admin/notifications")
@PreAuthorize("hasAnyRole('ADMIN','MANAGER','SUPPORT_SERVICE','ENTRYSTAFF')")
@RequiredArgsConstructor
public class AdminNotificationController {

    private final PaymentService paymentService;
    private final SystemNotificationService systemNotificationService;
    private final NotificationService notificationService;
    private final EventRepository eventRepository;
    private final MovieRepository movieRepository;

    // ==================== 通知 CRUD 操作 ====================

    /**
     * 獲取通知列表
     * GET /api/admin/notifications
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getNotifications(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String type) {

        Page<Notification> notificationPage = notificationService.getNotifications(page, size, query, type);

        Map<String, Object> response = new HashMap<>();
        response.put("notifications", notificationPage.getContent());
        response.put("currentPage", notificationPage.getNumber() + 1);
        response.put("totalItems", notificationPage.getTotalElements());
        response.put("totalPages", notificationPage.getTotalPages());

        return ResponseEntity.ok(response);
    }

    /**
     * 根據 ID 獲取通知
     * GET /api/admin/notifications/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Notification> getNotification(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.getNotificationById(id));
    }

    /**
     * 創建通知
     * POST /api/admin/notifications
     */
    @PostMapping
    public ResponseEntity<Notification> createNotification(@RequestBody NotificationRequest request) {
        return ResponseEntity.ok(notificationService.createNotification(request));
    }

    /**
     * 更新通知
     * PUT /api/admin/notifications/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Notification> updateNotification(
            @PathVariable Long id,
            @RequestBody NotificationRequest request) {
        return ResponseEntity.ok(notificationService.updateNotification(id, request));
    }

    /**
     * 刪除通知
     * DELETE /api/admin/notifications/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "通知刪除成功");
        return ResponseEntity.ok(response);
    }

    /**
     * 推送通知
     * POST /api/admin/notifications/{id}/send
     */
    @PostMapping("/{id}/send")
    public ResponseEntity<Map<String, String>> sendNotification(@RequestBody SendNotificationRequest request) {
        notificationService.sendNotification(request);
        Map<String, String> response = new HashMap<>();
        response.put("message", "通知推送成功");
        return ResponseEntity.ok(response);
    }

    // ==================== 快速創建通知 ====================

    /**
     * 從活動快速創建並推送通知
     * POST /api/admin/notifications/quick/event
     */
    @PostMapping("/quick/event")
    public ResponseEntity<Map<String, Object>> quickCreateFromEvent(@RequestBody QuickNotificationRequest request) {
        Notification notification = notificationService.createNotificationFromEvent(
                request.getSourceId(),
                request.getPushType(),
                request.getUserIds());

        Map<String, Object> response = new HashMap<>();
        response.put("message", "通知創建並推送成功");
        response.put("notification", notification);
        return ResponseEntity.ok(response);
    }

    /**
     * 從電影快速創建並推送通知
     * POST /api/admin/notifications/quick/movie
     */
    @PostMapping("/quick/movie")
    public ResponseEntity<Map<String, Object>> quickCreateFromMovie(@RequestBody QuickNotificationRequest request) {
        Notification notification = notificationService.createNotificationFromMovie(
                request.getSourceId(),
                request.getPushType(),
                request.getUserIds());

        Map<String, Object> response = new HashMap<>();
        response.put("message", "通知創建並推送成功");
        response.put("notification", notification);
        return ResponseEntity.ok(response);
    }

    /**
     * 獲取活動列表（用於快速創建）
     * GET /api/admin/notifications/sources/events
     */
    @GetMapping("/sources/events")
    public ResponseEntity<List<Event>> getEventSources(@RequestParam(required = false) String category) {
        List<Event> events;
        if (category != null && !category.trim().isEmpty() && !"all".equals(category)) {
            events = eventRepository.findByCategoryAndIsActiveTrue(category);
        } else {
            events = eventRepository.findByIsActiveTrue();
        }
        return ResponseEntity.ok(events);
    }

    /**
     * 獲取電影列表（用於快速創建）
     * GET /api/admin/notifications/sources/movies
     */
    @GetMapping("/sources/movies")
    public ResponseEntity<List<Movie>> getMovieSources(@RequestParam(defaultValue = "true") Boolean published) {
        List<Movie> movies;
        if (published) {
            movies = movieRepository.findByIsPublishedTrue();
        } else {
            movies = movieRepository.findAll();
        }
        return ResponseEntity.ok(movies);
    }

    // ==================== 支付通知測試端點 ====================

    /**
     * 測試：發送支付成功通知
     * POST /api/admin/notifications/test/payment-success
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
     * POST /api/admin/notifications/test/payment-failure
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
     * POST /api/admin/notifications/test/refund-success
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
     * POST /api/admin/notifications/test/payment-timeout
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
     * POST /api/admin/notifications/system/maintenance
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
     * POST /api/admin/notifications/system/emergency
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
     * POST /api/admin/notifications/system/upgrade
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
     * POST /api/admin/notifications/system/holiday-schedule
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
     * POST /api/admin/notifications/system/security-alert
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
