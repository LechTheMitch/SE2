package com.lechthemitch.notification.controller;

import com.lechthemitch.notification.dto.AttendanceNotificationRequest;
import com.lechthemitch.notification.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/attendance")
    public ResponseEntity<Void> notifyAttendance(@RequestBody AttendanceNotificationRequest request) {
        notificationService.sendAttendanceNotification(request);
        return ResponseEntity.ok().build();
    }
}
