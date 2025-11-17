package tw.com.ispan.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import tw.com.ispan.dto.request.UserChangePwdRequest;
import tw.com.ispan.dto.request.UserLoginRequest;
import tw.com.ispan.dto.request.UserRegisterRequest;
import tw.com.ispan.dto.request.UserUpdateRequest;
import tw.com.ispan.entity.UserEntity;
import tw.com.ispan.jwt.JsonWebTokenUtility;
import tw.com.ispan.service.UserService;

@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private JsonWebTokenUtility jwtUtility;
    //註冊
    @PostMapping("/auth/register")
public ResponseEntity<String> register(@RequestBody UserRegisterRequest req) {
    try {
        userService.registerUser(req.getUsername(), req.getPassword(), req.getEmail(), req.getPhoneNumber(), req.getNickname() ,req.getGender(),req.getBirthday() , req.getAvatarUrl());
        return ResponseEntity.ok("註冊成功，請至信箱點擊驗證連結啟用帳號");
    } catch (RuntimeException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}

    
@PostMapping("/auth/login")
public ResponseEntity<Map<String, Object>> login(@RequestBody UserLoginRequest req) {
    try {
        UserEntity user = userService.loginUser(req.getUsername(), req.getPassword());
        String token = jwtUtility.createToken(user.getUsername());
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("message", "登入成功");
        return ResponseEntity.ok(response);
    } catch (RuntimeException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
    }
}
    


//  @PostMapping("/ajax/secure/login")
// 	public String login(HttpSession session, @RequestBody String json) {
// 		JSONObject responseJson = new JSONObject();
// //接收資料
// 		JSONObject obj = new JSONObject(json);
// 		String username = obj.isNull("username") ? null : obj.getString("username");
// 		String password = obj.isNull("password") ? null : obj.getString("password");

// //驗證資料
// 		if(username==null || username.length()==0 || password==null || password.length()==0) {
// 			responseJson.put("success", false);
// 			responseJson.put("message", "請輸入帳號與密碼");
// 			return responseJson.toString();
// 		}

// //呼叫model
// 		UserEntity bean = UserService.loginUser(username, password);

// //回傳執行結果
// 		if(bean==null) {
// 			responseJson.put("success", false);
// 			responseJson.put("message", "登入失敗");
// 		} else {
// 			responseJson.put("success", true);
// 			responseJson.put("message", "登入成功");
// 			session.setAttribute("user", bean);
// 			// jwt begin
// 			String birth = DatetimeConverter.toString(bean.getBirth(), "yyyy-MM-dd");
// 			JSONObject user = new JSONObject()
// 					.put("custid", bean.getCustid())
// 					.put("email", bean.getEmail())
// 					.put("birth", birth);
// 			String token = jwtUtility.createToken(user.toString());
			
// 			responseJson.put("token", token);
// 			responseJson.put("email", bean.getEmail());
// 			// jwt finish
// 		}
// 		return responseJson.toString();
// 	}
// }




    @GetMapping("/auth/verify")
    public ResponseEntity<String> verify(@RequestParam("code") String code) {
        Optional<UserEntity> optUser = userService.findByVerificationCode(code);
        if (optUser.isEmpty()) {
            return ResponseEntity.badRequest().body("驗證碼無效");
        }
        UserEntity user = optUser.get();
        user.setStatus((byte)1); // 啟用帳號
        user.setVerificationCode(null);
        userService.save(user);
        System.out.println("Updated user status: " + user.getStatus());
        return ResponseEntity.ok("帳號已啟用，請重新登入");
    }





    // 用戶請求發送重置密碼信
    @PostMapping("/auth/forgotpassword")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        try {
            userService.sendResetPasswordEmail(email);
            return ResponseEntity.ok("密碼重置信已發送，請查收信箱");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 用戶依帶的 token 重置密碼
    @PostMapping("/auth/reset_password")
    public ResponseEntity<String> resetPassword(@RequestParam String token, @RequestBody String newPassword) {
        try {
            userService.resetPassword(token, newPassword);
            return ResponseEntity.ok("密碼重置成功，請重新登入");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }



    // 用戶登出
    @PostMapping("/user/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("登出成功");
    }


  // 用戶取得個人資料
@GetMapping("/user/profile")
public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String authorizationHeader) {
    String token = authorizationHeader.substring(7);
    String username = jwtUtility.validateToken(token);
    if (username == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token 無效");
    }
    UserEntity user = userService.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("用戶不存在"));
    return ResponseEntity.ok(user);
}

  //   用戶更新個人資料
    @PutMapping("/user/profile")
    public ResponseEntity<?> updateProfile(@RequestBody UserUpdateRequest req,
                                           @RequestHeader("Authorization") String authorizationHeader) {
        // 取出 token
        String token = authorizationHeader.substring(7);

        // 解析 token 得到 username
        String username = jwtUtility.validateToken(token);
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token 無效");
        }
        // 呼叫 service 根據 username 更新資料（防止用戶改別人資料）
        try {
            UserEntity updatedUser = userService.updateUserProfile(username, req);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

     //用戶更新個人密碼
    @PutMapping("/user/change_password")
public ResponseEntity<String> changePassword(@RequestBody UserChangePwdRequest req, @RequestHeader("Authorization") String auth) {
    // 取得 token 並驗證取得 username
    String token = auth.substring(7);
    String username = jwtUtility.validateToken(token);
    if (username == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token 無效");
    }
    try {
        userService.changePassword(username, req.getOldPassword(), req.getNewPassword());
        return ResponseEntity.ok("密碼修改成功");
    } catch (RuntimeException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}



//      @PostMapping("/oauth2/login")
// public ResponseEntity<?> oauth2Login(@RequestBody OAuth2LoginRequest req) {
//     UserEntity user = userService.loginOrCreateThirdPartyUser(
//             req.getProviderId(),   // 例如 Google 回傳的用戶ID 
//             req.getProviderName() // 例如 "google"
//     );

//     String token = jwtUtility.createToken(user.getUsername());
//     Map<String, Object> resp = new HashMap<>();
//     resp.put("token", token);
//     resp.put("user", user);
//     return ResponseEntity.ok(resp);
// }

}
