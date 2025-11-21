package tw.com.ispan.controller.emp;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import tw.com.ispan.dto.request.admin.EmpCreateDto;
import tw.com.ispan.entity.EmpEntity;
import tw.com.ispan.service.empandroll.EmpService;

@RestController
@RequestMapping("/api/admin/employees")
public class EmpController {

    private final EmpService empService;

    public EmpController(EmpService empService) {
        this.empService = empService;
    }

    // 列表 + 搜尋：ADMIN、MANAGER 可以看
 @GetMapping
public Page<EmpEntity> listEmployees(@PageableDefault Pageable pageable) {
    return empService.findAllEmployees(pageable);
}

    // 搜尋：ADMIN、MANAGER 可以看
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public Page<EmpEntity> searchEmployees(
        @RequestParam String keyword,
        Pageable pageable
) {
    return empService.searchEmployees(keyword, pageable);
}

    // 單筆詳情：ADMIN、MANAGER 可以看
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public EmpEntity findOne(@PathVariable Integer id) {
        return empService.findById(id);
    }

    // 新增員工：只有 ADMIN
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public EmpEntity create(@RequestBody EmpCreateDto dto,
                            @RequestParam Integer roleId) {

        EmpEntity emp = new EmpEntity();
        emp.setEmpName(dto.getEmpName());
        emp.setEmpEmail(dto.getEmpEmail());
        emp.setEmpPhone(dto.getEmpPhone());
        emp.setStatus(dto.getStatus() != null ? dto.getStatus() : (byte) 1);

        return empService.create(emp, roleId, dto.getPlainPassword());
    }

    // 更新員工：ADMIN、MANAGER
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public EmpEntity update(@PathVariable Integer id,
                            @RequestBody EmpEntity emp,
                            @RequestParam Integer roleId) {
        return empService.update(id, emp, roleId);
    }

    // 刪除員工：只有 ADMIN（如果你真要支援刪除）
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public void delete(@PathVariable Integer id) {
        empService.delete(id);
    }
}
