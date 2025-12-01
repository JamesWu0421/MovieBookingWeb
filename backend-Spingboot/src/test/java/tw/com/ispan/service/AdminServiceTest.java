package tw.com.ispan.service;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;  // ← 加這行

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.transaction.Transactional;
import tw.com.ispan.entity.EmpEntity;
import tw.com.ispan.repository.rollpermission.EmpRepository;
import tw.com.ispan.repository.rollpermission.RoleRepository;
import tw.com.ispan.service.empandroll.EmpService;

@SpringBootTest
public class AdminServiceTest {
 
   @Autowired
    private EmpService empService;

    @Autowired
    private EmpRepository empRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void createEmployee_withBcryptPassword() {
        // 準備一個 EmpEntity（不含密碼）
        EmpEntity emp = new EmpEntity();
        emp.setEmpName("Test Admin");
        emp.setEmpEmail("testadmin@test.com");
        emp.setEmpPhone("0910000000");
        emp.setStatus((byte) 1);
        emp.setCreatedAt(LocalDateTime.now());

        Integer roleId = 1;               // 你資料庫裡 ADMIN 的角色 ID
        String plainPassword = "123456";  // 測試密碼

        // 呼叫 service.create，裡面會做 encode + 設定角色
        EmpEntity saved = empService.create(emp, roleId, plainPassword);

        // 驗證真的存進 DB 了
        EmpEntity fromDb = empRepository.findByEmpEmail("testadmin@test.com")
                .orElseThrow();

        // 密碼應該是 BCrypt 雜湊而且 matches 成功
        assertThat(fromDb.getEmpPasswordHash()).isNotEqualTo(plainPassword);
        assertThat(passwordEncoder.matches(plainPassword, fromDb.getEmpPasswordHash()))
                .isTrue();

        // 角色有設定
        assertThat(fromDb.getRoles()).isNotEmpty();
    }
}
