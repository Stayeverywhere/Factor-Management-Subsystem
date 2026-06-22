package com.factor.domain.factor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DerivedFactor(
        String id,
        String name,
        String formulaId,
        BigDecimal value,
        String unit,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
