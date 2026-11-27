package org.n3gd0r.crm.gym.adapters.ports.out.inmemory;

import jakarta.annotation.PostConstruct;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.n3gd0r.crm.gym.application.domain.exception.TraineeNotFoundException;
import org.n3gd0r.crm.gym.application.domain.model.Trainee;
import org.n3gd0r.crm.gym.application.domain.model.User;
import org.n3gd0r.crm.gym.application.ports.out.trainee.TraineeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.Map;
import java.util.Scanner;

@Slf4j
@Repository("InMemoryTraineeRepository")
public class TraineeDao extends GymDao<Trainee> implements TraineeRepository {
    @Setter(onMethod_ = {@Autowired, @Qualifier("TraineeDataFile")})
    private File traineeInitialData;

    public TraineeDao(@Qualifier("TraineeTable") Map<Long, Trainee> traineeTable) {
        super(traineeTable,
                Trainee.class,
                User::getUserId,
                User::setUserId,
                TraineeNotFoundException::new);
    }

    @PostConstruct
    void initTable() {
        try (Scanner sc = new Scanner(traineeInitialData)) {
            log.info("Begin loading Trainee initial data from file");
            while (sc.hasNextLine()) {
                String traineeRow = sc.nextLine();
                if (traineeRow.isBlank()) {
                    log.info("Current Trainee initial data row is blank");
                    continue;
                }
                String[] traineeFields = traineeRow.split(",");
                if (traineeFields.length != 8) {
                    log.debug("Incomplete Trainee fields for Trainee initialization");
                    continue;
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

                table.put(traineeId, trainee);
                log.debug("Trainee with ID {} saved in persistence unit from file", traineeId);
            }
            log.info("Loaded Trainee initial data from file");
        } catch (FileNotFoundException ex) {
            log.error("Trainee initial data file was not found", ex);
        }
    }
}
