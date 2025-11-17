package tw.com.ispan.jwt;

import java.io.IOException;
import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import tw.com.ispan.entity.UserEntity;
import tw.com.ispan.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JsonWebTokenUtility jwtUtil;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        
        String authHeader = request.getHeader("Authorization");
        
        // 檢查是否有 Bearer Token
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            
            try {
                // ✅ validateToken 會回傳 email（成功）或 null（失敗）
                String email = jwtUtil.validateToken(token);
                System.out.println("JWT 驗證結果: email = " + email);
                
                if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    // 從資料庫取得用戶
                    UserEntity user = userRepository.findByEmail(email).orElse(null);
                    
                    if (user != null) {
                        // 建立 Authentication 並設定到 SecurityContext
                        UsernamePasswordAuthenticationToken authentication = 
                            new UsernamePasswordAuthenticationToken(
                                user, null, Collections.emptyList()
                            );
                        
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        System.out.println("JWT 驗證成功，用戶: " + email);
                    } else {
                        System.out.println("JWT 驗證失敗: 找不到用戶 " + email);
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
