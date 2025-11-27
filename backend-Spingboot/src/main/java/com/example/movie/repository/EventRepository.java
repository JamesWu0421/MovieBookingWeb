package com.example.movie.repository;

import com.example.movie.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {

       // 查詢啟用的活動
       List<Event> findByIsActiveTrue();

       // 根據分類查詢啟用的活動
       List<Event> findByCategoryAndIsActiveTrue(String category);

       /**
        * 根據分類查詢
        */
       List<Event> findByCategory(String category);

       /**
        * 根據優惠碼和啟用狀態查詢
        */
       Optional<Event> findByCouponCodeAndIsActive(String couponCode, Boolean isActive);

       /**
        * 查詢目前進行中的活動（使用 LocalDate 比較）
        * 因為查詢時只需要比較日期部分
        */
       @Query("SELECT e FROM Event e WHERE e.isActive = true " +
                     "AND CAST(e.startDate AS date) <= :today " +
                     "AND CAST(e.endDate AS date) >= :today")
       List<Event> findActiveEvents(@Param("today") LocalDate today);

       /**
        * 根據分類查詢目前進行中的活動
        */
       @Query("SELECT e FROM Event e WHERE e.category = :category AND e.isActive = true " +
                     "AND CAST(e.startDate AS date) <= :today " +
                     "AND CAST(e.endDate AS date) >= :today")
       List<Event> findActiveByCategoryAndDate(@Param("category") String category, @Param("today") LocalDate today);

       /**
        * 查詢目前進行中的活動（使用 LocalDateTime 精確比較）
        */
       @Query("SELECT e FROM Event e WHERE e.isActive = true " +
                     "AND e.startDate <= :now " +
                     "AND e.endDate >= :now")
       List<Event> findActiveEventsWithTime(@Param("now") LocalDateTime now);

       /**
        * 根據分類查詢目前進行中的活動（使用 LocalDateTime 精確比較）
        */
       @Query("SELECT e FROM Event e WHERE e.category = :category AND e.isActive = true " +
                     "AND e.startDate <= :now " +
                     "AND e.endDate >= :now")
       List<Event> findActiveByCategoryAndDateTime(@Param("category") String category, @Param("now") LocalDateTime now);
}
