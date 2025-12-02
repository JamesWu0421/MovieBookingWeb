package tw.com.ispan.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import tw.com.ispan.domain.MovieBean;
import tw.com.ispan.domain.Order;
import tw.com.ispan.domain.OrderDetail;
import tw.com.ispan.domain.ShowBean;
import tw.com.ispan.domain.TicketBean;
import tw.com.ispan.dto.TicketDTO;
import tw.com.ispan.repository.MovieRepository;
import tw.com.ispan.repository.OrderDetailRepository;
import tw.com.ispan.repository.OrderRepository;
import tw.com.ispan.repository.ShowRepository;
import tw.com.ispan.repository.TicketRepository;

@Service
@Transactional
public class TicketService {

    @Autowired
    private TicketRepository ticketRepo;

    @Autowired
    private OrderRepository orderRepo;

    @Autowired
    private OrderDetailRepository orderDetailRepo;

    @Autowired
    private ShowRepository showRepo;

    @Autowired
    private MovieRepository movieRepo;

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

    public List<TicketBean> generateTicketsByOrder(Integer orderId) {

        // 0. 找 order（為了取得 showId）
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        Integer showId = order.getShowId(); // ⭐ 重點：從 order 取

        // 1. 撈該訂單的所有明細
        List<OrderDetail> details = orderDetailRepo.findByOrderId(orderId);

        List<TicketBean> result = new ArrayList<>();

        // 2. 每筆明細轉成一張電影票
        for (OrderDetail d : details) {

            TicketBean t = new TicketBean();
            t.setOrderId(orderId);
            t.setShowId(showId); // ⭐ 使用 order.showId
            t.setSeatId(d.getSeatId());
            t.setPrice(d.getTicketPrice());
            t.setCreatedAt(LocalDateTime.now());

            ticketRepo.save(t);
            result.add(t);
        }

        return result;
    }

    public List<TicketBean> getTicketsByOrder(Integer orderId) {
        return ticketRepo.findByOrderId(orderId);
    }

    public List<TicketDTO> getTicketDTOByOrder(Integer orderId) {

        List<TicketBean> tickets = ticketRepo.findByOrderId(orderId);
        List<TicketDTO> result = new ArrayList<>();

        for (TicketBean t : tickets) {

            TicketDTO dto = new TicketDTO();
            dto.setId(t.getId());
            dto.setOrderId(t.getOrderId());
            dto.setShowId(t.getShowId());
            dto.setSeatId(t.getSeatId());
            dto.setPrice(t.getPrice());

            // ★ 取得票種
            OrderDetail detail = orderDetailRepo.findByOrderIdAndSeatId(orderId, t.getSeatId());
            if (detail != null) {
                dto.setTicketType(detail.getTicketType());
            }

            // ★ 場次資訊
            ShowBean s = showRepo.findById(t.getShowId()).orElse(null);
            if (s != null) {

                // ⭐ 合併 showDate（Date） + showTime（Time）
                LocalDateTime dateTime = LocalDateTime.of(
                        s.getShowDate().toLocalDate(),
                        s.getShowTime().toLocalTime());

                dto.setShowTime(dateTime);

                // ★ 電影名稱
                MovieBean m = movieRepo.findById(s.getMovieId()).orElse(null);
                if (m != null) {
                    dto.setMovieName(m.getTitle());
                }
            }
            result.add(dto);
        }

        return result;
    }

}
