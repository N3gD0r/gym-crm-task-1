package org.n3gd0r.crm.gym;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.n3gd0r.crm.gym.application.domain.exception.TraineeNotFoundException;
import org.n3gd0r.crm.gym.application.domain.exception.TrainingNotFoundException;
import org.n3gd0r.crm.gym.application.ports.in.query.trainee.GetTraineeQuery;
import org.n3gd0r.crm.gym.application.ports.in.query.training.GetTrainingQuery;
import org.n3gd0r.crm.gym.application.ports.in.usecase.trainee.TraineeUseCase;
import org.n3gd0r.crm.gym.application.ports.in.usecase.training.TrainingUseCase;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(OutputCaptureExtension.class)
class ExternalStorageTests {
    @Test
    void externalPropertiesOverrideDefaultsAndBadInputDoesNotStopStartup(@TempDir Path tempDir,
                                                                         CapturedOutput output) throws Exception {
        var traineeFile = Files.writeString(tempDir.resolve("trainees.csv"), """
                71,Demo,External,Demo.External,EXAMPLE-01,false,,
                invalid-row
                72,Demo,Invalid,Demo.Invalid,EXAMPLE-02,true,,INVALID-DATE
                71,Another,Profile,Another.Profile,EXAMPLE-03,true,,
                73,Demo,External,Demo.External,EXAMPLE-04,true,,
                74,Other,External,Other.External,EXAMPLE-05,true,,
                """);
        var trainingFile = Files.writeString(tempDir.resolve("trainings.csv"), """
                81,201,71,External session,YOGA,45,2025-02-03
                82,900,71,No trainer,YOGA,45,2025-02-03
                83,201,71,Bad duration,YOGA,-1,2025-02-03
                """);
        var properties = Files.writeString(tempDir.resolve("storage.yaml"), """
                storage:
                  trainee: %s
                  training: %s
                """.formatted(traineeFile, trainingFile.toUri()));

        try (var context = new SpringApplicationBuilder(GymApplication.class)
                .run("--spring.config.additional-location=" + properties.toUri())) {
            var traineeUseCase = context.getBean(TraineeUseCase.class);
            var trainingUseCase = context.getBean(TrainingUseCase.class);
            var trainee = traineeUseCase.getTrainee(new GetTraineeQuery(71L));
            assertThat(trainee.getUsername()).isEqualTo("Demo.External");
            assertThat(trainee.isActive()).isFalse();
            assertThat(trainee.getDateOfBirth()).isNull();
            assertThat(traineeUseCase.getTrainee(new GetTraineeQuery(74L))).isNotNull();
            for (long skippedId : new long[]{72L, 73L, 101L}) {
                assertThatThrownBy(() -> traineeUseCase.getTrainee(new GetTraineeQuery(skippedId)))
                        .isInstanceOf(TraineeNotFoundException.class);
            }
            assertThat(trainingUseCase.getTraining(new GetTrainingQuery(81L)).getTraineeId()).isEqualTo(71L);
            for (long skippedId : new long[]{82L, 83L}) {
                assertThatThrownBy(() -> trainingUseCase.getTraining(new GetTrainingQuery(skippedId)))
                        .isInstanceOf(TrainingNotFoundException.class);
            }
        }
        assertThat(output).contains("Skipped trainee seed line 2", "Skipped trainee seed line 3",
                        "Skipped trainee seed line 4", "Skipped trainee seed line 5",
                        "Skipped training seed line 2", "Skipped training seed line 3")
                .doesNotContain("INVALID-DATE", "EXAMPLE-02", "Demo.External");
    }
}
