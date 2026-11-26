package org.n3gd0r.crm.gym.application.ports.in.usecase.trainee;

import org.n3gd0r.crm.gym.application.domain.model.Trainee;
import org.n3gd0r.crm.gym.application.ports.in.command.trainee.CreateTraineeCommand;

public interface AddTraineeUseCase {
    Trainee addTrainee(CreateTraineeCommand command);
}
