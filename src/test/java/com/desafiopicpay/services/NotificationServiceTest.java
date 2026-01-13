package com.desafiopicpay.services;

import com.desafiopicpay.dtos.NotificationRequestDTO;
import com.desafiopicpay.exceptions.ServiceUnivaliablleException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    @DisplayName("Should send notification successfully")
    void sendNotification_Success() {
        // Arrange
        String email = "test@test.com";
        String message = "Hello";
        
        when(restTemplate.postForEntity(eq("https://util.devi.tools/api/v1/notify"), any(NotificationRequestDTO.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>("Success", HttpStatus.OK));

        // Act & Assert
        assertDoesNotThrow(() -> notificationService.sendNotification(email, message));
        verify(restTemplate).postForEntity(eq("https://util.devi.tools/api/v1/notify"), any(NotificationRequestDTO.class), eq(String.class));
    }

    @Test
    @DisplayName("Should throw exception when notification service is down")
    void sendNotification_Failure() {
        // Arrange
        String email = "test@test.com";
        String message = "Hello";

        when(restTemplate.postForEntity(eq("https://util.devi.tools/api/v1/notify"), any(NotificationRequestDTO.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>("Error", HttpStatus.INTERNAL_SERVER_ERROR));

        // Act & Assert
        assertThrows(ServiceUnivaliablleException.class, () -> notificationService.sendNotification(email, message));
    }
}
