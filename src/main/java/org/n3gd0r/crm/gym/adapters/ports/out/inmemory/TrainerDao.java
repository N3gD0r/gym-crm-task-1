package org.n3gd0r.crm.gym.adapters.ports.out.inmemory;

import jakarta.annotation.PostConstruct;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.n3gd0r.crm.gym.application.domain.exception.TrainerNotFoundException;
import org.n3gd0r.crm.gym.application.domain.model.Trainer;
import org.n3gd0r.crm.gym.application.domain.model.TrainingType;
import org.n3gd0r.crm.gym.application.domain.model.User;
import org.n3gd0r.crm.gym.application.ports.out.trainer.TrainerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Scanner;

@Slf4j
@Repository("InMemory")
public class TrainerDao extends GymDao<Trainer> implements TrainerRepository {
    @Setter(onMethod_ = {@Autowired, @Qualifier("TrainerDataFile")})
    private File trainerInitialData;

    public TrainerDao(@Qualifier("TrainerTable") Map<Long, Trainer> trainerTable) {
        super(trainerTable,
                Trainer.class,
                User::getUserId,
                User::setUserId,
                TrainerNotFoundException::new);
    }

    // TODO: initial data loaded may be abstracted for reuse (DRY)
    @PostConstruct
    void init() {
        try (Scanner sc = new Scanner(trainerInitialData)) {
            log.info("Begin loading Trainer initial data from file");
            while (sc.hasNextLine()) {
                String trainerRow = sc.nextLine();
                if (trainerRow.isBlank()) {
                    log.debug("Current Trainer initial data row is blank");
                    continue;
                }
                String[] trainerFields = trainerRow.split(",");
                if (trainerFields.length != 7) {
                    log.debug("Incomplete Trainer fields for Trainee initialization");
                    continue;
                }
                Long trainerId = Long.parseLong(trainerFields[0]);
                String firstname = trainerFields[1];
                String lastname = trainerFields[2];
                String username = trainerFields[3];
                String password = trainerFields[4];
                boolean active = Boolean.parseBoolean(trainerFields[5]);
                TrainingType trainingType = Utils.type(trainerFields[6]);


                Trainer trainer = new Trainer(firstname, lastname, active, trainingType);
                trainer.setUserId(trainerId);
                trainer.setUsername(username);
                trainer.setPassword(password);

                table.put(trainerId, trainer);
                log.debug("Trainer with ID {} saved in persistence unit from file", trainerId);
            }
            log.info("Loaded Trainer initial data from file");
        } catch (FileNotFoundException ex) {
            log.error("Trainer initial data file was not found", ex);
        }
    }
}
