package tw.com.ispan.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

/**
 * 快速創建並推送通知請求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuickNotificationRequest {

    private String sourceType; // "EVENT" 或 "MOVIE"
    private Integer sourceId; // 活動ID 或 電影ID
    private String pushType; // "all" 或 "specific"
    private List<Integer> userIds; // 當 pushType 為 "specific" 時使用
}
