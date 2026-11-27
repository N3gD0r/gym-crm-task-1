package org.n3gd0r.crm.gym.application.domain.model;

public enum TrainingType {
    FITNESS(1L),
    YOGA(2L),
    RESISTANCE(3L),
    ZUMBA(4L),
    STRETCHING(5L),
    UNKNOWN(0L);
    public final Long id;

    TrainingType(Long id) {
        this.id = id;
    }
}
