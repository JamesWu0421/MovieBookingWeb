package tw.com.ispan.controller.emp;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import tw.com.ispan.entity.EmpLogEntity;
import tw.com.ispan.service.empandroll.EmpLogService;

@RestController
@RequestMapping("/api/admin/logs")
public class EmpLogController {

    @Autowired
    private EmpLogService empLogService;

    // ===== 基本查詢 =====
    
    // 查詢所有日誌 (僅管理員)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<EmpLogEntity>> getAllLogs() {
        return ResponseEntity.ok(empLogService.getAllLogs());
    }

    // 查詢最近 10 筆日誌
    @GetMapping("/recent")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<EmpLogEntity>> getRecentLogs() {
        return ResponseEntity.ok(empLogService.getRecentLogs());
    }

    // 查詢今天的日誌
    @GetMapping("/today")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<EmpLogEntity>> getTodayLogs() {
        return ResponseEntity.ok(empLogService.getTodayLogs());
    }

    // 查詢最近 7 天的日誌
    @GetMapping("/last-week")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<EmpLogEntity>> getLastWeekLogs() {
        return ResponseEntity.ok(empLogService.getLastWeekLogs());
    }

    // 查詢最近 30 天的日誌
    @GetMapping("/last-month")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<EmpLogEntity>> getLastMonthLogs() {
        return ResponseEntity.ok(empLogService.getLastMonthLogs());
    }

    // ===== 單一條件查詢 =====
    
    // 依員工 Email 查詢日誌
    @GetMapping("/employee")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<EmpLogEntity>> getLogsByEmployee(@RequestParam String email) {
        return ResponseEntity.ok(empLogService.getLogsByEmployeeEmail(email));
    }

    // 依角色名稱查詢日誌
    @GetMapping("/role")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<EmpLogEntity>> getLogsByRole(@RequestParam String roleName) {
        return ResponseEntity.ok(empLogService.getLogsByRoleName(roleName));
    }

    // 依操作類型查詢日誌
    @GetMapping("/action-type")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<EmpLogEntity>> getLogsByActionType(@RequestParam String actionType) {
        return ResponseEntity.ok(empLogService.getLogsByActionType(actionType));
    }

    // 依時間範圍查詢日誌
    @GetMapping("/date-range")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<EmpLogEntity>> getLogsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(empLogService.getLogsByDateRange(start, end));
    }

    // 在查詢參數中搜尋關鍵字
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<EmpLogEntity>> searchByKeyword(@RequestParam String keyword) {
        return ResponseEntity.ok(empLogService.searchByKeyword(keyword));
    }

    // ===== 組合條件查詢 =====
    
    // 員工 Email + 時間範圍
    @GetMapping("/employee/date-range")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<EmpLogEntity>> getLogsByEmployeeAndDateRange(
            @RequestParam String email,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(empLogService.getLogsByEmployeeAndDateRange(email, start, end));
    }

    // 角色 + 時間範圍
    @GetMapping("/role/date-range")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<EmpLogEntity>> getLogsByRoleAndDateRange(
            @RequestParam String roleName,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(empLogService.getLogsByRoleAndDateRange(roleName, start, end));
    }

    // 操作類型 + 時間範圍
    @GetMapping("/action-type/date-range")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<EmpLogEntity>> getLogsByActionTypeAndDateRange(
            @RequestParam String actionType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(empLogService.getLogsByActionTypeAndDateRange(actionType, start, end));
    }

    // ===== 刪除功能 =====
    
    // 刪除日誌記錄
    @DeleteMapping("/{logId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteLog(@PathVariable Integer logId) {
        empLogService.deleteLog(logId);
        return ResponseEntity.ok().build();
    }
}
