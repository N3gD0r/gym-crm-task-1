package org.n3gd0r.crm.gym.application.ports.in.command.trainee;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record UpdateTraineeCommand(
        @Positive Long traineeId,
        @NotBlank String firstname,
        @NotBlank String lastname,
        @NotBlank String password,
        String address,
        @Past LocalDate dateOfBirth,
        boolean active) {
}
