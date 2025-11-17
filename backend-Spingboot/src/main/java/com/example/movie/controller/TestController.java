package com.example.movie.controller;

import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*")
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
        info.put("endpoints", new String[]{
            "/api/test",
            "/api/test/hello",
            "/api/test/info",
            "/api/events",
            "/api/reports/dashboard"
        });
        return info;
    }
}
