package com.desafiopicpay.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionResponseDTO(
        Long id,
        BigDecimal value,
        Long senderId,
        Long receiverId,
        LocalDate timestamp
) {}
