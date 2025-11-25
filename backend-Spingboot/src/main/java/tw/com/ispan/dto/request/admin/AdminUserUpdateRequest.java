package tw.com.ispan.dto.request.admin;

import java.time.LocalDate;

import lombok.Data;
@Data
public class AdminUserUpdateRequest {
    private String email;
    private String phoneNumber;
    private String nickname;
    private String gender;
    private LocalDate birthday;
    private String avatarUrl;
    private Byte status;   // 0=未驗證, 1=啟用, 2=停權 / 黑名單

}