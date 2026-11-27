package org.n3gd0r.crm.gym.application.ports.in.query.training;

import jakarta.validation.constraints.Positive;

public record GetTrainingQuery(@Positive Long trainingId) {
}
