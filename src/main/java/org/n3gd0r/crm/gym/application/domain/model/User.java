package org.n3gd0r.crm.gym.application.domain.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class User {
    private Long userId;
    private String firstname;
    private String lastname;
    private String username;
    private String password;
    private boolean active;

    protected User(String firstName, String lastName, boolean active) {
        this.firstname = firstName;
        this.lastname = lastName;
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof User user)) return false;
        return Objects.equals(userId, user.userId) &&
                Objects.equals(firstname, user.firstname) &&
                Objects.equals(lastname, user.lastname) &&
                Objects.equals(username, user.username) &&
                Objects.equals(password, user.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, firstname, lastname, username, password);
    }
}
