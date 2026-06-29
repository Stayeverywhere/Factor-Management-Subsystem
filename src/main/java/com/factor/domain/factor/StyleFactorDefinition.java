package com.factor.domain.factor;

import java.time.LocalDateTime;

public record StyleFactorDefinition(
        String id,
        String name,
        String createdBy,
        LocalDateTime createdAt,
        String description,
        boolean enabled
) {
}
