package tw.com.ispan.dto.request;

import lombok.Data;

@Data
public class OAuth2LoginRequest {
    private String providerId;
    private String providerName;
}
