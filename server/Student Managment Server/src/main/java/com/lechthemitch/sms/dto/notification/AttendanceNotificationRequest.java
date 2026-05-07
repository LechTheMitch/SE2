package com.lechthemitch.sms.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AttendanceNotificationRequest {
    private String studentName;
    private String parentEmail;
    private String parentPhoneNumber;
    private String sessionName;
    private OffsetDateTime attendanceTime;
}
