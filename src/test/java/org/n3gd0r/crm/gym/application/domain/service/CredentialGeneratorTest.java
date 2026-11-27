package org.n3gd0r.crm.gym.application.domain.service;

import org.junit.jupiter.api.Test;

import java.security.SecureRandom;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CredentialGeneratorTest {
    @Test
    void keepsNamesAndUsesTheFirstAvailableSerialOnCollision() {
        var occupiedNames = Set.of("Ada.Lovelace", "Ada.Lovelace1", "Ada.Lovelace3");
        var generator = new CredentialGenerator(occupiedNames::contains);

        assertThat(generator.username("Grace", "Hopper")).isEqualTo("Grace.Hopper");
        assertThat(generator.username("Ada", "Lovelace")).isEqualTo("Ada.Lovelace2");
    }

    @Test
    void generatesExactlyTenCharactersFromTheFullAlphabet() {
        var generator = new CredentialGenerator(username -> false);
        generator.setRandom(new SecureRandom() {
            private int calls;

            @Override
            public int nextInt(int bound) {
                return calls++ % 2 == 0 ? 0 : bound - 1;
            }
        });

        assertThat(generator.password()).isEqualTo("A9A9A9A9A9");
    }
}
