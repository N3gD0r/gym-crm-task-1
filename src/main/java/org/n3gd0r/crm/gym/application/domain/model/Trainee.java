package org.n3gd0r.crm.gym.application.domain.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
public class Trainee extends User {
    private String address;
    private LocalDate dateOfBirth;

    public Trainee(String firstname, String lastname, String address, LocalDate dateOfBirth, boolean active) {
        super(firstname, lastname, active);
        this.address = address;
        this.dateOfBirth = dateOfBirth;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Trainee trainee)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(address, trainee.address) && Objects.equals(dateOfBirth, trainee.dateOfBirth);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), address, dateOfBirth);
    }
}
