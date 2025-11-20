package tw.com.ispan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tw.com.ispan.entity.TicketPackageBean;



@Repository
public interface TicketPackageRepository extends JpaRepository<TicketPackageBean, Long> {
  
}
