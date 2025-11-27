package com.example.movie.service;

import com.example.movie.dto.EventDetailDTO;
import com.example.movie.dto.EventHomeDTO;
import com.example.movie.model.Event;
import com.example.movie.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventPublicService {

    private final EventRepository eventRepository;

    // -----------------------
    // 列表頁
    // -----------------------
    public List<EventHomeDTO> getHomeEvents() {
        LocalDateTime now = LocalDateTime.now();

        List<Event> events = eventRepository.findAll();

        return events.stream()
                .map(this::toHomeDto)
                .sorted((a, b) -> {
                    // 未結束排前面
                    if (!a.isEnded() && b.isEnded()) return -1;
                    if (a.isEnded() && !b.isEnded()) return 1;

                    // 其次按開始時間倒序
                    return b.getStartDate().compareTo(a.getStartDate());
                })
                .toList();
    }

    // -----------------------
    // 詳細頁
    // -----------------------
    public EventDetailDTO getEventDetail(Integer id) {
        Event e = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("找不到此活動"));

        // 自動 viewCount +1
        e.setCurrentUsageCount(e.getCurrentUsageCount() + 1);
        eventRepository.save(e);

        return toDetailDto(e);
    }

    // -----------------------
    // DTO 轉換（列表用）
    // -----------------------
    private EventHomeDTO toHomeDto(Event e) {
        EventHomeDTO dto = new EventHomeDTO();

        dto.setId(e.getId());
        dto.setCategory(e.getCategory());
        dto.setName(e.getName());
        dto.setDescription(e.getDescription());
        dto.setImageUrl(e.getImageUrl());

        dto.setStartDate(e.getStartDate());
        dto.setEndDate(e.getEndDate());

        dto.setRequiresCoupon(e.getRequiresCoupon());
        dto.setCouponCode(e.getCouponCode());
        dto.setMinAmount(e.getMinAmount());
        dto.setMaxUsagePerUser(e.getMaxUsagePerUser());

        dto.setPrizeDescription(e.getPrizeDescription());
        dto.setLotteryRequirement(e.getLotteryRequirement());

        dto.setViewCount(e.getCurrentUsageCount());

        dto.setEnded(e.getEndDate().isBefore(LocalDateTime.now()));

        return dto;
    }

    // -----------------------
    // DTO 轉換（詳細頁）
    // -----------------------
    private EventDetailDTO toDetailDto(Event e) {
        EventDetailDTO dto = new EventDetailDTO();

        dto.setId(e.getId());
        dto.setCategory(e.getCategory());
        dto.setName(e.getName());
        dto.setDescription(e.getDescription());
        dto.setNotes(e.getNotes());
        dto.setImageUrl(e.getImageUrl());

        dto.setStartDate(e.getStartDate());
        dto.setEndDate(e.getEndDate());

        dto.setRequiresCoupon(e.getRequiresCoupon());
        dto.setCouponCode(e.getCouponCode());
        dto.setMinAmount(e.getMinAmount());
        dto.setMaxUsagePerUser(e.getMaxUsagePerUser());

        dto.setPrizeDescription(e.getPrizeDescription());
        dto.setLotteryRequirement(e.getLotteryRequirement());

        dto.setViewCount(e.getCurrentUsageCount());
        dto.setEnded(e.getEndDate().isBefore(LocalDateTime.now()));

        return dto;
    }
}
