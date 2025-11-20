package tw.com.ispan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tw.com.ispan.entity.ShowBean;

public interface ShowRepository extends JpaRepository<ShowBean, Integer> {

}
