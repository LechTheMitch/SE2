package com.lechthemitch.sms.service;

import com.lechthemitch.sms.dto.notification.AttendanceNotificationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationClient {

    private final RestTemplate restTemplate;

    @Value("${notification.service.url:http://localhost:8081}")
    private String notificationServiceUrl;

    public void sendAttendanceNotification(AttendanceNotificationRequest request) {
        String url = notificationServiceUrl + "/api/notifications/attendance";
        try {
            restTemplate.postForEntity(url, request, Void.class);
            log.info("Successfully sent notification request for student: {}", request.getStudentName());
        } catch (Exception e) {
            log.error("Failed to send notification for student: {}. Error: {}", request.getStudentName(), e.getMessage());
        }
    }
}
