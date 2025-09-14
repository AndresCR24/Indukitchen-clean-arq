package com.indukitchen.indukitchen.domain.dto.request;

public record EmailRequestDto(
        String to,
        String subject,
        String text
) {
}
