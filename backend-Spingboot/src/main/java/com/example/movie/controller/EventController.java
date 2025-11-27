package com.example.movie.controller;

import com.example.movie.dto.EventDTO;
import com.example.movie.model.Event;
import com.example.movie.service.EventsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "*")
public class EventController {

    @Autowired
    private EventsService eventsService;

    @GetMapping
    public ResponseEntity<List<Event>> getAllEvents(
            @RequestParam(required = false) String category) {
        List<Event> events;

        if (category != null) {
            events = eventsService.getEventsByCategory(category);
        } else {
            events = eventsService.getAllEvents();
        }

        return ResponseEntity.ok(events);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> getEvent(@PathVariable Integer id) {
        Event event = eventsService.getEventById(id);
        return ResponseEntity.ok(event);
    }

    @GetMapping("/active-promotions")
    public ResponseEntity<List<Event>> getActivePromotions() {
        List<Event> events = eventsService.getActiveEventsByCategory("promotion");
        return ResponseEntity.ok(events);
    }

    @GetMapping("/active-announcements")
    public ResponseEntity<List<Event>> getActiveAnnouncements() {
        List<Event> events = eventsService.getActiveEventsByCategory("announcement");
        return ResponseEntity.ok(events);
    }

    // ==========================
    // 👉 新增活動：使用 EventDTO
    // ==========================
    @PostMapping
    public ResponseEntity<Event> createEvent(@RequestBody EventDTO dto) {
        Event created = eventsService.createEvent(dto);
        return ResponseEntity.ok(created);
    }

    // ==========================
    // 👉 更新活動：使用 EventDTO
    // ==========================
    @PutMapping("/{id}")
    public ResponseEntity<Event> updateEvent(
            @PathVariable Integer id,
            @RequestBody EventDTO dto) {
        Event updated = eventsService.updateEvent(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Integer id) {
        eventsService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/validate-coupon")
    public ResponseEntity<Map<String, Object>> validateCoupon(
            @RequestParam String couponCode,
            @RequestParam Integer orderAmount) {

        try {
            Event event = eventsService.validateCoupon(couponCode, orderAmount);
            Integer discount = eventsService.calculateDiscount(event, orderAmount);

            Map<String, Object> response = new HashMap<>();
            response.put("valid", true);
            response.put("event", event);
            response.put("discount", discount);
            response.put("finalAmount", orderAmount - discount);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("valid", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
