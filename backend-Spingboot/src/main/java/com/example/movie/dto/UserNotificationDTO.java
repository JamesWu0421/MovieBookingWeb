package com.example.movie.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserNotificationDTO {
    
    private Long id; // UserNotification ID
    private Long notificationId;
    private String type;
    private String title;
    private String content;
    private String relatedType;
    private String relatedId;
    private Boolean isRead;
    private LocalDateTime readAt;
    private LocalDateTime createdAt;
}
