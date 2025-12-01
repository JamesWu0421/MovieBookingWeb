package tw.com.ispan.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.io.Serializable;

/**
 * notification_users 表的複合主鍵
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationUserId implements Serializable {

    private Integer usersId;
    private Long notificationId;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        NotificationUserId that = (NotificationUserId) o;
        return usersId.equals(that.usersId) && notificationId.equals(that.notificationId);
    }

    @Override
    public int hashCode() {
        return usersId.hashCode() + notificationId.hashCode();
    }
}
