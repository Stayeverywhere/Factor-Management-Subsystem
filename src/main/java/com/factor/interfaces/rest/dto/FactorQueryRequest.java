package com.factor.interfaces.rest.dto;

import com.factor.domain.factor.FactorCategory;

public record FactorQueryRequest(
        FactorCategory category,
        long page,
        long size
) {
}
