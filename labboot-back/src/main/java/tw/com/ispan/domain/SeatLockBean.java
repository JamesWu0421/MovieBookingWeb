// package tw.com.ispan.domain;

// import java.time.LocalDateTime;

// import jakarta.persistence.*;

// @Entity
// @Table(name = "seat_locks")
// public class SeatLockBean {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Integer id;

//     @Column(name = "show_id", nullable = false)
//     private Integer showId;

//     @Column(name = "seat_id", nullable = false)
//     private Integer seatId;

//     @Column(name = "user_id", nullable = false)
//     private Integer userId;

//     @Column(name = "locked_until", nullable = false)
//     private LocalDateTime lockedUntil;

//     @Column(name = "created_at", nullable = false)
//     private LocalDateTime createdAt;

//     @Column(name = "status", nullable = false)
//     private String status; // active / released / expired 之類

//     @Override
//     public String toString() {
//         return "SeatLockBean [id=" + id + ", showId=" + showId + ", seatId=" + seatId
//                 + ", userId=" + userId + ", lockedUntil=" + lockedUntil
//                 + ", createdAt=" + createdAt + ", status=" + status + "]";
//     }

//     // Getter / Setter
//     public Integer getId() { return id; }
//     public void setId(Integer id) { this.id = id; }

//     public Integer getShowId() { return showId; }
//     public void setShowId(Integer showId) { this.showId = showId; }

//     public Integer getSeatId() { return seatId; }
//     public void setSeatId(Integer seatId) { this.seatId = seatId; }

//     public Integer getUserId() { return userId; }
//     public void setUserId(Integer userId) { this.userId = userId; }

//     public LocalDateTime getLockedUntil() { return lockedUntil; }
//     public void setLockedUntil(LocalDateTime lockedUntil) { this.lockedUntil = lockedUntil; }

//     public LocalDateTime getCreatedAt() { return createdAt; }
//     public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

//     public String getStatus() { return status; }
//     public void setStatus(String status) { this.status = status; }
// }
