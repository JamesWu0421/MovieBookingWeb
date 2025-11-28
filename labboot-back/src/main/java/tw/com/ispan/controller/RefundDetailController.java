package tw.com.ispan.controller;

import tw.com.ispan.domain.RefundDetail;
import tw.com.ispan.service.RefundDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/refund-details")
@CrossOrigin(origins = "*")
public class RefundDetailController {

    @Autowired
    private RefundDetailService refundDetailService;

    @GetMapping("/refund/{refundId}")
    public List<RefundDetail> getDetailsByRefund(@PathVariable Integer refundId) {
        return refundDetailService.getRefundDetailsByRefundId(refundId);
    }

    @PostMapping("/refund/{refundId}/order-detail/{orderDetailId}")
    public RefundDetail createRefundDetail(
            @PathVariable Integer refundId,
            @PathVariable Integer orderDetailId,
            @RequestParam Integer amount) {
        return refundDetailService.createRefundDetail(refundId, orderDetailId, amount);
    }
}