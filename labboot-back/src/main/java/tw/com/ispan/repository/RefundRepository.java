package tw.com.ispan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tw.com.ispan.domain.Refund;

import java.util.List;

@Repository
public interface RefundRepository extends JpaRepository<Refund, Integer> {
    List<Refund> findByOrderId(Integer orderId);

    List<Refund> findByRefundStatus(String status);
}