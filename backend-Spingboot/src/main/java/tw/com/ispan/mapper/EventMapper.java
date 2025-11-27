package tw.com.ispan.mapper;

import tw.com.ispan.dto.EventDTO;
import tw.com.ispan.model.Event;

public class EventMapper {

    public static Event toEntity(EventDTO dto) {
        Event event = new Event();
        updateEntity(event, dto);
        return event;
    }

    public static void updateEntity(Event event, EventDTO dto) {

        event.setCategory(dto.getCategory());
        event.setName(dto.getName());
        event.setDescription(dto.getDescription());
        event.setNotes(dto.getNotes());
        event.setImageUrl(dto.getImageUrl());

        event.setStartDate(dto.getStartDate());
        event.setEndDate(dto.getEndDate());

        event.setDiscountType(dto.getDiscountType());
        event.setDiscountValue(dto.getDiscountValue());

        // 🆕 新增：抽獎活動欄位
        event.setPrizeDescription(dto.getPrizeDescription());
        event.setLotteryRequirement(dto.getLotteryRequirement());

        event.setMinAmount(dto.getMinAmount());
        event.setMaxUsagePerUser(dto.getMaxUsagePerUser());

        event.setIsActive(dto.getIsActive());
        event.setRequiresCoupon(dto.getRequiresCoupon());
        event.setCouponCode(dto.getCouponCode());
    }
}