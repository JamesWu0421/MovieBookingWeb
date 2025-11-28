package tw.com.ispan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
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

}