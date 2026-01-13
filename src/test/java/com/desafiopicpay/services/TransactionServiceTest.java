package com.desafiopicpay.services;

import com.desafiopicpay.domain.transaction.Transaction;
import com.desafiopicpay.domain.user.User;
import com.desafiopicpay.domain.user.UserType;
import com.desafiopicpay.dtos.PaginatedTransactionsResponseDTO;
import com.desafiopicpay.dtos.TransactionRequestDTO;
import com.desafiopicpay.dtos.TransactionResponseDTO;
import com.desafiopicpay.dtos.UserResponseDTO;
import com.desafiopicpay.exceptions.InvalidOperationException;
import com.desafiopicpay.mappers.TransactionMapper;
import com.desafiopicpay.repositories.TransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.NonNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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
    private RestTemplate restTemplate;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    @DisplayName("Should create transaction successfully when authorized")
    void createTransaction_Success() {
        // Arrange
        Long senderId = 1L;
        Long receiverId = 2L;
        BigDecimal value = new BigDecimal("100.00");
        TransactionRequestDTO requestDTO = new TransactionRequestDTO(value, senderId, receiverId);

        User sender = new User();
        sender.setId(senderId);
        sender.setBalance(new BigDecimal("200.00"));
        sender.setUserType(UserType.COMMON);
        sender.setEmail("sender@test.com");

        User receiver = new User();
        receiver.setId(receiverId);
        receiver.setBalance(new BigDecimal("50.00"));
        receiver.setEmail("receiver@test.com");

        Transaction transactionEntity = new Transaction();
        transactionEntity.setId(10L);
        transactionEntity.setAmount(value);
        transactionEntity.setSender(sender);
        transactionEntity.setReceiver(receiver);
        transactionEntity.setTimestamp(LocalDateTime.now());

        UserResponseDTO senderResponse = new UserResponseDTO(senderId, "John", "Doe", "doc1", "sender@test.com", new BigDecimal("100.00"), UserType.COMMON, null);
        UserResponseDTO receiverResponse = new UserResponseDTO(receiverId, "Jane", "Doe", "doc2", "receiver@test.com", new BigDecimal("150.00"), UserType.COMMON, null);
        TransactionResponseDTO responseDTO = new TransactionResponseDTO(10L, value, senderResponse, receiverResponse, LocalDateTime.now());

        when(userService.findUserEntityById(senderId)).thenReturn(sender);
        when(userService.findUserEntityById(receiverId)).thenReturn(receiver);
        when(restTemplate.getForEntity(eq("https://util.devi.tools/api/v2/authorize"), eq(Map.class)))
                .thenReturn(new ResponseEntity<>(Map.of("status", "success"), HttpStatus.OK));
        when(transactionMapper.toTransactionEntity(any(), any(), any())).thenReturn(transactionEntity);
        when(transactionMapper.toTransactionResponseDTO(transactionEntity)).thenReturn(responseDTO);

        // Act
        TransactionResponseDTO result = transactionService.createTransaction(requestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(value, result.value());
        assertEquals(new BigDecimal("100.00"), sender.getBalance()); // 200 - 100
        assertEquals(new BigDecimal("150.00"), receiver.getBalance()); // 50 + 100
        
        verify(transactionRepository, times(1)).save(transactionEntity);
        verify(userService, times(1)).saveUser(sender);
        verify(userService, times(1)).saveUser(receiver);
    }

    @Test
    @DisplayName("Should throw InvalidOperationException when transaction unauthorized")
    void createTransaction_Unauthorized() {
        // Arrange
        TransactionRequestDTO requestDTO = new TransactionRequestDTO(new BigDecimal("100.00"), 1L, 2L);
        User sender = new User();
        sender.setBalance(new BigDecimal("200.00"));
        sender.setUserType(UserType.COMMON);
        User receiver = new User();

        when(userService.findUserEntityById(1L)).thenReturn(sender);
        when(userService.findUserEntityById(2L)).thenReturn(receiver);
        
        // Mocking forbidden response
        when(restTemplate.getForEntity(eq("https://util.devi.tools/api/v2/authorize"), eq(Map.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.FORBIDDEN));

        // Act & Assert
        assertThrows(InvalidOperationException.class, () -> transactionService.createTransaction(requestDTO));
        verify(transactionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw InvalidOperationException when sender is MERCHANT")
    void createTransaction_SenderIsMerchant() {
        // Arrange
        TransactionRequestDTO requestDTO = new TransactionRequestDTO(new BigDecimal("100.00"), 1L, 2L);
        User sender = new User();
        sender.setUserType(UserType.MERCHANT);
        sender.setBalance(new BigDecimal("200.00"));
        User receiver = new User();

        when(userService.findUserEntityById(1L)).thenReturn(sender);
        when(userService.findUserEntityById(2L)).thenReturn(receiver);

        // Act & Assert
        assertThrows(InvalidOperationException.class, () -> transactionService.createTransaction(requestDTO));
        verify(restTemplate, never()).getForEntity(anyString(), eq(Map.class));
    }

    @Test
    @DisplayName("Should throw InvalidOperationException when sender has insufficient balance")
    void createTransaction_InsufficientBalance() {
        // Arrange
        TransactionRequestDTO requestDTO = new TransactionRequestDTO(new BigDecimal("300.00"), 1L, 2L);
        User sender = new User();
        sender.setUserType(UserType.COMMON);
        sender.setBalance(new BigDecimal("200.00"));
        User receiver = new User();

        when(userService.findUserEntityById(1L)).thenReturn(sender);
        when(userService.findUserEntityById(2L)).thenReturn(receiver);

        // Act & Assert
        assertThrows(InvalidOperationException.class, () -> transactionService.createTransaction(requestDTO));
    }

    @Test
    @DisplayName("Should find all transactions by user ID paginated")
    void findAllTransactionsByUserIdPaginated_Success() {
        // Arrange
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        Page<@NonNull Transaction> page = new PageImpl<>(List.of(new Transaction()));
        PaginatedTransactionsResponseDTO responseDTO = new PaginatedTransactionsResponseDTO(List.of(), 0, 1, 1, 1);

        when(transactionRepository.findAllBySenderIdOrReceiverId(userId, userId, pageable)).thenReturn(page);
        when(transactionMapper.toPaginatedResponseDTO(page)).thenReturn(responseDTO);

        // Act
        PaginatedTransactionsResponseDTO result = transactionService.findAllTransactionsByUserIdPaginated(pageable, userId);

        // Assert
        assertNotNull(result);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when no transactions found")
    void findAllTransactionsByUserIdPaginated_Empty() {
        // Arrange
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        when(transactionRepository.findAllBySenderIdOrReceiverId(userId, userId, pageable)).thenReturn(Page.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> transactionService.findAllTransactionsByUserIdPaginated(pageable, userId));
    }
}
