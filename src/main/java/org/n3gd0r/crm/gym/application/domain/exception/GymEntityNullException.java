package org.n3gd0r.crm.gym.application.domain.exception;

public class GymEntityNullException extends RuntimeException {
    public GymEntityNullException(Class<?> type) {
        super("Gym entity %s cannot be null for persistence processing".formatted(type.getSimpleName()));
    }
}
