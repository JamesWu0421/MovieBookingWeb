package tw.com.ispan.jwt;

import java.io.IOException;
import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import tw.com.ispan.entity.UserEntity;
import tw.com.ispan.repository.UserRepository;
import tw.com.ispan.security.details.EmpDetails;
import tw.com.ispan.security.details.EmpDetailsService;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JsonWebTokenUtility jwtUtil;
    private final UserRepository userRepository;
    private final EmpDetailsService empDetailsService; // 新增這個

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        String uri = request.getRequestURI();
        String authHeader = request.getHeader("Authorization");
        System.out.println("JwtFilter URI = " + uri + ", Authorization = " + authHeader);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                String email = jwtUtil.validateEncryptedToken(token);
                System.out.println("JWT 驗證結果: email = " + email);

                if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                    // 1) 先當「前台會員」處理
                    UserEntity user = userRepository.findByEmail(email).orElse(null);

                    if (user != null) {
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                        user,
                                        null,
                                        Collections.emptyList() // 你原本的會員如果有 roles，也可以放進來
                                );

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        System.out.println("JWT 驗證成功（前台會員）: " + email);

                    } else {
                        // 2) 找不到 User，就當「後台員工」處理
                        try {
                            EmpDetails empDetails =
                                    (EmpDetails) empDetailsService.loadUserByUsername(email);

                            UsernamePasswordAuthenticationToken authentication =
                                    new UsernamePasswordAuthenticationToken(
                                            empDetails,
                                            null,
                                            empDetails.getAuthorities() // 這裡就是 ROLE_ADMIN / ROLE_MANAGER...
                                    );

                            SecurityContextHolder.getContext().setAuthentication(authentication);
                            System.out.println("JWT 驗證成功（後台員工）: " + email);

                        } catch (UsernameNotFoundException ex) {
                            System.out.println("JWT 驗證失敗: 找不到 user 或 emp " + email);
                        }
                    }
                } else {
                    System.out.println("JWT 驗證失敗: Token 無效或過期");
                }
            } catch (Exception e) {
                System.err.println("JWT 驗證失敗: " + e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
}
