package tw.com.ispan.controller;

import tw.com.ispan.dto.CustomerServiceTicketRequest;
import tw.com.ispan.domain.CustomerServiceTicket;
import tw.com.ispan.service.CustomerServiceTicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer-service")
public class CustomerServiceTicketController {

    @Autowired
    private CustomerServiceTicketService service;

    @GetMapping
    public List<CustomerServiceTicket> getAll() {
        return service.getAll();
    }

    @GetMapping("/order/{orderId}")
    public List<CustomerServiceTicket> getByOrder(@PathVariable Integer orderId) {
        return service.getByOrderId(orderId);
    }

    @GetMapping("/{id}")
    public CustomerServiceTicket getById(@PathVariable Integer id) {
        return service.getById(id);
    }

    @PostMapping
    public CustomerServiceTicket create(@RequestBody CustomerServiceTicketRequest req) {
        return service.create(req);
    }

    @PutMapping("/{id}/status")
    public CustomerServiceTicket updateStatus(
            @PathVariable Integer id,
            @RequestParam String status) {
        return service.updateStatus(id, status);
    }

    @PutMapping("/{id}/assign")
    public CustomerServiceTicket assign(
            @PathVariable Integer id,
            @RequestParam Integer employeeId) {
        return service.assign(id, employeeId);
    }
}
