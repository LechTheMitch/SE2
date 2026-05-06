package com.lechthemitch.sms.dto;

import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

public record AttendanceRecordDTO(
        @NotNull Integer studentId,
        @NotNull Integer sessionId,
        @NotNull Integer hallId,
        @NotNull OffsetDateTime attendanceDate
) {
}
