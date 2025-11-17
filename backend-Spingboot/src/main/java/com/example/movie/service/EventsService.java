package com.example.movie.service;

import com.example.movie.model.Event;
import com.example.movie.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
public class EventsService {

    @Autowired
    private EventRepository eventRepository;

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Event getEventById(Integer id) {
        return eventRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Event not found with id: " + id));
    }

    public List<Event> getActiveEvents() {
        return eventRepository.findActiveEvents(LocalDate.now());
    }

    public List<Event> getEventsByCategory(String category) {
        return eventRepository.findByCategory(category);
    }

    public List<Event> getActiveEventsByCategory(String category) {
        return eventRepository.findActiveByCategoryAndDate(category, LocalDate.now());
    }

    @Transactional
    public Event createEvent(Event event) {
        validateEvent(event);
        return eventRepository.save(event);
    }

    @Transactional
    public Event updateEvent(Integer id, Event event) {
        Event existing = getEventById(id);
        event.setId(id);
        event.setCreatedAt(existing.getCreatedAt());
        validateEvent(event);
        return eventRepository.save(event);
    }

    @Transactional
    public void deleteEvent(Integer id) {
        Event event = getEventById(id);
        event.setIsActive(false);
        eventRepository.save(event);
    }

    public Event validateCoupon(String couponCode, Integer orderAmount) {
        Event event = eventRepository.findByCouponCodeAndIsActive(couponCode, true)
            .orElseThrow(() -> new RuntimeException("Invalid or expired coupon code"));

        LocalDate today = LocalDate.now();
        if (event.getStartDate() != null && event.getStartDate().isAfter(today)) {
            throw new RuntimeException("Coupon is not valid yet");
        }
        if (event.getEndDate() != null && event.getEndDate().isBefore(today)) {
            throw new RuntimeException("Coupon has expired");
        }

        if (event.getMinAmount() != null && orderAmount < event.getMinAmount()) {
            throw new RuntimeException("Order amount does not meet minimum requirement: " + event.getMinAmount());
        }

        return event;
    }

    public Integer calculateDiscount(Event event, Integer originalAmount) {
        if (event.getDiscountType() == null || event.getDiscountValue() == null) {
            return 0;
        }

        BigDecimal discount = BigDecimal.ZERO;
        
        if ("percentage".equals(event.getDiscountType())) {
            discount = BigDecimal.valueOf(originalAmount)
                .multiply(event.getDiscountValue())
                .divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP);
        } else if ("fixed".equals(event.getDiscountType())) {
            discount = event.getDiscountValue();
        }

        return discount.intValue();
    }

    private void validateEvent(Event event) {
        if (event.getName() == null || event.getName().trim().isEmpty()) {
            throw new RuntimeException("Event name is required");
        }
        if (event.getCategory() == null || event.getCategory().trim().isEmpty()) {
            throw new RuntimeException("Event category is required");
        }
        if (event.getStartDate() == null || event.getEndDate() == null) {
            throw new RuntimeException("Start date and end date are required");
        }
        if (event.getEndDate().isBefore(event.getStartDate())) {
            throw new RuntimeException("End date must be after start date");
        }
        if (Boolean.TRUE.equals(event.getRequiresCoupon()) && 
            (event.getCouponCode() == null || event.getCouponCode().trim().isEmpty())) {
            throw new RuntimeException("Coupon code is required when requires_coupon is true");
        }
        if (event.getDiscountValue() != null && event.getDiscountValue().compareTo(BigDecimal.ZERO) > 0) {
            if (event.getDiscountType() == null || event.getDiscountType().trim().isEmpty()) {
                throw new RuntimeException("Discount type is required when discount value is set");
            }
        }
    }
}
