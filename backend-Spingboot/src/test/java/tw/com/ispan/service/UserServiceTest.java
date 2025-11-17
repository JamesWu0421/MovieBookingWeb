package tw.com.ispan.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import tw.com.ispan.entity.UserEntity;
import tw.com.ispan.repository.UserRepository;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private UserService userService;

     @Autowired
    private UserRepository userRepository;


    @Test
    public void sendTestEmail() {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("kaydi754@gmail.com"); //請換成您的 email
        message.setSubject("Test Email");
        message.setText("This is a test email.");
        mailSender.send(message);
        System.out.println("測試郵件已發送");
    }

@Test
    public void verifyTest() {
        // 建立一個測試用資料（未啟用狀態）
        UserEntity user = new UserEntity();
        user.setUsername("user1");
        user.setEmail("kaydi711154@gmail.com");
        user.setPasswordHash("password123");
        user.setStatus((byte)0);
        user.setVerificationCode("testcode123");
        userRepository.save(user);

        // 呼叫找到用戶方法
        Optional<UserEntity> optUser = userService.findByVerificationCode("testcode123");
        assertTrue(optUser.isPresent());

        UserEntity foundUser = optUser.get();
        foundUser.setStatus((byte)1); // 啟用帳號
        foundUser.setVerificationCode(null);
        userService.save(foundUser);

        // 重新從資料庫找，檢查狀態是否改為 1
        UserEntity updatedUser = userRepository.findById(foundUser.getId()).get();
        assertEquals((byte)1, updatedUser.getStatus());
        assertNull(updatedUser.getVerificationCode());
    }
    @Test
    public void testFindByUserName() {
        Optional<UserEntity> user = userService.findByUsername("testuser1");
        assertEquals("testuser1", user.get().getUsername());
        System.out.println("user found: "+user.get().getUsername());
    }
    
}

