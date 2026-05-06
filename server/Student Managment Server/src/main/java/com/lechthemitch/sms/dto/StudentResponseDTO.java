package com.lechthemitch.sms.dto;

public record StudentResponseDTO(
        Integer id,
        Integer userId,
        Integer parentId,
        String parentPhoneNumber,
        String qrCode
) {
}
