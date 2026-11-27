package org.n3gd0r.crm.gym.adapters.ports.out.inmemory;

import org.n3gd0r.crm.gym.application.domain.exception.TraineeNotFoundException;
import org.n3gd0r.crm.gym.application.domain.model.Trainee;
import org.n3gd0r.crm.gym.application.domain.model.User;
import org.n3gd0r.crm.gym.application.ports.out.trainee.TraineeRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository("InMemoryTraineeRepository")
public class TraineeDao extends GymDao<Trainee> implements TraineeRepository {
    public TraineeDao(@Qualifier("TraineeTable") Map<Long, Trainee> table) {
        super(table, Trainee.class, User::getUserId, User::setUserId, TraineeNotFoundException::new);
    }
}
