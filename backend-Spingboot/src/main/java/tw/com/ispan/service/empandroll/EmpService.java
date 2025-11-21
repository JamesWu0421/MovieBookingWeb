package tw.com.ispan.service.empandroll;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import tw.com.ispan.entity.EmpEntity;
import tw.com.ispan.entity.RoleEntity;
import tw.com.ispan.repository.rollpermission.EmpRepository;
import tw.com.ispan.repository.rollpermission.RoleRepository;

@Service
public class EmpService {

    private final EmpRepository empRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder; // 🔹 新增

    public EmpService(EmpRepository empRepository,
                      RoleRepository roleRepository,
                      PasswordEncoder passwordEncoder) {   // 🔹 建構子注入
        this.empRepository = empRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Page<EmpEntity> findAllEmployees(Pageable pageable) {
    return empRepository.findAll(pageable);
}

    public EmpEntity findById(Integer id) {
        return empRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
    }

    // 先做一個簡單版本：只給一個角色，之後再改成多選
    public EmpEntity create(EmpEntity emp, Integer roleId, String plainPassword) {
        emp.setId(null);
        emp.setCreatedAt(LocalDateTime.now());
        emp.setStatus((byte) 1); // 預設啟用
        
        // 🔹 這裡把原始密碼做 BCrypt
        String encoded = passwordEncoder.encode(plainPassword);
        emp.setEmpPasswordHash(encoded);

        RoleEntity role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        emp.setRoles(Set.of(role));

        return empRepository.save(emp);
    }

    public EmpEntity update(Integer id, EmpEntity dto, Integer roleId) {
        EmpEntity emp = findById(id);
        emp.setEmpName(dto.getEmpName());
        emp.setEmpPhone(dto.getEmpPhone());
        emp.setEmpEmail(dto.getEmpEmail());
        emp.setStatus(dto.getStatus());

        // 🔹 如果前端有給新密碼，可以選擇在這裡更新
        // if (dto.getPlainPassword() != null && !dto.getPlainPassword().isBlank()) {
        //     String encoded = passwordEncoder.encode(dto.getPlainPassword());
        //     emp.setEmpPasswordHash(encoded);
        // }

        RoleEntity role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        Set<RoleEntity> roles = new HashSet<>();
        roles.add(role);
        emp.setRoles(roles);
        return empRepository.save(emp);
    }

    public Page<EmpEntity> searchEmployees(String keyword, Pageable pageable) {
    return empRepository.findByEmpNameContainingOrEmpEmailContaining(
        keyword, keyword, pageable
    );
}

    public void delete(Integer id) {
    EmpEntity target = empRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Employee not found"));

    boolean targetIsAdmin = target.getRoles().stream()
            .anyMatch(r -> "ADMIN".equals(r.getRoleName()));

    if (targetIsAdmin) {
        throw new AccessDeniedException("Cannot delete ADMIN employee");
    }

    empRepository.delete(target);
}

}
