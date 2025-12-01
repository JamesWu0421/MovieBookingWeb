package tw.com.ispan.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tw.com.ispan.entity.EmpEntity;
import tw.com.ispan.entity.EmpLogEntity;

@Repository
public interface EmpLogRepository extends JpaRepository<EmpLogEntity, Integer>{
    // 查詢某個員工的所有操作紀錄
    List<EmpLogEntity> findByEmployee(EmpEntity employee);
}
