package tw.com.ispan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;
import tw.com.ispan.domain.Order;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    // 可額外自訂查詢方法
    List<Order> findByUserId(Integer userId);

    List<Order> findByOrderStatus(String orderStatus);

    Optional<Order> findTopByOrderByIdDesc();

    Order findTopByUserIdOrderByIdDesc(Integer userId);

    Optional<Order> findByTradeNo(String tradeNo);

    // 新增狀態更新方法 — 將 PENDING 改為 COMPLETED
    @Transactional
    @Modifying
    @Query("UPDATE Order o SET o.orderStatus = 'COMPLETED' WHERE o.id = :orderId")
    int updateStatusToCompleted(@Param("orderId") Integer orderId);

}
