package com.example.movie.repository;

import com.example.movie.model.NotificationUser;
import com.example.movie.model.NotificationUserId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationUserRepository extends JpaRepository<NotificationUser, NotificationUserId> {
    
    // 查詢用戶的通知
    Page<NotificationUser> findByUsersId(Integer usersId, Pageable pageable);
    
    // 查詢用戶的未讀通知
    Page<NotificationUser> findByUsersIdAndIsReadFalse(Integer usersId, Pageable pageable);
    
    // 查詢用戶的已讀通知
    Page<NotificationUser> findByUsersIdAndIsReadTrue(Integer usersId, Pageable pageable);
    
    // 統計用戶未讀通知數量
    long countByUsersIdAndIsReadFalse(Integer usersId);
    
    // 查詢用戶是否已收到特定通知
    Optional<NotificationUser> findByUsersIdAndNotificationId(Integer usersId, Long notificationId);
    
    // 查詢特定通知的所有接收用戶
    List<NotificationUser> findByNotificationId(Long notificationId);
    
    // 刪除用戶的特定通知
    @Modifying
    @Query("DELETE FROM NotificationUser nu WHERE nu.usersId = :usersId AND nu.notificationId = :notificationId")
    void deleteByUsersIdAndNotificationId(@Param("usersId") Integer usersId, @Param("notificationId") Long notificationId);
    
    // 刪除某通知的所有用戶記錄
    @Modifying
    @Query("DELETE FROM NotificationUser nu WHERE nu.notificationId = :notificationId")
    void deleteByNotificationId(@Param("notificationId") Long notificationId);
}
