package tw.com.ispan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tw.com.ispan.domain.BatchOperationsBean;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BatchOperationsRepository
        extends JpaRepository<BatchOperationsBean, Integer> {

    List<BatchOperationsBean> findAllByOrderByCreatedAtDesc();

    List<BatchOperationsBean> findByOperatorIdOrderByCreatedAtDesc(Integer operatorId);

    List<BatchOperationsBean> findByStatusOrderByCreatedAtDesc(String status);

    List<BatchOperationsBean> findByOperationTypeOrderByCreatedAtDesc(String operationType);

    List<BatchOperationsBean> findByOperatorIdAndStatusOrderByCreatedAtDesc(Integer operatorId, String status);

    List<BatchOperationsBean> findByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime start,
                                                                         LocalDateTime end);
}