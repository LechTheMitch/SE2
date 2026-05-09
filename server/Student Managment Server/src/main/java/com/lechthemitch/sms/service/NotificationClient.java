package com.lechthemitch.sms.service;

import com.lechthemitch.sms.dto.notification.AttendanceNotificationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notification-service")
public interface NotificationClient {

    @PostMapping("/api/notifications/attendance")
    void sendAttendanceNotification(@RequestBody AttendanceNotificationRequest request);
}
