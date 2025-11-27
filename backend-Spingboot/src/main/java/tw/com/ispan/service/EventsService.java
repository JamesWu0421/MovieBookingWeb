package tw.com.ispan.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tw.com.ispan.dto.EventDTO;
import tw.com.ispan.mapper.EventMapper;
import tw.com.ispan.model.Event;
import tw.com.ispan.repository.EventRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventsService {

    @Autowired
    private EventRepository eventRepository;

    /**
     * 取得所有活動
     */
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    /**
     * 根據分類取得活動
     */
    public List<Event> getEventsByCategory(String category) {
        return eventRepository.findByCategory(category);
    }

    /**
     * 根據 ID 取得活動
     */
    public Event getEventById(Integer id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("找不到 ID 為 " + id + " 的活動"));
    }

    /**
     * 取得啟用中的活動
     */
    public List<Event> getActiveEventsByCategory(String category) {
        LocalDateTime now = LocalDateTime.now();
        return eventRepository.findActiveByCategoryAndDate(category, now.toLocalDate());
    }

    // ======================================================
    // 👉 使用 EventDTO 建立活動
    // ======================================================
    @Transactional
    public Event createEvent(EventDTO dto) {

        Event event = EventMapper.toEntity(dto);

        // 預設值
        if (event.getCurrentUsageCount() == null)
            event.setCurrentUsageCount(0);

        if (event.getIsActive() == null)
            event.setIsActive(true);

        if (event.getRequiresCoupon() == null)
            event.setRequiresCoupon(false);

        return eventRepository.save(event);
    }

    // ======================================================
    // 👉 使用 EventDTO 更新活動
    // ======================================================
    @Transactional
    public Event updateEvent(Integer id, EventDTO dto) {

        Event existing = getEventById(id);

        // 覆寫欄位
        EventMapper.updateEntity(existing, dto);

        return eventRepository.save(existing);
    }

    /**
     * 刪除活動
     */
    @Transactional
    public void deleteEvent(Integer id) {
        Event event = getEventById(id);
        eventRepository.delete(event);
    }

    /**
     * 驗證優惠碼
     */
    public Event validateCoupon(String couponCode, Integer orderAmount) {
        Event event = eventRepository.findByCouponCodeAndIsActive(couponCode, true)
                .orElseThrow(() -> new RuntimeException("優惠碼無效或已過期"));

        LocalDateTime now = LocalDateTime.now();

        if (event.getStartDate() != null && now.isBefore(event.getStartDate()))
            throw new RuntimeException("優惠活動尚未開始");

        if (event.getEndDate() != null && now.isAfter(event.getEndDate()))
            throw new RuntimeException("優惠活動已結束");

        if (event.getMinAmount() != null && orderAmount < event.getMinAmount())
            throw new RuntimeException("未達最低消費金額 " + event.getMinAmount());

        if (event.getMaxUsagePerUser() != null && event.getCurrentUsageCount() != null)
            if (event.getCurrentUsageCount() >= event.getMaxUsagePerUser())
                throw new RuntimeException("此優惠碼已達使用上限");

        return event;
    }

    /**
     * 計算折扣金額
     */
    public Integer calculateDiscount(Event event, Integer orderAmount) {
        if (event.getDiscountType() == null || event.getDiscountValue() == null)
            return 0;

        if ("percentage".equals(event.getDiscountType())) {
            BigDecimal discount = BigDecimal.valueOf(orderAmount)
                    .multiply(event.getDiscountValue())
                    .divide(BigDecimal.valueOf(100));
            return discount.intValue();
        } else if ("fixed".equals(event.getDiscountType())) {
            return event.getDiscountValue().intValue();
        }

        return 0;
    }
}
