package com.factor.domain.factor;

import java.math.BigDecimal;
import java.util.List;

public record DerivativeFactorCreateRequest(
        String name,
        List<Item> items,
        String formula
) {
    public record Item(String baseFactorId, BigDecimal weight) {}
}
