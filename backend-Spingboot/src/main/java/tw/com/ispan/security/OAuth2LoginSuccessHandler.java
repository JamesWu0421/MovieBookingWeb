package tw.com.ispan.security;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import tw.com.ispan.entity.UserEntity;
import tw.com.ispan.jwt.JsonWebTokenUtility;
import tw.com.ispan.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JsonWebTokenUtility jwtUtil;

    @Value("${frontend.url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oauthUser.getAttributes();

        String email = (String) attributes.get("email");
        // String name = (String) attributes.get("name");
        // String picture = (String) attributes.get("picture");
        String sub = (String) attributes.get("sub"); // Google 用戶唯一 ID

        UserEntity user = userRepository.findByEmail(email)
                .orElseGet(() -> createUser(email, attributes));

        // 更新第三方登入相關資訊（如果之前不是由 google 登入）
        if (!"google".equalsIgnoreCase(user.getProviderName())) {
            user.setProviderName("google");
            user.setProviderId(sub);
            user.setStatus((byte) 1);  // 啟用帳號
            userRepository.save(user);
        }

        // 產生 JWT Token，傳入 email 作為 subject
        String jwt = jwtUtil.createToken(user.getEmail());

        // 建立重定向 URL，帶上 Token 與 nickname，用於前端接收
        String targetUrl = UriComponentsBuilder.fromUriString(frontendUrl + "/auth/callback")
                .queryParam("token", jwt)
                .queryParam("nickname", user.getNickname())
                .build()
                .toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private UserEntity createUser(String email, Map<String, Object> attributes) {
        UserEntity newUser = new UserEntity();
        newUser.setEmail(email);
        newUser.setUsername(email);   // 你可改成 null 或其他規則，確保資料庫允許 nullable
        newUser.setNickname((String) attributes.get("name"));
        newUser.setAvatarUrl((String) attributes.get("picture"));
        newUser.setProviderName("google");
        newUser.setProviderId((String) attributes.get("sub"));
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setStatus((byte) 1);  // 啟用狀態
        return userRepository.save(newUser);
    }
}
