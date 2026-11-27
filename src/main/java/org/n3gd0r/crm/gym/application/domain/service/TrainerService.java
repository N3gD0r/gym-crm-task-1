package org.n3gd0r.crm.gym.application.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.n3gd0r.crm.gym.application.domain.model.Trainer;
import org.n3gd0r.crm.gym.application.mapper.TrainerMapper;
import org.n3gd0r.crm.gym.application.ports.in.command.trainer.CreateTrainerCommand;
import org.n3gd0r.crm.gym.application.ports.in.command.trainer.UpdateTrainerCommand;
import org.n3gd0r.crm.gym.application.ports.in.query.trainer.GetTrainerQuery;
import org.n3gd0r.crm.gym.application.ports.in.usecase.trainer.TrainerUseCase;
import org.n3gd0r.crm.gym.application.ports.in.usecase.user.UserCredentialGenerator;
import org.n3gd0r.crm.gym.application.ports.out.trainer.TrainerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainerService implements TrainerUseCase {
    private final TrainerRepository repository;
    private final Object writeLock;
    private final UserCredentialGenerator credentialGenerator;
    @Setter(onMethod_ = @Autowired)
    private TrainerMapper mapper;

    @Override
    public Trainer addTrainer(CreateTrainerCommand command) {
        synchronized (writeLock) {
            String username = credentialGenerator.username(command.firstname(), command.lastname());
            Trainer trainer = mapper.toTrainer(command, username, credentialGenerator.password());
            repository.persist(trainer);
            log.info("Created trainer profile with ID {}", trainer.getUserId());
            return trainer;
        }
    }

    @Override
    public Trainer getTrainer(GetTrainerQuery query) {
        log.debug("Selecting trainer profile with ID {}", query.trainerId());
        return repository.getEntity(query.trainerId());
    }

    @Override
    public void updateTrainer(UpdateTrainerCommand command) {
        synchronized (writeLock) {
            Trainer trainer = repository.getEntity(command.trainerId());
            mapper.updateTrainer(command, trainer);
            repository.persist(trainer);
        }
        log.info("Updated trainer profile with ID {}", command.trainerId());
    }
}
