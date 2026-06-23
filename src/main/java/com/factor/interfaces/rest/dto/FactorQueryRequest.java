package com.factor.interfaces.rest.dto;

import java.time.LocalDate;

public record FactorQueryRequest(
        String fundCode,
        String factorId,
        LocalDate startDate,
        LocalDate endDate,
        long page,
        long size
) {
}
