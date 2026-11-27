package org.n3gd0r.crm.gym.adapters.ports.out.inmemory;

import org.n3gd0r.crm.gym.application.domain.model.TrainingType;

class Utils {
    static TrainingType type(String typeStr) {
        return switch (typeStr) {
            case "FITNESS" -> TrainingType.FITNESS;
            case "YOGA" -> TrainingType.YOGA;
            case "RESISTANCE" -> TrainingType.RESISTANCE;
            case "ZUMBA" -> TrainingType.ZUMBA;
            case "STRETCHING" -> TrainingType.STRETCHING;
            default -> TrainingType.UNKNOWN;
        };
    }
}
