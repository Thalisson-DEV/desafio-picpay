package com.desafiopicpay.controllers;

import com.desafiopicpay.dtos.TransactionRequestDTO;
import com.desafiopicpay.dtos.TransactionResponseDTO;
import com.desafiopicpay.services.TransactionService;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.desafiopicpay.dtos.PaginatedTransactionsResponseDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<@NonNull TransactionResponseDTO> createTransaction(@Valid @RequestBody TransactionRequestDTO transaction) {
        TransactionResponseDTO newTransaction = transactionService.createTransaction(transaction);
        return ResponseEntity.ok(newTransaction);
    }

    @GetMapping("/{id}")
    public ResponseEntity<@NonNull TransactionResponseDTO> findTransactionById(@NonNull @PathVariable Long id) {
        TransactionResponseDTO transaction = transactionService.findTransactionById(id);
        return ResponseEntity.ok(transaction);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<@NonNull PaginatedTransactionsResponseDTO> findAllTransactionsByUserIdPaginated(Pageable pageable, @NonNull @PathVariable Long userId) {
        PaginatedTransactionsResponseDTO transactions = transactionService.findAllTransactionsByUserIdPaginated(pageable, userId);
        return ResponseEntity.ok(transactions);
    }
}
