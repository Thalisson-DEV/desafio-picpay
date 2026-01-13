package com.desafiopicpay.mappers;

import com.desafiopicpay.domain.user.User;
import com.desafiopicpay.domain.user.UserType;
import com.desafiopicpay.dtos.UserRequestDTO;
import com.desafiopicpay.dtos.UserResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

    @Test
    @DisplayName("Should map UserRequestDTO to User entity")
    void toUserEntity_Success() {
        UserRequestDTO dto = new UserRequestDTO("John", "Doe", "12345678900", new BigDecimal("100.00"), "john@test.com", "password", UserType.COMMON, null);

        User entity = mapper.toUserEntity(dto);

        assertThat(entity.getFirstName()).isEqualTo("John");
        assertThat(entity.getLastName()).isEqualTo("Doe");
        assertThat(entity.getEmail()).isEqualTo("john@test.com");
        assertThat(entity.getBalance()).isEqualByComparingTo("100.00");
    }

    @Test
    @DisplayName("Should map User entity to UserResponseDTO")
    void toUserResponseDTO_Success() {
        User entity = new User();
        entity.setId(1L);
        entity.setFirstName("Jane");
        entity.setLastName("Doe");
        entity.setBalance(new BigDecimal("50.00"));

        UserResponseDTO dto = mapper.toUserResponseDTO(entity);

        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.firstName()).isEqualTo("Jane");
        assertThat(dto.balance()).isEqualByComparingTo("50.00");
    }
}
