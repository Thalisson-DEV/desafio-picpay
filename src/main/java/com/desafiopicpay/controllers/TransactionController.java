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
}
