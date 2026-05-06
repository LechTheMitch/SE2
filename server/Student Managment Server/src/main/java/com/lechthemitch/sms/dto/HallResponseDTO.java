package com.lechthemitch.sms.dto;

import java.time.OffsetDateTime;

public record HallResponseDTO(
        Integer id,
        String name,
        String location,
        OffsetDateTime sessionTime,
        Integer sessionId
) {
}