package tw.com.ispan.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import tw.com.ispan.dto.request.UserUpdateRequest;
import tw.com.ispan.dto.request.admin.AdminUserUpdateRequest;
import tw.com.ispan.entity.UserEntity;
import tw.com.ispan.repository.UserRepository;
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender;
//註冊
     public UserEntity registerUser(String username, String rawPassword, String email,String phoneNumber, String nickname, String gender, LocalDate birthday, String avatarUrl) {
        
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("帳號已存在");
        }
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email已存在");
        }

        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setEmail(email);
        user.setNickname(nickname);
        user.setGender(gender);
        user.setBirthday(birthday);
        user.setPhoneNumber(phoneNumber);
        
        // ✅ 簡單版：用暱稱生成頭像（沒暱稱就用帳號）
        if (avatarUrl == null || avatarUrl.isEmpty()) {
            String name = (nickname != null && !nickname.isEmpty()) ? nickname : username;
            try {
                String encodedName = URLEncoder.encode(name, StandardCharsets.UTF_8);
                user.setAvatarUrl("https://ui-avatars.com/api/?name=" + encodedName + "&background=random&size=300");
            } catch (Exception e) {
                user.setAvatarUrl("https://ui-avatars.com/api/?name=User&background=random&size=300");
            }
        } else {
            user.setAvatarUrl(avatarUrl);
        }
        
        user.setPasswordHash(passwordEncoder.encode(rawPassword)); // 密碼加密
        user.setStatus((byte) 0); // 0:未啟用, 1:已啟用, 2:已鎖定

        String verificationCode = UUID.randomUUID().toString();
        user.setVerificationCode(verificationCode);

        userRepository.save(user);
        sendVerificationEmail(email, verificationCode);

        return user;
    }
//==============================================================
  //驗證帳號
        private void sendVerificationEmail(String toEmail, String verificationCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("請驗證您的帳號");
        String activateUrl = "http://localhost:8080/api/auth/verify?code=" + verificationCode;
        message.setText("請點擊以下連結以啟用帳號:\n" + activateUrl);
        mailSender.send(message);
    }

    public UserEntity loginUser(String username, String rawPassword) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用戶不存在"));

        if (user.getStatus() != 1) {
            throw new RuntimeException("帳號尚未啟用或已被鎖定");
        }

        if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            throw new RuntimeException("密碼錯誤");
        }

        return user;
    }
    public Optional<UserEntity> findByVerificationCode(String code) {
    return userRepository.findByVerificationCode(code);
    }
    @Transactional
    public UserEntity save(UserEntity user) {
    return userRepository.save(user);
    }
    
//==============================================================
  //寄出重設密碼的email
public void sendResetPasswordEmail(String email) {
    Optional<UserEntity> userOpt = userRepository.findByEmail(email);
    if (userOpt.isEmpty()) {
        throw new RuntimeException("Email不存在");
    }
    UserEntity user = userOpt.get();

    // 產生重置密碼用的 Token
    String resetToken = UUID.randomUUID().toString();
    user.setResetToken(resetToken);
    userRepository.save(user);

    // 建立重置連結
    String resetLink = "http://localhost:5173/reset_password?token=" + resetToken;

    // 寄信內容
    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(email);
    message.setSubject("重置密碼請求");
    message.setText("請點擊以下連結來重置您的密碼：\n" + resetLink);

    mailSender.send(message);
    }
   

//==============================================================
 /// 重置密碼
    public void resetPassword(String token, String newPassword) {
    Optional<UserEntity> userOpt = userRepository.findByResetToken(token);
    if (userOpt.isEmpty()) {
        throw new RuntimeException("無效或過期的重置密碼連結");
    }
    UserEntity user = userOpt.get();

    // 更新密碼（建議加密）
    user.setPasswordHash(passwordEncoder.encode(newPassword));

    // 清除重置令牌，避免重複使用
    user.setResetToken(null);

    // 保存用戶資料
    userRepository.save(user);
}



 // ✅ 修改參數名稱和查詢方法
