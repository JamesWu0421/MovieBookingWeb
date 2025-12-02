package tw.com.ispan.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    // 檔案上傳實體路徑，可在 application.properties / yml 裡面設定 upload.path
    @Value("${upload.path:uploads}")
    private String uploadPath;

    /**
     * 配置 CORS - 允許前端訪問後端資源（包括圖片）
     */
//     @Override
//     public void addCorsMappings(CorsRegistry registry) {
//         registry.addMapping("/**") // 允許所有路徑
//                 .allowedOrigins(
//                         "http://localhost:5173",
//                         "http://localhost:5174", // Vite 開發伺服器
//                         "http://localhost:3000", // Vue CLI 開發伺服器
//                         "http://localhost:8080"  // 本機測試
//                 )
//                 .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD")
//                 .allowedHeaders("*")
//                 .allowCredentials(true)
//                 .maxAge(3600);
//     }

    /**
     * 配置靜態資源處理器
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 1) 原本的 /uploads/** → 實體資料夾 uploads
        //    訪問：http://localhost:8080/uploads/xxx.jpg
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath + "/");

        // 2) 新增：/images/** → classpath:/static/images/
        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/")
                .setCachePeriod(3600); // 快取 1 小時

        // 3) 其他靜態資源
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }

    /**
     * 額外的 CORS Filter Bean（更強力的 CORS 支援）
     */
//     @Bean
//     public CorsFilter corsFilter() {
//         UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//         CorsConfiguration config = new CorsConfiguration();

//         config.setAllowCredentials(true);
//         config.addAllowedOriginPattern("http://localhost:5173");
//         config.addAllowedOriginPattern("http://localhost:5174");
//         config.addAllowedOriginPattern("http://localhost:3000");
//         config.addAllowedOriginPattern("http://localhost:8080");
//         config.addAllowedHeader("*");
//         config.addAllowedMethod("*");

//         source.registerCorsConfiguration("/**", config);
//         return new CorsFilter(source);
//     }
}
