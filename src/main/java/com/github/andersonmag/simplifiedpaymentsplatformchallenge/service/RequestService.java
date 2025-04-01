package com.github.andersonmag.simplifiedpaymentsplatformchallenge.service;

import com.github.andersonmag.simplifiedpaymentsplatformchallenge.domain.dtos.NotificationSendResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@AllArgsConstructor
@Service
public class RequestService {

    private static final String AUTHORIZATION_URL = "https://util.devi.tools/api/v2/authorize";
    private static final String NOTIFICATION_URL = "https://util.devi.tools/api/v1/notify";
    private final RestTemplate restTemplate;

    public ResponseEntity<Void> requestAuthorization() {
        return restTemplate.getForEntity(AUTHORIZATION_URL, Void.class);
    }

    public ResponseEntity<NotificationSendResponse> sendNotification() {
        return restTemplate.postForEntity(NOTIFICATION_URL, null, NotificationSendResponse.class);
    }
}
