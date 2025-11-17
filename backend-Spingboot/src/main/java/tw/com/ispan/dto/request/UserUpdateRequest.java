package tw.com.ispan.dto.request;

import java.time.LocalDate;

import lombok.Data;
@Data
public class UserUpdateRequest {    //使用者更新資料
    private String email;      //使用者帳號
    private String nickname;       //使用者暱稱
    private String gender;       //使用者性別
    private LocalDate birthday;     //使用者生日
    private String phoneNumber; //使用者手機
    private String avatarUrl;  //使用者頭像
    private String password;   //使用者密碼
}

