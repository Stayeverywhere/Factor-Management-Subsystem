package com.factor.domain.factor;

import java.time.LocalDateTime;

public record StyleFactor(
        String id,
        String name,
        String category,
        String description,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
