package com.desafiopicpay.repositories;

import com.desafiopicpay.domain.user.User;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<@NonNull User, @NonNull Long> {
    Optional<User> findUserByDocument(String document);
    Optional<User> findUserById(Long id);
    Optional<User> findUserByEmail(String email);
}
