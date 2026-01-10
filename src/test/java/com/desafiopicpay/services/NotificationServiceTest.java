package com.desafiopicpay.services;

import com.desafiopicpay.dtos.NotificationRequestDTO;
import com.desafiopicpay.exceptions.ServiceUnivaliablleException;
import lombok.NonNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    @DisplayName("Should send notification successfully")
    void sendNotificationSuccess() {
        ResponseEntity<@NonNull String> response = new ResponseEntity<>("Sent", HttpStatus.OK);
        when(restTemplate.postForEntity(anyString(), any(NotificationRequestDTO.class), eq(String.class)))
                .thenReturn(response);

        assertDoesNotThrow(() -> notificationService.sendNotification("test@test.com", "Message"));
        verify(restTemplate, times(1)).postForEntity(anyString(), any(NotificationRequestDTO.class), eq(String.class));
    }

    @Test
    @DisplayName("Should throw exception when notification service is down")
    void sendNotificationFail() {
        ResponseEntity<@NonNull String> response = new ResponseEntity<>("Error", HttpStatus.INTERNAL_SERVER_ERROR);
        when(restTemplate.postForEntity(anyString(), any(NotificationRequestDTO.class), eq(String.class)))
                .thenReturn(response);

        assertThrows(ServiceUnivaliablleException.class, () -> notificationService.sendNotification("test@test.com", "Message"));
    }
}
