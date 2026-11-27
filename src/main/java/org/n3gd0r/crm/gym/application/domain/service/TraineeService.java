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
import org.n3gd0r.crm.gym.application.ports.out.training.TrainingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TraineeService implements TraineeUseCase {
    private final TraineeRepository repository;
    private final TrainingRepository trainingRepository;
    private final Object writeLock;
    private final UserCredentialGenerator credentialGenerator;
    @Setter(onMethod_ = @Autowired)
    private TraineeMapper mapper;

    @Override
    public Trainee addTrainee(CreateTraineeCommand command) {
        synchronized (writeLock) {
            String username = credentialGenerator.username(command.firstname(), command.lastname());
            Trainee trainee = mapper.toTrainee(command, username, credentialGenerator.password());
            repository.persist(trainee);
            log.info("Created trainee profile with ID {}", trainee.getUserId());
            return trainee;
        }
    }

    @Override
    public Trainee getTrainee(GetTraineeQuery query) {
        log.debug("Selecting trainee profile with ID {}", query.traineeId());
        return repository.getEntity(query.traineeId());
    }

    @Override
    public void modifyTrainee(UpdateTraineeCommand command) {
        synchronized (writeLock) {
            Trainee trainee = repository.getEntity(command.traineeId());
            mapper.updateTrainee(command, trainee);
            repository.persist(trainee);
        }
        log.info("Updated trainee profile with ID {}", command.traineeId());
    }

    @Override
    public void removeTrainee(DeleteTraineeCommand command) {
        synchronized (writeLock) {
            Trainee trainee = repository.getEntity(command.traineeId());
            trainingRepository.removeByTraineeId(command.traineeId());
            repository.remove(trainee);
        }
        log.info("Removed trainee profile with ID {} and its trainings", command.traineeId());
    }
}
