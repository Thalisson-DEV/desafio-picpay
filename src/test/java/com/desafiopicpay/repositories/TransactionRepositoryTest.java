package com.desafiopicpay.repositories;

import com.desafiopicpay.domain.transaction.Transaction;
import com.desafiopicpay.domain.user.User;
import com.desafiopicpay.domain.user.UserType;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("Should save and retrieve Transaction successfully")
    void saveAndRetrieveTransactionSuccess() {
        User sender = createUser("Sender", "111", "sender@test.com");
        User receiver = createUser("Receiver", "222", "receiver@test.com");

        Transaction transaction = new Transaction();
        transaction.setAmount(new BigDecimal("50.00"));
        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setTimestamp(LocalDateTime.now());

        Transaction savedTransaction = transactionRepository.save(transaction);
        
        Optional<Transaction> retrievedTransaction = transactionRepository.findById(savedTransaction.getId());
        
        assertThat(retrievedTransaction.isPresent()).isTrue();
        assertThat(retrievedTransaction.get().getId()).isEqualTo(savedTransaction.getId());
        assertThat(retrievedTransaction.get().getAmount()).isEqualByComparingTo(new BigDecimal("50.00"));
        assertThat(retrievedTransaction.get().getSender()).isEqualTo(sender);
        assertThat(retrievedTransaction.get().getReceiver()).isEqualTo(receiver);
    }

    private User createUser(String firstName, String document, String email) {
        User newUser = new User();
        newUser.setFirstName(firstName);
        newUser.setLastName("Test");
        newUser.setDocument(document);
        newUser.setBalance(new BigDecimal("100.00"));
        newUser.setEmail(email);
        newUser.setPassword("password");
        newUser.setUserType(UserType.COMMON);

        this.entityManager.persist(newUser);
        return newUser;
    }
}
