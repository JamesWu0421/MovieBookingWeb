package tw.com.ispan.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "seats")
public class SeatBean {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "screen_id", nullable = false)
    private Integer screenId;

    @Column(name = "row_label", nullable = false, columnDefinition = "char(1)")
    private String rowLabel;

    @Column(name = "seat_number", nullable = false)
    private Integer seatNumber;

    @Column(name = "is_blocked", nullable = false)
    private Boolean isBlocked;

    @Override
    public String toString() {
        return "SeatBean [id=" + id + ", screenId=" + screenId +
               ", rowLabel=" + rowLabel + ", seatNumber=" + seatNumber +
               ", isBlocked=" + isBlocked + "]";
    }

    // Getter / Setter
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getScreenId() { return screenId; }
    public void setScreenId(Integer screenId) { this.screenId = screenId; }

    public String getRowLabel() { return rowLabel; }
    public void setRowLabel(String rowLabel) { this.rowLabel = rowLabel; }

    public Integer getSeatNumber() { return seatNumber; }
    public void setSeatNumber(Integer seatNumber) { this.seatNumber = seatNumber; }

    public Boolean getIsBlocked() { return isBlocked; }
    public void setIsBlocked(Boolean isBlocked) { this.isBlocked = isBlocked; }
}
