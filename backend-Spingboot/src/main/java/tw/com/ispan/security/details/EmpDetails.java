package tw.com.ispan.security.details;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import tw.com.ispan.entity.EmpEntity;
import tw.com.ispan.entity.RoleEntity;

public class EmpDetails implements UserDetails {

    private final EmpEntity emp;

    public EmpDetails(EmpEntity emp) {
        this.emp = emp;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 把 DB 裡的 ADMIN / MANAGER / SUPPORT_SERVICE / ENTRYSTAFF
        // 轉成 ROLE_ADMIN / ROLE_MANAGER / ROLE_SUPPORT_SERVICE / ROLE_ENTRYSTAFF
        return emp.getRoles().stream()
                .map(RoleEntity::getRoleName)
                .map(roleName -> new SimpleGrantedAuthority("ROLE_" + roleName))
                .collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return emp.getEmpPasswordHash();
    }

    @Override
    public String getUsername() {
        // 使用 email 當登入帳號
        return emp.getEmpEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 目前先不做帳號過期控制
    }

    @Override
    public boolean isAccountNonLocked() {
        // 你之前 status 設計：0 停用、1 啟用、2 鎖住
        return emp.getStatus() != 2;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 先不做密碼過期控制
    }

    @Override
    public boolean isEnabled() {
        return emp.getStatus() == 1;
    }

    public EmpEntity getEmp() {
        return emp;
    }

    // ====== 新增以下兩個方法給 LoggingAspect 使用 ======
    
    /**
     * 取得員工實體 (給 LoggingAspect 用)
     */
    public EmpEntity getEmployee() {
        return emp;
    }
    
    /**
     * 取得主要角色 (給 LoggingAspect 用)
     * 如果員工只有一個角色就回傳那個角色
     * 如果有多個角色,回傳優先級最高的角色
     */
    public RoleEntity getRole() {
        if (emp.getRoles() == null || emp.getRoles().isEmpty()) {
            return null;
        }
        
        // 如果只有一個角色,直接回傳
        if (emp.getRoles().size() == 1) {
            return emp.getRoles().iterator().next();
        }
        
        // 如果有多個角色,依優先級回傳: ADMIN > MANAGER > SUPPORT_SERVICE > ENTRYSTAFF
        for (RoleEntity role : emp.getRoles()) {
            if ("ADMIN".equalsIgnoreCase(role.getRoleName())) {
                return role;
            }
        }
        
        for (RoleEntity role : emp.getRoles()) {
            if ("MANAGER".equalsIgnoreCase(role.getRoleName())) {
                return role;
            }
        }
        
        for (RoleEntity role : emp.getRoles()) {
            if ("SUPPORT_SERVICE".equalsIgnoreCase(role.getRoleName())) {
                return role;
            }
        }
        
        for (RoleEntity role : emp.getRoles()) {
            if ("ENTRYSTAFF".equalsIgnoreCase(role.getRoleName())) {
                return role;
            }
        }
        
        // 如果都不匹配,回傳第一個
        return emp.getRoles().iterator().next();
    }
}
