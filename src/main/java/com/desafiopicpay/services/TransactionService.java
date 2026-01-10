package com.desafiopicpay.services;

import com.desafiopicpay.domain.transaction.Transaction;
import com.desafiopicpay.domain.user.User;
import com.desafiopicpay.dtos.TransactionRequestDTO;
import com.desafiopicpay.dtos.TransactionResponseDTO;
import com.desafiopicpay.exceptions.InvalidOperationException;
import com.desafiopicpay.mappers.TransactionMapper;
import com.desafiopicpay.repositories.TransactionRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final UserService userService;
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final NotificationService notificationService;
    private final RestTemplate restTemplate;

    @Transactional
    public TransactionResponseDTO createTransaction(@Valid TransactionRequestDTO transaction) {

        User sender = this.userService.findUserEntityById(transaction.senderId());
        User receiver = this.userService.findUserEntityById(transaction.receiverId());

        userService.validateTransaction(sender, transaction.value());

        boolean isAuthorized = this.authorizeTransaction();
        if (!isAuthorized) {
            throw new InvalidOperationException("Unauthorized");
        }

        Transaction newTransaction = this.transactionMapper.toTransactionEntity(transaction, sender, receiver);

        sender.setBalance(sender.getBalance().subtract(transaction.value()));
        receiver.setBalance(receiver.getBalance().add(transaction.value()));

        this.transactionRepository.save(newTransaction);
        this.userService.saveUser(sender);
        this.userService.saveUser(receiver);

        this.notificationService.sendNotification(sender.getEmail(), "Transaction successfully finished");
        this.notificationService.sendNotification(receiver.getEmail(), "Transaction successfully finished");

        return transactionMapper.toTransactionDTO(newTransaction);
    }

    public boolean authorizeTransaction() {
        var authorizationResponse = restTemplate.getForEntity("https://util.devi.tools/api/v2/authorize", Map.class);

        if (authorizationResponse.getStatusCode().is2xxSuccessful() && authorizationResponse.getBody() != null) {
            String message = (String) authorizationResponse.getBody().get("status");
            return "Autorizado".equalsIgnoreCase(message);
        } else return false;
    }

}
