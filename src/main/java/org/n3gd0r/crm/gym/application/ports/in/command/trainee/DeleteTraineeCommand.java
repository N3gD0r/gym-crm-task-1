package org.n3gd0r.crm.gym.application.ports.in.command.trainee;

import jakarta.validation.constraints.Positive;

public record DeleteTraineeCommand(@Positive Long traineeId) {
}
