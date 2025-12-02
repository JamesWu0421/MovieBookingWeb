package tw.com.ispan.service;

import tw.com.ispan.domain.Order;
import tw.com.ispan.domain.OrderDetail;
import tw.com.ispan.domain.Refund;
import tw.com.ispan.repository.OrderDetailRepository;
//import com.example.demo.domain.RefundDetail;
import tw.com.ispan.repository.OrderRepository;
import tw.com.ispan.repository.RefundRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RefundService {

    @Autowired
    private RefundRepository refundRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    // 建立退款單
    public Refund createRefund(Integer orderId, String reason) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            Refund refund = new Refund();
            refund.setOrder(orderOpt.get());
            refund.setRefundReason(reason);
            refund.setRefundStatus("REQUESTED");
            return refundRepository.save(refund);
        }
        return null;
    }

    // 查全部退款單
    public List<Refund> getAllRefunds() {
        return refundRepository.findAll();
    }

    // 查訂單的退款單
    public List<Refund> getRefundsByOrderId(Integer orderId) {
        return refundRepository.findByOrderId(orderId);
    }

    // 更新退款狀態
    public Refund updateRefundStatus(Integer id, String status) {
        Optional<Refund> opt = refundRepository.findById(id);
        if (opt.isPresent()) {
            Refund refund = opt.get();
            refund.setRefundStatus(status);
            return refundRepository.save(refund);
        }
        return null;
    }

    public boolean refundOrderDetail(Integer detailId) {
        Optional<OrderDetail> optional = orderDetailRepository.findById(detailId);

        if (optional.isEmpty()) {
            return false;
        }

        OrderDetail detail = optional.get();

        // 只有 ACTIVE 才能退款
        if (!"ACTIVE".equals(detail.getStatus())) {
            return false;
        }

        detail.setStatus("refunded"); // ⭐改狀態

        orderDetailRepository.save(detail);
        return true;
    }
}
