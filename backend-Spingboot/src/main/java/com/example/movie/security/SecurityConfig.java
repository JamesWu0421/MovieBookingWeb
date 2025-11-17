package com.example.movie.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 停用 CSRF（開發階段，前後端分離）
                .csrf(csrf -> csrf.disable())

                // CORS 設定
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 授權規則（使用新的 Lambda DSL）
                .authorizeHttpRequests(auth -> auth
                        // 允許所有 API 訪問（開發階段）
                        .requestMatchers("/api/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        // 其他請求也暫時開放（開發階段）
                        .anyRequest().permitAll() // 改這裡！
                )

                // Session 管理
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Frame Options（支援 H2 Console）
                .headers(headers -> headers
                        .frameOptions(frame -> frame.disable()));

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 🔧 修正：指定具體的前端地址
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:5173", // Vue 開發伺服器
                "http://localhost:5174", // 如果有第二個前端
                "http://127.0.0.1:5173",
                "http://127.0.0.1:5174"));

        // 允許的 HTTP 方法
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        // 允許的 Headers
        configuration.setAllowedHeaders(Arrays.asList("*"));

        // 🔧 修正：開發階段可以關閉憑證
        configuration.setAllowCredentials(false); // 改這裡！

        // 預檢請求的有效期
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}