package com.desafiopicpay.mappers;

import com.desafiopicpay.domain.transaction.Transaction;
import com.desafiopicpay.domain.user.User;
import com.desafiopicpay.dtos.TransactionRequestDTO;
import com.desafiopicpay.dtos.TransactionResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class TransactionMapperTest {

    private final TransactionMapper mapper = Mappers.getMapper(TransactionMapper.class);

    @Test
    @DisplayName("Should map TransactionRequestDTO to Transaction entity")
    void toTransactionEntity_Success() {
        TransactionRequestDTO dto = new TransactionRequestDTO(new BigDecimal("100.00"), 1L, 2L);
        User sender = new User(); sender.setId(1L);
        User receiver = new User(); receiver.setId(2L);

        Transaction entity = mapper.toTransactionEntity(dto, sender, receiver);

        assertThat(entity.getAmount()).isEqualByComparingTo("100.00");
        assertThat(entity.getSender().getId()).isEqualTo(1L);
        assertThat(entity.getReceiver().getId()).isEqualTo(2L);
        assertThat(entity.getTimestamp()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should map Transaction entity to TransactionResponseDTO")
    void toTransactionResponseDTO_Success() {
        Transaction entity = new Transaction();
        entity.setId(10L);
        entity.setAmount(new BigDecimal("50.00"));
        entity.setTimestamp(LocalDateTime.now());
        User sender = new User(); sender.setId(1L);
        User receiver = new User(); receiver.setId(2L);
        entity.setSender(sender);
        entity.setReceiver(receiver);

        TransactionResponseDTO dto = mapper.toTransactionResponseDTO(entity);

        assertThat(dto.id()).isEqualTo(10L);
        assertThat(dto.value()).isEqualByComparingTo("50.00");
        assertThat(dto.sender().id()).isEqualTo(1L);
        assertThat(dto.receiver().id()).isEqualTo(2L);
    }
}
