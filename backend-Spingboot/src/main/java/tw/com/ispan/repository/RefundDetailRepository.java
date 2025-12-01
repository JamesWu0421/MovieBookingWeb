package tw.com.ispan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tw.com.ispan.domain.RefundDetail;

import java.util.List;

@Repository
public interface RefundDetailRepository extends JpaRepository<RefundDetail, Integer> {
    List<RefundDetail> findByRefundId(Integer refundId);
}