    package tw.com.ispan.entity;

    import jakarta.persistence.*;
    import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

    @Entity
    @Table(name = "users")
    @Data                   // 生成 getter, setter, toString, equals, hashCode
    @NoArgsConstructor      // 無參建構子
    @AllArgsConstructor     // 全參建構子
    public class UserEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer id;

        @Column(nullable = true, length = 50) 
        private String username; 

        @Column(nullable = false, unique = true, length = 100)
        private String email; 
        @Column(length = 10)
        private String gender; // 性別: male, female, other

        @Column
        private LocalDate birthday; // 生日 (yyyy-MM-dd)

        @Column(length = 50)
        private String nickname; // 暱稱

        @Column(name = "phone_number",length = 20)
        private String phoneNumber;

        @Column(name = "password_hash",length = 255)
        private String passwordHash;  // 第三方登入用戶可為 NULL

        @Column(name = "created_at",nullable = false)
        private LocalDateTime createdAt = LocalDateTime.now(); // 註冊時間

        @Column(name = "provider_name",length = 50)
        private String providerName;  // google, facebook, apple

        @Column(name = "provider_id",length = 100)
        private String providerId;    // 第三方平台唯一 ID

        @Column(name = "avatar_url" ,length = 255)
        private String avatarUrl;

        @Column(name = "reset_token",length = 255)
        private String resetToken;

        @Column(nullable = false)
        private Byte status;

        @Column(name = "verification_code", length = 255)
        private String verificationCode;

        @Override
        public String toString() {
            return "UserEntity [id=" + id + ", username=" + username + ", email=" + email + ", gender=" + gender
                    + ", birthday=" + birthday + ", nickname=" + nickname + ", phoneNumber=" + phoneNumber
                    + ", createdAt=" + createdAt + ", providerName=" + providerName + ", providerId=" + providerId
                    + ", avatarUrl=" + avatarUrl + ", status=" + status + "]";
        }
     
    }
