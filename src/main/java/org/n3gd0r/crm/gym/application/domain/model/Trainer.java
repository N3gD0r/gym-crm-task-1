package org.n3gd0r.crm.gym.application.domain.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Trainer extends User {
    private TrainingType specialization;

    public Trainer(String firstname, String lastname, boolean active, TrainingType specialization) {
        super(firstname, lastname, active);
        this.specialization = specialization;
    }
}
