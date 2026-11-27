package org.n3gd0r.crm.gym.adapters.ports.out.inmemory;

import lombok.extern.slf4j.Slf4j;
import org.n3gd0r.crm.gym.application.domain.exception.GymEntityNullException;
import org.n3gd0r.crm.gym.application.ports.out.GymRepository;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.function.Function;

@Slf4j
public abstract class GymDao<T> implements GymRepository<T, Long> {
    protected final Map<Long, T> table;
    private final Class<T> type;
    private final Function<T, Long> readId;
    private final BiConsumer<T, Long> writeId;
    private final Function<Long, RuntimeException> notFound;
    private final AtomicLong lastId;

    protected GymDao(Map<Long, T> table, Class<T> type, Function<T, Long> readId, BiConsumer<T, Long> writeId, Function<Long, RuntimeException> notFound) {
        this.table = table;
        this.type = type;
        this.readId = readId;
        this.writeId = writeId;
        this.notFound = notFound;
        this.lastId = new AtomicLong();
    }


    @Override
    public void remove(T entity) {
        validateEntity(entity);
        Long id = readId.apply(entity);
        if (id == null || table.remove(id) == null) {
            log.debug("{} with ID {} was not found for removal", type.getSimpleName(), id);
            throw notFound.apply(id);
        }
        log.debug("Removed {} with ID {}", type.getSimpleName(), id);
    }

    @Override
    public T getEntity(Long id) {
        T entity = id == null ? null : table.get(id);
        if (entity == null) {
            log.debug("{} with ID {} was not found", type.getSimpleName(), id);
            throw notFound.apply(id);
        }
        return entity;
    }

    @Override
    public void persist(T entity) {
        validateEntity(entity);
        Long id = readId.apply(entity);
        if (id == null) {
            id = lastId.updateAndGet(Math::incrementExact);
            writeId.accept(entity, id);
        } else {
            lastId.accumulateAndGet(id, Math::max);
        }
        table.put(id, entity);
        log.debug("Persisted {} with ID {}", type.getSimpleName(), id);
    }

    boolean insertSeed(T entity) {
        Long id = readId.apply(entity);
        if (table.putIfAbsent(id, entity) != null) {
            return false;
        }
        lastId.accumulateAndGet(id, Math::max);
        return true;
    }

    private void validateEntity(T entity) {
        if (entity == null) {
            log.debug("Null {} entity rejected", type.getSimpleName());
            throw new GymEntityNullException(type);
        }
    }
}
