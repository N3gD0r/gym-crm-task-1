package org.n3gd0r.crm.gym.application.ports.in.command.training;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.n3gd0r.crm.gym.application.domain.model.TrainingType;

import java.time.LocalDate;

public record CreateTrainingCommand(
        @Positive Long trainerId,
        @Positive Long traineeId,
        @NotBlank String name,
        @NotNull TrainingType trainingType,
        @FutureOrPresent LocalDate trainingDate,
        @Positive Integer duration) {
}
