package org.inn.mailsense.users.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.inn.mailsense.users.dtos.AuthResultDto;
import org.inn.mailsense.users.dtos.LoginDto;
import org.inn.mailsense.users.dtos.SignupDto;
import org.inn.mailsense.users.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/auth")
public class AuthController {

    private final AuthService authService;

    @GetMapping("/oauth2/authorize")
    public ResponseEntity<Map<String, String>> authorize(@RequestParam String provider, @RequestParam String redirectUri, @RequestParam(required = false) String state) {
        String url = authService.buildAuthorizationUrl(provider, redirectUri, state);
        return ResponseEntity.status(302).location(URI.create(url)).build();
    }

//    @GetMapping("/oauth2/callback")
//    public ResponseEntity<AuthResultDto> callback(@RequestParam String provider, @RequestParam String code, @RequestParam String state, HttpServletRequest request) throws Exception {
//        String ip = request.getRemoteAddr();
//        String ua = request.getHeader("User-Agent");
//        AuthResultDto res = authService.handleGoogleCallback(provider, code, redirectUri(provider), ip, ua);
//        return ResponseEntity.ok(res);
//    }

    @GetMapping("/oauth2/callback")
    public void callback(
            @RequestParam String provider,
            @RequestParam String code,
            @RequestParam(required = false) String state,
            HttpServletResponse response) throws Exception {

        AuthResultDto res = authService.handleGoogleCallback(provider, code, redirectUri(provider), "0.0.0.0", "browser");

        // Persist user/session, then redirect back to Angular with status
        String redirectUrl = "http://localhost:4200/auth/success?accessToken=" + res.accessToken();
        response.sendRedirect(redirectUrl);
    }


    // helper to build the redirectUri expected earlier (should match url used to build authorization)
    private String redirectUri(String provider) {
        // In our OAuth2Client we used redirectUri passed from client; for simplicity assume app base + callback
        return "http://localhost:8080/api/auth/oauth2/callback";
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResultDto> signup(@RequestBody SignupDto dto, HttpServletRequest request) throws Exception {
        String ip = request.getRemoteAddr();
        String ua = request.getHeader("User-Agent");
        AuthResultDto res = authService.signUp(dto.email(), dto.password(), dto.displayName(), ip, ua);
        return ResponseEntity.status(201).body(res);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResultDto> login(@RequestBody LoginDto dto, HttpServletRequest request) throws Exception {
        String ip = request.getRemoteAddr();
        String ua = request.getHeader("User-Agent");
        AuthResultDto res = authService.login(dto.email(), dto.password(), ip, ua);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResultDto> refresh(@RequestBody Map<String, String> body) throws Exception {
        String refresh = body.get("refreshToken");
        AuthResultDto res = authService.refresh(refresh);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody Map<String, String> body) throws Exception {
        String refresh = body.get("refreshToken");
        authService.logout(refresh);
        return ResponseEntity.noContent().build();
    }
}
