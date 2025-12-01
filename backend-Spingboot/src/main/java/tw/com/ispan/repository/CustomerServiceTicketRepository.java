package tw.com.ispan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tw.com.ispan.domain.CustomerServiceTicket;

import java.util.List;

@Repository
public interface CustomerServiceTicketRepository
        extends JpaRepository<CustomerServiceTicket, Integer> {

    List<CustomerServiceTicket> findByOrderId(Integer orderId);

    List<CustomerServiceTicket> findByHandledBy(Integer handledBy);
}
