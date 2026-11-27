package org.n3gd0r.crm.gym.application.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.n3gd0r.crm.gym.application.domain.model.Training;
import org.n3gd0r.crm.gym.application.mapper.TrainingMapper;
import org.n3gd0r.crm.gym.application.ports.in.command.training.CreateTrainingCommand;
import org.n3gd0r.crm.gym.application.ports.in.query.training.GetTrainingQuery;
import org.n3gd0r.crm.gym.application.ports.in.usecase.training.TrainingUseCase;
import org.n3gd0r.crm.gym.application.ports.out.trainee.TraineeRepository;
import org.n3gd0r.crm.gym.application.ports.out.trainer.TrainerRepository;
import org.n3gd0r.crm.gym.application.ports.out.training.TrainingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TrainingService implements TrainingUseCase {
    private final TrainingRepository repository;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final Object writeLock;
    @Setter(onMethod_ = @Autowired)
    private TrainingMapper mapper;

    @Override
    public Training addTraining(CreateTrainingCommand command) {
        synchronized (writeLock) {
            traineeRepository.getEntity(command.traineeId());
            trainerRepository.getEntity(command.trainerId());
            Training training = mapper.toTraining(command);
            repository.persist(training);
            log.info("Created training record with ID {}", training.getId());
            return training;
        }
    }

    @Override
    public Training getTraining(GetTrainingQuery query) {
        log.debug("Selecting training record with ID {}", query.trainingId());
        return repository.getEntity(query.trainingId());
    }
}
