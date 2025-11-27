package tw.com.ispan.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import tw.com.ispan.dto.NotificationRequest;
import tw.com.ispan.dto.SendNotificationRequest;
import tw.com.ispan.dto.UserNotificationDTO;
import tw.com.ispan.model.Event;
import tw.com.ispan.model.Movie;
import tw.com.ispan.model.Notification;
import tw.com.ispan.model.NotificationUser;
import tw.com.ispan.repository.EventRepository;
import tw.com.ispan.repository.MovieRepository;
import tw.com.ispan.repository.NotificationRepository;
import tw.com.ispan.repository.NotificationUserRepository;
import tw.com.ispan.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class NotificationService {

    // 修改為 public，讓 SystemNotificationService 可以訪問
    public final NotificationRepository notificationRepository;
    public final NotificationUserRepository notificationUserRepository;

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final MovieRepository movieRepository;

    /**
     * 獲取通知列表（管理員用）
     */
    public Page<Notification> getNotifications(int page, int size, String query, String type) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());

        if (query != null && !query.trim().isEmpty() && type != null && !type.trim().isEmpty()) {
            return notificationRepository.findByTitleContainingAndType(query, type, pageable);
        } else if (query != null && !query.trim().isEmpty()) {
            return notificationRepository.findByTitleContaining(query, pageable);
        } else if (type != null && !type.trim().isEmpty()) {
            return notificationRepository.findByType(type, pageable);
        } else {
            return notificationRepository.findAll(pageable);
        }
    }

    /**
     * 根據 ID 獲取通知
     */
    public Notification getNotificationById(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("通知不存在: " + id));
    }

    /**
     * 創建通知
     */
    @Transactional
    public Notification createNotification(NotificationRequest request) {
        Notification notification = new Notification();
        notification.setType(request.getType());
        notification.setTitle(request.getTitle());
        notification.setContent(request.getContent());
        notification.setRelatedType(request.getRelatedType());
        notification.setRelatedId(request.getRelatedId());
        notification.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);

        return notificationRepository.save(notification);
    }

    /**
     * 更新通知
     */
    @Transactional
    public Notification updateNotification(Long id, NotificationRequest request) {
        Notification notification = getNotificationById(id);

        notification.setType(request.getType());
        notification.setTitle(request.getTitle());
        notification.setContent(request.getContent());
        notification.setRelatedType(request.getRelatedType());
        notification.setRelatedId(request.getRelatedId());
        notification.setIsActive(request.getIsActive());

        return notificationRepository.save(notification);
    }

    /**
     * 刪除通知
     */
    @Transactional
    public void deleteNotification(Long id) {
        notificationUserRepository.deleteByNotificationId(id);
        notificationRepository.deleteById(id);
    }

    /**
     * 推送通知給用戶
     * 
     * ✅ 修復：使用 findAllIds() 避免查詢 password 欄位
     */
    @Transactional
    public void sendNotification(SendNotificationRequest request) {
        Notification notification = getNotificationById(request.getNotificationId());

        List<Integer> targetUserIds;

        if ("all".equals(request.getType())) {
            // ✅ 修復：只查詢用戶 ID，不查詢所有欄位
            targetUserIds = userRepository.findAllIds();
        } else if ("specific".equals(request.getType())) {
            // 推送給指定用戶
            targetUserIds = request.getUserIds();
        } else {
            throw new RuntimeException("無效的推送類型: " + request.getType());
        }

        // 為每個用戶創建通知記錄
        for (Integer usersId : targetUserIds) {
            Long notificationId = notification.getId();

            // 檢查是否已經發送過
            if (notificationUserRepository.findByUsersIdAndNotificationId(usersId, notificationId).isEmpty()) {
                NotificationUser notificationUser = new NotificationUser();
                notificationUser.setUsersId(usersId);
                notificationUser.setNotificationId(notificationId);
                notificationUser.setIsRead(false);
                notificationUserRepository.save(notificationUser);
            }
        }

        System.out.println(String.format("✅ 通知已推送給 %d 位用戶", targetUserIds.size()));
    }

    /**
     * 【新功能】從活動快速創建並推送通知
     */
    @Transactional
    public Notification createNotificationFromEvent(Integer eventId, String pushType, List<Integer> userIds) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("活動不存在: " + eventId));

        // 根據活動類型決定通知類型
        String notificationType = "promotion".equals(event.getCategory()) ? "PROMOTION" : "SYSTEM";

        // 創建通知
        Notification notification = new Notification();
        notification.setType(notificationType);
        notification.setTitle(event.getName());
        notification.setContent(event.getDescription());
        notification.setRelatedType("EVENT");
        notification.setRelatedId(eventId.toString());
        notification.setIsActive(true);

        notification = notificationRepository.save(notification);

        // 推送通知
        SendNotificationRequest sendRequest = new SendNotificationRequest();
        sendRequest.setNotificationId(notification.getId());
        sendRequest.setType(pushType);
        sendRequest.setUserIds(userIds);
        sendNotification(sendRequest);

        return notification;
    }

    /**
     * 【新功能】從電影快速創建並推送通知
     */
    @Transactional
    public Notification createNotificationFromMovie(Integer movieId, String pushType, List<Integer> userIds) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("電影不存在: " + movieId));

        // 創建通知
        Notification notification = new Notification();
        notification.setType("MOVIE");
        notification.setTitle("新片上映：" + movie.getTitle());
        notification.setContent(String.format("%s 即將上映！%s",
                movie.getTitle(),
                movie.getDescription() != null ? movie.getDescription() : ""));
        notification.setRelatedType("MOVIE");
        notification.setRelatedId(movieId.toString());
        notification.setIsActive(true);

        notification = notificationRepository.save(notification);

        // 推送通知
        SendNotificationRequest sendRequest = new SendNotificationRequest();
        sendRequest.setNotificationId(notification.getId());
        sendRequest.setType(pushType);
        sendRequest.setUserIds(userIds);
        sendNotification(sendRequest);

        return notification;
    }

    /**
     * 【新功能】自動推送：新活動發布時調用
     */
    @Transactional
    public void autoNotifyNewEvent(Integer eventId) {
        createNotificationFromEvent(eventId, "all", null);
    }

    /**
     * 【新功能】自動推送：電影發布時調用
     */
    @Transactional
    public void autoNotifyNewMovie(Integer movieId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("電影不存在: " + movieId));

        if (movie.getIsPublished()) {
            createNotificationFromMovie(movieId, "all", null);
        }
    }

    /**
     * 【新功能】自動推送：訂單狀態變更時調用
     */
    @Transactional
    public void autoNotifyOrderStatus(Integer orderId, Integer userId, String status) {
        Notification notification = new Notification();
        notification.setType("ORDER");

        String title;
        String content;

        switch (status) {
            case "confirmed":
                title = "訂單確認";
                content = "您的訂單已確認，請準時入場觀影。訂單編號：" + orderId;
                break;
            case "cancelled":
                title = "訂單取消";
                content = "您的訂單已取消。訂單編號：" + orderId;
                break;
            case "refunded":
                title = "退款完成";
                content = "您的退款已處理完成。訂單編號：" + orderId;
                break;
            default:
                title = "訂單更新";
                content = "您的訂單狀態已更新。訂單編號：" + orderId;
        }

        notification.setTitle(title);
        notification.setContent(content);
        notification.setRelatedType("ORDER");
        notification.setRelatedId(orderId.toString());
        notification.setIsActive(true);

        notification = notificationRepository.save(notification);

        // 只推送給該用戶
        NotificationUser notificationUser = new NotificationUser();
        notificationUser.setUsersId(userId);
        notificationUser.setNotificationId(notification.getId());
        notificationUser.setIsRead(false);
        notificationUserRepository.save(notificationUser);
    }

    /**
     * 獲取用戶的通知列表
     */
    public Page<UserNotificationDTO> getUserNotifications(Integer usersId, int page, int size, Boolean unreadOnly) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "notificationId"));

        Page<NotificationUser> notificationUsers;
        if (unreadOnly != null && unreadOnly) {
            notificationUsers = notificationUserRepository.findByUsersIdAndIsReadFalse(usersId, pageable);
        } else {
            notificationUsers = notificationUserRepository.findByUsersId(usersId, pageable);
        }

        return notificationUsers.map(nu -> {
            Notification notification = notificationRepository.findById(nu.getNotificationId())
                    .orElse(null);

            if (notification == null) {
                return null;
            }

            UserNotificationDTO dto = new UserNotificationDTO();
            dto.setId(nu.getNotificationId());
            dto.setNotificationId(notification.getId());
            dto.setType(notification.getType());
            dto.setTitle(notification.getTitle());
            dto.setContent(notification.getContent());
            dto.setRelatedType(notification.getRelatedType());
            dto.setRelatedId(notification.getRelatedId());
            dto.setIsRead(nu.getIsRead());
            dto.setReadAt(nu.getReadAt());
            dto.setCreatedAt(notification.getCreatedAt());

            return dto;
        });
    }

    /**
     * 標記通知為已讀
     */
    @Transactional
    public void markAsRead(Integer usersId, Long notificationId) {
        NotificationUser notificationUser = notificationUserRepository
                .findByUsersIdAndNotificationId(usersId, notificationId)
                .orElseThrow(() -> new RuntimeException("用戶通知不存在"));

        if (!notificationUser.getIsRead()) {
            notificationUser.markAsRead();
            notificationUserRepository.save(notificationUser);
        }
    }

    /**
     * 獲取用戶未讀通知數量
     */
    public long getUnreadCount(Integer usersId) {
        return notificationUserRepository.countByUsersIdAndIsReadFalse(usersId);
    }
}