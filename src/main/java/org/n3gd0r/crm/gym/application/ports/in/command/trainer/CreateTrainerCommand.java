package org.n3gd0r.crm.gym.application.ports.in.command.trainer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.n3gd0r.crm.gym.application.domain.model.TrainingType;

public record CreateTrainerCommand(
        @NotBlank String firstname,
        @NotBlank String lastname,
        @NotNull TrainingType specialization,
        boolean active) {
}
