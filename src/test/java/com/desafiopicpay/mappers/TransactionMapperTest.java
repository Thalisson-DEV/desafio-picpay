package com.desafiopicpay.mappers;

import com.desafiopicpay.domain.transaction.Transaction;
import com.desafiopicpay.domain.user.User;
import com.desafiopicpay.domain.user.UserType;
import com.desafiopicpay.dtos.TransactionRequestDTO;
import com.desafiopicpay.dtos.TransactionResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TransactionMapperTest {

    private TransactionMapper transactionMapper;

    @BeforeEach
    void setUp() {
        transactionMapper = new TransactionMapperImpl();
    }

    @Test
    @DisplayName("Should map TransactionRequestDTO to Transaction entity")
    void toTransactionEntity() {
        TransactionRequestDTO request = new TransactionRequestDTO(new BigDecimal("50.00"), 1L, 2L);
        User sender = new User(1L, "John", "Doe", "111", "john@test.com", "123", new BigDecimal("100"), UserType.COMMON, Collections.emptySet());
        User receiver = new User(2L, "Jane", "Doe", "222", "jane@test.com", "456", new BigDecimal("100"), UserType.MERCHANT, Collections.emptySet());

        Transaction transaction = transactionMapper.toTransactionEntity(request, sender, receiver);

        assertNotNull(transaction);
        assertEquals(request.value(), transaction.getAmount());
        assertEquals(sender, transaction.getSender());
        assertEquals(receiver, transaction.getReceiver());
    }

    @Test
    @DisplayName("Should map Transaction entity to TransactionResponseDTO")
    void toTransactionDTO() {
        User sender = new User(1L, "John", "Doe", "111", "john@test.com", "123", new BigDecimal("100"), UserType.COMMON, Collections.emptySet());
        User receiver = new User(2L, "Jane", "Doe", "222", "jane@test.com", "456", new BigDecimal("100"), UserType.MERCHANT, Collections.emptySet());
        
        Transaction transaction = new Transaction();
        transaction.setId(10L);
        transaction.setAmount(new BigDecimal("50.00"));
        transaction.setSender(sender);
        transaction.setReceiver(receiver);

        TransactionResponseDTO dto = transactionMapper.toTransactionDTO(transaction);

        assertNotNull(dto);
        assertEquals(transaction.getAmount(), dto.value());
        assertEquals(sender.getId(), dto.senderId());
        assertEquals(receiver.getId(), dto.receiverId());
    }
}
