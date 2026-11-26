package org.n3gd0r.crm.gym.application.domain.exception;

public class TrainingNotFoundException extends RuntimeException {
    public TrainingNotFoundException(Long trainingId) {
        super("Training with ID %d not found".formatted(trainingId));
    }
}
