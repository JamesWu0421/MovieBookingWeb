package tw.com.ispan.controller;

import tw.com.ispan.domain.Refund;
import tw.com.ispan.service.RefundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/refund")

public class RefundController {

    @Autowired
    private RefundService refundService;

    @GetMapping
    public List<Refund> getAllRefunds() {
        return refundService.getAllRefunds();
    }

    @GetMapping("/order/{orderId}")
    public List<Refund> getRefundsByOrder(@PathVariable Integer orderId) {
        return refundService.getRefundsByOrderId(orderId);
    }

    @PostMapping("/order/{orderId}")
    public Refund createRefund(@PathVariable Integer orderId, @RequestParam String reason) {
        return refundService.createRefund(orderId, reason);
    }

    @PutMapping("/{id}/status")
    public Refund updateStatus(@PathVariable Integer id, @RequestParam String status) {
        return refundService.updateRefundStatus(id, status);
    }

    @PostMapping("/single/{detailId}")
    public ResponseEntity<?> refundSingle(@PathVariable Integer detailId) {
        boolean ok = refundService.refundOrderDetail(detailId);
        if (ok) {
            return ResponseEntity.ok("退款成功");
        }
        return ResponseEntity.badRequest().body("退款失敗");
    }
}