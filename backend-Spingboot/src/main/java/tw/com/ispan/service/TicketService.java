package tw.com.ispan.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import tw.com.ispan.domain.TicketBean;
import tw.com.ispan.repository.TicketRepository;

@Service
@Transactional
public class TicketService {

    @Autowired
    private TicketRepository ticketRepo;

    // 查某場次所有已售出的座位
    public List<TicketBean> getSoldSeats(Integer showId) {
        return ticketRepo.findByShowId(showId);
    }

    // 建立票券（購買成功）
    public TicketBean createTicket(Integer showId, Integer seatId, Integer orderId, Integer price) {
        TicketBean t = new TicketBean();
        t.setShowId(showId);
        t.setSeatId(seatId);
        t.setOrderId(orderId);
        t.setPrice(price);
        t.setCreatedAt(LocalDateTime.now());
        return ticketRepo.save(t);
    }

    // 防止重複售票
    public boolean seatsAlreadySold(Integer showId, List<Integer> seatIds) {
        List<TicketBean> list = ticketRepo.findByShowIdAndSeatIdIn(showId, seatIds);
        return !list.isEmpty();
    }
}
