package com.factor.domain.factor;

import java.time.LocalDate;

public record FactorQueryCondition(
        String fundCode,
        String factorId,
        LocalDate startDate,
        LocalDate endDate,
        long page,
        long size
) {
}
