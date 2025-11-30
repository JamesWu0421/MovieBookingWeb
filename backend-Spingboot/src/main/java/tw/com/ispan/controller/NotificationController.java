package tw.com.ispan.controller;

import tw.com.ispan.dto.UserNotificationDTO;
import tw.com.ispan.service.NotificationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 用戶通知控制器
 * 
 * 此控制器只包含用戶前台的通知功能：
 * 1. 獲取用戶的通知列表
 * 2. 標記通知為已讀
 * 3. 獲取未讀通知數量
 */
@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*", maxAge = 3600)
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    /**
     * 測試端點 - 確認 Controller 有被註冊
     * GET /api/notifications/test
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, String>> test() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "NotificationController is working!");
        response.put("status", "OK");
        response.put("timestamp", String.valueOf(System.currentTimeMillis()));
        return ResponseEntity.ok(response);
    }

    /**
     * 獲取用戶通知列表
     * GET /api/notifications/user/{userId}?page=1&size=10&unreadOnly=false
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserNotifications(
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Boolean unreadOnly) {

        try {
            System.out.println("=== 獲取用戶通知 ===");
            System.out.println("userId: " + userId);
            System.out.println("page: " + page);
            System.out.println("size: " + size);
            System.out.println("unreadOnly: " + unreadOnly);

            Page<UserNotificationDTO> notificationPage = notificationService.getUserNotifications(
                    userId, page, size, unreadOnly);

            Map<String, Object> response = new HashMap<>();
            response.put("notifications", notificationPage.getContent());
            response.put("currentPage", notificationPage.getNumber() + 1);
            response.put("totalItems", notificationPage.getTotalElements());
            response.put("totalPages", notificationPage.getTotalPages());

            System.out.println("✅ 成功返回 " + notificationPage.getContent().size() + " 條通知");

            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("❌ 獲取通知失敗: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> error = new HashMap<>();
            error.put("message", "獲取通知失敗: " + e.getMessage());
            error.put("error", e.getClass().getSimpleName());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * 獲取用戶未讀通知數量
     * GET /api/notifications/user/{userId}/unread-count
     */
    @GetMapping("/user/{userId}/unread-count")
    public ResponseEntity<Map<String, Object>> getUnreadCount(@PathVariable Integer userId) {
        try {
            System.out.println("=== 獲取未讀數量 ===");
            System.out.println("userId: " + userId);

            long count = notificationService.getUnreadCount(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("unreadCount", count);

            System.out.println("✅ 未讀數量: " + count);

            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("❌ 獲取未讀數失敗: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> error = new HashMap<>();
            error.put("unreadCount", 0L);
            error.put("message", "獲取失敗: " + e.getMessage());
            error.put("error", e.getClass().getSimpleName());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * 標記通知為已讀
     * PUT /api/notifications/user/{userId}/notification/{notificationId}/read
     */
    @PutMapping("/user/{userId}/notification/{notificationId}/read")
    public ResponseEntity<Map<String, Object>> markAsRead(
            @PathVariable Integer userId,
            @PathVariable Long notificationId) {
        try {
            System.out.println("=== 標記已讀 ===");
            System.out.println("userId: " + userId);
            System.out.println("notificationId: " + notificationId);

            notificationService.markAsRead(userId, notificationId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "標記已讀成功");
            response.put("success", true);

            System.out.println("✅ 標記已讀成功");

            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("❌ 標記已讀失敗: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> error = new HashMap<>();
            error.put("message", "標記失敗: " + e.getMessage());
            error.put("success", false);
            error.put("error", e.getClass().getSimpleName());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * 標記所有通知為已讀
     * PUT /api/notifications/user/{userId}/read-all
     */
    @PutMapping("/user/{userId}/read-all")
    public ResponseEntity<Map<String, Object>> markAllAsRead(@PathVariable Integer userId) {
        try {
            System.out.println("=== 標記全部已讀 ===");
            System.out.println("userId: " + userId);

            // 獲取所有未讀通知
            Page<UserNotificationDTO> unreadNotifications = notificationService.getUserNotifications(
                    userId, 1, 1000, true);

            // 逐一標記為已讀
            int count = 0;
            for (UserNotificationDTO notification : unreadNotifications.getContent()) {
                notificationService.markAsRead(userId, notification.getNotificationId());
                count++;
            }

            Map<String, Object> response = new HashMap<>();
            response.put("message", "全部標記成功");
            response.put("success", true);
            response.put("count", count);

            System.out.println("✅ 已標記 " + count + " 條通知為已讀");

            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("❌ 標記全部已讀失敗: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> error = new HashMap<>();
            error.put("message", "標記失敗: " + e.getMessage());
            error.put("success", false);
            error.put("error", e.getClass().getSimpleName());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}