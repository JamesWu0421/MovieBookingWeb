package tw.com.ispan.dto.request;

import lombok.Data;

@Data
public class UserResetPwdRequest { //用於前端傳遞資料
    private String token;
    private String newPassword;
}
