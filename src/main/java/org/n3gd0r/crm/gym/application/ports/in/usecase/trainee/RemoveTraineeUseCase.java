package org.n3gd0r.crm.gym.application.ports.in.usecase.trainee;

import org.n3gd0r.crm.gym.application.ports.in.command.trainee.DeleteTraineeCommand;

public interface RemoveTraineeUseCase {
    void removeTrainee(DeleteTraineeCommand command);
}
