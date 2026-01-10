package com.desafiopicpay.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TransactionRequestDTO(
        @Positive(message = "Value must be positive")
        @NotNull(message = "Value cannot be null")
        BigDecimal value,
        @NotNull(message = "Sender cannot be null")
        Long senderId,
        @NotNull(message = "Receiver cannot be null")
        Long receiverId
) {}
