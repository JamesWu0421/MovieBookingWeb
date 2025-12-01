package tw.com.ispan.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "shows")
public class ShowBean {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "movie_id", nullable = false)
    private Integer movieId;

    @Column(name = "screen_id", nullable = false)
    private Integer screenId;

    @Column(name = "show_date", nullable = false)
    private java.sql.Date showDate;

    @Column(name = "show_time", nullable = false)
    private java.sql.Time showTime;

    @Column(name = "end_time", nullable = false)
    private java.sql.Time endTime;

    @Override
    public String toString() {
        return "ShowBean [id=" + id + ", movieId=" + movieId +
               ", screenId=" + screenId + ", showDate=" + showDate +
               ", showTime=" + showTime + ", endTime=" + endTime + "]";
    }

    // Getter / Setter
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getMovieId() { return movieId; }
    public void setMovieId(Integer movieId) { this.movieId = movieId; }

    public Integer getScreenId() { return screenId; }
    public void setScreenId(Integer screenId) { this.screenId = screenId; }

    public java.sql.Date getShowDate() { return showDate; }
    public void setShowDate(java.sql.Date showDate) { this.showDate = showDate; }

    public java.sql.Time getShowTime() { return showTime; }
    public void setShowTime(java.sql.Time showTime) { this.showTime = showTime; }

    public java.sql.Time getEndTime() { return endTime; }
    public void setEndTime(java.sql.Time endTime) { this.endTime = endTime; }
}
