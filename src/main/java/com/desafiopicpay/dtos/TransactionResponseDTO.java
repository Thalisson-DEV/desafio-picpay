package com.desafiopicpay.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record TransactionResponseDTO(
        Long id,
        BigDecimal value,
        UserResponseDTO sender,
        UserResponseDTO receiver,
        LocalDateTime timestamp
) {}
