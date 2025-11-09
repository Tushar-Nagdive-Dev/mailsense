package org.inn.mailsense.users.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityBeans {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt is safe default. Not used for OAuth2 sign-in flows but useful if you support local accounts.
        return new BCryptPasswordEncoder(10);
    }
}
