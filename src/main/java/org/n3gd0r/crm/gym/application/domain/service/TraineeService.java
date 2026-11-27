package org.n3gd0r.crm.gym.application.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.n3gd0r.crm.gym.application.domain.model.Trainee;
import org.n3gd0r.crm.gym.application.mapper.TraineeMapper;
import org.n3gd0r.crm.gym.application.ports.in.command.trainee.CreateTraineeCommand;
import org.n3gd0r.crm.gym.application.ports.in.command.trainee.DeleteTraineeCommand;
import org.n3gd0r.crm.gym.application.ports.in.command.trainee.UpdateTraineeCommand;
import org.n3gd0r.crm.gym.application.ports.in.query.trainee.GetTraineeQuery;
import org.n3gd0r.crm.gym.application.ports.in.usecase.trainee.TraineeUseCase;
import org.n3gd0r.crm.gym.application.ports.in.usecase.user.UserCredentialGenerator;
import org.n3gd0r.crm.gym.application.ports.out.trainee.TraineeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TraineeService implements TraineeUseCase {
    private final TraineeRepository repository;
    private final UserCredentialGenerator credentialGenerator;
    @Setter(onMethod_ = {@Autowired})
    private TraineeMapper mapper;

    @Override
    public Trainee addTrainee(CreateTraineeCommand command) {
        log.debug("CREATE Trainee profile: Saving new Trainee");
        String username = credentialGenerator.username(command.firstname(), command.lastname());
        log.debug("CREATE Trainee profile: Generating Trainee username");
        String password = credentialGenerator.password();
        log.debug("CREATE Trainee profile: Generating Trainee password");
        Trainee trainee = mapper.toTrainee(command, username, password);
        repository.persist(trainee);
        log.debug("CREATE Trainee profile: completed");
        return trainee;
    }

    @Override
    public Trainee getTrainee(GetTraineeQuery query) {
        return repository.getEntity(query.traineeId());
    }

    @Override
    public void modifyTrainee(UpdateTraineeCommand command) {
        log.debug("UPDATE Trainee: Checking if trainee exists with ID {}", command.traineeId());
        Trainee trainee = repository.getEntity(command.traineeId());
        log.debug("UPDATE Trainee: found");
        mapper.updateTrainee(command, trainee);
        log.debug("UPDATE Trainee: Updating Trainee in place");
        repository.persist(trainee);
        log.debug("UPDATE Trainee: updated");

    }

    @Override
    public void removeTrainee(DeleteTraineeCommand command) {
        log.debug("DELETE Trainee: Checking if trainee exists with ID {}", command.traineeId());
        Trainee trainee = repository.getEntity(command.traineeId());
        log.debug("DELETE Trainee: found");
        repository.remove(trainee);
        log.debug("DELETE Trainee: completed");
    }
}
