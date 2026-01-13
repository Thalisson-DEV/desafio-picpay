package com.desafiopicpay.mappers;

import com.desafiopicpay.domain.transaction.Transaction;
import com.desafiopicpay.domain.user.User;
import com.desafiopicpay.dtos.PaginatedTransactionsResponseDTO;
import com.desafiopicpay.dtos.TransactionRequestDTO;
import com.desafiopicpay.dtos.TransactionResponseDTO;
import lombok.NonNull;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

@Mapper(imports = {LocalDateTime.class})
public interface TransactionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "timestamp", expression = "java(LocalDateTime.now())")
    @Mapping(source = "dto.value", target = "amount")
    @Mapping(source = "sender", target = "sender")
    @Mapping(source = "receiver", target = "receiver")
    Transaction toTransactionEntity(TransactionRequestDTO dto, User sender, User receiver);

    @Mapping(target = "value", source = "amount")
    TransactionResponseDTO toTransactionResponseDTO(Transaction entity);

    default PaginatedTransactionsResponseDTO toPaginatedResponseDTO(Page<@NonNull Transaction> page) {
        return new PaginatedTransactionsResponseDTO(
                page.getContent()
                        .stream()
                        .map(this::toTransactionResponseDTO)
                        .toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}
