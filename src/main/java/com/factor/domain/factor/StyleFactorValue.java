package com.factor.domain.factor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record StyleFactorValue(
        String id,
        String fundCode,
        String styleFactorId,
        LocalDate dataDate,
        BigDecimal value,
        LocalDateTime calculatedAt
) {
}
