package com.lechthemitch.notification.controller;

import com.lechthemitch.notification.dto.AttendanceNotificationRequest;
import com.lechthemitch.notification.service.NotificationService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

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

    @GetMapping(value = "/subscribe/{email}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@PathVariable String email) {
        return notificationService.subscribe(email);
    }
}
