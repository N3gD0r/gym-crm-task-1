package org.n3gd0r.crm.gym.application.ports.in.usecase.user;

public interface UserCredentialGenerator {
    String password();

    String username(String firstname, String lastname);
}
