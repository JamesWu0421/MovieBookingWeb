package com.example.movie.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class EventDetailDTO {

    private Integer id;
    private String category;
    private String name;

    private String description;
    private String notes;
    private String imageUrl;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private Boolean requiresCoupon;
    private String couponCode;

    private Integer maxUsagePerUser;
    private Integer minAmount;

    // Lottery
    private String prizeDescription;
    private String lotteryRequirement;

    private Integer viewCount;

    private boolean ended;
}