public UserEntity updateUserProfile(String username, UserUpdateRequest req) {
    // 改用 findByUsername 查詢
    UserEntity user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("用戶不存在"));

    // 更新欄位
    if (req.getNickname() != null) {
        user.setNickname(req.getNickname());
    }
    if (req.getGender() != null) {
        user.setGender(req.getGender());
    }
    if (req.getBirthday() != null) {
        user.setBirthday(req.getBirthday());
    }
    if (req.getPhoneNumber() != null) {
        user.setPhoneNumber(req.getPhoneNumber());
    }
    if (req.getPassword() != null) {
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
    }
    if (req.getAvatarUrl() != null) {
        user.setAvatarUrl(req.getAvatarUrl());
    }

    userRepository.save(user);
    return user;
}


public void changePassword(String username, String oldPassword, String newPassword) {
    // 取得用戶資料
    UserEntity user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("用戶不存在"));
    
    // 檢查輸入的舊密碼是否正確
    if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
        throw new RuntimeException("舊密碼錯誤");
    }
    
    // 若舊密碼正確，更新新密碼（需加密）
    user.setPasswordHash(passwordEncoder.encode(newPassword));
    
    // 儲存更新
    userRepository.save(user);
}

    public Optional<UserEntity> findByUsername(String username) {
    return userRepository.findByUsername(username);
}
 public Optional<UserEntity> findByEmail(String email) {
    return userRepository.findByEmail(email);
 }





// public UserEntity login(String username, String rawPassword) {
//         // 1. 從資料庫取得用戶
//         UserEntity user = userRepository.findByUsername(username)
//                 .orElseThrow(() -> new RuntimeException("用戶不存在"));

//         // 2. 檢查密碼，比對加密密碼
//         if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
//             throw new RuntimeException("密碼錯誤");
//         }

//         // 3. 驗證通過回傳用戶資料
//         return user;
//     }

//==============================================================
//後台會員管理
// 1. 查全部會員（後台列表用）
public Page<UserEntity> adminFindAllUsers(Pageable pageable) {
    System.out.println("Page number: " + pageable.getPageNumber());
    System.out.println("Page size: " + pageable.getPageSize());
    return userRepository.findAll(pageable);
}

    // 2. 關鍵字搜尋（username / email / phoneNumber 不分大小寫、包含）
    public List<UserEntity> adminSearchUsers(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return userRepository.findAll();
        }
        return userRepository
                .findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneNumberContainingIgnoreCase(
                        keyword, keyword, keyword
                );
    }

    // 3. 查單筆會員（詳細資料 / 編輯用）
    public UserEntity adminFindById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("會員不存在"));
    }

    // 4. 更新會員狀態（啟用 / 停權 / 黑名單）
    @Transactional
    public UserEntity adminUpdateUserStatus(Integer id, Byte status) {
        UserEntity user = adminFindById(id);
        user.setStatus(status);
        return userRepository.save(user);
    }

    // 5. 更新會員基本資料（後台改資料用，不含密碼）
    @Transactional
    public UserEntity adminUpdateUserBasicInfo(Integer id, AdminUserUpdateRequest req) {
        UserEntity user = adminFindById(id);

        if (req.getEmail() != null) {
            user.setEmail(req.getEmail());
        }
        if (req.getPhoneNumber() != null) {
            user.setPhoneNumber(req.getPhoneNumber());
        }
        if (req.getNickname() != null) {
            user.setNickname(req.getNickname());
        }
        if (req.getGender() != null) {
            user.setGender(req.getGender());
        }
        if (req.getBirthday() != null) {
            user.setBirthday(req.getBirthday());
        }
        if (req.getAvatarUrl() != null) {
            user.setAvatarUrl(req.getAvatarUrl());
        }
        if (req.getStatus() != null) {
            user.setStatus(req.getStatus());
        }

        return userRepository.save(user);
    }

}
