package com.factor.domain.factor;

import java.math.BigDecimal;

public record FormulaItem(
        String factorId,
        String factorName,
        BigDecimal weight,
        String operator
) {
}
