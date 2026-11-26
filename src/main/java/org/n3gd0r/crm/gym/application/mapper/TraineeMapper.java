package org.n3gd0r.crm.gym.application.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.n3gd0r.crm.gym.application.domain.model.Trainee;
import org.n3gd0r.crm.gym.application.ports.in.command.trainee.CreateTraineeCommand;
import org.n3gd0r.crm.gym.application.ports.in.command.trainee.UpdateTraineeCommand;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.SETTER)
public interface TraineeMapper {
    @Mapping(target = "userId", ignore = true)
    Trainee toTrainee(CreateTraineeCommand command, String username, String password);

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "username", ignore = true)
    void updateTrainee(UpdateTraineeCommand command, @MappingTarget Trainee trainee);
}
