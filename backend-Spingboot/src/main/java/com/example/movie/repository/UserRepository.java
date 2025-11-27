package com.example.movie.repository;

import com.example.movie.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // 你原來的方法保持不變
    Optional<User> findByUsername(String username);
    
    // ⭐ 添加這個方法（只查詢 ID，不查詢所有欄位）
    @Query("SELECT CAST(u.id AS integer) FROM User u")
    List<Integer> findAllIds();
}