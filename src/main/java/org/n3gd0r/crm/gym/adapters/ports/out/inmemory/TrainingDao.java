package org.n3gd0r.crm.gym.adapters.ports.out.inmemory;

import jakarta.annotation.PostConstruct;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.n3gd0r.crm.gym.application.domain.exception.TraineeNotFoundException;
import org.n3gd0r.crm.gym.application.domain.model.Training;
import org.n3gd0r.crm.gym.application.domain.model.TrainingType;
import org.n3gd0r.crm.gym.application.ports.out.training.TrainingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.Map;
import java.util.Scanner;

@Slf4j
@Repository("InMemoryTrainingRepository")
public class TrainingDao extends GymDao<Training> implements TrainingRepository {
    @Setter(onMethod_ = {@Autowired, @Qualifier("TrainingDataFile")})
    private File trainingInitialData;

    public TrainingDao(@Qualifier("TrainingTable") Map<Long, Training> trainingTable) {
        super(trainingTable,
                Training.class,
                Training::getId,
                Training::setId,
                TraineeNotFoundException::new);
    }

    @PostConstruct
    void init() {
        try (Scanner sc = new Scanner(trainingInitialData)) {
            log.info("Begin loading training initial data from file");
            while (sc.hasNextLine()) {
                String trainingRow = sc.nextLine();
                if (trainingRow.isBlank()) {
                    log.debug("Current Trainee initial data row is blank");
                    continue;
                }
                String[] trainingFields = trainingRow.split(",");
                if (trainingFields.length != 6) {
                    log.debug("Incomplete training fields for Trainee initialization");
                    continue;
                }
                Long trainingId = Long.parseLong(trainingFields[0]);
                Long trainerId = Long.parseLong(trainingFields[1]);
                Long traineeId = Long.parseLong(trainingFields[2]);
                String name = trainingFields[3];
                TrainingType trainingType = Utils.type(trainingFields[4]);
                Integer duration = Integer.parseInt(trainingFields[5]);
                String[] dateStr = trainingFields[6].split("-");
                if (dateStr.length != 3) {
                    log.debug("Incomplete Training start date data for Training initialization");
                }
                int year = Integer.parseInt(dateStr[0]);
                int month = Integer.parseInt(dateStr[1]);
                int day = Integer.parseInt(dateStr[2]);
                LocalDate startDate = LocalDate.of(year, month, day);
                Training training = new Training(trainingId, traineeId, trainerId, name, trainingType, startDate, duration);
                table.put(trainingId, training);
                log.debug("Training with ID {} saved in persistence unit from file", trainingId);
            }
            log.info("Loaded training initial data from file");
        } catch (FileNotFoundException ex) {
            log.error("Training initial data file was not found", ex);
        }
    }
}
