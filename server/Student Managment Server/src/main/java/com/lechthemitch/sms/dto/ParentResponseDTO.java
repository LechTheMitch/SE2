package com.lechthemitch.sms.dto;

import java.util.List;

public record ParentResponseDTO(
        Integer id,
        Integer userId,
        String userPhone,
        List<Integer> childIds
) {
}

