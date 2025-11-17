package tw.com.ispan.dto.request;

import lombok.Data;

@Data
public class UserChangePwdRequest { //修改密碼
    private String oldPassword;
    private String newPassword;
}
