package com.lechthemitch.sms.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lechthemitch.sms.entity.PermissionType;
import com.lechthemitch.sms.entity.RoleType;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotBlank;

import java.util.Set;

public record RegisterDTO(
        @NotBlank String firstName,
        String lastName,
        @Email String email,
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) String password,
        @NotBlank @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Phone number must be in international format, e.g. +15551234567") String phoneNumber,
        RoleType role,
        @Nullable Set<PermissionType> permissions
) {
}
