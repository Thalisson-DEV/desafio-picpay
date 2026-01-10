package com.desafiopicpay.services;

import com.desafiopicpay.domain.user.User;
import com.desafiopicpay.domain.user.UserType;
import com.desafiopicpay.dtos.PaginatedUsersResponseDTO;
import com.desafiopicpay.dtos.UserRequestDTO;
import com.desafiopicpay.dtos.UserResponseDTO;
import com.desafiopicpay.exceptions.InvalidOperationException;
import com.desafiopicpay.mappers.UserMapper;
import com.desafiopicpay.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.NonNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Should validate transaction successfully when user is COMMON and has balance")
    void validateTransactionSuccess() {
        User sender = new User(1L, "John", "Doe", "12345678900", "john@test.com", "123456", new BigDecimal("100.00"), UserType.COMMON, Collections.emptySet());
        assertDoesNotThrow(() -> userService.validateTransaction(sender, new BigDecimal("50.00")));
    }

    @Test
    @DisplayName("Should throw exception when user is MERCHANT")
    void validateTransactionMerchantFail() {
        User sender = new User(1L, "John", "Doe", "12345678900", "john@test.com", "123456", new BigDecimal("100.00"), UserType.MERCHANT, Collections.emptySet());
        assertThrows(InvalidOperationException.class, () -> userService.validateTransaction(sender, new BigDecimal("50.00")));
    }

    @Test
    @DisplayName("Should throw exception when balance is insufficient")
    void validateTransactionBalanceFail() {
        User sender = new User(1L, "John", "Doe", "12345678900", "john@test.com", "123456", new BigDecimal("10.00"), UserType.COMMON, Collections.emptySet());
        assertThrows(InvalidOperationException.class, () -> userService.validateTransaction(sender, new BigDecimal("50.00")));
    }

    @Test
    @DisplayName("Should create user successfully")
    void createUserSuccess() {
        UserRequestDTO request = new UserRequestDTO("John", "Doe", "12345678900", new BigDecimal("100"), "test@test.com", "123", UserType.COMMON, Collections.emptySet());
        User user = new User(1L, "John", "Doe", "12345678900", "test@test.com", "123", new BigDecimal("100"), UserType.COMMON, Collections.emptySet());
        UserResponseDTO responseDTO = new UserResponseDTO(1L, "John", "Doe", "12345678900", "test@test.com", new BigDecimal("100"), UserType.COMMON, Collections.emptySet());

        when(userMapper.toUserEntity(request)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toUserResponseDTO(user)).thenReturn(responseDTO);

        UserResponseDTO result = userService.createUser(request);

        assertNotNull(result);
        assertEquals(responseDTO, result);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("Should find user by ID successfully")
    void findUserByIdSuccess() {
        User user = new User(1L, "John", "Doe", "12345678900", "test@test.com", "123", new BigDecimal("100"), UserType.COMMON, Collections.emptySet());
        UserResponseDTO responseDTO = new UserResponseDTO(1L, "John", "Doe", "12345678900", "test@test.com", new BigDecimal("100"), UserType.COMMON, Collections.emptySet());

        when(userRepository.findUserById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toUserResponseDTO(user)).thenReturn(responseDTO);

        UserResponseDTO result = userService.findUserById(1L);

        assertNotNull(result);
        assertEquals(responseDTO, result);
    }

    @Test
    @DisplayName("Should throw exception when user not found by ID")
    void findUserByIdNotFound() {
        when(userRepository.findUserById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.findUserById(1L));
    }
    
    @Test
    @DisplayName("Should return paginated users successfully")
    void findAllUsersPaginatedSuccess() {
        User user = new User(1L, "John", "Doe", "12345678900", "test@test.com", "123", new BigDecimal("100"), UserType.COMMON, Collections.emptySet());
        Page<@NonNull User> page = new PageImpl<>(List.of(user));
        UserResponseDTO responseDTO = new UserResponseDTO(1L, "John", "Doe", "12345678900", "test@test.com", new BigDecimal("100"), UserType.COMMON, Collections.emptySet());
        PaginatedUsersResponseDTO paginatedResponse = new PaginatedUsersResponseDTO(List.of(responseDTO), 0, 10, 1, 1);
        Pageable pageable = PageRequest.of(0, 10);

        when(userRepository.findAll(pageable)).thenReturn(page);
        when(userMapper.toPaginatedResponseDTO(page)).thenReturn(paginatedResponse);

        PaginatedUsersResponseDTO result = userService.findAllUsersPaginated(pageable);

        assertNotNull(result);
        assertEquals(1, result.data().size());
        assertEquals(responseDTO, result.data().getFirst());
    }
}
