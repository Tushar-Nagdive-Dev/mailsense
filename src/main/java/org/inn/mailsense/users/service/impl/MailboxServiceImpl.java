package org.inn.mailsense.users.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.inn.mailsense.users.config.OAuth2Client;
import org.inn.mailsense.users.entity.UserMailbox;
import org.inn.mailsense.users.repo.UserMailboxRepository;
import org.inn.mailsense.users.service.MailboxService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailboxServiceImpl implements MailboxService {

    private final OAuth2Client oAuth2Client;

    private final UserMailboxRepository userMailboxRepository;

    @Override
    public String buildAuthorizationUrl(String provider, String redirectUri, String state) {
        log.info("@class MailboxServiceImpl @method buildAuthorizationUrl provider : {}, redirectUri : {}, state : {}", provider, redirectUri, state);
        if ("google".equalsIgnoreCase(provider)) {
            return oAuth2Client.buildGoogleAuthorizationUrl(redirectUri, state);
        }
        throw new IllegalArgumentException("Unsupported provider: " + provider);
    }

    @Override
    public List<UserMailbox> listMailboxes(UUID userId) {
        log.info("@class MailboxServiceImpl @method listMailboxes userId : {}", userId);
        return userMailboxRepository.findByUserId(userId);
    }

    @Override
    public void disconnect(UUID userId, UUID mailboxId) {
        log.info("@class MailboxServiceImpl  @method disconnect userId : {}, mailboxId : {}", userId, mailboxId);
        userMailboxRepository.findById(mailboxId).ifPresent(m -> {
            if (m.getUser().getId().equals(userId)) {
                m.setStatus("DISCONNECTED");
                m.setOauthAccessTokenEncrypted(null);
                m.setOauthRefreshTokenEncrypted(null);
                userMailboxRepository.save(m);
            } else {
                throw new IllegalArgumentException("Mailbox does not belong to user");
            }
        });
    }
}
