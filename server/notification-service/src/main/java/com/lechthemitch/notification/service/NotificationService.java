package com.lechthemitch.notification.service;

import com.lechthemitch.notification.dto.AttendanceNotificationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    public void sendAttendanceNotification(AttendanceNotificationRequest request) {
        log.info("Sending attendance notification for student: {}", request.getStudentName());
        log.info("Parent Email: {}, Parent Phone: {}", request.getParentEmail(), request.getParentPhoneNumber());
        log.info("Session: {}, Time: {}", request.getSessionName(), request.getAttendanceTime());
        
        // In a real scenario, this is where you'd call an Email or SMS API.
        log.info("Notification sent successfully!");
    }
}
