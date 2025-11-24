package tw.com.ispan.service.empandroll;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tw.com.ispan.entity.EmpEntity;
import tw.com.ispan.entity.EmpLogEntity;
import tw.com.ispan.entity.RoleEntity;
import tw.com.ispan.repository.rollpermission.EmpLogRepository;

@Service
public class EmpLogService {

    @Autowired
    private EmpLogRepository empLogRepository;

    // 記錄操作日誌
    @Transactional
    public void logAction(EmpEntity employee, RoleEntity role, String actionType, String queryParams) {
        EmpLogEntity log = new EmpLogEntity();
        log.setEmployee(employee);
        log.setRole(role);
        log.setActionType(actionType);
        log.setQueryParams(queryParams);
        log.setAccessTime(LocalDateTime.now());
        empLogRepository.save(log);
    }

    // ===== 基本查詢 =====
    
    // 查詢所有日誌
    public List<EmpLogEntity> getAllLogs() {
        return empLogRepository.findAll();
    }

    // 查詢最近 10 筆日誌
    public List<EmpLogEntity> getRecentLogs() {
        return empLogRepository.findTop10ByOrderByAccessTimeDesc();
    }

    // 查詢今天的日誌
    public List<EmpLogEntity> getTodayLogs() {
        return empLogRepository.findTodayLogs();
    }

    // 查詢最近 7 天的日誌
    public List<EmpLogEntity> getLastWeekLogs() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        return empLogRepository.findLogsAfterDate(startDate);
    }

    // 查詢最近 30 天的日誌
    public List<EmpLogEntity> getLastMonthLogs() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(30);
        return empLogRepository.findLogsAfterDate(startDate);
    }

    // ===== 單一條件查詢 =====
    
    // 依員工 ID 查詢日誌
    public List<EmpLogEntity> getLogsByEmployeeId(Integer empId) {
        return empLogRepository.findByEmployee_Id(empId);
    }

    // 依員工 Email 查詢日誌
    public List<EmpLogEntity> getLogsByEmployeeEmail(String empEmail) {
        return empLogRepository.findByEmployee_EmpEmail(empEmail);
    }

    // 依角色 ID 查詢日誌
    public List<EmpLogEntity> getLogsByRoleId(Integer roleId) {
        return empLogRepository.findByRole_Id(roleId);
    }

    // 依角色名稱查詢日誌
    public List<EmpLogEntity> getLogsByRoleName(String roleName) {
        return empLogRepository.findByRole_RoleName(roleName);
    }

    // 依操作類型查詢 (模糊搜尋)
    public List<EmpLogEntity> getLogsByActionType(String actionType) {
        return empLogRepository.findByActionTypeContaining(actionType);
    }

    // 依時間範圍查詢
    public List<EmpLogEntity> getLogsByDateRange(LocalDateTime start, LocalDateTime end) {
        return empLogRepository.findByAccessTimeBetween(start, end);
    }

    // 在查詢參數中搜尋關鍵字
    public List<EmpLogEntity> searchByKeyword(String keyword) {
        return empLogRepository.findByQueryParamsContaining(keyword);
    }

    // ===== 組合條件查詢 =====
    
    // 員工 Email + 時間範圍
    public List<EmpLogEntity> getLogsByEmployeeAndDateRange(String email, LocalDateTime start, LocalDateTime end) {
        return empLogRepository.findByEmployeeEmailAndDateRange(email, start, end);
    }

    // 角色 + 時間範圍
    public List<EmpLogEntity> getLogsByRoleAndDateRange(String roleName, LocalDateTime start, LocalDateTime end) {
        return empLogRepository.findByRoleNameAndDateRange(roleName, start, end);
    }

    // 操作類型 + 時間範圍
    public List<EmpLogEntity> getLogsByActionTypeAndDateRange(String actionType, LocalDateTime start, LocalDateTime end) {
        return empLogRepository.findByActionTypeAndDateRange(actionType, start, end);
    }

    // ===== 其他功能 =====
    
    // 刪除日誌
    @Transactional
    public void deleteLog(Integer logId) {
        empLogRepository.deleteById(logId);
    }
}
