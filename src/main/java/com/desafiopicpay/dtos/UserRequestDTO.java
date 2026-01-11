package com.desafiopicpay.dtos;

import com.desafiopicpay.domain.role.Role;
import com.desafiopicpay.domain.user.UserType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

import java.util.Set;

public record UserRequestDTO(
        @NotBlank(message = "First name cannot be blank")
        String firstName,
        @NotBlank(message = "Last name cannot be blank")
        String lastName,
        @NotBlank(message = "Document cannot be blank")
        String document,
        @NotNull(message = "Balance cannot be null")
        @Positive(message = "Balance must be positive")
        BigDecimal balance,
        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Invalid email format")
        String email,
        @NotBlank(message = "Password cannot be blank")
        String password,
        @NotNull(message = "User type cannot be null")
        UserType userType,
        @NotNull(message = "Roles cannot be null")
        Set<Role> roles
) { }
