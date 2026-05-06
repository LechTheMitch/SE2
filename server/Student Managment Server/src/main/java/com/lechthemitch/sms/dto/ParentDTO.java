package com.lechthemitch.sms.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ParentDTO(
        @NotNull @Positive Integer userId
) {
}

