package com.factor.domain.factor;

import java.math.BigDecimal;

public record StyleFactorItem(
        String id,
        String styleFactorId,
        String derivativeFactorId,
        BigDecimal weight
) {
}
