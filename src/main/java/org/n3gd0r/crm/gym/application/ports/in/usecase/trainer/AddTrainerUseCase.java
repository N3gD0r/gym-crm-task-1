package org.n3gd0r.crm.gym.application.ports.in.usecase.trainer;

import org.n3gd0r.crm.gym.application.domain.model.Trainer;
import org.n3gd0r.crm.gym.application.ports.in.command.trainer.CreateTrainerCommand;

public interface AddTrainerUseCase {
    Trainer addTrainer(CreateTrainerCommand command);
}
