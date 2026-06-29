package com.factor.domain.factor;

import java.math.BigDecimal;

public record DerivativeFactorItem(
        String id,
        String derivativeFactorId,
        String baseFactorId,
        BigDecimal weight
) {
}
