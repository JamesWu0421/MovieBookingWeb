package tw.com.ispan.repository.rollpermission;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import tw.com.ispan.entity.EmpLogEntity;

@Repository
public interface EmpLogRepository extends JpaRepository<EmpLogEntity, Integer> {
    
    // 依員工 ID 查詢日誌
    List<EmpLogEntity> findByEmployee_Id(Integer empId);
    
    // 依員工 Email 查詢日誌
    List<EmpLogEntity> findByEmployee_EmpEmail(String empEmail);
    
    // 依角色 ID 查詢日誌
    List<EmpLogEntity> findByRole_Id(Integer roleId);
    
    // 依角色名稱查詢日誌
    List<EmpLogEntity> findByRole_RoleName(String roleName);
    
    // 依操作類型查詢
    List<EmpLogEntity> findByActionType(String actionType);
    
    // 依時間範圍查詢
    List<EmpLogEntity> findByAccessTimeBetween(LocalDateTime start, LocalDateTime end);
    
    // 查詢最近的 10 筆日誌記錄
    List<EmpLogEntity> findTop10ByOrderByAccessTimeDesc();
    
    // ===== 修改後的查詢方法 =====
    
    // 依操作類型模糊查詢
    @Query("SELECT e FROM EmpLogEntity e WHERE e.actionType LIKE %:actionType% ORDER BY e.accessTime DESC")
    List<EmpLogEntity> findByActionTypeContaining(@Param("actionType") String actionType);
    
    // 查詢今天的日誌 (修改:使用 CAST 轉換)
    @Query("SELECT e FROM EmpLogEntity e WHERE CAST(e.accessTime AS LocalDate) = CURRENT_DATE ORDER BY e.accessTime DESC")
    List<EmpLogEntity> findTodayLogs();
    
    // 查詢指定日期之後的所有日誌
    @Query("SELECT e FROM EmpLogEntity e WHERE e.accessTime >= :startDate ORDER BY e.accessTime DESC")
    List<EmpLogEntity> findLogsAfterDate(@Param("startDate") LocalDateTime startDate);
    
    // 組合查詢:員工 Email + 時間範圍
    @Query("SELECT e FROM EmpLogEntity e WHERE e.employee.empEmail = :email " +
           "AND e.accessTime BETWEEN :start AND :end ORDER BY e.accessTime DESC")
    List<EmpLogEntity> findByEmployeeEmailAndDateRange(
        @Param("email") String email,
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );
    
    // 組合查詢:角色 + 時間範圍
    @Query("SELECT e FROM EmpLogEntity e WHERE e.role.roleName = :roleName " +
           "AND e.accessTime BETWEEN :start AND :end ORDER BY e.accessTime DESC")
    List<EmpLogEntity> findByRoleNameAndDateRange(
        @Param("roleName") String roleName,
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );
    
    // 組合查詢:操作類型 + 時間範圍
    @Query("SELECT e FROM EmpLogEntity e WHERE e.actionType LIKE %:actionType% " +
           "AND e.accessTime BETWEEN :start AND :end ORDER BY e.accessTime DESC")
    List<EmpLogEntity> findByActionTypeAndDateRange(
        @Param("actionType") String actionType,
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );
    
    // 在 queryParams 中搜尋關鍵字
    @Query("SELECT e FROM EmpLogEntity e WHERE e.queryParams LIKE %:keyword% ORDER BY e.accessTime DESC")
    List<EmpLogEntity> findByQueryParamsContaining(@Param("keyword") String keyword);
}
