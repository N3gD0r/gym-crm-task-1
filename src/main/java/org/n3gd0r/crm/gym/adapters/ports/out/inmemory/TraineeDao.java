package org.n3gd0r.crm.gym.adapters.ports.out.inmemory;

import jakarta.annotation.PostConstruct;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.n3gd0r.crm.gym.application.domain.exception.GymEntityNullException;
import org.n3gd0r.crm.gym.application.domain.exception.TraineeNotFoundException;
import org.n3gd0r.crm.gym.application.domain.model.Trainee;
import org.n3gd0r.crm.gym.application.ports.out.trainee.TraineeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Repository
public class TraineeDao implements TraineeRepository {
    private final Map<Long, Trainee> traineeTable;
    private final AtomicLong lastId;

    @Setter(onMethod_ = {@Autowired, @Qualifier("TraineeDataFile")})
    private File traineeInitialData;

    public TraineeDao(@Qualifier("TraineeTable") Map<Long, Trainee> traineeTable) {
        this.traineeTable = traineeTable;
        lastId = new AtomicLong();
    }

    @Override
    public void remove(Trainee entity) {
        validateEntity(entity);
        Long traineeId = entity.getUserId();
        validateEntityExists(traineeId);
        traineeTable.remove(traineeId, entity);
        log.info("Removed a trainee from persistence unit");
        log.debug("Removed trainee with ID {}", traineeId);
    }

    @Override
    public Trainee getEntity(Long id) {
        validateEntityExists(id);
        return traineeTable.get(id);
    }

    @Override
    public void persist(Trainee entity) {
        validateEntity(entity);
        Long traineeId = entity.getUserId();
        if (traineeId == null) {
            traineeId = lastId.incrementAndGet();
            log.debug("Trainee entity has null ID, saving new Trainee with serial ID {}", traineeId);
        }
        traineeTable.put(traineeId, entity);
        log.info("Saved a new Trainee with ID {}", traineeId);
    }

    @PostConstruct
    void initTable() {
        try (Scanner sc = new Scanner(traineeInitialData)) {
            log.info("Begin Trainee initial data from file");
            while (sc.hasNextLine()) {
                String traineeRow = sc.nextLine();
                if (traineeRow.isBlank()) {
                    log.info("Current Trainee initial data row is blank");
                    continue;
                }
                String[] traineeFields = traineeRow.split(",");
                if (traineeFields.length != 8) {
                    log.debug("Incomplete Trainee fields for Trainee initialization");
                }
                Long traineeId = Long.parseLong(traineeFields[0]);
                String firstname = traineeFields[1];
                String lastname = traineeFields[2];
                String username = traineeFields[3];
                String password = traineeFields[4];
                boolean active = Boolean.parseBoolean(traineeFields[5]);
                String address = traineeFields[6];

                String[] dateStr = traineeFields[7].split("-");
                if (dateStr.length != 3) {
                    log.debug("Incomplete Trainee date of birth data for Trainee initialization");
                }
                int year = Integer.parseInt(dateStr[0]);
                int month = Integer.parseInt(dateStr[0]);
                int day = Integer.parseInt(dateStr[0]);
                LocalDate dateOfBirth = LocalDate.of(year, month, day);

                Trainee trainee = new Trainee(firstname, lastname, address, dateOfBirth, active);
                trainee.setUserId(traineeId);
                trainee.setUsername(username);
                trainee.setPassword(password);

                traineeTable.put(traineeId, trainee);
                log.debug("Trainee with ID {} saved in persistence unit from file", traineeId);
            }
            log.info("Finalized Trainee initial data from file");
        } catch (FileNotFoundException ex) {
            log.error("Trainee initial data file was not found", ex);
        }
    }

    private void validateEntity(Trainee entity) {
        if (entity == null) {
            log.error("Got a null entity when removing a Trainee");
            throw new GymEntityNullException(Trainee.class);
        }
    }

    private void validateEntityExists(Long id) {
        if (!traineeTable.containsKey(id)) {
            log.error("Trainee with ID {}", id);
            throw new TraineeNotFoundException(id);
        }
    }
}
