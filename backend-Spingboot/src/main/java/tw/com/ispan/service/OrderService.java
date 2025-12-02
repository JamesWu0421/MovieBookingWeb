package tw.com.ispan.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import tw.com.ispan.domain.Order;
import tw.com.ispan.domain.OrderDetail;
import tw.com.ispan.domain.SeatLockBean;
import tw.com.ispan.domain.ShowTicketPricesBean;
import tw.com.ispan.repository.OrderDetailRepository;
import tw.com.ispan.repository.OrderRepository;
import tw.com.ispan.repository.SeatLockRepository;
import tw.com.ispan.repository.ShowTicketPricesRepository;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderDetailRepository detailRepository;
    @Autowired
    private SeatLockRepository seatLockRepository;
    @Autowired
    private ShowTicketPricesRepository ticketRepository;

    // 新增訂單
    public Order createOrder(Order order) {
        order.setOrderStatus("PENDING");
        return orderRepository.save(order);
    }

    // 查看全部訂單
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // 依Id查
    public Optional<Order> getOrderById(Integer id) {
        return orderRepository.findById(id);
    }

    // 修改訂單狀態
    public Order updateOrderStatus(Integer id, String newStatus) {
        Optional<Order> orderOpt = orderRepository.findById(id);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            order.setOrderStatus(newStatus);
            return orderRepository.save(order);
        }
        return null;
    }

    public void deleteOrder(Integer id) {
        orderRepository.deleteById(id);
    }

    public Order getLatestOrder() {
        // findTopByOrderByIdDesc() 回傳 Optional<Order>
        return orderRepository.findTopByOrderByIdDesc().orElse(null);
    }

    public List<Order> getOrdersByUserId(Integer userId) {
        return orderRepository.findByUserId(userId);
    }

    public Order getLatestOrderByUserId(Integer userId) {
        return orderRepository.findTopByUserIdOrderByIdDesc(userId);
    }

    @Transactional
    public Order createOrderFromSeatLock(Integer userId, Integer showId) {

        // 1) 取得 seat_locks（由你 repository 提供，需手動 filter userId）
        List<SeatLockBean> locks = seatLockRepository
                .findByShowIdAndStatusAndLockedUntilAfter(showId, "active", LocalDateTime.now())
                .stream()
                .filter(l -> l.getUserId().equals(userId))
                .toList();

        if (locks.isEmpty())
            throw new RuntimeException("❗沒有可用座位鎖定 (userId/showId 無對應)");

        // 2) 找票價（你目前只需取最新一筆 available=true）
        ShowTicketPricesBean priceBean = ticketRepository
                .findTopByShowIdAndAvailable(showId, Boolean.TRUE)
                .orElseThrow(() -> new RuntimeException("❗此場無票價設定"));

        int ticketPrice = priceBean.getFinalPrice();
        String ticketType = (priceBean.getTicketPackageName() != null)
                ? priceBean.getTicketPackageName()
                : "PKG-" + priceBean.getTicketPackageId();

        // 3) 建訂單
        Order order = new Order();
        order.setUserId(userId);
        order.setShowId(showId);
        order.setOrderStatus("PENDING");
        order.setTradeNo("ORDER" + System.currentTimeMillis());
        orderRepository.save(order);

        // 4) 為每個 seatLock 建 OrderDetail
        for (SeatLockBean lock : locks) {
            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setSeatId(lock.getSeatId());
            detail.setTicketPrice(ticketPrice);
            detail.setTicketType(ticketType);
            detail.setStatus("ACTIVE");
            detailRepository.save(detail);
        }

        // 5) 自動回寫訂單總額（✔ 已修正使用 sumPriceByOrder）
        updateTotal(order.getId());

        return order;
    }

    // 🔥 sum方法已正確調整
    public void updateTotal(Integer orderId) {
        Integer total = detailRepository.sumPriceByOrder(orderId);
        Order order = orderRepository.findById(orderId).orElseThrow();
        order.setTotalAmount(total != null ? total : 0);
        orderRepository.save(order);
    }

    public void updateTradeNo(Integer orderId, String tradeNo) {
        Order order = orderRepository.findById(orderId).orElseThrow();
        order.setTradeNo(tradeNo);
        orderRepository.save(order);
    }

    public void updateStatusByTradeNo(String tradeNo, String status) {
        Order order = orderRepository.findByTradeNo(tradeNo)
                .orElseThrow(() -> new RuntimeException("找不到此 tradeNo 訂單"));

        order.setOrderStatus(status);
        orderRepository.save(order);
    }

    public boolean completeOrder(Integer orderId) {
        return orderRepository.updateStatusToCompleted(orderId) > 0;
    }
}