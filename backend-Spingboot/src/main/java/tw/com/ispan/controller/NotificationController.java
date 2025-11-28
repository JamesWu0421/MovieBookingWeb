package tw.com.ispan.controller;

import lombok.RequiredArgsConstructor;
import tw.com.ispan.dto.UserNotificationDTO;
import tw.com.ispan.service.NotificationService;

import org.springframework.data.domain.Page;
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
 * 
 * 後台管理功能已移至 AdminNotificationController
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * 獲取用戶通知列表
     * GET /api/notifications/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserNotifications(
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Boolean unreadOnly) {

        Page<UserNotificationDTO> notificationPage = notificationService.getUserNotifications(
                userId, page, size, unreadOnly);

        Map<String, Object> response = new HashMap<>();
        response.put("notifications", notificationPage.getContent());
        response.put("currentPage", notificationPage.getNumber() + 1);
        response.put("totalItems", notificationPage.getTotalElements());
        response.put("totalPages", notificationPage.getTotalPages());

        return ResponseEntity.ok(response);
    }

    /**
     * 標記通知為已讀
     * PUT /api/notifications/user/{userId}/notification/{notificationId}/read
     */
    @PutMapping("/user/{userId}/notification/{notificationId}/read")
    public ResponseEntity<Map<String, String>> markAsRead(
            @PathVariable Integer userId,
            @PathVariable Long notificationId) {
        notificationService.markAsRead(userId, notificationId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "標記已讀成功");
        return ResponseEntity.ok(response);
    }

    /**
     * 獲取用戶未讀通知數量
     * GET /api/notifications/user/{userId}/unread-count
     */
    @GetMapping("/user/{userId}/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@PathVariable Integer userId) {
        long count = notificationService.getUnreadCount(userId);
        Map<String, Long> response = new HashMap<>();
        response.put("unreadCount", count);
        return ResponseEntity.ok(response);
    }
}
