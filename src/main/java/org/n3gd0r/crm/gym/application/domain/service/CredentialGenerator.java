package org.n3gd0r.crm.gym.application.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.n3gd0r.crm.gym.application.ports.in.usecase.user.UserCredentialGenerator;
import org.n3gd0r.crm.gym.application.ports.out.user.UsernameLookupPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
@RequiredArgsConstructor
public class CredentialGenerator implements UserCredentialGenerator {
    private static final int PASSWORD_LENGTH = 10;
    private static final String PASSWORD_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private final UsernameLookupPort usernameLookupPort;
    @Setter(onMethod_ = @Autowired)
    private SecureRandom random;

    @Override
    public String password() {
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            password.append(PASSWORD_CHARACTERS.charAt(random.nextInt(PASSWORD_CHARACTERS.length())));
        }
        return password.toString();
    }

    @Override
    public String username(String firstname, String lastname) {
        String baseUsername = "%s.%s".formatted(firstname, lastname);
        String username = baseUsername;
        for (int i = 1; usernameLookupPort.existsByName(username); i++) {
            username = "%s%d".formatted(baseUsername, i);
        }
        return username;
    }
}
