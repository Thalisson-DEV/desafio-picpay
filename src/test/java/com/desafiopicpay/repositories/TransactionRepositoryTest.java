package com.desafiopicpay.repositories;

import com.desafiopicpay.domain.transaction.Transaction;
import com.desafiopicpay.domain.user.User;
import com.desafiopicpay.domain.user.UserType;
import lombok.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    private User sender;
    private User receiver;

    @BeforeEach
    void setUp() {
        sender = new User();
        sender.setFirstName("Sender");
        sender.setLastName("User");
        sender.setDocument("12345678901");
        sender.setEmail("sender@test.com");
        sender.setPassword("password");
        sender.setBalance(new BigDecimal("1000.00"));
        sender.setUserType(UserType.COMMON);
        userRepository.save(sender);

        receiver = new User();
        receiver.setFirstName("Receiver");
        receiver.setLastName("User");
        receiver.setDocument("98765432109");
        receiver.setEmail("receiver@test.com");
        receiver.setPassword("password");
        receiver.setBalance(new BigDecimal("500.00"));
        receiver.setUserType(UserType.COMMON);
        userRepository.save(receiver);
    }

    @Test
    @DisplayName("Should find transactions by sender or receiver ID")
    void findAllBySenderIdOrReceiverId_Success() {
        Transaction transaction = new Transaction();
        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setAmount(new BigDecimal("100.00"));
        transaction.setTimestamp(LocalDateTime.now());
        transactionRepository.save(transaction);

        Page<@NonNull Transaction> result = transactionRepository.findAllBySenderIdOrReceiverId(
                sender.getId(), sender.getId(), PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getAmount()).isEqualByComparingTo("100.00");
    }

    @Test
    @DisplayName("Should find transactions by sender ID")
    void findAllBySenderId_Success() {
        Transaction transaction = new Transaction();
        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setAmount(new BigDecimal("100.00"));
        transaction.setTimestamp(LocalDateTime.now());
        transactionRepository.save(transaction);

        Page<@NonNull Transaction> result = transactionRepository.findAllBySenderId(sender.getId(), PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("Should find transactions by receiver ID")
    void findAllByReceiverId_Success() {
        Transaction transaction = new Transaction();
        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setAmount(new BigDecimal("100.00"));
        transaction.setTimestamp(LocalDateTime.now());
        transactionRepository.save(transaction);

        Page<@NonNull Transaction> result = transactionRepository.findAllByReceiverId(receiver.getId(), PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
    }
}
