package org.n3gd0r.crm.gym.application.ports.in.query.trainee;

import jakarta.validation.constraints.Positive;

public record GetTraineeQuery(@Positive Long traineeId) {
}
