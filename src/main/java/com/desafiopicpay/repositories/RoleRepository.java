package com.desafiopicpay.repositories;

import com.desafiopicpay.domain.role.Role;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<@NonNull Role, @NonNull Long> {
}
