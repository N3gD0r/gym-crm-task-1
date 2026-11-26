package org.n3gd0r.crm.gym.application.ports.in.command.trainee;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

public record CreateTraineeCommand(
        @NotBlank String firstname,
        @NotBlank String lastname,
        String address,
        @Past LocalDate dateOfBirth,
        boolean active) {
}
