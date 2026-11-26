package org.n3gd0r.crm.gym.application.ports.out;

public interface GymPersistPort<T> {
    void persist(T entity);
}
