package com.example.movie.dto;

/**
 * 電影熱門排行 DTO
 */
public class ReportMovieRankingDTO {
    private Integer movieId;        // 電影 ID
    private String movieTitle;      // 電影標題
    private Integer totalTickets;   // 總票數
    private Integer totalRevenue;   // 總營收
    private Integer showCount;      // 場次數

    // Constructors
    public ReportMovieRankingDTO() {
    }

    public ReportMovieRankingDTO(Integer movieId, String movieTitle, Integer totalTickets, 
                                Integer totalRevenue, Integer showCount) {
        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.totalTickets = totalTickets;
        this.totalRevenue = totalRevenue;
        this.showCount = showCount;
    }

    // Getters and Setters
    public Integer getMovieId() {
        return movieId;
    }

    public void setMovieId(Integer movieId) {
        this.movieId = movieId;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public Integer getTotalTickets() {
        return totalTickets;
    }

    public void setTotalTickets(Integer totalTickets) {
        this.totalTickets = totalTickets;
    }

    public Integer getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(Integer totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public Integer getShowCount() {
        return showCount;
    }

    public void setShowCount(Integer showCount) {
        this.showCount = showCount;
    }

    @Override
    public String toString() {
        return "ReportMovieRankingDTO{" +
                "movieId=" + movieId +
                ", movieTitle='" + movieTitle + '\'' +
                ", totalTickets=" + totalTickets +
                ", totalRevenue=" + totalRevenue +
                ", showCount=" + showCount +
                '}';
    }
}
