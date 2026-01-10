package com.desafiopicpay.services;

import com.desafiopicpay.domain.transaction.Transaction;
import com.desafiopicpay.domain.user.User;
import com.desafiopicpay.domain.user.UserType;
import com.desafiopicpay.dtos.TransactionRequestDTO;
import com.desafiopicpay.dtos.TransactionResponseDTO;
import com.desafiopicpay.exceptions.InvalidOperationException;
import com.desafiopicpay.mappers.TransactionMapper;
import com.desafiopicpay.repositories.TransactionRepository;
import lombok.NonNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @Mock
    private NotificationService notificationService;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    @DisplayName("Should create transaction successfully when authorized")
    void createTransactionSuccess() {
        User sender = new User(1L, "John", "Doe", "111", "john@test.com", "123", new BigDecimal("100.00"), UserType.COMMON, Collections.emptySet());
        User receiver = new User(2L, "Jane", "Doe", "222", "jane@test.com", "123", new BigDecimal("50.00"), UserType.COMMON, Collections.emptySet());
        TransactionRequestDTO request = new TransactionRequestDTO(new BigDecimal("10.00"), 1L, 2L);
        Transaction transaction = new Transaction(); 
        TransactionResponseDTO responseDTO = new TransactionResponseDTO(1L, new BigDecimal("10.00"), 1L, 2L, LocalDate.now());

        when(userService.findUserEntityById(1L)).thenReturn(sender);
        when(userService.findUserEntityById(2L)).thenReturn(receiver);
        
        // Mock authorization
        ResponseEntity<@NonNull Map> authResponse = new ResponseEntity<>(Map.of("status", "Autorizado"), HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(Map.class))).thenReturn(authResponse);

        when(transactionMapper.toTransactionEntity(request, sender, receiver)).thenReturn(transaction);
        when(transactionRepository.save(transaction)).thenReturn(transaction);
        when(transactionMapper.toTransactionDTO(transaction)).thenReturn(responseDTO);

        TransactionResponseDTO result = transactionService.createTransaction(request);

        assertNotNull(result);
        assertEquals(responseDTO, result);
        
        verify(userService, times(1)).validateTransaction(sender, new BigDecimal("10.00"));
        verify(transactionRepository, times(1)).save(transaction);
        verify(userService, times(1)).saveUser(sender);
        verify(userService, times(1)).saveUser(receiver);
        verify(notificationService, times(2)).sendNotification(anyString(), anyString());
    }

    @Test
    @DisplayName("Should throw exception when transaction is unauthorized")
    void createTransactionUnauthorized() {
        User sender = new User(1L, "John", "Doe", "111", "john@test.com", "123", new BigDecimal("100.00"), UserType.COMMON, Collections.emptySet());
        User receiver = new User(2L, "Jane", "Doe", "222", "jane@test.com", "123", new BigDecimal("50.00"), UserType.COMMON, Collections.emptySet());
        TransactionRequestDTO request = new TransactionRequestDTO(new BigDecimal("10.00"), 1L, 2L);

        when(userService.findUserEntityById(1L)).thenReturn(sender);
        when(userService.findUserEntityById(2L)).thenReturn(receiver);

        // Mock authorization fail
        ResponseEntity<@NonNull Map> authResponse = new ResponseEntity<>(Map.of("status", "Negado"), HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(Map.class))).thenReturn(authResponse);

        assertThrows(InvalidOperationException.class, () -> transactionService.createTransaction(request));
        
        verify(transactionRepository, never()).save(any());
        verify(notificationService, never()).sendNotification(anyString(), anyString());
    }
}
