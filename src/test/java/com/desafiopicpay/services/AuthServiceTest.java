package com.desafiopicpay.services;

import com.desafiopicpay.domain.role.Role;
import com.desafiopicpay.domain.user.User;
import com.desafiopicpay.dtos.LoginRequestDTO;
import com.desafiopicpay.dtos.LoginResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private JwtEncoder jwtEncoder;

    @Mock
    private UserService userService;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("Should return access token when credentials are valid")
    void login_Success() {
        // Arrange
        String email = "test@test.com";
        String password = "password";
        LoginRequestDTO loginRequest = new LoginRequestDTO(email, password);
        
        User user = new User();
        user.setId(1L);
        user.setEmail(email);
        user.setPassword("encodedPassword");
        user.setRoles(Set.of(new Role(1L, "BASIC")));

        when(userService.findUserEntityByEmail(email)).thenReturn(user);
        when(bCryptPasswordEncoder.matches(password, "encodedPassword")).thenReturn(true);

        Jwt jwt = mock(Jwt.class);
        when(jwt.getTokenValue()).thenReturn("mocked-token");
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(jwt);

        // Act
        LoginResponseDTO response = authService.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals("mocked-token", response.accessToken());
        assertEquals(300L, response.expiresIn());
    }

    @Test
    @DisplayName("Should throw BadCredentialsException when password is invalid")
    void login_InvalidPassword() {
        // Arrange
        String email = "test@test.com";
        String password = "wrongPassword";
        LoginRequestDTO loginRequest = new LoginRequestDTO(email, password);

        User user = new User();
        user.setPassword("encodedPassword");

        when(userService.findUserEntityByEmail(email)).thenReturn(user);
        when(bCryptPasswordEncoder.matches(password, "encodedPassword")).thenReturn(false);

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> authService.login(loginRequest));
    }
}
