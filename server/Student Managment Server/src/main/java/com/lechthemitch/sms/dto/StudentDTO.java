package com.lechthemitch.sms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Pattern;

public record StudentDTO(
        @NotNull @Positive Integer userId,
        @Nullable @Positive Integer parentId,
        @NotBlank @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Parent phone number must be in international format, e.g. +15551234567") String parentPhoneNumber
) {
}

