package tw.com.ispan.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "notification_users")
@IdClass(NotificationUserId.class)
public class NotificationUser {

    @Id
    @Column(name = "users_id", nullable = false)
    private Integer usersId; // 用戶ID

    @Id
    @Column(name = "notification_id", nullable = false)
    private Long notificationId; // 通知ID

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false; // 是否已讀

    @Column(name = "read_at")
    private LocalDateTime readAt; // 已讀時間

    // 標記為已讀
    public void markAsRead() {
        this.isRead = true;
        this.readAt = LocalDateTime.now();
    }
}
