package tw.com.ispan.controller;

import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 測試控制器 - 僅用於開發階段
 * TODO: 正式上線前移除此控制器
 */

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping
    public Map<String, Object> test() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Backend is running!");
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello from Movie Backend!";
    }

    @GetMapping("/info")
    public Map<String, Object> info() {
        Map<String, Object> info = new HashMap<>();
        info.put("application", "Movie Ticket System");
        info.put("version", "1.0.0");
        info.put("endpoints", new String[] {
                "/api/test",
                "/api/test/hello",
                "/api/test/info",
                "/api/events",
                "/api/reports/dashboard"
        });
        return info;
    }
}
