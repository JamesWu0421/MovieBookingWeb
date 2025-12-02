package tw.com.ispan.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import tw.com.ispan.domain.TicketBean;

public interface TicketRepository extends JpaRepository<TicketBean, Integer> {

    // 查某場次已售出的座位
    List<TicketBean> findByShowId(Integer showId);

    // 查某場次 + 多個座位（避免重複買）
    List<TicketBean> findByShowIdAndSeatIdIn(Integer showId, List<Integer> seatIds);

    List<TicketBean> findByOrderId(Integer orderId);
}
