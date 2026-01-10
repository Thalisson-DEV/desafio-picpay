package com.desafiopicpay.services;

import com.desafiopicpay.dtos.NotificationRequestDTO;
import com.desafiopicpay.exceptions.ServiceUnivaliablleException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final RestTemplate restTemplate;

    public void sendNotification(String email, String message) {
        NotificationRequestDTO notificationRequest = new NotificationRequestDTO(email, message);

        ResponseEntity<@NonNull String> notificationResponse = restTemplate.postForEntity("https://util.devi.tools/api/v1/notify", notificationRequest, String.class);

        if (!(notificationResponse.getStatusCode().equals(HttpStatus.OK))) {
            throw new ServiceUnivaliablleException("Service is temporally unalienable");
        }
    }
}
