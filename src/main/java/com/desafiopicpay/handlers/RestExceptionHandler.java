package com.desafiopicpay.handlers;

import com.desafiopicpay.dtos.ExceptionResponseDTO;
import com.desafiopicpay.exceptions.InvalidOperationException;
import com.desafiopicpay.exceptions.ServiceUnivaliablleException;
import jakarta.persistence.EntityNotFoundException;
import lombok.NonNull;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<@NonNull ExceptionResponseDTO> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        ExceptionResponseDTO exceptionResponse = new ExceptionResponseDTO(
                LocalDateTime.now(),
                e.getMessage(),
                "/api/v1/users",
                status.value()
        );

        return ResponseEntity.badRequest().body(exceptionResponse);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<@NonNull ExceptionResponseDTO> handleEntityNotFoundException() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(InvalidOperationException.class)
    public ResponseEntity<@NonNull ExceptionResponseDTO> handleInvalidOperationException(InvalidOperationException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        ExceptionResponseDTO exceptionResponse = new ExceptionResponseDTO(
                LocalDateTime.now(),
                e.getMessage(),
                "/api/v1/transactions",
                status.value()
        );

        return ResponseEntity.badRequest().body(exceptionResponse);
    }

    @ExceptionHandler(ServiceUnivaliablleException.class)
    public ResponseEntity<@NonNull ExceptionResponseDTO> handleServiceUnivaliablleException(ServiceUnivaliablleException e) {
        HttpStatus status = HttpStatus.BAD_GATEWAY;

        ExceptionResponseDTO exceptionResponse = new ExceptionResponseDTO(
                LocalDateTime.now(),
                e.getMessage(),
                null,
                status.value()
        );

        return ResponseEntity.internalServerError().body(exceptionResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<@NonNull ExceptionResponseDTO> handleMethodArgumentNotValidException() {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        ExceptionResponseDTO exceptionResponse = new ExceptionResponseDTO(
                LocalDateTime.now(),
                "Invalid request parameters.",
                null,
                status.value()
        );

        return ResponseEntity.badRequest().body(exceptionResponse);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<@NonNull ExceptionResponseDTO> handleBadCredentialsException(BadCredentialsException e) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;

        ExceptionResponseDTO exceptionResponse = new ExceptionResponseDTO(
                LocalDateTime.now(),
                e.getMessage(),
                "api/v1/login",
                status.value()
        );

        return ResponseEntity.internalServerError().body(exceptionResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<@NonNull ExceptionResponseDTO> handleGeneralException(Exception e) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        ExceptionResponseDTO exceptionResponse = new ExceptionResponseDTO(
                LocalDateTime.now(),
                e.getMessage(),
                null,
                status.value()
        );

        return ResponseEntity.internalServerError().body(exceptionResponse);
    }
}
