package com.desafiopicpay.repositories;

import com.desafiopicpay.domain.user.User;
import com.desafiopicpay.domain.user.UserType;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("Should get User successfully from DB")
    void findUserByDocumentSuccess() {
        String document = "12345678901";
        UserRequestDTO data = new UserRequestDTO("John", "Doe", document, new BigDecimal("100"), "test@test.com", "123", UserType.COMMON);
        this.createUser(data);

        Optional<User> result = this.userRepository.findUserByDocument(document);

        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().getDocument()).isEqualTo(document);
    }

    @Test
    @DisplayName("Should not get User from DB when user not exists")
    void findUserByDocumentFail() {
        String document = "12345678901";

        Optional<User> result = this.userRepository.findUserByDocument(document);

        assertThat(result.isEmpty()).isTrue();
    }
    
    @Test
    @DisplayName("Should find User by ID successfully")
    void findUserByIdSuccess() {
        String document = "12345678901";
        UserRequestDTO data = new UserRequestDTO("John", "Doe", document, new BigDecimal("100"), "test@test.com", "123", UserType.COMMON);
        User createdUser = this.createUser(data);

        Optional<User> result = this.userRepository.findUserById(createdUser.getId());

        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().getId()).isEqualTo(createdUser.getId());
    }

    private User createUser(UserRequestDTO data) {
        User newUser = new User();
        newUser.setFirstName(data.firstName());
        newUser.setLastName(data.lastName());
        newUser.setDocument(data.document());
        newUser.setBalance(data.balance());
        newUser.setEmail(data.email());
        newUser.setPassword(data.password());
        newUser.setUserType(data.userType());
        
        this.entityManager.persist(newUser);
        return newUser;
    }
    
    // Helper DTO class for test
    record UserRequestDTO(String firstName, String lastName, String document, BigDecimal balance, String email, String password, UserType userType) {}
}
