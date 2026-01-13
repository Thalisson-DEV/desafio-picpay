package com.desafiopicpay.repositories;

import com.desafiopicpay.domain.role.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    @DisplayName("Should save and find role by ID")
    void saveAndFindById_Success() {
        Role role = new Role();
        role.setName("ADMIN");
        Role savedRole = roleRepository.save(role);

        Optional<Role> result = roleRepository.findById(savedRole.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("ADMIN");
    }

    @Test
    @DisplayName("Should return empty when role not found")
    void findById_NotFound() {
        Optional<Role> result = roleRepository.findById(999L);
        assertThat(result).isEmpty();
    }
}
