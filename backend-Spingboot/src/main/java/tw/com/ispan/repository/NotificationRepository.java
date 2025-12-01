package tw.com.ispan.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tw.com.ispan.model.Notification;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // 根據標題模糊搜尋
    Page<Notification> findByTitleContaining(String title, Pageable pageable);

    // 根據類型查詢
    Page<Notification> findByType(String type, Pageable pageable);

    // 根據標題和類型搜尋
    Page<Notification> findByTitleContainingAndType(String title, String type, Pageable pageable);

    // 查詢啟用的通知
    Page<Notification> findByIsActiveTrue(Pageable pageable);

    // 根據標題模糊搜尋啟用的通知
    Page<Notification> findByTitleContainingAndIsActiveTrue(String title, Pageable pageable);

    // 查詢所有啟用的通知（不分頁）
    List<Notification> findAllByIsActiveTrue();
}
