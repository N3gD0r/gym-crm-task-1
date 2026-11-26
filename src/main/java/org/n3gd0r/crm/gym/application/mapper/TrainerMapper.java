package org.n3gd0r.crm.gym.application.mapper;

import org.mapstruct.*;
import org.n3gd0r.crm.gym.application.domain.model.Trainer;
import org.n3gd0r.crm.gym.application.ports.in.command.trainer.CreateTrainerCommand;
import org.n3gd0r.crm.gym.application.ports.in.command.trainer.UpdateTrainerCommand;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.SETTER, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TrainerMapper {
    @Mapping(target = "userId", ignore = true)
    Trainer toTrainer(CreateTrainerCommand command, String username, String password);

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "username", ignore = true)
    void updateTrainer(UpdateTrainerCommand command, @MappingTarget Trainer trainer);
}
