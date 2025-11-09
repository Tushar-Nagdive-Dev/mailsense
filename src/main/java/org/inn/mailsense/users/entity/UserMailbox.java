package org.inn.mailsense.users.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "user_mailboxes")
public class UserMailbox {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;

    private String emailAddress;

    private String provider;

    private String providerUserId;

    @Column(columnDefinition = "text")
    private String oauthAccessTokenEncrypted;

    @Column(columnDefinition = "text")
    private String oauthRefreshTokenEncrypted;

    private String oauthScope;

    private OffsetDateTime tokenExpiry;

    private Boolean isPrimary = false;

    private OffsetDateTime connectedAt = OffsetDateTime.now();

    private String status;
}
