package tw.com.ispan.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import tw.com.ispan.domain.Order;
import tw.com.ispan.service.OrderService;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://localhost:5174",
        "http://localhost:5175"
})
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    // 查單筆
    @GetMapping("/by-id/{id}")
    public Optional<Order> getOrderById(@PathVariable Integer id) {
        return orderService.getOrderById(id);
    }

    // 新增
    @PostMapping
    public Order createOrder(@RequestBody Order order) {
        return orderService.createOrder(order);
    }

    // 更新狀態
    @PutMapping("/{id}/status")
    public Order updateOrderStatus(@PathVariable Integer id, @RequestParam String status) {
        return orderService.updateOrderStatus(id, status);
    }

    // 刪除
    @DeleteMapping("/{id}")
    public void deleteOrder(@PathVariable Integer id) {
        orderService.deleteOrder(id);
    }

    // 取最新一筆
    // @GetMapping("/latest")
    // public Order getLatestOrder() {

    // return orderService.getLatestOrder();
    // }

    // 根據用戶ID查訂單
    @GetMapping("/by-user/{userId}")
    public List<Order> getOrdersByUserId(@PathVariable Integer userId) {
        return orderService.getOrdersByUserId(userId);
    }

    @GetMapping("/latest/{userId}")
    public Order getLatestOrderByUser(@PathVariable Integer userId) {
        return orderService.getLatestOrderByUserId(userId);
    }

    @PostMapping("/create")
    public Order createOrderAndGenerateTradeNo(@RequestBody Order req) {

        String tradeNo = "ORDER" + System.currentTimeMillis(); // 🔥 綠界用訂單編號

        req.setTradeNo(tradeNo);
        req.setOrderStatus("PENDING");
        req.setOrderTime(java.time.LocalDateTime.now());

        return orderService.createOrder(req);
    }

    @PostMapping("/from-seatlock")
    public Order createFromSeatLock(@RequestParam Integer userId,
            @RequestParam Integer showId) {
        return orderService.createOrderFromSeatLock(userId, showId);
    }

    @PutMapping("/{orderId}/complete")
    public ResponseEntity<String> completeOrder(@PathVariable Integer orderId) {
        boolean success = orderService.completeOrder(orderId);
        return success
                ? ResponseEntity.ok("Order Status = COMPLETED")
                : ResponseEntity.badRequest().body("Update Failed");
    }
}
