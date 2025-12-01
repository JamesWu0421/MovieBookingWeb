package tw.com.ispan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tw.com.ispan.domain.ScreenBean;

public interface ScreenRepository extends JpaRepository<ScreenBean, Integer> {

}
