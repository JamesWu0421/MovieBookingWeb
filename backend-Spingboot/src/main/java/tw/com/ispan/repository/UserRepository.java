package tw.com.ispan.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import tw.com.ispan.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByProviderNameAndProviderId(String providerName, String providerId);

    // 依帳號名稱查找用戶
    Optional<UserEntity> findByUsername(String username);

    // 依驗證碼查找用戶
    Optional<UserEntity> findByVerificationCode(String code);

    Optional<UserEntity> findByResetToken(String resetToken);

    // 依 email 查找用戶
    // UserEntity findByEmail(String email);

    // // 依第三方 providerName 和 providerId 查找用戶
    // UserEntity findByProviderNameAndProviderId(String providerName, String
    // providerId);

    List<UserEntity> findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneNumberContainingIgnoreCase(
            String username, String email, String phoneNumber);

    // ⭐ 添加這個方法（只查詢 ID，不查詢所有欄位）
    @Query("SELECT CAST(u.id AS integer) FROM UserEntity u")
    List<Integer> findAllIds();

}
