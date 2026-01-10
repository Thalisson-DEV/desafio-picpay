package com.desafiopicpay.dtos;


import com.desafiopicpay.domain.role.Role;
import com.desafiopicpay.domain.user.UserType;

import java.math.BigDecimal;
import java.util.Set;

public record UserResponseDTO(
        Long id,
        String firstName,
        String lastName,
        String document,
        String email,
        BigDecimal balance,
        UserType userType,
        Set<Role> roles
) {}
