package org.n3gd0r.crm.gym.adapters.ports.out.inmemory;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.n3gd0r.crm.gym.application.domain.model.Trainee;
import org.n3gd0r.crm.gym.application.domain.model.Trainer;
import org.n3gd0r.crm.gym.application.domain.model.Training;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.core.io.DefaultResourceLoader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(OutputCaptureExtension.class)
class StorageInitializerTest {
    @Test
    void loadsOtherNamespacesWhenOneFileDoesNotExist(@TempDir Path tempDir, CapturedOutput output) throws Exception {
        var trainees = new ConcurrentHashMap<Long, Trainee>();
        var trainers = new ConcurrentHashMap<Long, Trainer>();
        var trainings = new ConcurrentHashMap<Long, Training>();
        var traineeDao = new TraineeDao(trainees);
        var trainerDao = new TrainerDao(trainers);
        var trainingDao = new TrainingDao(trainings);
        var lookup = new UsernameLookupDao();
        lookup.setTraineeTable(trainees);
        lookup.setTrainerTable(trainers);

        var initializer = new StorageInitializer(lookup);
        initializer.setTraineeDao(traineeDao);
        initializer.setTrainerDao(trainerDao);
        initializer.setTrainingDao(trainingDao);
        initializer.setResourceLoader(new DefaultResourceLoader());
        initializer.setTraineePath(tempDir.resolve("missing.csv").toString());
        initializer.setTrainerPath("classpath:seed/trainers.csv");
        initializer.setTrainingPath(Files.writeString(tempDir.resolve("trainings.csv"), """
                
                1,201,42,Missing trainee,YOGA,45,2025-02-03
                """).toUri().toString());

        initializer.initialize();

        assertThat(trainees).isEmpty();
        assertThat(trainers).containsOnlyKeys(201L);
        assertThat(trainings).isEmpty();
        assertThat(output).contains("Could not read trainee seed file", "Skipped training seed line 2: missing participant")
                .doesNotContain("missing.csv", "Missing trainee");
    }
}
