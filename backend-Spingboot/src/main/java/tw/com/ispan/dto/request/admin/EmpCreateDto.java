package tw.com.ispan.dto.request.admin;

import lombok.Data;

@Data
public class EmpCreateDto {
    private String empName;
    private String empEmail;
    private String empPhone;
    private Byte status;          // 1=啟用, 0=停用, 2=封鎖
    private String plainPassword; // 原始密碼
}