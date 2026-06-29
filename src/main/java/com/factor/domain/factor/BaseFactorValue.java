package com.factor.domain.factor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record BaseFactorValue(
        String id,
        String fundCode,
        String baseFactorId,
        LocalDate dataDate,
        BigDecimal value,
        LocalDateTime updatedAt
) {
}
