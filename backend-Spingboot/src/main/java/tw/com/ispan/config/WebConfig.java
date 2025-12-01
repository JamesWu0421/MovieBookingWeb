package tw.com.ispan.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web 配置類
 * 解決 CORS 跨域問題和靜態資源訪問
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 配置 CORS - 允許前端訪問後端資源（包括圖片）
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // 允許所有路徑
                .allowedOrigins(
                    "http://localhost:5175",  // Vite 開發伺服器
                    "http://localhost:3000",  // Vue CLI 開發伺服器
                    "http://localhost:8080"   // 本機測試
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD")  // 允許所有常用方法
                .allowedHeaders("*")  // 允許所有 headers
                .allowCredentials(true)  // 允許攜帶憑證
                .maxAge(3600);  // 預檢請求快取時間（秒）
    }

    /**
     * 配置靜態資源處理器
     * 將 /images/** 路徑映射到 classpath:/static/images/
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置圖片資源
        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/")
                .setCachePeriod(3600);  // 快取 1 小時
        
        // 如果還有其他靜態資源，也可以在這裡配置
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }

    /**
     * 額外的 CORS Filter Bean（更強力的 CORS 支援）
     * 如果上面的配置還不夠，這個會確保所有請求都支援 CORS
     */
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*");  // 允許所有來源（開發時使用）
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}