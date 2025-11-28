package tw.com.ispan.controller;

import tw.com.ispan.domain.OrderDetail;
import tw.com.ispan.service.OrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order-details")
public class OrderDetailController {

    @Autowired
    private OrderDetailService orderDetailService;

    /** 取得全部 OrderDetail */
    @GetMapping
    public List<OrderDetail> getAllOrderDetails() {
        return orderDetailService.getAllOrderDetails();
    }

    /** 根據訂單 ID 取得所有細項 */
    @GetMapping("/order/{orderId}")
    public List<OrderDetail> getDetailsByOrder(@PathVariable Integer orderId) {
        return orderDetailService.getDetailsByOrderId(orderId);
    }

    /** 建立 order detail（會自動更新 Order.totalAmount） */
    @PostMapping("/order/{orderId}")
    public OrderDetail createDetail(@PathVariable Integer orderId, @RequestBody OrderDetail detail) {
        return orderDetailService.createOrderDetail(orderId, detail);
    }

    /** 更新 order detail（會重新計算總額） */
    @PutMapping("/{id}")
    public OrderDetail updateDetail(@PathVariable Integer id, @RequestBody OrderDetail detail) {
        return orderDetailService.updateOrderDetail(id, detail);
    }

    /** 刪除 order detail（總額同步變動） */
    @DeleteMapping("/{id}")
    public void deleteDetail(@PathVariable Integer id) {
        orderDetailService.deleteOrderDetail(id);
    }
}
