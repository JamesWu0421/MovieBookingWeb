package com.example.movie.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 電影熱門排行 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportMovieRankingDTO {
    private Integer movieId; // 電影 ID
    private String movieTitle; // 電影標題
    private Integer totalTickets; // 總票數
    private Integer totalRevenue; // 總營收
    private Integer showCount; // 場次數
}