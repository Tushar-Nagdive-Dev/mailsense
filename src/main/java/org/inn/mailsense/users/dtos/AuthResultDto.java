package org.inn.mailsense.users.dtos;

import java.util.UUID;

public record AuthResultDto(
        String accessToken,
        String refreshToken,
        UUID userId,
        String email
) {}
