package tw.com.ispan.dto;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 建立 / 更新 batch_sessions_temp 用的請求 DTO
 */
public class BatchSessionsTempRequestDTO {

    private Integer movieId;
    private Integer screenId;
    private LocalDate showDate;
    private LocalTime showTime;
    private LocalTime endTime;
    private Integer batchId;
    private String status;        // 可選，預設 pending
    private String errorMessage;  // 可選，通常錯誤時才給

    public BatchSessionsTempRequestDTO() {
    }

    // ===== Getter / Setter =====

    public Integer getMovieId() {
        return movieId;
    }

    public void setMovieId(Integer movieId) {
        this.movieId = movieId;
    }

    public Integer getScreenId() {
        return screenId;
    }

    public void setScreenId(Integer screenId) {
        this.screenId = screenId;
    }

    public LocalDate getShowDate() {
        return showDate;
    }

    public void setShowDate(LocalDate showDate) {
        this.showDate = showDate;
    }

    public LocalTime getShowTime() {
        return showTime;
    }

    public void setShowTime(LocalTime showTime) {
        this.showTime = showTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public Integer getBatchId() {
        return batchId;
    }

    public void setBatchId(Integer batchId) {
        this.batchId = batchId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}