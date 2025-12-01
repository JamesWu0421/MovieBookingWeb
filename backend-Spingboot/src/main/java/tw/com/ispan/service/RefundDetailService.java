package tw.com.ispan.service;

import tw.com.ispan.domain.OrderDetail;
import tw.com.ispan.domain.Refund;
import tw.com.ispan.domain.RefundDetail;
import tw.com.ispan.repository.OrderDetailRepository;
import tw.com.ispan.repository.RefundDetailRepository;
import tw.com.ispan.repository.RefundRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RefundDetailService {

    @Autowired
    private RefundDetailRepository refundDetailRepository;

    @Autowired
    private RefundRepository refundRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    // 建立退款明細
    public RefundDetail createRefundDetail(Integer refundId, Integer orderDetailId, Integer amount) {
        Optional<Refund> refundOpt = refundRepository.findById(refundId);
        Optional<OrderDetail> detailOpt = orderDetailRepository.findById(orderDetailId);
        if (refundOpt.isPresent() && detailOpt.isPresent()) {
            RefundDetail rd = new RefundDetail();
            rd.setRefund(refundOpt.get());
            rd.setOrderDetail(detailOpt.get());
            rd.setRefundAmount(amount);
            rd.setStatus("PENDING");
            return refundDetailRepository.save(rd);
        }
        return null;
    }

    // 查退款明細
    public List<RefundDetail> getRefundDetailsByRefundId(Integer refundId) {
        return refundDetailRepository.findByRefundId(refundId);
    }
}