package org.n3gd0r.crm.gym.application.domain.service;

import lombok.Setter;
import org.n3gd0r.crm.gym.application.ports.in.usecase.user.UserCredentialGenerator;
import org.n3gd0r.crm.gym.application.ports.out.user.UsernameLookupPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class CredentialGenerator implements UserCredentialGenerator {
    private final int PASSWORD_LENGTH;
    private final UsernameLookupPort usernameLookupPort;
    @Setter(onMethod_ = {@Autowired, @Qualifier("PasswordRandomizer")})
    private Random random;

    public CredentialGenerator(UsernameLookupPort usernameLookupPort, @Value("${security.password.length}") int passwordLength) {
        this.usernameLookupPort = usernameLookupPort;
        PASSWORD_LENGTH = passwordLength;
    }

    @Override
    public String password() {
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            password.append((char) (' ' + random.nextInt('~')));
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
