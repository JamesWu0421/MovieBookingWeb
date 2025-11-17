package tw.com.ispan.dto.request;

import java.time.LocalDate;

import lombok.Data;

@Data
public class UserRegisterRequest { //註冊時的請求
    private String username; //帳號
    private String email; //信箱
    private String gender; //性別
    private LocalDate birthday; //生日
    private String nickname; //暱稱
    private String password; //密碼
    private String phoneNumber; //手機
    private String avatarUrl; //頭像
}
