package org.n3gd0r.crm.gym.application.ports.in.usecase.trainee;

import org.n3gd0r.crm.gym.application.domain.model.Trainee;
import org.n3gd0r.crm.gym.application.ports.in.query.trainee.GetTraineeQuery;

public interface GetTraineeUseCase {
    Trainee getTrainee(GetTraineeQuery query);
}
