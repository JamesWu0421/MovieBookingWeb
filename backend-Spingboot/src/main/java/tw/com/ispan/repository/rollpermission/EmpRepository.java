package tw.com.ispan.repository.rollpermission;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tw.com.ispan.entity.EmpEntity;

@Repository
public interface EmpRepository extends JpaRepository<EmpEntity, Integer> {

    Optional<EmpEntity> findByEmpEmail(String empEmail);
     Page<EmpEntity> findByEmpNameContainingOrEmpEmailContaining(
    String empName, String empEmail, Pageable pageable
);
}