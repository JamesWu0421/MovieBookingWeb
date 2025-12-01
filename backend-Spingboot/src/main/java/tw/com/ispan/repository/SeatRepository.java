package tw.com.ispan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tw.com.ispan.domain.SeatBean;
import java.util.List;

public interface SeatRepository extends JpaRepository<SeatBean, Integer> {
  List<SeatBean> findByScreenId(Integer screenId);
}
