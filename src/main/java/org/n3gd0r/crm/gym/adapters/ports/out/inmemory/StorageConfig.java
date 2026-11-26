package org.n3gd0r.crm.gym.adapters.ports.out.inmemory;

import org.n3gd0r.crm.gym.application.domain.model.Trainee;
import org.n3gd0r.crm.gym.application.domain.model.Trainer;
import org.n3gd0r.crm.gym.application.domain.model.Training;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class StorageConfig {
    @Bean("TraineeTable")
    Map<Long, Trainee> traineeTable() {
        return new ConcurrentHashMap<>();
    }

    @Bean("TrainerTable")
    Map<Long, Trainer> trainerTable() {
        return new ConcurrentHashMap<>();
    }

    @Bean("TrainingTable")
    Map<Long, Training> trainingTable() {
        return new ConcurrentHashMap<>();
    }

    @Bean("TraineeDataFile")
    File traineeFile(@Value("${storage.trainee}") String path) {
        return new File(path);
    }
}
