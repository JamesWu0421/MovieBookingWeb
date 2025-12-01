package tw.com.ispan.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import tw.com.ispan.domain.TicketBean;
import tw.com.ispan.service.TicketService;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    // 查某場次已售出的座位
    @GetMapping("/sold")
    public ResponseEntity<List<TicketBean>> getSoldSeats(@RequestParam Integer showId) {
        return ResponseEntity.ok(ticketService.getSoldSeats(showId));
    }

    // 結帳成功後建立票券
    @PostMapping("/create")
    public ResponseEntity<TicketBean> createTicket(@RequestBody TicketRequest request) {
        TicketBean t = ticketService.createTicket(
                request.getShowId(),
                request.getSeatId(),
                request.getOrderId(),
                request.getPrice()
        );
        return ResponseEntity.ok(t);
    }

    public static class TicketRequest {
        private Integer showId;
        private Integer seatId;
        private Integer orderId;
        private Integer price;

        public Integer getShowId() { return showId; }
        public void setShowId(Integer showId) { this.showId = showId; }

        public Integer getSeatId() { return seatId; }
        public void setSeatId(Integer seatId) { this.seatId = seatId; }

        public Integer getOrderId() { return orderId; }
        public void setOrderId(Integer orderId) { this.orderId = orderId; }

        public Integer getPrice() { return price; }
        public void setPrice(Integer price) { this.price = price; }
    }
}
