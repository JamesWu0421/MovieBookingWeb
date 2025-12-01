package tw.com.ispan.service;

import lombok.RequiredArgsConstructor;
import tw.com.ispan.model.Notification;
import tw.com.ispan.model.NotificationUser;
import tw.com.ispan.repository.NotificationRepository;
import tw.com.ispan.repository.NotificationUserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 支付服務 - 處理支付相關業務並自動發送通知
 */
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final NotificationRepository notificationRepository;
    private final NotificationUserRepository notificationUserRepository;
    // private final PaymentRepository paymentRepository; // 如果你有 Payment 實體的話

    /**
     * 處理支付成功並自動發送通知給用戶
     * 
     * @param paymentId 支付ID
     * @param userId    用戶ID
     * @param orderId   訂單ID
     * @param amount    支付金額
     */
    @Transactional
    public void handlePaymentSuccess(Integer paymentId, Integer userId, Integer orderId, Double amount) {
        // TODO: 如果你有 Payment 實體，可以在這裡更新支付狀態
        // Payment payment = paymentRepository.findById(paymentId)
        // .orElseThrow(() -> new RuntimeException("支付記錄不存在"));
        // payment.setStatus("SUCCESS");
        // payment.setPaidAt(LocalDateTime.now());
        // paymentRepository.save(payment);

        // 創建支付成功通知
        Notification notification = new Notification();
        notification.setType("PAYMENT");
        notification.setTitle("支付成功");
        notification.setContent(String.format(
                "您的訂單 #%d 已支付成功！\n" +
                        "支付金額：NT$ %.2f\n" +
                        "感謝您的購買，祝您觀影愉快！",
                orderId,
                amount));
        notification.setRelatedType("PAYMENT");
        notification.setRelatedId(paymentId.toString());
        notification.setIsActive(true);

        // 保存通知
        notification = notificationRepository.save(notification);

        // 推送給該用戶
        NotificationUser notificationUser = new NotificationUser();
        notificationUser.setUsersId(userId);
        notificationUser.setNotificationId(notification.getId());
        notificationUser.setIsRead(false);
        notificationUserRepository.save(notificationUser);

        System.out.println(String.format(
                "✅ 支付成功通知已發送 - 用戶ID: %d, 訂單ID: %d, 金額: %.2f",
                userId, orderId, amount));
    }

    /**
     * 處理支付失敗並自動發送通知給用戶
     * 
     * @param paymentId 支付ID
     * @param userId    用戶ID
     * @param orderId   訂單ID
     * @param reason    失敗原因
     */
    @Transactional
    public void handlePaymentFailure(Integer paymentId, Integer userId, Integer orderId, String reason) {
        // TODO: 如果你有 Payment 實體，可以在這裡更新支付狀態
        // Payment payment = paymentRepository.findById(paymentId)
        // .orElseThrow(() -> new RuntimeException("支付記錄不存在"));
        // payment.setStatus("FAILED");
        // payment.setFailureReason(reason);
        // paymentRepository.save(payment);

        // 創建支付失敗通知
        Notification notification = new Notification();
        notification.setType("PAYMENT");
        notification.setTitle("支付失敗");
        notification.setContent(String.format(
                "訂單 #%d 支付失敗。\n" +
                        "失敗原因：%s\n" +
                        "請檢查您的付款方式或聯繫客服協助處理。",
                orderId,
                reason != null ? reason : "未知原因"));
        notification.setRelatedType("PAYMENT");
        notification.setRelatedId(paymentId.toString());
        notification.setIsActive(true);

        // 保存通知
        notification = notificationRepository.save(notification);

        // 推送給該用戶
        NotificationUser notificationUser = new NotificationUser();
        notificationUser.setUsersId(userId);
        notificationUser.setNotificationId(notification.getId());
        notificationUser.setIsRead(false);
        notificationUserRepository.save(notificationUser);

        System.out.println(String.format(
                "❌ 支付失敗通知已發送 - 用戶ID: %d, 訂單ID: %d, 原因: %s",
                userId, orderId, reason));
    }

    /**
     * 處理退款成功並自動發送通知給用戶
     * 
     * @param paymentId    支付ID
     * @param userId       用戶ID
     * @param orderId      訂單ID
     * @param refundAmount 退款金額
     */
    @Transactional
    public void handleRefundSuccess(Integer paymentId, Integer userId, Integer orderId, Double refundAmount) {
        // 創建退款成功通知
        Notification notification = new Notification();
        notification.setType("PAYMENT");
        notification.setTitle("退款成功");
        notification.setContent(String.format(
                "訂單 #%d 的退款已處理完成。\n" +
                        "退款金額：NT$ %.2f\n" +
                        "退款將在 3-5 個工作日內退回您的原支付方式。",
                orderId,
                refundAmount));
        notification.setRelatedType("PAYMENT");
        notification.setRelatedId(paymentId.toString());
        notification.setIsActive(true);

        // 保存通知
        notification = notificationRepository.save(notification);

        // 推送給該用戶
        NotificationUser notificationUser = new NotificationUser();
        notificationUser.setUsersId(userId);
        notificationUser.setNotificationId(notification.getId());
        notificationUser.setIsRead(false);
        notificationUserRepository.save(notificationUser);

        System.out.println(String.format(
                "💰 退款成功通知已發送 - 用戶ID: %d, 訂單ID: %d, 金額: %.2f",
                userId, orderId, refundAmount));
    }

    /**
     * 處理支付超時並自動發送通知給用戶
     * 
     * @param paymentId 支付ID
     * @param userId    用戶ID
     * @param orderId   訂單ID
     */
    @Transactional
    public void handlePaymentTimeout(Integer paymentId, Integer userId, Integer orderId) {
        // 創建支付超時通知
        Notification notification = new Notification();
        notification.setType("PAYMENT");
        notification.setTitle("支付超時");
        notification.setContent(String.format(
                "訂單 #%d 支付已超時。\n" +
                        "如需購買，請重新下單。",
                orderId));
        notification.setRelatedType("PAYMENT");
        notification.setRelatedId(paymentId.toString());
        notification.setIsActive(true);

        // 保存通知
        notification = notificationRepository.save(notification);

        // 推送給該用戶
        NotificationUser notificationUser = new NotificationUser();
        notificationUser.setUsersId(userId);
        notificationUser.setNotificationId(notification.getId());
        notificationUser.setIsRead(false);
        notificationUserRepository.save(notificationUser);

        System.out.println(String.format(
                "⏰ 支付超時通知已發送 - 用戶ID: %d, 訂單ID: %d",
                userId, orderId));
    }
}
