package com.factor.domain.factor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record DerivativeFactorValue(
        String id,
        String fundCode,
        String derivativeFactorId,
        LocalDate dataDate,
        BigDecimal value,
        LocalDateTime calculatedAt
) {
}
