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
    private final UserCredentialGenerator credentialGenerator;
    @Setter(onMethod_ = {@Autowired})
    private TrainerMapper mapper;

    @Override
    public Trainer addTrainer(CreateTrainerCommand command) {
        log.debug("CREATE Trainer Profile: Saving new trainer");
        String username = credentialGenerator.username(command.firstname(), command.lastname());
        log.debug("CREATE Trainer Profile: Generated username");
        String password = credentialGenerator.password();
        log.debug("CREATE Trainer Profile: Generated password");
        Trainer trainer = mapper.toTrainer(command, username, password);
        repository.persist(trainer);
        log.debug("CREATE Trainer Profile: completed");
        return trainer;
    }

    @Override
    public Trainer getTrainer(GetTrainerQuery query) {
        return repository.getEntity(query.trainerId());
    }

    @Override
    public void updateTrainer(UpdateTrainerCommand command) {
        log.debug("UPDATE Trainer: Checking if trainer exists with ID {}", command.trainerId());
        Trainer trainer = repository.getEntity(command.trainerId());
        log.debug("UPDATE Trainer: found");
        mapper.updateTrainer(command, trainer);
        log.debug("UPDATE Trainer: Updating Trainer in place");
        repository.persist(trainer);
        log.debug("UPDATE Trainer: updated");
    }
}
