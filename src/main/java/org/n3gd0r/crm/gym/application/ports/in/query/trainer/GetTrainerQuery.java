package org.n3gd0r.crm.gym.application.ports.in.query.trainer;

import jakarta.validation.constraints.Positive;

public record GetTrainerQuery(@Positive Long trainerId) {
}
