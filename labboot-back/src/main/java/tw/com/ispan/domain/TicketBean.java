// package tw.com.ispan.domain;

// import java.time.LocalDateTime;

// import jakarta.persistence.*;

// @Entity
// @Table(name = "tickets")
// public class TicketBean {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Integer id;

//     @Column(name = "show_id", nullable = false)
//     private Integer showId;

//     @Column(name = "seat_id", nullable = false)
//     private Integer seatId;

//     @Column(name = "order_id", nullable = false)
//     private Integer orderId;

//     @Column(name = "price", nullable = false)
//     private Integer price;

//     @Column(name = "created_at", nullable = false)
//     private LocalDateTime createdAt;

//     @Override
//     public String toString() {
//         return "TicketBean [id=" + id + ", showId=" + showId 
//             + ", seatId=" + seatId + ", orderId=" + orderId 
//             + ", price=" + price + ", createdAt=" + createdAt + "]";
//     }

//     // Getter / Setter
//     public Integer getId() { return id; }
//     public void setId(Integer id) { this.id = id; }

//     public Integer getShowId() { return showId; }
//     public void setShowId(Integer showId) { this.showId = showId; }

//     public Integer getSeatId() { return seatId; }
//     public void setSeatId(Integer seatId) { this.seatId = seatId; }

//     public Integer getOrderId() { return orderId; }
//     public void setOrderId(Integer orderId) { this.orderId = orderId; }

//     public Integer getPrice() { return price; }
//     public void setPrice(Integer price) { this.price = price; }

//     public LocalDateTime getCreatedAt() { return createdAt; }
//     public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
// }
