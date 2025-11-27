package com.example.movie.controller;

import com.example.movie.dto.EventDetailDTO;
import com.example.movie.dto.EventHomeDTO;
import com.example.movie.service.EventPublicService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/events") // 公共活動相關的API
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EventPublicController {

    private final EventPublicService eventPublicService;

    // 列表
    @GetMapping("/home") // GET /api/public/events/home
    public List<EventHomeDTO> getHomeEvents() {
        return eventPublicService.getHomeEvents();
    }

    // 詳細
    @GetMapping("/{id}") // GET /api/public/events/1
    public EventDetailDTO getEventDetail(@PathVariable Integer id) {
        return eventPublicService.getEventDetail(id);
    }
}
