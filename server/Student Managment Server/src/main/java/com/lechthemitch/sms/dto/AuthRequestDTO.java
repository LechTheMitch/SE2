package com.lechthemitch.sms.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthRequestDTO(@NotBlank String emailOrPhone, @NotBlank String password) {
}
