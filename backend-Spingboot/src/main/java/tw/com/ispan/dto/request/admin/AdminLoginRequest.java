package tw.com.ispan.dto.request.admin;

import lombok.Data;

@Data
public class AdminLoginRequest {
    private String email;
    private String password;
}
