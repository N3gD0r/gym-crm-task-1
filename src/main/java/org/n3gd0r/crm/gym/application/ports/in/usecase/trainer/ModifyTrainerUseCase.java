package org.n3gd0r.crm.gym.application.ports.in.usecase.trainer;

import org.n3gd0r.crm.gym.application.ports.in.command.trainer.UpdateTrainerCommand;

public interface ModifyTrainerUseCase {
    void updateTrainer(UpdateTrainerCommand command);
}
