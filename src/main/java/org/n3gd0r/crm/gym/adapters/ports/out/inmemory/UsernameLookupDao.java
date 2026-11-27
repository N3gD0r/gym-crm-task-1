package org.n3gd0r.crm.gym.adapters.ports.out.inmemory;

import org.n3gd0r.crm.gym.application.domain.model.Trainee;
import org.n3gd0r.crm.gym.application.domain.model.Trainer;
import org.n3gd0r.crm.gym.application.ports.out.user.UsernameLookupPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class UsernameLookupDao implements UsernameLookupPort {
    private Map<Long, Trainee> traineeTable;
    private Map<Long, Trainer> trainerTable;

    @Autowired
    public void setTraineeTable(@Qualifier("TraineeTable") Map<Long, Trainee> traineeTable) {
        this.traineeTable = traineeTable;
    }

    @Autowired
    public void setTrainerTable(@Qualifier("TrainerTable") Map<Long, Trainer> trainerTable) {
        this.trainerTable = trainerTable;
    }

    @Override
    public boolean existsByName(String username) {
        return traineeTable.values().stream().anyMatch(user -> username.equals(user.getUsername()))
                || trainerTable.values().stream().anyMatch(user -> username.equals(user.getUsername()));
    }
}
