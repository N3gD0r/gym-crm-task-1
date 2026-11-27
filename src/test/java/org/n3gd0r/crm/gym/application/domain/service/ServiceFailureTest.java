package org.n3gd0r.crm.gym.application.domain.service;

import org.junit.jupiter.api.Test;
import org.n3gd0r.crm.gym.application.domain.exception.TraineeNotFoundException;
import org.n3gd0r.crm.gym.application.domain.exception.TrainerNotFoundException;
import org.n3gd0r.crm.gym.application.domain.model.Trainee;
import org.n3gd0r.crm.gym.application.domain.model.TrainingType;
import org.n3gd0r.crm.gym.application.mapper.TrainingMapper;
import org.n3gd0r.crm.gym.application.ports.in.command.trainee.DeleteTraineeCommand;
import org.n3gd0r.crm.gym.application.ports.in.command.training.CreateTrainingCommand;
import org.n3gd0r.crm.gym.application.ports.in.usecase.user.UserCredentialGenerator;
import org.n3gd0r.crm.gym.application.ports.out.trainee.TraineeRepository;
import org.n3gd0r.crm.gym.application.ports.out.trainer.TrainerRepository;
import org.n3gd0r.crm.gym.application.ports.out.training.TrainingRepository;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class ServiceFailureTest {
    @Test
    void deletingAMissingTraineeDoesNotDeleteAnyTrainings() {
        var trainees = mock(TraineeRepository.class);
        var trainings = mock(TrainingRepository.class);
        var credsGen = mock(UserCredentialGenerator.class);
        var service = new TraineeService(trainees, trainings, new Object(), credsGen);
        when(trainees.getEntity(42L)).thenThrow(new TraineeNotFoundException(42L));

        assertThatThrownBy(() -> service.removeTrainee(new DeleteTraineeCommand(42L)))
                .isInstanceOf(TraineeNotFoundException.class);
        verifyNoInteractions(trainings);
    }

    @Test
    void creatingTrainingWithAnUnknownTrainerDoesNotMapOrPersistIt() {
        var trainees = mock(TraineeRepository.class);
        var trainers = mock(TrainerRepository.class);
        var trainings = mock(TrainingRepository.class);
        var mapper = mock(TrainingMapper.class);
        var service = new TrainingService(trainings, trainees, trainers, new Object());
        service.setMapper(mapper);
        when(trainees.getEntity(1L)).thenReturn(new Trainee("Test", "Trainee", null, null, true));
        when(trainers.getEntity(2L)).thenThrow(new TrainerNotFoundException(2L));
        var command = new CreateTrainingCommand(2L, 1L, "Test", TrainingType.YOGA, LocalDate.of(2035, 1, 1), 45);

        assertThatThrownBy(() -> service.addTraining(command)).isInstanceOf(TrainerNotFoundException.class);
        verifyNoInteractions(mapper, trainings);
    }
}
