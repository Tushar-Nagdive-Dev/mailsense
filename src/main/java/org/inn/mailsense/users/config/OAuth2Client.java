package org.inn.mailsense.users.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Component
public class OAuth2Client {

    private final WebClient webClient = WebClient.create();

    @Value("${oauth.base-url}")
    private String baseAppUrl;

    @Value("${oauth.providers.google.auth-uri}")
    private String googleAuthUri;

    @Value("${oauth.providers.google.token-uri}")
    private String googleTokenUri;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;

    @Value("${oauth.providers.google.userinfo-uri}")
    private String googleUserInfoUri;

    // Build authorization URL for Google (example)
    public String buildGoogleAuthorizationUrl(String redirectUri, String state) {
        return UriComponentsBuilder.fromUriString(googleAuthUri)
                .queryParam("client_id", googleClientId)
                .queryParam("response_type", "code")
                .queryParam("scope", "openid email profile https://www.googleapis.com/auth/gmail.readonly")
                .queryParam("access_type", "offline")
                .queryParam("prompt", "consent")
                .queryParam("redirect_uri", redirectUri)
                .queryParam("state", state)
                .build().toUriString();
    }

    //Exchange code for tokens (blocking for simplicity)
    public Map<String, Object> exchangeGoogleCode(String code, String redirectUri) {
        StringBuilder body = new StringBuilder("");
        body.append("code=").append(code)
                .append("&client_id=").append(googleClientId)
                .append("&client_secret=").append(googleClientSecret)
                .append("&redirect_uri=").append(redirectUri)
                .append("&grant_type=authorization_code");
        return webClient.post()
                .uri(googleTokenUri)
                .bodyValue(body.toString())
                .header("Content-Type", "application/x-www-form-urlencoded")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();
    }

    // Get userinfo
    public Map<String, Object> fetchGoogleUserInfo(String accessToken) {
        return webClient.get()
                .uri(googleUserInfoUri)
                .headers(h -> h.setBearerAuth(accessToken))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();
    }

}
