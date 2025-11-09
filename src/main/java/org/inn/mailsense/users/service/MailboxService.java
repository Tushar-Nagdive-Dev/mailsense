package org.inn.mailsense.users.service;

import org.inn.mailsense.users.entity.UserMailbox;

import java.util.List;
import java.util.UUID;

public interface MailboxService {

    String buildAuthorizationUrl(String provider, String redirectUri, String state);

    List<UserMailbox> listMailboxes(UUID userId);

    void disconnect(UUID userId, UUID mailboxId);
}
