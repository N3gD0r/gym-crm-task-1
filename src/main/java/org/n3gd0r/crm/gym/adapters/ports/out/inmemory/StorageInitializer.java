package org.n3gd0r.crm.gym.adapters.ports.out.inmemory;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.n3gd0r.crm.gym.application.domain.exception.TraineeNotFoundException;
import org.n3gd0r.crm.gym.application.domain.exception.TrainerNotFoundException;
import org.n3gd0r.crm.gym.application.domain.model.Trainee;
import org.n3gd0r.crm.gym.application.domain.model.Trainer;
import org.n3gd0r.crm.gym.application.domain.model.Training;
import org.n3gd0r.crm.gym.application.domain.model.TrainingType;
import org.n3gd0r.crm.gym.application.domain.model.User;
import org.n3gd0r.crm.gym.application.ports.out.user.UsernameLookupPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.InvalidPathException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.function.Predicate;

@Slf4j
@Component
@RequiredArgsConstructor
class StorageInitializer {
    private final UsernameLookupPort usernameLookup;
    @Setter(onMethod_ = @Autowired)
    private TraineeDao traineeDao;
    @Setter(onMethod_ = @Autowired)
    private TrainerDao trainerDao;
    @Setter(onMethod_ = @Autowired)
    private TrainingDao trainingDao;
    @Setter(onMethod_ = @Autowired)
    private ResourceLoader resourceLoader;

    private String traineePath;
    private String trainerPath;
    private String trainingPath;

    @Autowired
    void setTraineePath(@Value("${storage.trainee}") String traineePath) {
        this.traineePath = traineePath;
    }

    @Autowired
    void setTrainerPath(@Value("${storage.trainer}") String trainerPath) {
        this.trainerPath = trainerPath;
    }

    @Autowired
    void setTrainingPath(@Value("${storage.training}") String trainingPath) {
        this.trainingPath = trainingPath;
    }

    @PostConstruct
    void initialize() {
        load("trainee", traineePath, 8, fields -> {
            LocalDate birthDate = fields[7].isEmpty() ? null : LocalDate.parse(fields[7]);
            Trainee trainee = new Trainee(required(fields[1]), required(fields[2]), fields[6],
                    birthDate, active(fields[5]));
            return insertProfile(traineeDao, trainee, fields);
        });
        load("trainer", trainerPath, 7, fields -> {
            Trainer trainer = new Trainer(required(fields[1]), required(fields[2]),
                    active(fields[5]), TrainingType.valueOf(fields[6]));
            return insertProfile(trainerDao, trainer, fields);
        });
        load("training", trainingPath, 7, fields -> {
            long trainerId = positiveId(fields[1]);
            long traineeId = positiveId(fields[2]);
            traineeDao.getEntity(traineeId);
            trainerDao.getEntity(trainerId);
            int duration = Integer.parseInt(fields[5]);
            if (duration <= 0) {
                throw new IllegalArgumentException();
            }
            return trainingDao.insertSeed(new Training(positiveId(fields[0]), traineeId, trainerId,
                    required(fields[3]), TrainingType.valueOf(fields[4]), LocalDate.parse(fields[6]), duration));
        });
    }

    private <T extends User> boolean insertProfile(GymDao<T> dao, T profile, String[] fields) {
        profile.setUserId(positiveId(fields[0]));
        profile.setUsername(required(fields[3]));
        profile.setPassword(required(fields[4]));
        return !usernameLookup.existsByName(profile.getUsername()) && dao.insertSeed(profile);
    }

    private void load(String entity, String path, int columns, Predicate<String[]> insertRow) {
        int loaded = 0;
        try (var reader = new BufferedReader(new InputStreamReader(resource(path).getInputStream(), StandardCharsets.UTF_8))) {
            int lineNumber = 0;
            for (String line; (line = reader.readLine()) != null; ) {
                lineNumber++;
                if (line.isBlank() || line.stripLeading().startsWith("#")) {
                    continue;
                }
                String[] fields = line.split(",", -1);
                try {
                    if (fields.length != columns) {
                        log.warn("Skipped {} seed line {}: expected {} fields", entity, lineNumber, columns);
                    } else if (insertRow.test(fields)) {
                        loaded++;
                    } else {
                        log.warn("Skipped {} seed line {}: duplicate ID or username", entity, lineNumber);
                    }
                } catch (IllegalArgumentException | DateTimeParseException invalidFields) {
                    log.warn("Skipped {} seed line {}: invalid field value", entity, lineNumber);
                } catch (TraineeNotFoundException | TrainerNotFoundException missingParticipant) {
                    log.warn("Skipped {} seed line {}: missing participant", entity, lineNumber);
                }
            }
        } catch (IOException | InvalidPathException unreadable) {
            log.warn("Could not read {} seed file; keeping {} loaded records", entity, loaded);
            return;
        }
        log.info("Loaded {} {} seed records", loaded, entity);
    }

    private Resource resource(String path) {
        return path.startsWith("classpath:") || path.startsWith("file:")
                ? resourceLoader.getResource(path)
                : new FileSystemResource(path);
    }

    private static String required(String field) {
        if (field.isBlank()) {
            throw new IllegalArgumentException();
        }
        return field;
    }

    private static long positiveId(String field) {
        long id = Long.parseLong(field);
        if (id <= 0 || id == Long.MAX_VALUE) {
            throw new IllegalArgumentException();
        }
        return id;
    }

    private static boolean active(String field) {
        return switch (field) {
            case "true" -> true;
            case "false" -> false;
            default -> throw new IllegalArgumentException();
        };
    }
}
