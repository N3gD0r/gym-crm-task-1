package org.n3gd0r.crm.gym.application.ports.out;

public interface GymRepository<T, K> extends GymDeletePort<T>, GymGetPort<T, K>, GymPersistPort<T> {
}
