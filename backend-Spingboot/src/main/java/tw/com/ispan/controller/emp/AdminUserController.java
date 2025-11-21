package tw.com.ispan.controller.emp;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import tw.com.ispan.dto.request.admin.AdminUserUpdateRequest;
import tw.com.ispan.entity.UserEntity;
import tw.com.ispan.service.UserService;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

  //分頁 
@GetMapping
public Page<UserEntity> listAll(@PageableDefault Pageable pageable) {
    return userService.adminFindAllUsers(pageable);

}

    // 搜尋
    @GetMapping("/search")
    public Page<Employee> searchEmployees(
    @RequestParam String keyword,
    Pageable pageable
) {
    return EmpService.searchEmployees(keyword, pageable);
}

    // 單筆詳情
    @GetMapping("/{id}")
    public UserEntity findOne(@PathVariable Integer id) {
        return userService.adminFindById(id);
    }

    // 更新狀態（啟用 / 停權）
    @PutMapping("/{id}/status")
    public UserEntity updateStatus(@PathVariable Integer id,
                                @RequestParam Byte status) {
        return userService.adminUpdateUserStatus(id, status);
    }

    // 選擇性：後台可改部分資料（例如暱稱、電話）
    @PutMapping("/{id}")
    public UserEntity updateBasicInfo(@PathVariable Integer id,
                                      @RequestBody AdminUserUpdateRequest req) {
        return userService.adminUpdateUserBasicInfo(id, req);
    }
    @GetMapping("path")
    public String getMethodName(@RequestParam String param) {
        return new String();
    }

}