package com.example.movie.controller;

import com.example.movie.dto.EventDTO;
import com.example.movie.model.Event;
import com.example.movie.service.EventsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/announcements")
@CrossOrigin(origins = "*")
public class AnnouncementController {

    @Autowired
    private EventsService eventsService;

    // 查詢所有公告
    @GetMapping
    public ResponseEntity<List<Event>> getAllAnnouncements() {
        List<Event> announcements = eventsService.getEventsByCategory("announcement");
        return ResponseEntity.ok(announcements);
    }

    // 查詢單一公告
    @GetMapping("/{id}")
    public ResponseEntity<Event> getAnnouncement(@PathVariable Integer id) {
        Event announcement = eventsService.getEventById(id);
        return ResponseEntity.ok(announcement);
    }

    // 查詢進行中的公告
    @GetMapping("/active")
    public ResponseEntity<List<Event>> getActiveAnnouncements() {
        List<Event> announcements = eventsService.getActiveEventsByCategory("announcement");
        return ResponseEntity.ok(announcements);
    }

    // 新增公告
    @PostMapping
    public ResponseEntity<Event> createAnnouncement(@RequestBody EventDTO dto) {
        dto.setCategory("announcement");   // 強制 category=announcement
        Event created = eventsService.createEvent(dto);
        return ResponseEntity.ok(created);
    }

    // 更新公告
    @PutMapping("/{id}")
    public ResponseEntity<Event> updateAnnouncement(
            @PathVariable Integer id,
            @RequestBody EventDTO dto) {
        dto.setCategory("announcement");
        Event updated = eventsService.updateEvent(id, dto);
        return ResponseEntity.ok(updated);
    }

    // 刪除公告
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnnouncement(@PathVariable Integer id) {
        eventsService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }
}
