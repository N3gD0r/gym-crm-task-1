package org.n3gd0r.crm.gym.adapters.ports.out.inmemory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.n3gd0r.crm.gym.application.domain.exception.GymEntityNullException;
import org.n3gd0r.crm.gym.application.domain.exception.TraineeNotFoundException;
import org.n3gd0r.crm.gym.application.domain.model.Trainee;

import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GymDaoTest {
    private TraineeDao dao;

    private static Trainee trainee(Long id) {
        var trainee = new Trainee("Test", "Profile", null, null, true);
        trainee.setUserId(id);
        return trainee;
    }

    @BeforeEach
    void setUp() {
        dao = new TraineeDao(new ConcurrentHashMap<>());
    }

    @Test
    void preservesSeedsAndDoesNotRecycleIdsAfterDeletion() {
        var seeded = trainee(20L);
        assertThat(dao.insertSeed(seeded)).isTrue();
        assertThat(dao.insertSeed(trainee(20L))).isFalse();
        var first = trainee(null);
        dao.persist(first);
        assertThat(first.getUserId()).isEqualTo(21L);
        assertThat(dao.getEntity(20L)).isSameAs(seeded);

        dao.remove(first);
        var second = trainee(null);
        dao.persist(second);
        assertThat(second.getUserId()).isEqualTo(22L);
    }

    @Test
    void rejectsNullEntitiesAndReportsMissingIds() {
        assertThatThrownBy(() -> dao.persist(null)).isInstanceOf(GymEntityNullException.class);
        assertThatThrownBy(() -> dao.remove(null)).isInstanceOf(GymEntityNullException.class);
        assertThatThrownBy(() -> dao.remove(trainee(null))).isInstanceOf(TraineeNotFoundException.class);
        assertThatThrownBy(() -> dao.remove(trainee(1L))).isInstanceOf(TraineeNotFoundException.class);
        assertThatThrownBy(() -> dao.getEntity(null)).isInstanceOf(TraineeNotFoundException.class);
    }
}
