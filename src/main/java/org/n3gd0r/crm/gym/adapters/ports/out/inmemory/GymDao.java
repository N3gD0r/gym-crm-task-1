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

    public GymDao(Map<Long, T> table, Class<T> type, Function<T, Long> readId, BiConsumer<T, Long> writeId, Function<Long, RuntimeException> notFound) {
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
            log.error("Unable to remove a {} entity with ID {}: not found in persistence unit", type.getSimpleName(), id);
            throw notFound.apply(id);
        }
        log.info("Removed a {} entity from persistence unit", type.getSimpleName());
        log.debug("Removed a {} entity with ID {}", type.getSimpleName(), id);
    }

    @Override
    public T getEntity(Long id) {
        T entity = id == null ? null : table.get(id);
        if (entity == null) {
            log.error("{} with ID {} was not found in persistence unit", type.getSimpleName(), id);
            throw notFound.apply(id);
        }
        return entity;
    }

    @Override
    public void persist(T entity) {
        Long id = readId.apply(entity);
        if (id == null) {
            id = lastId.incrementAndGet();
            writeId.accept(entity, id);
            log.debug("{} entity ID is null, adding new ID {}", type.getSimpleName(), id);
        } else {
            log.debug("Updating {} entity with ID {}", type.getSimpleName(), id);
        }
        table.put(id, entity);
        log.info("Saved a new {} entity with ID {}", type.getSimpleName(), id);
    }

    private void validateEntity(T entity) {
        if (entity == null) {
            log.error("{} entity is null", type.getSimpleName());
            throw new GymEntityNullException(type);
        }
    }
}
