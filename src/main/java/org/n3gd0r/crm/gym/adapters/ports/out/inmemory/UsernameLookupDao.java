package org.n3gd0r.crm.gym.adapters.ports.out.inmemory;

import org.n3gd0r.crm.gym.application.domain.model.User;
import org.n3gd0r.crm.gym.application.ports.out.user.UsernameLookupPort;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class UsernameLookupDao implements UsernameLookupPort {
    private final Map<Long, User> userTable;

    public UsernameLookupDao(@Qualifier("UserTable") Map<Long, User> userTable) {
        this.userTable = userTable;
    }

    @Override
    public boolean existsByName(String username) {
        return userTable.values().stream()
                .anyMatch(u -> u.getUsername().equals(username));
    }
}
