package org.n3gd0r.crm.gym.application.ports.in.usecase.training;

import org.n3gd0r.crm.gym.application.domain.model.Training;
import org.n3gd0r.crm.gym.application.ports.in.command.training.CreateTrainingCommand;

public interface AddTrainingUseCase {
    Training addTraining(CreateTrainingCommand command);
}
