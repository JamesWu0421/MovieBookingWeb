package tw.com.ispan.repository;

import tw.com.ispan.domain.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {

    List<OrderDetail> findByOrderId(Integer orderId);

    /** 計算指定訂單的票價總額 */
    @Query("SELECT SUM(od.ticketPrice) FROM OrderDetail od WHERE od.order.id = :orderId")
    Integer sumPriceByOrder(Integer orderId);
}
