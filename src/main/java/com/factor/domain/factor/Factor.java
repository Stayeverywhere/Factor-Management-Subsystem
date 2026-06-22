package com.factor.domain.factor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

public record Factor(
        String id,
        String code,
        String name,
        FactorCategory category,
        String source,
        String description,
        BigDecimal latestValue,
        String unit,
        Map<String, Object> metadata,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
