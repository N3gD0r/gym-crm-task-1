package org.n3gd0r.crm.gym.application.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.n3gd0r.crm.gym.application.domain.model.Training;
import org.n3gd0r.crm.gym.application.ports.in.command.training.CreateTrainingCommand;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.SETTER)
public interface TrainingMapper {
    @Mapping(target = "id", ignore = true)
    Training toTraining(CreateTrainingCommand command);
}
