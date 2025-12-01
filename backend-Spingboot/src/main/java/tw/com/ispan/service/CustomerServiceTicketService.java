package tw.com.ispan.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tw.com.ispan.dto.CustomerServiceTicketRequest;
import tw.com.ispan.domain.CustomerServiceTicket;
import tw.com.ispan.domain.Order;
import tw.com.ispan.repository.CustomerServiceTicketRepository;
import tw.com.ispan.repository.OrderRepository;

@Service
public class CustomerServiceTicketService {

    @Autowired
    private CustomerServiceTicketRepository repo;

    public List<CustomerServiceTicket> getAll() {
        return repo.findAll();
    }

    public List<CustomerServiceTicket> getByOrderId(Integer orderId) {
        return repo.findByOrderId(orderId);
    }

    public CustomerServiceTicket getById(Integer id) {
        return repo.findById(id).orElse(null);
    }

    public CustomerServiceTicket create(CustomerServiceTicketRequest request) {

        CustomerServiceTicket ticket = new CustomerServiceTicket();
        ticket.setOrderId(request.getOrderId());
        ticket.setIssueType(request.getIssueType());

        // 合併 title + description
        ticket.setDescription("【" + request.getTitle() + "】\n" + request.getDescription());

        ticket.setHandledBy(request.getHandledBy() == null ? 0 : request.getHandledBy());
        ticket.setStatus("pending");

        LocalDateTime now = LocalDateTime.now();
        ticket.setCreatedAt(now);
        ticket.setUpdatedAt(now);

        return repo.save(ticket);
    }

    public CustomerServiceTicket updateStatus(Integer id, String status) {
        CustomerServiceTicket ticket = repo.findById(id).orElseThrow();
        ticket.setStatus(status);
        ticket.setUpdatedAt(LocalDateTime.now());
        return repo.save(ticket);
    }

    public CustomerServiceTicket assign(Integer id, Integer employeeId) {
        CustomerServiceTicket ticket = repo.findById(id).orElseThrow();
        ticket.setHandledBy(employeeId);
        ticket.setUpdatedAt(LocalDateTime.now());
        return repo.save(ticket);
    }
}
