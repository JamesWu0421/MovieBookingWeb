package com.example.movie.repository;

import com.example.movie.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {
    
    List<Event> findByCategory(String category);
    
    Optional<Event> findByCouponCodeAndIsActive(String couponCode, Boolean isActive);
    
    @Query("SELECT e FROM Event e WHERE e.isActive = true AND e.startDate <= :today AND e.endDate >= :today")
    List<Event> findActiveEvents(LocalDate today);
    
    @Query("SELECT e FROM Event e WHERE e.category = :category AND e.isActive = true AND e.startDate <= :today AND e.endDate >= :today")
    List<Event> findActiveByCategoryAndDate(String category, LocalDate today);
}