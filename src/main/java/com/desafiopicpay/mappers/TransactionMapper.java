package com.desafiopicpay.mappers;

import com.desafiopicpay.domain.transaction.Transaction;
import com.desafiopicpay.domain.user.User;
import com.desafiopicpay.dtos.TransactionRequestDTO;
import com.desafiopicpay.dtos.TransactionResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "timestamp", ignore = true)
    @Mapping(source = "dto.value", target = "amount")
    @Mapping(source = "sender", target = "sender")
    @Mapping(source = "receiver", target = "receiver")
    Transaction toTransactionEntity(TransactionRequestDTO dto, User sender, User receiver);

    @Mapping(target = "value", source = "amount")
    @Mapping(target = "senderId", source = "sender.id")
    @Mapping(target = "receiverId", source = "receiver.id")
    TransactionResponseDTO toTransactionDTO(Transaction entity);
}
