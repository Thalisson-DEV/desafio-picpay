package com.desafiopicpay.services;

import com.desafiopicpay.domain.role.Role;
import com.desafiopicpay.domain.user.User;
import com.desafiopicpay.domain.user.UserType;
import com.desafiopicpay.dtos.PaginatedUsersResponseDTO;
import com.desafiopicpay.dtos.UserRequestDTO;
import com.desafiopicpay.dtos.UserResponseDTO;
import com.desafiopicpay.mappers.UserMapper;
import com.desafiopicpay.repositories.RoleRepository;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Should create user successfully when all data is valid")
    void createUser_Success() {
        // Arrange
        UserRequestDTO requestDTO = new UserRequestDTO("John", "Doe", "12345678900", new BigDecimal("100.00"), "john@test.com", "password", UserType.COMMON, null);
        User userEntity = new User();
        userEntity.setPassword("password");
        userEntity.setRoles(Set.of(new Role(1L, "BASIC")));
        
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setPassword("encodedPassword");

        UserResponseDTO responseDTO = new UserResponseDTO(1L, "John", "Doe", "12345678900", "john@test.com", new BigDecimal("100.00"), UserType.COMMON, null);

        when(userMapper.toUserEntity(requestDTO)).thenReturn(userEntity);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(roleRepository.findById(1L)).thenReturn(Optional.of(new Role(1L, "BASIC")));
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userMapper.toUserResponseDTO(any(User.class))).thenReturn(responseDTO);

        // Act
        UserResponseDTO result = userService.createUser(requestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(responseDTO.firstName(), result.firstName());
        verify(userRepository, times(1)).save(any(User.class));
        verify(passwordEncoder, times(1)).encode("password");
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when Role does not exist")
    void createUser_RoleNotFound() {
        // Arrange
        UserRequestDTO requestDTO = new UserRequestDTO("John", "Doe", "12345678900", new BigDecimal("100.00"), "john@test.com", "password",UserType.COMMON, null);
        User userEntity = new User();
        userEntity.setPassword("password");
        Role invalidRole = new Role();
        invalidRole.setId(99L);
        userEntity.setRoles(Set.of(invalidRole));

        when(userMapper.toUserEntity(requestDTO)).thenReturn(userEntity);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(roleRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> userService.createUser(requestDTO));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should find user by ID successfully")
    void findUserById_Success() {
        // Arrange
        Long id = 1L;
        User user = new User();
        user.setId(id);
        UserResponseDTO responseDTO = new UserResponseDTO(id, "John", "Doe", "12345678900", "john@test.com", BigDecimal.ZERO, UserType.COMMON, null);

        when(userRepository.findUserById(id)).thenReturn(Optional.of(user));
        when(userMapper.toUserResponseDTO(user)).thenReturn(responseDTO);

        // Act
        UserResponseDTO result = userService.findUserById(id);

        // Assert
        assertNotNull(result);
        assertEquals(id, result.id());
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when user ID not found")
    void findUserById_NotFound() {
        // Arrange
        Long id = 1L;
        when(userRepository.findUserById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> userService.findUserById(id));
    }

    @Test
    @DisplayName("Should find user by Document successfully")
    void findUserByDocument_Success() {
        // Arrange
        String doc = "12345678900";
        User user = new User();
        UserResponseDTO responseDTO = new UserResponseDTO(1L, "John", "Doe", doc, "john@test.com", BigDecimal.ZERO, UserType.COMMON, null);

        when(userRepository.findUserByDocument(doc)).thenReturn(Optional.of(user));
        when(userMapper.toUserResponseDTO(user)).thenReturn(responseDTO);

        // Act
        UserResponseDTO result = userService.findUserByDocument(doc);

        // Assert
        assertEquals(doc, result.document());
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when Document not found")
    void findUserByDocument_NotFound() {
        // Arrange
        String doc = "12345678900";
        when(userRepository.findUserByDocument(doc)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> userService.findUserByDocument(doc));
    }

    @Test
    @DisplayName("Should find all users paginated successfully")
    void findAllUsersPaginated_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        User user = new User();
        Page<@NonNull User> page = new PageImpl<>(List.of(user));
        PaginatedUsersResponseDTO responseDTO = new PaginatedUsersResponseDTO(List.of(new UserResponseDTO(1L, "John", "Doe", "doc", "email", BigDecimal.ZERO, UserType.COMMON, null)), 0, 1, 1, 1);

        when(userRepository.findAll(pageable)).thenReturn(page);
        when(userMapper.toPaginatedResponseDTO(page)).thenReturn(responseDTO);

        // Act
        PaginatedUsersResponseDTO result = userService.findAllUsersPaginated(pageable);

        // Assert
        assertNotNull(result);
        assertFalse(result.data().isEmpty());
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when no users found")
    void findAllUsersPaginated_Empty() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<@NonNull User> emptyPage = new PageImpl<>(Collections.emptyList());

        when(userRepository.findAll(pageable)).thenReturn(emptyPage);

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> userService.findAllUsersPaginated(pageable));
    }

    @Test
    @DisplayName("Should update user successfully")
    void updateUser_Success() {
        // Arrange
        Long id = 1L;
        UserRequestDTO requestDTO = new UserRequestDTO("Jane", "Doe", "12345678900", BigDecimal.ZERO, "jane@test.com", "password", UserType.COMMON,  null);
        User existingUser = new User();
        existingUser.setId(id);
        
        UserResponseDTO responseDTO = new UserResponseDTO(id, "Jane", "Doe", "12345678900", "jane@test.com", BigDecimal.ZERO, UserType.COMMON, null);

        when(userRepository.findUserById(id)).thenReturn(Optional.of(existingUser));
        when(userMapper.toUserResponseDTO(existingUser)).thenReturn(responseDTO);

        // Act
        UserResponseDTO result = userService.updateUser(id, requestDTO);

        // Assert
        assertEquals("Jane", result.firstName());
        verify(userMapper).updateUserFromDTO(requestDTO, existingUser);
    }
    
    @Test
    @DisplayName("Should delete user successfully")
    void deleteUserById_Success() {
        // Arrange
        Long id = 1L;
        when(userRepository.existsById(id)).thenReturn(true);

        // Act
        userService.deleteUserById(id);

        // Assert
        verify(userRepository, times(1)).deleteById(id);
    }
}
