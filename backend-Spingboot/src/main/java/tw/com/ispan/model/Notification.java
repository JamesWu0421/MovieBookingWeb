package tw.com.ispan.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String type; // SYSTEM, ORDER, PROMOTION, MOVIE, PAYMENT

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String content;

    @Column(name = "related_type", length = 50)
    private String relatedType; // 相關類型，例如 ORDER, MOVIE

    @Column(name = "related_id", length = 50)
    private String relatedId; // 相關項目的ID

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true; // 是否啟用

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
