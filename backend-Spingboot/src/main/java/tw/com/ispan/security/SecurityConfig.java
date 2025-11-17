package tw.com.ispan.security;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.http.HttpServletResponse;
import tw.com.ispan.jwt.JsonWebTokenInterceptor;

@Configuration
public class SecurityConfig implements WebMvcConfigurer {

    @Autowired
    private JsonWebTokenInterceptor jsonWebTokenInterceptor;

    @Value("${file.upload.path:uploads/avatars/}")
    private String uploadPath;

    @Autowired
    private OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())

            // OAuth2 流程需要 session 支援，完成後仍可用 JWT 作為業務驗證
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            )

            .authorizeHttpRequests(auth -> auth
                // 1) 先放行 CORS 預檢，避免 preflight 被攔截
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // 2) 公開端點全部放行
                .requestMatchers(
                    "/", 
                    "/index.html",
                    "/error",
                    "/api/auth/**",      // 註冊/登入/驗證/重置密碼
                    "/api/upload/**",    // 檔案上傳
                    "/uploads/**",       // 靜態資源
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/favicon.ico",
                    "/oauth2/**",        // OAuth2 授權端點
                    "/login/oauth2/**"   // OAuth2 回調端點
                ).permitAll()

                // 3) 讓 /api/user/** 由你的 Interceptor 驗證，不由 Spring Security 攔截
                .requestMatchers("/api/user/**").permitAll()

                // 4) 其他才需要 Spring Security 認證
                .anyRequest().authenticated()
            )

            // 不用表單登入
            .formLogin(form -> form.disable())

            // OAuth2 登入成功邏輯（在 success handler 產出 JWT 並重定向回前端）
            .oauth2Login(oauth2 -> oauth2
                .successHandler(oAuth2LoginSuccessHandler)
                .failureUrl("http://localhost:5173/login?error=oauth2_failed")
            )

            // 未認證時，API 回 401 JSON，不做任何重定向（避免 preflight 302）
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint((request, response, authException) -> {
                    String uri = request.getRequestURI();

                    // OAuth2 流程可重定向，其它 API 一律回 401 JSON
                    if (uri.startsWith("/oauth2/") || uri.startsWith("/login/oauth2/")) {
                        response.sendRedirect("/login");
                        return;
                    }

                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"error\":\"Unauthorized\",\"message\":\"請先登入\"}");
                })
            )

            .logout(logout -> logout.permitAll());

        return http.build();
    }

    // JWT 業務驗證放在 Interceptor 中處理
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jsonWebTokenInterceptor)
                .addPathPatterns("/api/user/**")
                .excludePathPatterns("/api/user/logout"); // logout 端點可視需求放行
    }

    // CORS 設定（讓 Spring Security 統一處理，不要在 Interceptor 裡手動設 header）
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins("http://localhost:5173", "http://127.0.0.1:5173")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
            .allowedHeaders("*")
            .exposedHeaders("Authorization")
            .allowCredentials(true)
            .maxAge(3600);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        String absolutePath = uploadDir.getAbsolutePath();
        if (!absolutePath.endsWith(File.separator)) {
            absolutePath += File.separator;
        }
        registry.addResourceHandler("/uploads/avatars/**")
                .addResourceLocations("file:" + absolutePath);
    }
}
