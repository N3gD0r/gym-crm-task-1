package org.n3gd0r.crm.gym.adapters.ports.out.inmemory;

import lombok.extern.slf4j.Slf4j;
import org.n3gd0r.crm.gym.application.domain.exception.TrainingNotFoundException;
import org.n3gd0r.crm.gym.application.domain.model.Training;
import org.n3gd0r.crm.gym.application.ports.out.training.TrainingRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Slf4j
@Repository("InMemoryTrainingRepository")
public class TrainingDao extends GymDao<Training> implements TrainingRepository {
    public TrainingDao(@Qualifier("TrainingTable") Map<Long, Training> table) {
        super(table, Training.class, Training::getId, Training::setId, TrainingNotFoundException::new);
    }

    @Override
    public void removeByTraineeId(Long traineeId) {
        table.values().removeIf(training -> traineeId.equals(training.getTraineeId()));
        log.debug("Removed associated training records for trainee ID {}", traineeId);
    }
}
