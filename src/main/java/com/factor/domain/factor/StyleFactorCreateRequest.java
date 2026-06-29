package com.factor.domain.factor;

import java.math.BigDecimal;
import java.util.List;

public record StyleFactorCreateRequest(
        String name,
        List<Item> items
) {
    public record Item(String derivativeFactorId, BigDecimal weight) {}
}
