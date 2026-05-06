package com.lechthemitch.sms.dto;

import java.util.Set;

public record SessionResponseDTO(
        Integer id,
        String title,
        String description,
        Set<HallResponseDTO> halls
) {
}