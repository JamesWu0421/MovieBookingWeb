package tw.com.ispan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tw.com.ispan.domain.BatchSessionsTempBean;

import java.util.List;

@Repository
public interface BatchSessionsTempRepository
        extends JpaRepository<BatchSessionsTempBean, Integer> {

    List<BatchSessionsTempBean> findByBatchId(Integer batchId);

    List<BatchSessionsTempBean> findByStatus(String status);

    List<BatchSessionsTempBean> findByMovieId(Integer movieId);
}