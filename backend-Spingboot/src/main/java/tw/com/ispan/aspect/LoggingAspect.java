package tw.com.ispan.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import tw.com.ispan.entity.EmpEntity;
import tw.com.ispan.entity.RoleEntity;
import tw.com.ispan.repository.rollpermission.EmpRepository;
import tw.com.ispan.security.details.EmpDetails;
import tw.com.ispan.service.empandroll.EmpLogService;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Autowired
    private EmpLogService empLogService;

    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private EmpRepository empRepository; // 新增:用來查詢員工資料

    // 定義切入點:排除登入 Controller 和所有 @GetMapping 的方法
    @Pointcut("execution(* tw.com.ispan.controller.emp..*(..)) " +
              "&& !execution(* tw.com.ispan.controller.emp.AdminAuthController.*(..)) " +
              "&& !@annotation(org.springframework.web.bind.annotation.GetMapping)")
    public void controllerMethods() {}
    
    // 新增:專門處理刪除操作
    @Before("execution(* tw.com.ispan.controller.emp.*.delete*(..)) && args(empId,..)")
    public void logBeforeDelete(JoinPoint joinPoint, Integer empId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            
            if (auth == null || !auth.isAuthenticated()) {
                return;
            }

            EmpEntity currentEmployee = getCurrentEmployee(auth);
            RoleEntity currentRole = getCurrentRole(auth);
            
            if (currentEmployee == null || currentRole == null) {
                return;
            }

            // 在刪除前先查詢要刪除的員工資料
            EmpEntity employeeToDelete = empRepository.findById(empId).orElse(null);
            
            if (employeeToDelete == null) {
                logger.warn("Employee with id {} not found for deletion", empId);
                return;
            }

            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                
                String actionType = "DELETE - " + joinPoint.getSignature().getName();
                
                // 記錄完整的員工資訊
                String queryParams = buildDeleteQueryParams(request, employeeToDelete);

                empLogService.logAction(currentEmployee, currentRole, actionType, queryParams);
                
                logger.info("Logged DELETE action for employee: {} [{}] - Deleted: {} ({})", 
                           currentEmployee.getEmpEmail(), currentRole.getRoleName(),
                           employeeToDelete.getEmpName(), employeeToDelete.getEmpEmail());
            }
        } catch (Exception e) {
            logger.error("Failed to log delete action", e);
        }
    }

    // 原本的方法執行後記錄 (排除刪除操作,因為已經在 @Before 處理了)
    @AfterReturning("controllerMethods() && !execution(* tw.com.ispan.controller.emp.*.delete*(..))")
    public void logAfterMethod(JoinPoint joinPoint) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            
            if (auth == null || !auth.isAuthenticated()) {
                return;
            }

            EmpEntity employee = getCurrentEmployee(auth);
            RoleEntity role = getCurrentRole(auth);
            
            if (employee == null || role == null) {
                logger.debug("Cannot log action - employee or role is null");
                return;
            }

            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                
                String actionType = request.getMethod() + " - " + joinPoint.getSignature().getName();
                String queryParams = buildQueryParams(request, joinPoint.getArgs());

                empLogService.logAction(employee, role, actionType, queryParams);
                
                logger.info("Logged action for employee: {} [{}] - {}", 
                           employee.getEmpEmail(), role.getRoleName(), actionType);
            }
        } catch (Exception e) {
            logger.error("Failed to log action", e);
        }
    }

    // 新增:建立刪除操作的詳細參數
    private String buildDeleteQueryParams(HttpServletRequest request, EmpEntity employeeToDelete) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("URI: ").append(request.getRequestURI());
            sb.append(" | Deleted Employee: ");
            sb.append("ID=").append(employeeToDelete.getId());
            sb.append(", Name=").append(employeeToDelete.getEmpName());
            sb.append(", Email=").append(employeeToDelete.getEmpEmail());
            sb.append(", Phone=").append(employeeToDelete.getEmpPhone());
            sb.append(", Status=").append(employeeToDelete.getStatus());
            sb.append(", CreatedAt=").append(employeeToDelete.getCreatedAt());
            
            return sb.toString();
        } catch (Exception e) {
            logger.error("Error building delete query params", e);
            return "Error parsing params";
        }
    }

    private String buildQueryParams(HttpServletRequest request, Object[] args) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("URI: ").append(request.getRequestURI());
            
            if (request.getQueryString() != null) {
                sb.append("?").append(request.getQueryString());
            }
            
            if (args != null && args.length > 0) {
                sb.append(" | Body: ");
                String argsJson = objectMapper.writeValueAsString(args);
                
                if (argsJson.length() > 2000) {
                    argsJson = argsJson.substring(0, 2000) + "... (truncated)";
                }
                sb.append(argsJson);
            }
            
            return sb.toString();
        } catch (Exception e) {
            logger.error("Error building query params", e);
            return "Error parsing params";
        }
    }

    private EmpEntity getCurrentEmployee(Authentication auth) {
        try {
            Object principal = auth.getPrincipal();
            
            if (principal instanceof String && "anonymousUser".equals(principal)) {
                return null;
            }
            
            if (principal instanceof EmpDetails) {
                EmpDetails empDetails = (EmpDetails) principal;
                return empDetails.getEmployee();
            }
            
            return null;
        } catch (Exception e) {
            logger.error("Error getting current employee", e);
            return null;
        }
    }

    private RoleEntity getCurrentRole(Authentication auth) {
        try {
            Object principal = auth.getPrincipal();
            
            if (principal instanceof String && "anonymousUser".equals(principal)) {
                return null;
            }
            
            if (principal instanceof EmpDetails) {
                EmpDetails empDetails = (EmpDetails) principal;
                return empDetails.getRole();
            }
            
            return null;
        } catch (Exception e) {
            logger.error("Error getting current role", e);
            return null;
        }
    }
}
