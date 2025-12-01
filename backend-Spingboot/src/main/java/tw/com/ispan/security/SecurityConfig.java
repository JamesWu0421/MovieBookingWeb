package tw.com.ispan.security;

import java.io.File;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.http.HttpServletResponse;
import tw.com.ispan.jwt.JsonWebTokenInterceptor;
import tw.com.ispan.jwt.JwtAuthenticationFilter;

@EnableMethodSecurity
@Configuration
public class SecurityConfig implements WebMvcConfigurer {

        @Autowired
        private JsonWebTokenInterceptor jsonWebTokenInterceptor;

        @Value("${file.upload.path:uploads/avatars/}")
        private String uploadPath;

        @Autowired
        private OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

        @Autowired
        private JwtAuthenticationFilter jwtAuthenticationFilter;

        @Value("${frontend.url}")
        private String frontendUrl;

        @Value("${admin.url}")
        private String adminUrl;

        @Value("${backend.url}")
        private String backendUrl;

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
                return configuration.getAuthenticationManager();
        }

        @Bean
        PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        // @Bean
        // @SuppressWarnings("deprecation")
        // public PasswordEncoder passwordEncoder() {
        // return
        // org.springframework.security.crypto.password.NoOpPasswordEncoder.getInstance();
        // }

        @Bean
        SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(csrf -> csrf.disable())
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                                .authorizeHttpRequests(auth -> auth
                                                // 1) 先放行 CORS 預檢，避免 preflight 被攔截
                                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                                                // 2) 公開端點全部放行
                                                .requestMatchers(
                                                                "/",
                                                                "/index.html",
                                                                "/error",
                                                                "/api/auth/**", // 註冊/登入/驗證/重置密碼
                                                                "/api/upload/**", // 檔案上傳
                                                                "/uploads/**", // 靜態資源
                                                                "/css/**",
                                                                "/api/public/**", // 公開資料
                                                                "/api/public/events/**",
                                                                "/js/**",
                                                                "/api/admin/auth/login", // 管理員
                                                                "/images/**",
                                                                "/favicon.ico",
                                                                "/oauth2/**", // OAuth2 授權端點
                                                                "/login/oauth2/**", // OAuth2 回調端點
                                                                "/api/admin/movies**"
                                                ).permitAll()

                                                // 3) 讓 /api/user/** 由你的 Interceptor 驗證，不由 Spring Security 攔截
                                                .requestMatchers("/api/user/**", "/api/notifications/**","/api/movies/**").permitAll()
                                                .requestMatchers("/api/admin/**").authenticated()

                                                // 4) 其他才需要 Spring Security 認證
                                                .anyRequest().authenticated())

                                // 不用表單登入
                                .formLogin(form -> form.disable())

                                // OAuth2 登入成功邏輯（在 success handler 產出 JWT 並重定向回前端）
                                .oauth2Login(oauth2 -> oauth2
                                                .successHandler(oAuth2LoginSuccessHandler)
                                                .failureUrl(frontendUrl + "/login?error=oauth2_failed"))
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                                // Frame Options（支援 H2 Console）
                                .headers(headers -> headers
                                                .frameOptions(frame -> frame.disable()))
                                // 未認證時，API 回 401 JSON，不做任何重定向（避免 preflight 302）
                                .exceptionHandling(exception -> exception
                                                .authenticationEntryPoint((request, response, authException) -> {
                                                        String uri = request.getRequestURI();

                                                        // OAuth2 流程可重定向，其它 API 一律回 401 JSON
                                                        if (uri.startsWith("/oauth2/")
                                                                        || uri.startsWith("/login/oauth2/")) {
                                                                response.sendRedirect("/login");
                                                                return;
                                                        }

                                                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                                        response.setContentType("application/json;charset=UTF-8");
                                                        response.getWriter().write(
                                                                        "{\"error\":\"Unauthorized\",\"message\":\"請先登入\"}");
                                                }))

                                .logout(logout -> logout.permitAll());
                http.addFilterBefore(jwtAuthenticationFilter,
                                org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);
                return http.build();
        }

        // JWT 業務驗證放在 Interceptor 中處理
        @Override
        public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(jsonWebTokenInterceptor)
                                .addPathPatterns("/api/user/**")
                                .excludePathPatterns("/api/user/logout"); // logout 端點可視需求放行
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

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();

                // 🔧 修正：指定具體的前端地址
                configuration.setAllowedOriginPatterns(Arrays.asList(
                                "http://localhost:5173",
                                "http://localhost:5174",
                                "http://127.0.0.1:5173",
                                "http://127.0.0.1:5174",
                                frontendUrl,
                                adminUrl));

                // 允許的 HTTP 方法
                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

                // 允許的 Headers
                configuration.setAllowedHeaders(Arrays.asList("*"));


                // 🔧 修正：開發階段可以關閉憑證
                configuration.setAllowCredentials(true); // 允許攜帶憑證(Cookie/Token)

                // 預檢請求的有效期
                configuration.setMaxAge(3600L);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);

                return source;
        }

}
