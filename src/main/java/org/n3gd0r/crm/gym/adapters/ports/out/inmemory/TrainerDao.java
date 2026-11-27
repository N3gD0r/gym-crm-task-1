package org.n3gd0r.crm.gym.adapters.ports.out.inmemory;

import org.n3gd0r.crm.gym.application.domain.exception.TrainerNotFoundException;
import org.n3gd0r.crm.gym.application.domain.model.Trainer;
import org.n3gd0r.crm.gym.application.domain.model.User;
import org.n3gd0r.crm.gym.application.ports.out.trainer.TrainerRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository("InMemoryTrainerRepository")
public class TrainerDao extends GymDao<Trainer> implements TrainerRepository {
    public TrainerDao(@Qualifier("TrainerTable") Map<Long, Trainer> table) {
        super(table, Trainer.class, User::getUserId, User::setUserId, TrainerNotFoundException::new);
    }
}
