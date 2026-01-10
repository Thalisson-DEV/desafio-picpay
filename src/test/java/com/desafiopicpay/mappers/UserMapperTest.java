package com.desafiopicpay.mappers;

import com.desafiopicpay.domain.user.User;
import com.desafiopicpay.domain.user.UserType;
import com.desafiopicpay.dtos.PaginatedUsersResponseDTO;
import com.desafiopicpay.dtos.UserRequestDTO;
import com.desafiopicpay.dtos.UserResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UserMapperTest {

    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapperImpl();
    }

    @Test
    @DisplayName("Should map UserRequestDTO to User entity")
    void toUserEntity() {
        UserRequestDTO dto = new UserRequestDTO("John", "Doe", "12345678900", new BigDecimal("100"), "test@test.com", "123", UserType.COMMON, Collections.emptySet());
        
        User user = userMapper.toUserEntity(dto);

        assertNotNull(user);
        assertEquals(dto.firstName(), user.getFirstName());
        assertEquals(dto.lastName(), user.getLastName());
        assertEquals(dto.document(), user.getDocument());
        assertEquals(dto.balance(), user.getBalance());
        assertEquals(dto.email(), user.getEmail());
        assertEquals(dto.password(), user.getPassword());
        assertEquals(dto.userType(), user.getUserType());
    }

    @Test
    @DisplayName("Should map User entity to UserResponseDTO")
    void toUserResponseDTO() {
        User user = new User(1L, "John", "Doe", "12345678900", "test@test.com", "123", new BigDecimal("100"), UserType.COMMON, Collections.emptySet());

        UserResponseDTO dto = userMapper.toUserResponseDTO(user);

        assertNotNull(dto);
        assertEquals(user.getId(), dto.id());
        assertEquals(user.getFirstName(), dto.firstName());
        assertEquals(user.getLastName(), dto.lastName());
        assertEquals(user.getDocument(), dto.document());
        assertEquals(user.getEmail(), dto.email());
        assertEquals(user.getBalance(), dto.balance());
        assertEquals(user.getUserType(), dto.userType());
    }

    @Test
    @DisplayName("Should update User from DTO")
    void updateUserFromDTO() {
        User user = new User(1L, "John", "Doe", "12345678900", "test@test.com", "123", new BigDecimal("100"), UserType.COMMON, Collections.emptySet());
        UserRequestDTO updateDto = new UserRequestDTO("Jane", "Doe", "12345678900", new BigDecimal("200"), "jane@test.com", "456", UserType.MERCHANT, Collections.emptySet());

        userMapper.updateUserFromDTO(updateDto, user);

        assertEquals(updateDto.firstName(), user.getFirstName());
        assertEquals(updateDto.lastName(), user.getLastName());
        assertEquals(updateDto.document(), user.getDocument());
        assertEquals(updateDto.balance(), user.getBalance());
        assertEquals(updateDto.email(), user.getEmail());
        assertEquals(updateDto.password(), user.getPassword());
        assertEquals(updateDto.userType(), user.getUserType());
    }

    @Test
    @DisplayName("Should map Page<User> to PaginatedUsersResponseDTO")
    void toPaginatedResponseDTO() {
        User user = new User(1L, "John", "Doe", "12345678900", "test@test.com", "123", new BigDecimal("100"), UserType.COMMON, Collections.emptySet());
        Page<User> page = new PageImpl<>(List.of(user));

        PaginatedUsersResponseDTO result = userMapper.toPaginatedResponseDTO(page);

        assertNotNull(result);
        assertEquals(1, result.data().size());
        assertEquals(page.getNumber(), result.pageNumber());
        assertEquals(page.getSize(), result.pageSize());
        assertEquals(page.getTotalElements(), result.totalElements());
        assertEquals(page.getTotalPages(), result.totalPages());
    }
}
