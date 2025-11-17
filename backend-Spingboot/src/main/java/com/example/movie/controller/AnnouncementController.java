package com.example.movie.controller;

import com.example.movie.model.Event;
import com.example.movie.service.EventsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 公告 Controller（使用 events 表，category='announcement'）
 */
@RestController
@RequestMapping("/api/announcements")
@CrossOrigin(origins = "*")
public class AnnouncementController {

    @Autowired
    private EventsService eventsService;

    // 查詢所有公告（category='announcement'）
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
    public ResponseEntity<Event> createAnnouncement(@RequestBody Event announcement) {
        // 強制設定 category 為 announcement
        announcement.setCategory("announcement");
        Event created = eventsService.createEvent(announcement);
        return ResponseEntity.ok(created);
    }

    // 更新公告
    @PutMapping("/{id}")
    public ResponseEntity<Event> updateAnnouncement(
            @PathVariable Integer id,
            @RequestBody Event announcement) {
        // 強制設定 category 為 announcement
        announcement.setCategory("announcement");
        Event updated = eventsService.updateEvent(id, announcement);
        return ResponseEntity.ok(updated);
    }

    // 刪除公告（軟刪除）
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnnouncement(@PathVariable Integer id) {
        eventsService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }
}
