package tw.com.ispan.controller;

import lombok.RequiredArgsConstructor;
import tw.com.ispan.dto.EventDetailDTO;
import tw.com.ispan.dto.EventHomeDTO;
import tw.com.ispan.service.EventPublicService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/events") // 公共活動相關的API
@RequiredArgsConstructor
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
