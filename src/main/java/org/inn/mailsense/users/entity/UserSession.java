package org.inn.mailsense.users.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "user_session")
public class UserSession {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Column(name = "refresh_token_hash", nullable = false)
    private String refreshTokenHash;

    private String ipAddress;

    private String userAgent;

    private OffsetDateTime createdAt = OffsetDateTime.now();

    private OffsetDateTime expiresAt;
}
