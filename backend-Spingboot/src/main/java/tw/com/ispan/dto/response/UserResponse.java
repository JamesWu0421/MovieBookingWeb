package tw.com.ispan.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
@Data
@AllArgsConstructor
public class UserResponse {
    private Integer id;
    private String username;
    private String email;
    private String phoneNumber;
    private LocalDateTime createdAt;
    private String providerName;
    private String avatarUrl;
    private Byte status;
}



