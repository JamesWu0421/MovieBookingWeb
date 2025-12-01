package tw.com.ispan.service;

import tw.com.ispan.domain.Order;
import tw.com.ispan.domain.OrderDetail;
import tw.com.ispan.repository.OrderDetailRepository;
import tw.com.ispan.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderDetailService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;

    public List<OrderDetail> getAllOrderDetails() {
        return orderDetailRepository.findAll();
    }

    public List<OrderDetail> getDetailsByOrderId(Integer orderId) {
        return orderDetailRepository.findByOrderId(orderId);
    }

    /** ➤ 新增明細後同步更新訂單金額 */
    @Transactional
    public OrderDetail createOrderDetail(Integer orderId, OrderDetail detail) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        detail.setOrder(order);
        orderDetailRepository.save(detail);

        updateOrderTotal(orderId);
        return detail;
    }

    /** ➤ 更新明細後重新計算金額 */
    @Transactional
    public OrderDetail updateOrderDetail(Integer id, OrderDetail detail) {
        OrderDetail exist = orderDetailRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("OrderDetail not found"));

        exist.setSeatId(detail.getSeatId());
        exist.setTicketPrice(detail.getTicketPrice());
        exist.setTicketType(detail.getTicketType());
        exist.setStatus(detail.getStatus());
        orderDetailRepository.save(exist);

        updateOrderTotal(exist.getOrder().getId());
        return exist;
    }

    /** ➤ 刪除 detail 也會同步更新 Order 總額 */
    @Transactional
    public void deleteOrderDetail(Integer id) {
        OrderDetail detail = orderDetailRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("OrderDetail not found"));

        Integer orderId = detail.getOrder().getId();
        orderDetailRepository.delete(detail);

        updateOrderTotal(orderId);
    }

    /** ⭐ 核心：依 OrderDetail 票價自動 SUM 回寫訂單 */
    private void updateOrderTotal(Integer orderId) {
        Integer total = orderDetailRepository.sumPriceByOrder(orderId);
        if (total == null)
            total = 0;

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setTotalAmount(total);
        orderRepository.save(order);
    }
}
