package com.factor.interfaces.rest.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record FactorValueRowDto(
        LocalDate tradeDate,
        String fundCode,
        String fundName,
        String factorId,
        String factorName,
        BigDecimal value,
        LocalDateTime updatedAt
) {
}
