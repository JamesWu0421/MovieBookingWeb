package tw.com.ispan.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {

    private String type; // SYSTEM, ORDER, PROMOTION, MOVIE, PAYMENT
    private String title;
    private String content;
    private String relatedType;
    private String relatedId;
    private Boolean isActive;
}
