package org.n3gd0r.crm.gym.application.ports.out;

public interface GymDeletePort<T> {
    void remove(T entity);
}
