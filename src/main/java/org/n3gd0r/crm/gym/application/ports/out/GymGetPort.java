package org.n3gd0r.crm.gym.application.ports.out;

public interface GymGetPort<T, K> {
    T getEntity(K id);
}
