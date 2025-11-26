package com.example.movie.controller;

import com.example.movie.dto.*;
import com.example.movie.model.Event;
import com.example.movie.model.Movie;
import com.example.movie.model.Notification;
import com.example.movie.repository.EventRepository;
import com.example.movie.repository.MovieRepository;
import com.example.movie.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class NotificationController {
    
    private final NotificationService notificationService;
    private final EventRepository eventRepository;
    private final MovieRepository movieRepository;
    
    /**
     * 獲取通知列表
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
     */
    @GetMapping("/{id}")
    public ResponseEntity<Notification> getNotification(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.getNotificationById(id));
    }
    
    /**
     * 創建通知
     */
    @PostMapping
    public ResponseEntity<Notification> createNotification(@RequestBody NotificationRequest request) {
        return ResponseEntity.ok(notificationService.createNotification(request));
    }
    
    /**
     * 更新通知
     */
    @PutMapping("/{id}")
    public ResponseEntity<Notification> updateNotification(
            @PathVariable Long id,
            @RequestBody NotificationRequest request) {
        return ResponseEntity.ok(notificationService.updateNotification(id, request));
    }
    
    /**
     * 刪除通知
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
     */
    @PostMapping("/{id}/send")
    public ResponseEntity<Map<String, String>> sendNotification(@RequestBody SendNotificationRequest request) {
        notificationService.sendNotification(request);
        Map<String, String> response = new HashMap<>();
        response.put("message", "通知推送成功");
        return ResponseEntity.ok(response);
    }
    
    /**
     * 【新功能】從活動快速創建並推送通知
     */
    @PostMapping("/quick/event")
    public ResponseEntity<Map<String, Object>> quickCreateFromEvent(@RequestBody QuickNotificationRequest request) {
        Notification notification = notificationService.createNotificationFromEvent(
            request.getSourceId(),
            request.getPushType(),
            request.getUserIds()  // 現在直接傳遞，不需要轉換！
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "通知創建並推送成功");
        response.put("notification", notification);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 【新功能】從電影快速創建並推送通知
     */
    @PostMapping("/quick/movie")
    public ResponseEntity<Map<String, Object>> quickCreateFromMovie(@RequestBody QuickNotificationRequest request) {
        Notification notification = notificationService.createNotificationFromMovie(
            request.getSourceId(),
            request.getPushType(),
            request.getUserIds()  // 現在直接傳遞，不需要轉換！
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "通知創建並推送成功");
        response.put("notification", notification);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 【新功能】獲取活動列表（用於快速創建）
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
     * 【新功能】獲取電影列表（用於快速創建）
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
    
    /**
     * 獲取用戶通知列表
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserNotifications(
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Boolean unreadOnly) {
        
        Page<UserNotificationDTO> notificationPage = 
            notificationService.getUserNotifications(userId, page, size, unreadOnly);
        
        Map<String, Object> response = new HashMap<>();
        response.put("notifications", notificationPage.getContent());
        response.put("currentPage", notificationPage.getNumber() + 1);
        response.put("totalItems", notificationPage.getTotalElements());
        response.put("totalPages", notificationPage.getTotalPages());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 標記通知為已讀
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
     */
    @GetMapping("/user/{userId}/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@PathVariable Integer userId) {
        long count = notificationService.getUnreadCount(userId);
        Map<String, Long> response = new HashMap<>();
        response.put("unreadCount", count);
        return ResponseEntity.ok(response);
    }
}