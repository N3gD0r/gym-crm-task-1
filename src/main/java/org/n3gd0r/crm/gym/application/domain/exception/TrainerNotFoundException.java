package org.n3gd0r.crm.gym.application.domain.exception;

public class TrainerNotFoundException extends RuntimeException {
    public TrainerNotFoundException(Long trainerId) {
        super("Trainer with ID %d not found".formatted(trainerId));
    }
}
