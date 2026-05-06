package com.lechthemitch.sms.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ForcedAccountUpdateDTO(
        @NotBlank String emailOrPhone,
        @NotBlank String currentPassword,
        @NotBlank @Email String newEmail,
        @NotBlank @Size(min = 8, max = 72, message = "Password must be between 8 and 72 characters") String newPassword
) {
}

