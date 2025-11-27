package com.example.movie.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendNotificationRequest {
    
    private Long notificationId;
    private String type; // "all" 或 "specific"
    private List<Integer> userIds; // 改為 Integer,與資料庫一致
}