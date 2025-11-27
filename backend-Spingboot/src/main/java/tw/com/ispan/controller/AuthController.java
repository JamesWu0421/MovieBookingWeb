package tw.com.ispan.controller;

import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> body) {
        // TODO: replace with real authentication
        String username = body.get("username");
        String token = "fake-jwt-token-for-" + username;
        Map<String, String> res = new HashMap<>();
        res.put("token", token);
        return res;
    }
}
