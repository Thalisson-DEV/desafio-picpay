package com.desafiopicpay.repositories;

import com.desafiopicpay.domain.user.User;
import com.desafiopicpay.domain.user.UserType;
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

    @Test
    @DisplayName("Should get User successfully from DB")
    void findUserByDocument_Success() {
        String document = "12345678900";
        User user = new User();
        user.setDocument(document);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("test@test.com");
        user.setBalance(new BigDecimal("10.0"));
        user.setUserType(UserType.COMMON);
        user.setPassword("password");
        
        this.userRepository.save(user);

        Optional<User> result = this.userRepository.findUserByDocument(document);

        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().getDocument()).isEqualTo(document);
    }

    @Test
    @DisplayName("Should not get User from DB when user not exists")
    void findUserByDocument_Failure() {
        String document = "12345678900";

        Optional<User> result = this.userRepository.findUserByDocument(document);

        assertThat(result.isEmpty()).isTrue();
    }
    
    @Test
    @DisplayName("Should find user by ID")
    void findUserById_Success() {
        User user = new User();
        user.setDocument("11122233344");
        user.setFirstName("Jane");
        user.setLastName("Doe");
        user.setEmail("jane@test.com");
        user.setBalance(new BigDecimal("10.0"));
        user.setUserType(UserType.COMMON);
        user.setPassword("password");
        
        User savedUser = this.userRepository.save(user);

        Optional<User> result = this.userRepository.findUserById(savedUser.getId());

        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().getId()).isEqualTo(savedUser.getId());
    }

    @Test
    @DisplayName("Should find user by Email")
    void findUserByEmail_Success() {
        String email = "email@test.com";
        User user = new User();
        user.setDocument("55566677788");
        user.setFirstName("Bob");
        user.setLastName("Builder");
        user.setEmail(email);
        user.setBalance(new BigDecimal("10.0"));
        user.setUserType(UserType.COMMON);
        user.setPassword("password");
        
        this.userRepository.save(user);

        Optional<User> result = this.userRepository.findUserByEmail(email);

        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().getEmail()).isEqualTo(email);
    }
}
