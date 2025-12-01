package tw.com.ispan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tw.com.ispan.domain.BatchTicketsTempBean;

import java.util.List;

@Repository
public interface BatchTicketsTempRepository
        extends JpaRepository<BatchTicketsTempBean, Integer> {

    List<BatchTicketsTempBean> findByBatchId(Integer batchId);

    List<BatchTicketsTempBean> findByBatchSessionId(Integer batchSessionId);

    List<BatchTicketsTempBean> findByStatus(String status);

    List<BatchTicketsTempBean> findByTicketPackagesId(Long ticketPackagesId);
}
