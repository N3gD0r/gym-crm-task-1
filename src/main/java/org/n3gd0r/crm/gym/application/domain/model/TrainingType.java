package org.n3gd0r.crm.gym.application.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
public class TrainingType {
    private Long id;
    private Type type;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TrainingType that)) return false;
        return Objects.equals(id, that.id) && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type);
    }

    enum Type {
        FITNESS,
        YOGA,
        RESISTANCE,
        ZUMBA,
        STRETCHING
    }
}
