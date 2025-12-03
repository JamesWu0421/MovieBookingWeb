package tw.com.ispan.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

        // 檔案上傳實體路徑，可在 application.properties / yml 裡面設定 upload.path
        @Value("${upload.path:uploads}")
        private String uploadPath;

        /**
         * 配置靜態資源處理器
         */
        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
                // 1) 原本的 /uploads/** → 實體資料夾 uploads
                // 訪問：http://localhost:8080/uploads/xxx.jpg
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

}
