package org.n3gd0r.crm.gym.application.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
public class Training {
    private Long id;
    private Long traineeId;
    private Long trainerId;
    private String name;
    private TrainingType trainingType;
    private LocalDate trainingDate;
    private Integer duration;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Training training)) return false;
        return Objects.equals(id, training.id) &&
                Objects.equals(traineeId, training.traineeId) &&
                Objects.equals(trainerId, training.trainerId) &&
                Objects.equals(name, training.name) &&
                Objects.equals(trainingType, training.trainingType) &&
                Objects.equals(trainingDate, training.trainingDate) &&
                Objects.equals(duration, training.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, traineeId, trainerId, name, trainingType, trainingDate, duration);
    }
}
