package com.factor.domain.factor;

import java.time.LocalDateTime;

public record DerivativeFactor(
        String id,
        String code,
        String name,
        String createdBy,
        LocalDateTime createdAt,
        String description,
        boolean enabled
) {
}
