package org.inn.mailsense.users.service;

import org.inn.mailsense.users.dtos.AuthResultDto;

public interface AuthService {

    String buildAuthorizationUrl(String provider, String redirectUri, String state);

    AuthResultDto handleGoogleCallback(String provider, String code, String redirectUri, String ip, String UserAgent) throws Exception;

    AuthResultDto signUp(String email, String password, String displayName, String ip, String userAgent) throws Exception;

    AuthResultDto login(String email, String password, String ip, String userAgent) throws Exception;

    AuthResultDto refresh(String refreshToken) throws Exception;

    void logout(String refreshToken) throws Exception;
}
