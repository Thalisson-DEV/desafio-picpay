package com.desafiopicpay.dtos;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public record ExceptionResponseDTO(
        LocalDateTime timestamp,
        String message,
        String instance,
        HttpStatus status
) {}
