package org.n3gd0r.crm.gym.application.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.n3gd0r.crm.gym.application.domain.exception.GymEntityNullException;
import org.n3gd0r.crm.gym.application.domain.model.Trainee;
import org.n3gd0r.crm.gym.application.domain.model.Trainer;
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
@RequiredArgsConstructor
public class TrainingService implements TrainingUseCase {
    private final TrainingRepository repository;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    @Setter(onMethod_ = {@Autowired})
    private TrainingMapper mapper;

    @Override
    public Training addTraining(CreateTrainingCommand command) {
        Trainee trainee = traineeRepository.getEntity(command.traineeId());
        if (trainee == null) {
            throw new GymEntityNullException(Trainee.class);
        }
        Trainer trainer = trainerRepository.getEntity(command.trainerId());
        if (trainer == null) {
            throw new GymEntityNullException(Trainer.class);
        }
        Training training = mapper.toTraining(command);
        repository.persist(training);
        return training;
    }

    @Override
    public Training getTraining(GetTrainingQuery query) {
        return repository.getEntity(query.trainingId());
    }
}
