package org.inn.mailsense.users.dtos;

public record SignupDto(
        String email,
        String password,
        String displayName
) {}
