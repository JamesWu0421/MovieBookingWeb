package tw.com.ispan.service;

import lombok.RequiredArgsConstructor;
import tw.com.ispan.model.Notification;
import tw.com.ispan.model.NotificationUser;
import tw.com.ispan.repository.NotificationRepository;
import tw.com.ispan.repository.NotificationUserRepository;
import tw.com.ispan.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 系統通知服務 - 處理系統級別的通知（維護、升級、緊急通知等）
 * 
 * 修復版：避免查詢 User 實體的 password 欄位
 */
@Service
@RequiredArgsConstructor
public class SystemNotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationUserRepository notificationUserRepository;
    private final UserRepository userRepository;

    /**
     * 發送系統維護通知（推送給所有用戶）
     */
    @Transactional
    public void notifySystemMaintenance(String startTime, String endTime, String reason) {
        Notification notification = new Notification();
        notification.setType("SYSTEM");
        notification.setTitle("【系統維護通知】");
        notification.setContent(String.format(
                "系統將於 %s 至 %s 進行維護。\n" +
                        "維護期間網站將暫停服務，請您提前安排觀影計劃。\n" +
                        "%s\n" +
                        "造成不便，敬請見諒！",
                startTime,
                endTime,
                reason != null && !reason.isEmpty() ? "維護內容：" + reason : ""));
        notification.setRelatedType("SYSTEM");
        notification.setRelatedId("MAINTENANCE");
        notification.setIsActive(true);

        notification = notificationRepository.save(notification);

        pushToAllUsers(notification.getId());

        System.out.println(String.format(
                "🔧 系統維護通知已發送給所有用戶 - 時間: %s ~ %s",
                startTime, endTime));
    }

    /**
     * 發送緊急系統通知（推送給所有用戶）
     */
    @Transactional
    public void notifyEmergency(String title, String content) {
        Notification notification = new Notification();
        notification.setType("SYSTEM");
        notification.setTitle("【緊急通知】" + title);
        notification.setContent(content);
        notification.setRelatedType("SYSTEM");
        notification.setRelatedId("EMERGENCY");
        notification.setIsActive(true);

        notification = notificationRepository.save(notification);

        pushToAllUsers(notification.getId());

        System.out.println(String.format(
                "🚨 緊急通知已發送給所有用戶 - 標題: %s",
                title));
    }

    /**
     * 發送系統升級完成通知
     */
    @Transactional
    public void notifySystemUpgrade(String version, String features) {
        Notification notification = new Notification();
        notification.setType("SYSTEM");
        notification.setTitle("系統升級完成");
        notification.setContent(String.format(
                "系統已成功升級至 v%s！\n\n" +
                        "新功能：\n%s\n\n" +
                        "感謝您的支持！",
                version,
                features));
        notification.setRelatedType("SYSTEM");
        notification.setRelatedId("UPGRADE");
        notification.setIsActive(true);

        notification = notificationRepository.save(notification);

        pushToAllUsers(notification.getId());

        System.out.println(String.format(
                "🎉 系統升級通知已發送給所有用戶 - 版本: v%s",
                version));
    }

    /**
     * 發送服務恢復通知
     */
    @Transactional
    public void notifyServiceRestored() {
        Notification notification = new Notification();
        notification.setType("SYSTEM");
        notification.setTitle("服務已恢復");
        notification.setContent(
                "系統維護已完成，所有服務已恢復正常。\n" +
                        "感謝您的耐心等待！");
        notification.setRelatedType("SYSTEM");
        notification.setRelatedId("SERVICE_RESTORED");
        notification.setIsActive(true);

        notification = notificationRepository.save(notification);

        pushToAllUsers(notification.getId());

        System.out.println("✅ 服務恢復通知已發送給所有用戶");
    }

    /**
     * 發送節假日營業時間調整通知
     */
    @Transactional
    public void notifyHolidaySchedule(String holiday, String adjustedHours) {
        Notification notification = new Notification();
        notification.setType("SYSTEM");
        notification.setTitle(holiday + " 營業時間調整");
        notification.setContent(String.format(
                "親愛的顧客，\n\n" +
                        "%s 期間，本影院營業時間調整如下：\n%s\n\n" +
                        "請提前安排您的觀影計劃。祝您假期愉快！",
                holiday,
                adjustedHours));
        notification.setRelatedType("SYSTEM");
        notification.setRelatedId("HOLIDAY");
        notification.setIsActive(true);

        notification = notificationRepository.save(notification);

        pushToAllUsers(notification.getId());

        System.out.println(String.format(
                "🎊 節假日通知已發送給所有用戶 - 節日: %s",
                holiday));
    }

    /**
     * 發送安全提醒通知
     */
    @Transactional
    public void notifySecurityAlert(String title, String content) {
        Notification notification = new Notification();
        notification.setType("SYSTEM");
        notification.setTitle("【安全提醒】" + title);
        notification.setContent(content);
        notification.setRelatedType("SYSTEM");
        notification.setRelatedId("SECURITY");
        notification.setIsActive(true);

        notification = notificationRepository.save(notification);

        pushToAllUsers(notification.getId());

        System.out.println(String.format(
                "🔒 安全提醒已發送給所有用戶 - 標題: %s",
                title));
    }

    /**
     * 推送通知給所有用戶
     * 
     * 修復：使用原生 SQL 查詢只獲取用戶 ID，避免查詢不存在的欄位
     */
    private void pushToAllUsers(Long notificationId) {
        try {
            // 方法1：使用 UserRepository 的自定義方法（如果你有的話）
            // List<Integer> userIds = userRepository.findAllUserIds();

            // 方法2：使用原生查詢（推薦）
            List<Integer> userIds = userRepository.findAllIds();

            // 為每個用戶創建通知記錄
            for (Integer userId : userIds) {
                // 檢查是否已經發送過
                if (notificationUserRepository.findByUsersIdAndNotificationId(
                        userId, notificationId).isEmpty()) {
                    NotificationUser notificationUser = new NotificationUser();
                    notificationUser.setUsersId(userId);
                    notificationUser.setNotificationId(notificationId);
                    notificationUser.setIsRead(false);
                    notificationUserRepository.save(notificationUser);
                }
            }

            System.out.println(String.format(
                    "📢 通知已推送給 %d 位用戶",
                    userIds.size()));
        } catch (Exception e) {
            System.err.println("推送通知失敗: " + e.getMessage());
            e.printStackTrace();
            // 重新拋出異常，讓事務回滾
            throw new RuntimeException("推送通知失敗", e);
        }
    }
}