package com.lechthemitch.sms.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.OffsetDateTime;

public record HallDTO(
        @NotBlank String name,
        String location,
        OffsetDateTime sessionTime,
        Integer sessionId
) {
}