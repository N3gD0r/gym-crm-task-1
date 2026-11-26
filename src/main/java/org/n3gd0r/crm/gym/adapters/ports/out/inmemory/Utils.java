package org.n3gd0r.crm.gym.adapters.ports.out.inmemory;

import org.n3gd0r.crm.gym.application.domain.model.TrainingType;

class Utils {
    static TrainingType type(String typeStr) {
        Long typeId = null;
        TrainingType.Type type = null;
        switch (typeStr) {
            case "FITNESS" -> {
                type = TrainingType.Type.FITNESS;
                typeId = 1L;
            }
            case "YOGA" -> {
                type = TrainingType.Type.YOGA;
                typeId = 2L;
            }
            case "RESISTANCE" -> {
                type = TrainingType.Type.RESISTANCE;
                typeId = 3L;
            }
            case "ZUMBA" -> {
                type = TrainingType.Type.ZUMBA;
                typeId = 4L;
            }
            case "STRETCHING" -> {
                type = TrainingType.Type.STRETCHING;
                typeId = 5L;
            }
        }
        return new TrainingType(typeId, type);
    }
}
