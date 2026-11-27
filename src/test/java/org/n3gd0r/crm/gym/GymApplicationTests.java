package org.n3gd0r.crm.gym;

import org.n3gd0r.crm.gym.application.domain.exception.TraineeNotFoundException;
import org.n3gd0r.crm.gym.application.domain.exception.TrainerNotFoundException;
import org.n3gd0r.crm.gym.application.domain.exception.TrainingNotFoundException;
import org.n3gd0r.crm.gym.application.domain.model.TrainingType;
import org.n3gd0r.crm.gym.application.ports.in.command.trainee.CreateTraineeCommand;
import org.n3gd0r.crm.gym.application.ports.in.command.trainee.DeleteTraineeCommand;
import org.n3gd0r.crm.gym.application.ports.in.command.trainee.UpdateTraineeCommand;
import org.n3gd0r.crm.gym.application.ports.in.command.trainer.CreateTrainerCommand;
import org.n3gd0r.crm.gym.application.ports.in.command.trainer.UpdateTrainerCommand;
import org.n3gd0r.crm.gym.application.ports.in.command.training.CreateTrainingCommand;
import org.n3gd0r.crm.gym.application.ports.in.query.trainee.GetTraineeQuery;
import org.n3gd0r.crm.gym.application.ports.in.query.trainer.GetTrainerQuery;
import org.n3gd0r.crm.gym.application.ports.in.query.training.GetTrainingQuery;
import org.n3gd0r.crm.gym.application.ports.in.usecase.trainee.TraineeUseCase;
import org.n3gd0r.crm.gym.application.ports.in.usecase.trainer.TrainerUseCase;
import org.n3gd0r.crm.gym.application.ports.in.usecase.training.TrainingUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.n3gd0r.crm.gym.adapters.ports.in.cli.GymCliRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class GymApplicationTests {
    private static final LocalDate BIRTH_DATE = LocalDate.of(1994, 7, 19);
    private static final LocalDate TRAINING_DATE = LocalDate.of(2035, 6, 17);

    @Autowired
    private TraineeUseCase traineeUseCase;
    @Autowired
    private TrainerUseCase trainerUseCase;
    @Autowired
    private TrainingUseCase trainingUseCase;
    @Autowired
    private GymCliRunner cliRunner;

    @Test
    @ExtendWith(OutputCaptureExtension.class)
    void demoRunsOnlyWhenRequested(CapturedOutput output) {
        cliRunner.run(new DefaultApplicationArguments());
        assertThat(output).doesNotContain("Demo completed");

        cliRunner.run(new DefaultApplicationArguments("--demo"));
        assertThat(output).containsPattern("Demo completed: trainee ID \\d+, trainer ID \\d+, training ID \\d+");
    }

    @Test
    void loadsSampleDataAndAllocatesNewIdsWithoutOverwritingIt() {
        var seededTrainee = traineeUseCase.getTrainee(new GetTraineeQuery(101L));
        var seededTrainer = trainerUseCase.getTrainer(new GetTrainerQuery(201L));
        var seededTraining = trainingUseCase.getTraining(new GetTrainingQuery(301L));
        assertThat(seededTrainee.getDateOfBirth()).isEqualTo(LocalDate.of(1995, 4, 12));
        assertThat(seededTrainer.getSpecialization()).isEqualTo(TrainingType.YOGA);
        assertThat(seededTraining.getTrainerId()).isEqualTo(201L);
        assertThat(seededTraining.getTraineeId()).isEqualTo(101L);
        assertThat(seededTraining.getDuration()).isEqualTo(45);
        assertThat(seededTraining.getTrainingDate()).isEqualTo(LocalDate.of(2025, 1, 20));

        var trainee = traineeUseCase.addTrainee(trainee("Taylor", "Sample"));
        var trainer = trainerUseCase.addTrainer(trainer("Jamie", "Sample"));
        var training = trainingUseCase.addTraining(training(trainer.getUserId(), trainee.getUserId()));
        assertThat(trainee.getUsername()).isEqualTo("Taylor.Sample1");
        assertThat(trainer.getUsername()).isEqualTo("Jamie.Sample1");
        assertThat(trainee.getUserId()).isGreaterThan(101L);
        assertThat(trainer.getUserId()).isGreaterThan(201L);
        assertThat(training.getId()).isGreaterThan(301L);
        assertThat(traineeUseCase.getTrainee(new GetTraineeQuery(101L))).isSameAs(seededTrainee);
        assertThat(trainerUseCase.getTrainer(new GetTrainerQuery(201L))).isSameAs(seededTrainer);
        assertThat(trainingUseCase.getTraining(new GetTrainingQuery(301L))).isSameAs(seededTraining);
    }

    @Test
    void updatesProfilesAndKeepsTheirOriginalUsernames() {
        var trainee = traineeUseCase.addTrainee(trainee("Avery", "Hart"));
        var trainer = trainerUseCase.addTrainer(trainer("Morgan", "Cruz"));
        assertThat(trainee.getPassword()).matches("[A-Za-z0-9]{10}");
        assertThat(trainer.getPassword()).matches("[A-Za-z0-9]{10}");

        traineeUseCase.modifyTrainee(new UpdateTraineeCommand(trainee.getUserId(), "Alex", "Lane",
                "changed123", "New address", BIRTH_DATE.plusDays(1), false));
        trainerUseCase.updateTrainer(new UpdateTrainerCommand(trainer.getUserId(), "Robin", "Lane",
                "changed456", TrainingType.ZUMBA, false));

        var updatedTrainee = traineeUseCase.getTrainee(new GetTraineeQuery(trainee.getUserId()));
        assertThat(updatedTrainee.getUsername()).isEqualTo("Avery.Hart");
        assertThat(updatedTrainee.getFirstname()).isEqualTo("Alex");
        assertThat(updatedTrainee.getLastname()).isEqualTo("Lane");
        assertThat(updatedTrainee.getAddress()).isEqualTo("New address");
        assertThat(updatedTrainee.getDateOfBirth()).isEqualTo(BIRTH_DATE.plusDays(1));
        assertThat(updatedTrainee.getPassword()).isEqualTo("changed123");
        assertThat(updatedTrainee.isActive()).isFalse();
        var updatedTrainer = trainerUseCase.getTrainer(new GetTrainerQuery(trainer.getUserId()));
        assertThat(updatedTrainer.getUsername()).isEqualTo("Morgan.Cruz");
        assertThat(updatedTrainer.getFirstname()).isEqualTo("Robin");
        assertThat(updatedTrainer.getLastname()).isEqualTo("Lane");
        assertThat(updatedTrainer.getSpecialization()).isEqualTo(TrainingType.ZUMBA);
        assertThat(updatedTrainer.getPassword()).isEqualTo("changed456");
        assertThat(updatedTrainer.isActive()).isFalse();
    }

    @Test
    void removesOnlyTheDeletedTraineesTrainings() {
        var trainee = traineeUseCase.addTrainee(trainee("Casey", "River"));
        var training = trainingUseCase.addTraining(training(201L, trainee.getUserId()));
        var anotherTraining = trainingUseCase.addTraining(training(201L, trainee.getUserId()));
        var unrelated = trainingUseCase.addTraining(training(201L, 101L));
        assertThat(trainingUseCase.getTraining(new GetTrainingQuery(training.getId())).getName()).isEqualTo("Test training");

        traineeUseCase.removeTrainee(new DeleteTraineeCommand(trainee.getUserId()));

        assertThatThrownBy(() -> traineeUseCase.getTrainee(new GetTraineeQuery(trainee.getUserId())))
                .isInstanceOf(TraineeNotFoundException.class);
        assertThatThrownBy(() -> trainingUseCase.getTraining(new GetTrainingQuery(training.getId())))
                .isInstanceOf(TrainingNotFoundException.class);
        assertThatThrownBy(() -> trainingUseCase.getTraining(new GetTrainingQuery(anotherTraining.getId())))
                .isInstanceOf(TrainingNotFoundException.class);
        assertThat(trainingUseCase.getTraining(new GetTrainingQuery(unrelated.getId()))).isSameAs(unrelated);
        assertThat(trainerUseCase.getTrainer(new GetTrainerQuery(201L))).isNotNull();
    }

    @Test
    void rejectsTrainingWhenEitherParticipantIsMissing() {
        assertThatThrownBy(() -> trainingUseCase.addTraining(training(201L, Long.MAX_VALUE)))
                .isInstanceOf(TraineeNotFoundException.class);
        assertThatThrownBy(() -> trainingUseCase.addTraining(training(Long.MAX_VALUE, 101L)))
                .isInstanceOf(TrainerNotFoundException.class);
    }

    @Test
    void concurrentCrossRoleSignupsAllocateDistinctUsernames() throws Exception {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var ready = new CountDownLatch(2);
            var start = new CountDownLatch(1);
            var trainee = executor.submit(() -> {
                ready.countDown();
                start.await();
                return traineeUseCase.addTrainee(trainee("Sam", "Parallel")).getUsername();
            });
            var trainer = executor.submit(() -> {
                ready.countDown();
                start.await();
                return trainerUseCase.addTrainer(trainer("Sam", "Parallel")).getUsername();
            });
            boolean bothReady = ready.await(5, TimeUnit.SECONDS);
            start.countDown();
            assertThat(bothReady).isTrue();
            assertThat(new String[]{trainee.get(5, TimeUnit.SECONDS), trainer.get(5, TimeUnit.SECONDS)})
                    .containsExactlyInAnyOrder("Sam.Parallel", "Sam.Parallel1");
        }
    }

    private static CreateTraineeCommand trainee(String firstName, String lastName) {
        return new CreateTraineeCommand(firstName, lastName, "Example address", BIRTH_DATE, true);
    }

    private static CreateTrainerCommand trainer(String firstName, String lastName) {
        return new CreateTrainerCommand(firstName, lastName, TrainingType.YOGA, true);
    }

    private static CreateTrainingCommand training(long trainerId, long traineeId) {
        return new CreateTrainingCommand(trainerId, traineeId, "Test training", TrainingType.YOGA, TRAINING_DATE, 35);
    }
}
