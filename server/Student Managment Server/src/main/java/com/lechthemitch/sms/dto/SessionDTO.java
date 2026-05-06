package com.lechthemitch.sms.dto;

import jakarta.validation.constraints.NotBlank;

public record SessionDTO(
        @NotBlank String title,
        String description
) {
}