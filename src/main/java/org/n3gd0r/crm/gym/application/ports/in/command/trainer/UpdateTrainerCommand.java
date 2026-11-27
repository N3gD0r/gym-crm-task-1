package org.n3gd0r.crm.gym.application.ports.in.command.trainer;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.n3gd0r.crm.gym.application.domain.model.TrainingType;

public record UpdateTrainerCommand(
        @Positive Long trainerId,
        @NotBlank String firstname,
        @NotBlank String lastname,
        @NotBlank @Min(10) String password,
        @NotNull TrainingType specialization,
        boolean active) {
}
