package com.lechthemitch.sms.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

public record AttendanceScanRequestDTO(
        @NotBlank String qrCode,
        @NotNull Integer sessionId,
        @NotNull Integer hallId,
        @Nullable OffsetDateTime attendanceDate
) {
}
