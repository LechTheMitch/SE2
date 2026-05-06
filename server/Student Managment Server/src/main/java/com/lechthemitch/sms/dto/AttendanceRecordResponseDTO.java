package com.lechthemitch.sms.dto;

import java.time.OffsetDateTime;

public record AttendanceRecordResponseDTO(
        Integer id,
        Integer studentId,
        Integer sessionId,
        Integer hallId,
        OffsetDateTime attendanceDate
) {
}
