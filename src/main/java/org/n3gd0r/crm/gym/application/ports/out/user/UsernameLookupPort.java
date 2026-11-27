package org.n3gd0r.crm.gym.application.ports.out.user;

public interface UsernameLookupPort {
    boolean existsByName(String username);
}
