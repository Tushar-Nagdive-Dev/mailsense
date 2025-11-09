package org.inn.mailsense.users.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.misc.NotNull;
import org.inn.mailsense.users.config.AesGcmEncryptor;
import org.inn.mailsense.users.config.JwtUtil;
import org.inn.mailsense.users.config.OAuth2Client;
import org.inn.mailsense.users.dtos.AuthResultDto;
import org.inn.mailsense.users.entity.UserMailbox;
import org.inn.mailsense.users.entity.UserSession;
import org.inn.mailsense.users.entity.Users;
import org.inn.mailsense.users.repo.UserMailboxRepository;
import org.inn.mailsense.users.repo.UserSessionRepository;
import org.inn.mailsense.users.repo.UsersRepository;
import org.inn.mailsense.users.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final OAuth2Client oAuth2Client;

    private final UsersRepository usersRepository;

    private final UserMailboxRepository userMailboxRepository;

    private final JwtUtil jwtUtil;

    private final AesGcmEncryptor encryptor;

    private final UserSessionRepository userSessionRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public String buildAuthorizationUrl(String provider, String redirectUri, String state) {
        log.info("@class AuthServiceImpl @method buildAuthorizationUrl");
        if("google".equalsIgnoreCase(provider)){
            log.debug("Google Auth provider is enabled");
            return oAuth2Client.buildGoogleAuthorizationUrl(redirectUri, state);
        }

        throw new IllegalArgumentException("Unsupported provider " + provider);
    }

    @Transactional
    public AuthResultDto handleGoogleCallback(String provider, String code, String redirectUri, String ip, String userAgent) throws Exception{
        log.info("@class AuthServiceImpl @method handleGoogleCallback code : {}, redirectUri : {}", code, redirectUri);

        if (!"google".equalsIgnoreCase(provider)) throw new IllegalArgumentException("Only google supported in this example");

        Map<String, Object> tokenResponse = oAuth2Client.exchangeGoogleCode(code, redirectUri);
        String accessToken = (String) tokenResponse.get("access_token");
        String refreshToken = (String) tokenResponse.get("refresh_token");
        Integer expiresIn = (Integer) tokenResponse.get("expires_in");

        Map<String, Object> userInfo = oAuth2Client.fetchGoogleUserInfo(accessToken);
        String email = (String) userInfo.get("email");
        String sub = (String) userInfo.get("sub");
        String name = (String) userInfo.get("name");

        Users user = usersRepository.findByEmail(email).orElseGet(() -> {
            Users users = new Users();
            users.setEmail(email);
            users.setDisplayName(name);
            log.debug("@class AuthServiceImpl @method handleGoogleCallback users : {}", users);
            return usersRepository.save(users);
        });

        // Create or update mailbox
        UserMailbox mailbox = userMailboxRepository.findByUserIdAndEmailAddress(user.getId(), email).orElseGet(() -> {
            UserMailbox userMailbox = new UserMailbox();
            userMailbox.setUser(user);
            userMailbox.setEmailAddress(email);
            userMailbox.setProvider("GMAIL");
            log.debug("@class AuthServiceImpl @method handleGoogleCallback mailbox : {}", userMailbox);
            return userMailbox;
        });

        mailbox.setProviderUserId(sub);
        mailbox.setOauthScope("openid email profile https://www.googleapis.com/auth/gmail.readonly");
        mailbox.setTokenExpiry(OffsetDateTime.now().plusSeconds(expiresIn != null ? expiresIn : 3600));
        mailbox.setOauthAccessTokenEncrypted(encryptor.encrypt(accessToken));
        if(refreshToken != null){
            mailbox.setOauthRefreshTokenEncrypted(encryptor.encrypt(refreshToken));
        }
        userMailboxRepository.save(mailbox);

        // Issue app JWT (Subject = user id)
        String accessJwt = jwtUtil.generateToken(user.getId().toString());

        // create refresh token for app session (random UUID)
        String appRefresh = UUID.randomUUID().toString();
        String hashed = passwordEncoder.encode(appRefresh);
        UserSession session = new UserSession();
        session.setUser(user);
        session.setRefreshTokenHash(hashed);
        session.setIpAddress(ip);
        session.setUserAgent(userAgent);
        session.setExpiresAt(OffsetDateTime.now().plusDays(30));
        userSessionRepository.save(session);

        // For simplicity return token in AuthResultDTO (you'd also issue app refresh token and set session row)
        return new AuthResultDto (
                accessJwt,
                refreshToken,
                user.getId(),
                email
        );
    }

    @Override
    @Transactional
    public AuthResultDto signUp(String email, String password, String displayName, String ip, String userAngent) {
        log.info("@class AuthServiceImpl @method signUp email : {}", email);
        Optional<Users> existing =  usersRepository.findByEmail(email);
        if(existing.isPresent()) throw new IllegalArgumentException("Email already exists");

        Users user = new Users();
        user.setEmail(email);
        user.setDisplayName(displayName);
        user.setPasswordHash(passwordEncoder.encode(password));
        usersRepository.save(user);

        return getAuthResultDto(ip, userAngent, user);
    }

    @Override
    @Transactional
    public AuthResultDto login(String email, String password, String ip, String userAgent) {
        log.info("@class AuthServiceImpl @method login email : {}, ip : {}, userAgent : {}", email, ip, userAgent);
        Users user = usersRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
        if (user.getPasswordHash() == null || !passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        return getAuthResultDto(ip, userAgent, user);
    }

    @NotNull
    private AuthResultDto getAuthResultDto(String ip, String userAgent, Users user) {
        String accessJwt = jwtUtil.generateToken(user.getId().toString());
        String appRefresh = UUID.randomUUID().toString();
        String hashed = passwordEncoder.encode(appRefresh);
        UserSession session = new UserSession();
        session.setUser(user);
        session.setRefreshTokenHash(hashed);
        session.setIpAddress(ip);
        session.setUserAgent(userAgent);
        session.setExpiresAt(OffsetDateTime.now().plusDays(30));
        userSessionRepository.save(session);

        return new AuthResultDto(accessJwt, appRefresh, user.getId(), user.getEmail());
    }

    @Override
    @Transactional
    public AuthResultDto refresh(String refreshToken) throws Exception {
        // find session by matching hash - iterate sessions and match BCrypt (cannot query by hash)
        // For performance you can store sessions per user and check candidate; here we check all (or add extra index if you store token id)
        for (UserSession s : userSessionRepository.findAll()) {
            if (passwordEncoder.matches(refreshToken, s.getRefreshTokenHash())) {
                if (s.getExpiresAt() != null && s.getExpiresAt().isBefore(OffsetDateTime.now())) {
                    throw new IllegalArgumentException("Refresh token expired");
                }
                String newAccess = jwtUtil.generateToken(s.getUser().getId().toString());
                String newRefresh = UUID.randomUUID().toString();
                s.setRefreshTokenHash(passwordEncoder.encode(newRefresh));
                s.setExpiresAt(OffsetDateTime.now().plusDays(30));
                userSessionRepository.save(s);

                return new AuthResultDto(newAccess, newRefresh, s.getUser().getId(), s.getUser().getEmail());
            }
        }
        throw new IllegalArgumentException("Invalid refresh token");
    }

    @Override
    @Transactional
    public void logout(String refreshToken) throws Exception {
        for (UserSession s : userSessionRepository.findAll()) {
            if (passwordEncoder.matches(refreshToken, s.getRefreshTokenHash())) {
                userSessionRepository.delete(s);
                return;
            }
        }
    }
}
