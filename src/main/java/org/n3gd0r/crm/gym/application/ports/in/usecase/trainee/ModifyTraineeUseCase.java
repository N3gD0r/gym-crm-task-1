package org.n3gd0r.crm.gym.application.ports.in.usecase.trainee;

import org.n3gd0r.crm.gym.application.ports.in.command.trainee.UpdateTraineeCommand;

public interface ModifyTraineeUseCase {
    void modifyTrainee(UpdateTraineeCommand command);
}
