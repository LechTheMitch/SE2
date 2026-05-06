package com.lechthemitch.sms.dto;

import com.lechthemitch.sms.entity.RoleType;

import java.util.Set;

public record CurrentUserResponseDTO(
        Integer userId,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        RoleType role,
        Set<String> permissions,
        boolean forcePasswordChange,
        boolean forceEmailChange,
        Integer parentId,
        Integer studentId
) {
}
