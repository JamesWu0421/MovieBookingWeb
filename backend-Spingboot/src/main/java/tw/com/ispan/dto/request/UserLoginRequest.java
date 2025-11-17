package tw.com.ispan.dto.request;

import lombok.Data;

@Data
public class UserLoginRequest { //登入時的請求
    private String username;
    private String password;
}
