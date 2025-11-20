package tw.com.ispan.controller.emp;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tw.com.ispan.dto.request.admin.AdminLoginRequest;
import tw.com.ispan.dto.response.admin.AdminLoginResponse;
import tw.com.ispan.jwt.JsonWebTokenUtility;
import tw.com.ispan.security.details.EmpDetails;

@RestController
@RequestMapping("/api/admin/auth")
public class AdminAuthController {

    private final AuthenticationManager authenticationManager;
    private final JsonWebTokenUtility jsonWebTokenUtility;

    public AdminAuthController(AuthenticationManager authenticationManager,
                               JsonWebTokenUtility jsonWebTokenUtility) {
        this.authenticationManager = authenticationManager;
        this.jsonWebTokenUtility = jsonWebTokenUtility;
    }

    @PostMapping("/login")
    public ResponseEntity<AdminLoginResponse> adminLogin(@RequestBody AdminLoginRequest req) {

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword());

        Authentication auth = authenticationManager.authenticate(authToken);

        EmpDetails empDetails = (EmpDetails) auth.getPrincipal();

        // 用員工 email 當 subject 產 JWT（看你是用 createToken 還是 createEncryptedToken）
        String token = jsonWebTokenUtility.createEncryptedToken(empDetails.getUsername());

        return ResponseEntity.ok(new AdminLoginResponse(token));
    }
}
