package org.n3gd0r.crm.gym.application.domain.exception;

public class TraineeNotFoundException extends RuntimeException {
    public TraineeNotFoundException(Long traineeId) {
        super("Trainee with ID %d not found".formatted(traineeId));
    }
}
